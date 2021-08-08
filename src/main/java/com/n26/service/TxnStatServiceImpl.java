package com.n26.service;

import com.n26.exception.OldTxnException;
import com.n26.exception.InvalidTxnException;
import com.n26.model.Stat;
import com.n26.model.Txn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;


/**
 * @author oyedpk
 */
@Service
public class TxnStatServiceImpl implements ITxnStatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TxnStatServiceImpl.class);
    private static final Map<Instant, Stat> dataStore = new ConcurrentHashMap<>();
    @Value("${txn.time.in.sec}")
    private static long TXN_TIME_IN_SEC=60;

    /**
     * @param txn txn instance to add
     * @throws InvalidTxnException  throws when txn fields are not valid
     * @throws OldTxnException throws when txn date is in past
     */
    @Override
    public void createTxn(Txn txn) throws InvalidTxnException, OldTxnException {
        requestValildator(txn);
        updateStats(Instant.parse(txn.getTimeStamp()).truncatedTo(ChronoUnit.SECONDS),
                new BigDecimal(txn.getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP));

        dataStore.entrySet()
                .removeIf(isOldKey());
    }

    public void requestValildator(Txn txn) throws InvalidTxnException, OldTxnException {
        Instant transactionTime;
        try {
            new BigDecimal(txn.getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP);
            transactionTime = Instant.parse(txn.getTimeStamp()).truncatedTo(ChronoUnit.SECONDS);
        } catch (NumberFormatException | DateTimeParseException ex) {
            LOGGER.error("Error while parsing txn data, Reason: {}", ex.getMessage());
            throw new InvalidTxnException("Error: Unable to parse txn input");
        }
        long timeDifference = Duration.between(transactionTime, Instant.now()).getSeconds();
        if (timeDifference >= TXN_TIME_IN_SEC) {
            throw new OldTxnException("Error: Txn Date is outdated");
        } else if (timeDifference < 0) {
            throw new InvalidTxnException("Error: Txn Date is in Future");
        }
    }

    /**
     * clear all the statistics from dataStore
     */
    @Override
    public void deleteTxns() {
        dataStore.clear();
    }

    /**
     * @return return the statistics of last 60 seconds
     */
    @Override
    public Stat getStatsForTxns() {
        dataStore.entrySet()
                .removeIf(isOldKey());
        return mergeStats();
    }

    /**
     * @param transactionTime   time of the transaction
     * @param transactionAmount Insert or update  statistics for every second
     */
    private void updateStats(Instant transactionTime, BigDecimal transactionAmount) {
        Stat stat = dataStore.getOrDefault(transactionTime, new Stat());
        stat.setSum(stat.getSum().add(transactionAmount));
        stat.setMax(transactionAmount.max(stat.getMax()));
        stat.setMin(stat.getMin().equals(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP)) ?
                transactionAmount:transactionAmount.min(stat.getMin()));
        stat.setCount(stat.getCount() + 1);
        stat.setAvg(stat.getSum()
                .divide(BigDecimal.valueOf(stat.getCount()), BigDecimal.ROUND_HALF_UP));
        dataStore.put(transactionTime, stat);
    }

    /**
     * @return statistic of all the transactions processed in last 60 seconds
     */
    private Stat mergeStats() {
        Stat stat = new Stat();
        for (Map.Entry<Instant, Stat> statisticsEntry : dataStore.entrySet()) {
            stat.setSum(stat.getSum().add(statisticsEntry.getValue().getSum()));
            stat.setCount(stat.getCount() + (statisticsEntry.getValue().getCount()));
            stat.setMax(stat.getMax().max(statisticsEntry.getValue().getMax()));
            stat.setMin((stat.getMin().equals(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP))
                    ?statisticsEntry.getValue().getMin():stat.getMin().min(statisticsEntry.getValue().getMin())));
            stat.setAvg(stat.getSum()
                    .divide(BigDecimal.valueOf(stat.getCount()), BigDecimal.ROUND_HALF_UP));
        }
        return stat;
    }

   public Predicate<? super Map.Entry<Instant, Stat>> isOldKey() {
            return (stat->Duration.between(stat.getKey(), Instant.now().truncatedTo(ChronoUnit.SECONDS))
                    .getSeconds() >= TXN_TIME_IN_SEC);
    }

}


