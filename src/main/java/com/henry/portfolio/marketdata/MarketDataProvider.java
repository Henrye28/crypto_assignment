package com.henry.portfolio.marketdata;

import com.google.common.collect.ImmutableMap;
import com.henry.portfolio.Tuple;
import com.henry.portfolio.ticker.Ticker;
import com.henry.portfolio.ticker.TickerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MarketDataProvider {
    /***
     * Sod Price: Ticker and start of day price of underlying stock
     */
    private static Map<String, BigDecimal> sodData = ImmutableMap.of(
            "0005.HK", BigDecimal.valueOf(40),
            "0700.HK", BigDecimal.valueOf(510),
            "INTC240426C00034000", BigDecimal.valueOf(500),
            "INTC240426C00037000", BigDecimal.valueOf(500),
            "INTC240426P00034000", BigDecimal.valueOf(500)
    );

    private Logger LOGGER = LoggerFactory.getLogger(MarketDataProvider.class);

    private ArrayBlockingQueue<Tuple<String, BigDecimal>> priceChannel;
    private TickerRepository tickerRepository;
    ExecutorService executorService;

    public MarketDataProvider(@Qualifier("priceChannel") ArrayBlockingQueue<Tuple<String, BigDecimal>> priceChannel,
                              TickerRepository tickerRepository) {
        this.priceChannel = priceChannel;
        this.tickerRepository = tickerRepository;
        this.executorService = Executors.newFixedThreadPool(5);
    }

    public void start() {
        List<Ticker> tickers = tickerRepository.findAll();
        LOGGER.info("Retrived tickers size: {}", tickers.size());
        tickers.forEach(t -> {
            executorService.submit(new MarketDataStream(sodData.get(t.getTicker()),t, priceChannel, new Timer()));
        });
    }

}
