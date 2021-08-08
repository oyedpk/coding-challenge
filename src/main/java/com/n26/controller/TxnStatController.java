package com.n26.controller;

import com.n26.model.Stat;
import com.n26.model.Txn;
import com.n26.service.ITxnStatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author oyedpk
 */
@RestController
public class TxnStatController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TxnStatController.class);

    @Autowired
    ITxnStatService txnStatService;

    @GetMapping("/statistics")
    public ResponseEntity<Stat> getStats() {
        LOGGER.info("Stat request received");
        Stat stat = txnStatService.getStatsForTxns();
        LOGGER.info("Stat Returned: {}", stat);
        return new ResponseEntity<>(stat, HttpStatus.OK);
    }

    @PostMapping("/transactions")
    @RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createTxn(@RequestBody Txn txn)  {
        LOGGER.info("Request to add txn {}", txn);
        txnStatService.createTxn(txn);
        LOGGER.info("Txn processed successfully");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/transactions")
    public ResponseEntity<Void> deleteTxns() {
        LOGGER.info("Request to delete all transactions");
        txnStatService.deleteTxns();
        LOGGER.info("All transactions are deleted successfully");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
