package com.henry.portfolio.configuration;

import com.henry.portfolio.Tuple;
import com.henry.portfolio.portfolio.MarketValue;
import com.henry.portfolio.portfolio.PortfolioConsole;
import com.henry.portfolio.portfolio.PortfolioService;
import com.henry.portfolio.portfolio.PortfolioStats;
import com.henry.portfolio.position.PositionService;
import com.henry.portfolio.ticker.TickerRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

@Configuration
public class PortfolioConfiguration {

    @Bean(name = "portfolioMarketValuesChannel")
    public ArrayBlockingQueue<Tuple<String, MarketValue>> portfolioMarketValuesChannel() {return new ArrayBlockingQueue<>(20); }

    /**
     * Instantiate portfolio stats with initial data
     */
    @Bean
    public PortfolioStats portfolioStats(){
       return new PortfolioStats(new ArrayList<Tuple<String, MarketValue>>(){{
           add(Tuple.of("0005.HK", new MarketValue("0005.HK", BigDecimal.valueOf(40.2), BigDecimal.valueOf(100), BigDecimal.valueOf(25000))));
           add(Tuple.of("0700.HK", new MarketValue("0700.HK", BigDecimal.valueOf(200.2), BigDecimal.valueOf(100), BigDecimal.valueOf(153000))));
           add(Tuple.of("INTC240426C00034000", new MarketValue("INTC240426C00034000", BigDecimal.valueOf(1.3), BigDecimal.valueOf(100), BigDecimal.valueOf(25000))));
           add(Tuple.of("INTC240426C00037000", new MarketValue("INTC240426C00037000", BigDecimal.valueOf(2.4), BigDecimal.valueOf(100), BigDecimal.valueOf(25000))));
           add(Tuple.of("INTC240426P00034000", new MarketValue("INTC240426P00034000", BigDecimal.valueOf(6.2), BigDecimal.valueOf(100), BigDecimal.valueOf(25000))));
       }});
    }

    @Bean
    public PortfolioService portfolioService(@Qualifier("priceChannel") ArrayBlockingQueue<Tuple<String, BigDecimal>> priceChannel,
                                             @Qualifier("portfolioMarketValuesChannel") ArrayBlockingQueue<Tuple<String, MarketValue>> portfolioMarketValuesChannel,
                                             PositionService positionService,
                                             TickerRepository repository){
        return new PortfolioService(priceChannel, portfolioMarketValuesChannel, positionService, repository);
    }

    @Bean
    public PortfolioConsole portfolioConsole(@Qualifier("portfolioMarketValuesChannel") ArrayBlockingQueue<Tuple<String, MarketValue>> portfolioMarketValuesChannel,
                                          PortfolioStats portfolioStats){
        return new PortfolioConsole(portfolioMarketValuesChannel, portfolioStats);
    }

}
