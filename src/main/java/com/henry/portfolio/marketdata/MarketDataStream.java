package com.henry.portfolio.marketdata;

import com.henry.portfolio.Tuple;
import com.henry.portfolio.ticker.Ticker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class MarketDataStream extends TimerTask {

    private Logger LOGGER = LoggerFactory.getLogger(MarketDataStream.class);

    private final Ticker ticker;
    private final Timer timer;
    private ArrayBlockingQueue<Tuple<String, BigDecimal>> arrayBlockingQueue;
    private BigDecimal currentPrice;

    public MarketDataStream(BigDecimal currentPrice, Ticker ticker, ArrayBlockingQueue<Tuple<String, BigDecimal>> arrayBlockingQueue, Timer timer) {
        this.currentPrice = currentPrice;
        this.ticker = ticker;
        this.arrayBlockingQueue = arrayBlockingQueue;
        this.timer = timer;
    }

    private BigDecimal geometric_brownian_motion(BigDecimal currentPrice, Ticker ticker, BigDecimal milSecs){
        if(currentPrice.compareTo(BigDecimal.ZERO) == -1){
            return BigDecimal.ZERO;
        }
        BigDecimal secondsDivConst = milSecs.divide(BigDecimal.valueOf(7257600), 6, BigDecimal.ROUND_HALF_UP);
        BigDecimal deltaPrice = ticker.getExpReturn()
                .multiply(secondsDivConst)
                .add(ticker.getStdDev()
                        .multiply(BigDecimal.valueOf(Math.sqrt(secondsDivConst.doubleValue())))
                        .multiply(BigDecimal.valueOf(new Random().nextGaussian())))
                .multiply(currentPrice);
        return currentPrice.add(deltaPrice).setScale(6, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Market Value provider here only provides raw stock price data
     *
     * To mock the real market data streaming:
     * - Each ticker has a thread to publish its price to message queue with random interval.
     * - Each ticker's interval keeps changing
     *
     */
    @Override
    public void run() {
        try {
            double delay = (Math.random() * 1.5 + 0.5)*1000;
            currentPrice = geometric_brownian_motion(currentPrice, ticker, BigDecimal.valueOf(delay));
            arrayBlockingQueue.put(Tuple.of(ticker.getTicker(), currentPrice));
            timer.schedule(new MarketDataStream(currentPrice, ticker, arrayBlockingQueue, timer), (long) delay);
            LOGGER.info("Thread: {}, Price ticking for {} : {}",Thread.currentThread().getId(), ticker.getTicker(), currentPrice);
        } catch (InterruptedException e) {
            LOGGER.error("Error streaming price {} : {}, with error {}", currentPrice, ticker.getTicker(), e);
            e.printStackTrace();
        }
    }

}
