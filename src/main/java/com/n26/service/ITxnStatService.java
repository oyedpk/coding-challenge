package com.n26.service;

import com.n26.exception.OldTxnException;
import com.n26.exception.InvalidTxnException;
import com.n26.model.Stat;
import com.n26.model.Txn;

/**
 * @author oyedpk
 */
public interface ITxnStatService {

    void createTxn(Txn txn) throws InvalidTxnException, OldTxnException;

    void deleteTxns();

    Stat getStatsForTxns();

}
