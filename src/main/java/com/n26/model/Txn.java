package com.n26.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author oyedpk
 */
@Data
@AllArgsConstructor
public class Txn {
    @JsonProperty("amount")
    String amount;

    @JsonProperty("timestamp")
    String timeStamp;
}
