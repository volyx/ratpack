package io.github.volyx.ratpack.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Avg {
    public Double avg;

    public Avg() {
    }

    public Avg(double avg) {
//            this.avg = (double) Math.round (avg * 100000.0) / 100000.0;  ;
        this.avg = BigDecimal.valueOf(avg)
                .setScale(5, RoundingMode.HALF_UP)
                .doubleValue();;
    }
}