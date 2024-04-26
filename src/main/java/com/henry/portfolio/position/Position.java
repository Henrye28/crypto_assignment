package com.henry.portfolio.position;

import java.math.BigDecimal;

public class Position {

    String ticker;
    String positionType;
    BigDecimal holding;

    public Position(String ticker, String positionType, BigDecimal holding) {
        this.ticker = ticker;
        this.positionType = positionType;
        this.holding = holding;
    }

    public String getTicker() {
        return ticker;
    }

    public String getPositionType() {
        return positionType;
    }

    public BigDecimal getHolding() {
        return holding;
    }

}
