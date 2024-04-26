# Real Time Portfolio Market Value Console  
<h3> Extra libs </h3>

- >commons-math3-3.6.1.jar : Apache commons maths for calculating the cumulative probability
- >awaitility-3.0.0.jar : Awaitility jar for testing message publish to queue with random interval


<h3> Design with message channel: BlockingQueue </h3>

Project can be divided into three components:
>MarketDataProvider --queue--> PortfolioService --queue--> PortfolioConsole

With features as below:
- All numbers are calculated based on BigDecimal for better precision
- Multi-threading on price calculation to reduce market value publishing delay
- Message queue to separate dependencies on each services
    - BlockingQueue as message queue for the sake of: 
    - Limit queue size in case of OOM
    - Remove concurrency complexity
- Components are connected by message queue
    - MarketDataProvider publishes real-time prices for tickers with random intervals using thread pool
    - PortfolioService consumes pricing message from MarketDataProvider, distributes calculation&publish tasks to a thread pool
    - PortfolioConsole consumes market value message from PortfolioService and print


Further enhancement:
- Message queue should be non-blocking and asynchronous, for the case of large data volume. Can be improved with below options:
    - Using external messaging system
    - Creating a datastructure with multiple number of queues inside, message will be assigned to particular queue by header hash, concurrency number can be increased with this solution.
    - For this particular case, queue should be conflated
- For saving time, stocks and options are stored in same security static data table
- For saving time, only integration test is added


<h3> User guide </h3>

- First gradle build the project under the root folder
- cd to ./portfolio/build/libs
- Start service by:
>java -jar portfolio-CRYPTO-SNAPSHOT.jar







 