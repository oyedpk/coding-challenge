package com.n26.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author oyedpk
 */
@Data
public class Stat {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonProperty("sum")
    BigDecimal sum;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonProperty("avg")
    BigDecimal avg;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonProperty("max")
    BigDecimal max;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonProperty("min")
    BigDecimal min;

    @JsonProperty("count")
    long count;

    public Stat() {
        sum = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
        avg = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
        min = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
        max = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
        count = 0;
    }
}
