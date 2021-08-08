package service;

import com.n26.Application;
import com.n26.model.Txn;
import com.n26.service.ITxnStatService;
import com.n26.exception.OldTxnException;
import com.n26.exception.InvalidTxnException;
import com.n26.model.Stat;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class TxnServiceTest {

    private static final BigDecimal BIG_DECIMAL_ZERO = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
    @Autowired
    ITxnStatService ITxnStatService;
    Txn validTxn;
    Txn invalidTxnWithInvalidTimeStamp;
    Txn invalidTxnWithInvalidAmount;
    Txn outdatedTxn;
    Txn futureTxn;
    Txn validTxn1;
    Txn validTxn2;

    @Before
    public void setup() {
        validTxn = new Txn("12.32", Instant.now().toString());
        invalidTxnWithInvalidTimeStamp = new Txn("12.32", "Invalid Time");
        invalidTxnWithInvalidAmount = new Txn("Invalid Amount", Instant.now().toString());
        outdatedTxn = new Txn("342.34", Instant.now().minus(61, ChronoUnit.SECONDS).toString());
        futureTxn = new Txn("342.34", Instant.now().plus(1, ChronoUnit.SECONDS).toString());
        validTxn1 = new Txn("12.32",
                Instant.now().minus(50, ChronoUnit.SECONDS).toString());
        validTxn2 = new Txn("4.34",
                Instant.now().minus(0, ChronoUnit.SECONDS).toString());
        ITxnStatService.deleteTxns();
    }

    @Test
    public void addTestShouldCompleteSuccessfullyWithValidTransaction() throws OldTxnException,
            InvalidTxnException {
        ITxnStatService.createTxn(validTxn);
    }

    @Test(expected = InvalidTxnException.class)
    public void addTransactionShouldThrowExceptionWithInValidTransactionWithInvalidDate() throws OldTxnException,
            InvalidTxnException {
        try {
            ITxnStatService.createTxn(invalidTxnWithInvalidTimeStamp);
        } catch (Exception ex) {
            Assert.assertEquals("Error: Unable to parse txn input", ex.getMessage());
            throw ex;
        }
    }

    @Test(expected = InvalidTxnException.class)
    public void addTransactionShouldThrowExceptionWithInValidTransactionWithInvalidAmount() throws OldTxnException,
            InvalidTxnException {
        try {
            ITxnStatService.createTxn(invalidTxnWithInvalidAmount);
        } catch (Exception ex) {
            Assert.assertEquals("Error: Unable to parse txn input", ex.getMessage());
            throw ex;
        }
    }

    @Test(expected = OldTxnException.class)
    public void addTransactionShouldThrowExceptionWithOutdatedTransaction() throws OldTxnException,
            InvalidTxnException {
        try {
            ITxnStatService.createTxn(outdatedTxn);
        } catch (Exception ex) {
            Assert.assertEquals("Error: Txn Date is outdated", ex.getMessage());
            throw ex;
        }
    }

    @Test(expected = InvalidTxnException.class)
    public void addTestShouldThrowExceptionWithFutureTransaction() throws OldTxnException,
            InvalidTxnException {
        try {
            ITxnStatService.createTxn(futureTxn);
        } catch (Exception ex) {
            Assert.assertEquals("Error: Txn Date is in Future", ex.getMessage());
            throw ex;
        }
    }

    @Test
    public void deleteTransactionShouldSuccessful() {
        ITxnStatService.deleteTxns();
    }


    @Test
    public void getStatisticsWhenNoTransactionsWereAdded() {
        Stat stat = ITxnStatService.getStatsForTxns();
        Assert.assertNotNull(stat);
        Assert.assertEquals(0, stat.getCount());
        Assert.assertEquals(BIG_DECIMAL_ZERO, stat.getSum());
        Assert.assertEquals(BIG_DECIMAL_ZERO, stat.getAvg());
        Assert.assertEquals(BIG_DECIMAL_ZERO, stat.getMin());
        Assert.assertEquals(BIG_DECIMAL_ZERO, stat.getMax());

    }

    @Test
    public void getStatisticsWhenOneTransactionsIsAdded() throws
            OldTxnException, InvalidTxnException {
        ITxnStatService.createTxn(validTxn1);
        Stat stat = ITxnStatService.getStatsForTxns();
        Assert.assertNotNull(stat);
        Assert.assertEquals(1, stat.getCount());
        Assert.assertEquals(BigDecimal.valueOf(12.32), stat.getSum());
        Assert.assertEquals(BigDecimal.valueOf(12.32), stat.getAvg());
        Assert.assertEquals(BigDecimal.valueOf(12.32), stat.getMax());
        Assert.assertEquals(BigDecimal.valueOf(12.32), stat.getMin());

    }

    @Test
    public void getStatisticsWhenMultipleTransactionsWereAdded() throws
            OldTxnException, InvalidTxnException {
        for (int i = 0; i < 5400; i++) {
            if (i % 2 == 0) {
                ITxnStatService.createTxn(validTxn1);
            } else {
                ITxnStatService.createTxn(validTxn2);
            }
        }
        Stat stat = ITxnStatService.getStatsForTxns();
        Assert.assertNotNull(stat);
        Assert.assertEquals(5400, stat.getCount());
        Assert.assertEquals(new BigDecimal("44982.00"), stat.getSum());
        Assert.assertEquals(BigDecimal.valueOf(8.33), stat.getAvg());
        Assert.assertEquals(BigDecimal.valueOf(12.32), stat.getMax());
        Assert.assertEquals(BigDecimal.valueOf(4.34), stat.getMin());
    }
}
