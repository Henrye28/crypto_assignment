package com.henry.portfolio.configuration;

import com.henry.portfolio.Tuple;
import com.henry.portfolio.marketdata.MarketDataProvider;
import com.henry.portfolio.ticker.TickerRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.concurrent.ArrayBlockingQueue;

@Configuration
public class MarketDataConfiguration {

    @Bean
    public MarketDataProvider marketDataProvider(@Qualifier("priceChannel") ArrayBlockingQueue<Tuple<String, BigDecimal>> blockingQueue,
                                                 TickerRepository tickerRepository){
        return new MarketDataProvider(blockingQueue, tickerRepository);
    }

    @Bean(name = "priceChannel")
    public ArrayBlockingQueue<Tuple<String, BigDecimal>> priceChannel(){
        return new ArrayBlockingQueue<>(20);
    }

}
