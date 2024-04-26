package com.henry.portfolio.portfolio;

import com.henry.portfolio.Tuple;
import com.henry.portfolio.position.Position;
import com.henry.portfolio.ticker.Ticker;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.concurrent.ArrayBlockingQueue;

public class MarketValueStream implements Runnable {
    private Logger LOGGER = LoggerFactory.getLogger(MarketValueStream.class);

    private final Tuple<String, BigDecimal> tickerPrice;
    private final Position position;
    private Ticker ticker;
    private ArrayBlockingQueue<Tuple<String, MarketValue>> portfolioMarketValuesChannel;
    private static BigDecimal rfr = BigDecimal.valueOf(0.02);

    public MarketValueStream(Tuple<String, BigDecimal> tickerPrice,
                             Position position,
                             Ticker ticker,
                             ArrayBlockingQueue<Tuple<String, MarketValue>> portfolioMarketValuesChannel) {
        this.tickerPrice = tickerPrice;
        this.position = position;
        this.ticker = ticker;
        this.portfolioMarketValuesChannel = portfolioMarketValuesChannel;
    }

    @Override
    public void run() {
        try {
            Tuple<String, MarketValue> marketValue = marketValue(tickerPrice, ticker);
            portfolioMarketValuesChannel.put(marketValue);
            LOGGER.debug("Publish market value for {} : {}", marketValue.getT1(),  marketValue.getT2());
        } catch (InterruptedException e) {
            LOGGER.error("Error streaming market value for ticker {}, with error {}", tickerPrice.getT1(), e);
            e.printStackTrace();
        }
    }

    private Tuple<String, MarketValue> marketValue(Tuple<String, BigDecimal> tickerPrice, Ticker ticker){
        BigDecimal price = null;

        switch(ticker.getSecurityType().toUpperCase()){
            case "CALL":
                price = callOptionPrice(tickerPrice.getT2(), ticker);
                break;
            case "PUT":
                price = putOptionPrice(tickerPrice.getT2(), ticker);
                break;
            case "COMMON_STOCK":
                price = tickerPrice.getT2();
                break;
            default:
                throw new IllegalArgumentException("Unknown security type");
        }

        BigDecimal position_qty = position.getHolding();
        BigDecimal marketValueAbs =price.multiply(position_qty);
        BigDecimal nav = position.getPositionType().equals("Long") ? marketValueAbs : marketValueAbs.negate();
        return Tuple.of(ticker.getTicker(), new MarketValue(ticker.getTicker(), price, position_qty, nav));
    }

    private BigDecimal callOptionPrice(BigDecimal price, Ticker ticker){
        BigDecimal ytm = ticker.getMaturity();
        BigDecimal stdDev = ticker.getStdDev();
        BigDecimal strike = ticker.getStrike();
        BigDecimal d1 = BigDecimal.valueOf(Math.log(price.doubleValue()/strike.doubleValue()))
                .add(rfr.add((stdDev.multiply(stdDev).divide(BigDecimal.valueOf(2)))).multiply(ytm))
                .divide(stdDev.multiply(BigDecimal.valueOf(Math.sqrt(ytm.doubleValue()))), 6, BigDecimal.ROUND_HALF_UP);

        BigDecimal d2 = d1.subtract(stdDev.multiply(BigDecimal.valueOf(Math.sqrt(ytm.doubleValue()))));

        NormalDistribution distribution = new NormalDistribution(0 ,1);
        BigDecimal cumulativeProbabilityD1 = BigDecimal.valueOf(distribution.cumulativeProbability(d1.doubleValue()));
        BigDecimal cumulativeProbabilityD2 = BigDecimal.valueOf(distribution.cumulativeProbability(d2.doubleValue()));

        BigDecimal callOptionPrice = price
                .multiply(cumulativeProbabilityD1)
                .subtract(strike
                        .multiply(BigDecimal.valueOf(Math.pow(Math.E, -rfr.doubleValue()*ytm.doubleValue())))
                        .multiply(cumulativeProbabilityD2));

        return callOptionPrice;
    }

    private BigDecimal putOptionPrice(BigDecimal price, Ticker ticker){
        BigDecimal ytm = ticker.getMaturity();
        BigDecimal stdDev = ticker.getStdDev();
        BigDecimal strike = ticker.getStrike();
        BigDecimal d1 = BigDecimal.valueOf(Math.log(price.doubleValue()/strike.doubleValue()))
                .add(rfr.add((stdDev.multiply(stdDev).divide(BigDecimal.valueOf(2)))).multiply(ytm))
                .divide(stdDev.multiply(BigDecimal.valueOf(Math.sqrt(ytm.doubleValue()))), 6, BigDecimal.ROUND_HALF_UP);

        BigDecimal d2 = d1.subtract(stdDev.multiply(BigDecimal.valueOf(Math.sqrt(ytm.doubleValue()))));

        NormalDistribution distribution = new NormalDistribution();
        BigDecimal cumulativeProbabilityNegativeD1 = BigDecimal.valueOf(distribution.cumulativeProbability(-d1.doubleValue()));
        BigDecimal cumulativeProbabilityNegativeD2 = BigDecimal.valueOf(distribution.cumulativeProbability(-d2.doubleValue()));

        BigDecimal putOptionPrice = strike
                .multiply(BigDecimal.valueOf(Math.pow(Math.E, -rfr.doubleValue()*ytm.doubleValue())))
                .multiply(cumulativeProbabilityNegativeD2)
                .subtract(strike.multiply(cumulativeProbabilityNegativeD1));

        return putOptionPrice;
    }
}
