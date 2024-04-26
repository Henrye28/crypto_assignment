package com.henry.portfolio.portfolio;

import java.math.BigDecimal;

public class MarketValue {
    private String ticker;
    private BigDecimal price;
    private BigDecimal qty;
    private BigDecimal nav;

    public MarketValue(String ticker, BigDecimal price, BigDecimal qty, BigDecimal nav) {
        this.ticker = ticker;
        this.price = price;
        this.qty = qty;
        this.nav = nav;
    }

    public String getTicker() {
        return ticker;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public BigDecimal getNav() {
        return nav;
    }
}
