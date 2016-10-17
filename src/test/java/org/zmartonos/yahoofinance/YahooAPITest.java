package org.zmartonos.yahoofinance;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.SystemClock;
import org.javatuples.Pair;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import rx.Observable;
import rx.subjects.PublishSubject;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.fx.FxSymbols;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;


public class YahooAPITest {
    static {
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
    }

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int POLL_INTERVAL = 5;

    @Test
    public void test() throws Exception {
        LOGGER.info("starting...");
        //A
        Observable<BigDecimal> googleA = Observable
                .interval(0, POLL_INTERVAL, TimeUnit.SECONDS)
                .map(index -> YahooAPITest.getStockPrice("GOOG"))
                .distinctUntilChanged();

        //C
        final Observable<Pair<BigDecimal, BigDecimal>> o = Observable.zip(googleA, Observable
                .interval(0, POLL_INTERVAL, TimeUnit.SECONDS)
                .map(index -> YahooAPITest.getFx(FxSymbols.EURUSD))
                .distinctUntilChanged(),
                (d,t)-> Pair.with(d ,t));

        o.subscribe(pair ->
                System.out.println(String.format("GOOG: %s, EURUSD: %s",
                        pair.getValue0(),
                        pair.getValue1()))
        );

        Thread.sleep(20000);
    }

    @Test
    public void test2() throws InterruptedException {
        Observable
                .interval(0, 2, TimeUnit.SECONDS)
                .map(index -> YahooAPITest.getFx(FxSymbols.EURUSD))
                .distinctUntilChanged()
                .subscribe(System.out::println);

        Thread.sleep(20000);
    }

    public static BigDecimal getStockPrice(final String ticker){
        try {
            Stock stock = YahooFinance.get(ticker);
            return stock.getQuote(true).getPrice();
        } catch (final Exception e){
            return BigDecimal.ZERO;
        }
    }

    public static BigDecimal getFx(final String ticker){
        try {
            Stock stock = YahooFinance.get(ticker);
            return stock.getQuote(true).getPrice();
        } catch (final Exception e){
            return BigDecimal.ZERO;
        }
    }
}
