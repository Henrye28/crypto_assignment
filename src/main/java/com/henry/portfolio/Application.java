package com.henry.portfolio;

import com.henry.portfolio.configuration.MarketDataConfiguration;
import com.henry.portfolio.configuration.PortfolioConfiguration;
import com.henry.portfolio.configuration.PositionConfiguration;
import com.henry.portfolio.marketdata.MarketDataProvider;
import com.henry.portfolio.portfolio.PortfolioConsole;
import com.henry.portfolio.portfolio.PortfolioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@Import({MarketDataConfiguration.class, PortfolioConfiguration.class, PositionConfiguration.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Profile("!integration")
    @Bean(name = "portfoliostart")
    public CommandLineRunner start(MarketDataProvider marketDataProvider,
                                  PortfolioService portfolioService,
                                  PortfolioConsole portfolioConsole) {
        return (args) -> {
            marketDataProvider.start();
            portfolioService.start();
            portfolioConsole.start();
        };
    }

}
