package com.henry.portfolio.portfolio;

import com.henry.portfolio.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.concurrent.ArrayBlockingQueue;

public class PortfolioConsole {

    private Logger LOGGER = LoggerFactory.getLogger(PortfolioConsole.class);

    private final ArrayBlockingQueue<Tuple<String, MarketValue>> portfolioMarketValuesChannel;
    private final PortfolioStats portfolioStats;

    public PortfolioConsole(@Qualifier("portfolioMarketValuesChannel") ArrayBlockingQueue<Tuple<String, MarketValue>> portfolioMarketValuesChannel,
                            PortfolioStats portfolioStats) {

        this.portfolioMarketValuesChannel = portfolioMarketValuesChannel;
        this.portfolioStats = portfolioStats;
    }

    private String repeatString(String s, int n) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < n; i++) {
            result.append(s);
        }
        return result.toString();
    }

    public void start(){
        new Thread(() -> {
            try {
                while (true) {
                    Tuple<String/*Ticker*/, MarketValue> marketData = portfolioMarketValuesChannel.take();
                    StringBuilder prettyPrint = new StringBuilder();
                    portfolioStats.setMarketValue(marketData);

                    int maxSymbolWidth = 0;
                    int maxPriceWidth = 0;
                    int maxQtyWidth = 0;
                    int maxValueWidth = 0;

                    for (Tuple<String, MarketValue> t : portfolioStats.getMarketValues()) {
                        maxSymbolWidth = Math.max(maxSymbolWidth, t.getT1().length());
                        maxPriceWidth = Math.max(maxPriceWidth, t.getT2().getPrice().setScale(4, BigDecimal.ROUND_HALF_UP).toString().length());
                        maxQtyWidth = Math.max(maxQtyWidth, t.getT2().getQty().setScale(4, BigDecimal.ROUND_HALF_UP).toString().length());
                        maxValueWidth = Math.max(maxValueWidth, t.getT2().getNav().setScale(4, BigDecimal.ROUND_HALF_UP).toString().length());
                    }

                    for (Tuple<String, MarketValue> t : portfolioStats.getMarketValues()) {
                        prettyPrint.append(String.format("%-" + (maxSymbolWidth + 2) + "s %-" + (maxPriceWidth + 2) + "s %-" + (maxQtyWidth + 2) + "s %-" + (maxValueWidth + 2) + "s\n",
                                t.getT1(),
                                t.getT2().getPrice().setScale(4, BigDecimal.ROUND_HALF_UP),
                                t.getT2().getQty().setScale(4, BigDecimal.ROUND_HALF_UP),
                                t.getT2().getNav().setScale(4, BigDecimal.ROUND_HALF_UP)
                        ));
                    }
                    String headerRow = String.format(
                            "symbol" + repeatString(" ", maxSymbolWidth - 5) +
                                    "\tprice" + repeatString(" ", maxPriceWidth - 4) +
                                    "\tqty" + repeatString(" ", maxQtyWidth - 2) +
                                    "\tvalue\n"
                    );

                    LOGGER.info("\n====================== MARKET DATA UPDATE ======================\n" +
                            "\nPortfolio\n" +
                            headerRow +
                            prettyPrint.toString());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
