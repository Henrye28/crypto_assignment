package integration;


import com.henry.portfolio.Application;
import com.henry.portfolio.Tuple;
import com.henry.portfolio.marketdata.MarketDataProvider;
import com.henry.portfolio.portfolio.MarketValue;
import com.henry.portfolio.portfolio.PortfolioService;
import org.awaitility.Duration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.concurrent.ArrayBlockingQueue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@ActiveProfiles("integration")
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class ApplicationIT {

    private Logger LOGGER = LoggerFactory.getLogger(ApplicationIT.class);

    @Autowired
    @Qualifier("portfolioMarketValuesChannel")
    ArrayBlockingQueue<Tuple<String, MarketValue>> marketValueChannel;

    @Autowired
    @Qualifier("priceChannel")
    ArrayBlockingQueue<Tuple<String, BigDecimal>> priceChannel;

    @Autowired
    MarketDataProvider marketDataProvider;

    @Autowired
    PortfolioService portfolioService;

    @Test
    public void should_publish_price(){

        try {
            marketDataProvider.start();

            await().pollDelay(Duration.FIVE_SECONDS)
                    .pollInterval(Duration.TEN_SECONDS)
                    .atMost(Duration.ONE_MINUTE)
                    .await("Waiting price data message..... Failing this means no message comes in within one min")
                    .until(() -> !priceChannel.isEmpty());

            assertThat(priceChannel.take().getT1()).isEqualTo("0005.HK");
        } catch (InterruptedException e) {
            LOGGER.error("No message received within one minute");
            e.printStackTrace();
        }
    }

    @Test
    public void should_publish_market_value(){
        try {
            portfolioService.start();

            await().pollDelay(Duration.FIVE_SECONDS)
                    .pollInterval(Duration.TEN_SECONDS)
                    .atMost(Duration.ONE_MINUTE)
                    .await("Waiting market value data message..... Failing this means no message comes in within one min")
                    .until(() -> !marketValueChannel.isEmpty());

            assertThat(marketValueChannel.take().getT1()).isEqualTo("0005.HK");
        } catch (InterruptedException e) {
            LOGGER.error("No message received within one minute");
            e.printStackTrace();
        }
    }


}
