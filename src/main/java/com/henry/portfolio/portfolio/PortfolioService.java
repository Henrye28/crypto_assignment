package com.henry.portfolio.portfolio;

import com.henry.portfolio.Tuple;
import com.henry.portfolio.position.Position;
import com.henry.portfolio.position.PositionService;
import com.henry.portfolio.ticker.Ticker;
import com.henry.portfolio.ticker.TickerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PortfolioService {

    private Logger LOGGER = LoggerFactory.getLogger(PortfolioService.class);

    private ArrayBlockingQueue<Tuple<String, BigDecimal>> priceChannel;
    private ArrayBlockingQueue<Tuple<String, MarketValue>> portfolioMarketValuesChannel;
    private PositionService positionService;
    private TickerRepository tickerRepository;
    private ExecutorService executorService;

    public PortfolioService(@Qualifier("priceChannel") ArrayBlockingQueue<Tuple<String, BigDecimal>> priceChannel,
                            @Qualifier("portfolioMarketValuesChannel") ArrayBlockingQueue<Tuple<String, MarketValue>> portfolioMarketValuesChannel,
                            PositionService positionService,
                            TickerRepository tickerRepository) {
        this.priceChannel = priceChannel;
        this.portfolioMarketValuesChannel = portfolioMarketValuesChannel;
        this.positionService = positionService;
        this.tickerRepository = tickerRepository;
        executorService = Executors.newFixedThreadPool(10);
    }

    public void start(){
        executorService.submit(() -> {
            try {
                while(true) {
                    Tuple<String, BigDecimal> tickerPrice = priceChannel.take();
                    LOGGER.info("Consuming price data for {} : {}", tickerPrice.getT2(), tickerPrice.getT1());
                    Position position = positionService.position(tickerPrice.getT1());
                    Optional<Ticker> maybeTicker = tickerRepository.findById(tickerPrice.getT1());
                    if(!maybeTicker.isPresent()){
                        throw new IllegalArgumentException(String.format("Ticker %s does not exist in db", tickerPrice.getT1()));
                    }
                    executorService.submit(new MarketValueStream(tickerPrice, position, maybeTicker.get(), portfolioMarketValuesChannel));
                }
            } catch (InterruptedException e) {
                LOGGER.error("Error consuming price with error {}", e);
                e.printStackTrace();
            }
        });

    }
}
