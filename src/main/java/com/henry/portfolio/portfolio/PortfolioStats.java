package com.henry.portfolio.portfolio;

import com.henry.portfolio.Tuple;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class PortfolioStats {
    List<Tuple<String, MarketValue>> marketValues;
    BigDecimal nav;

    public PortfolioStats(List<Tuple<String, MarketValue>> marketData) {
        this.marketValues = marketData;
        this.nav = marketValues.stream()
                .map(m -> m.getT2().getNav())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void setMarketValue(Tuple<String, MarketValue> marketValue) {
        Optional<Tuple<String, MarketValue>> maybeMarketValue = marketValues.stream()
                .filter(mv -> mv.getT1().equals(marketValue.getT1()))
                .findFirst();
        maybeMarketValue.ifPresent(oldMarketValue -> {
            marketValues.remove(oldMarketValue);
            marketValues.add(marketValue);

            this.nav = marketValues.stream()
                    .map(m -> m.getT2().getNav())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        });
    }

    public List<Tuple<String, MarketValue>> getMarketValues() {
        return marketValues;
    }

    public BigDecimal getNav() {
        return nav;
    }
}
