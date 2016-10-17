package org.zmartonos.selenium;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;
import org.zmartonos.reactivex.Obeservable;
import rx.Observable;
import rx.functions.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by zmartonos on 22.09.16.
 */
public class FinanzenTest {
    static{
        System.setProperty("webdriver.gecko.driver", "/home/zmartonos/Downloads/geckodriver");
    }

    private static final Logger LOGGER = LogManager.getLogger();
    private WebDriver driver;

    private static final String URL_BASF = "http://www.finanzen.net/realtimekurs/BASF";
    private static final String URL_DAX = "http://www.finanzen.net/";
    private static final By XPATH = By.xpath(".//th[@class='realtime']//div[@source='lightstreamer']");
    private static final By XPATH_DAX = By.xpath(".//div[@id='fragHomepageIndexTopFlop']/table[@class='jsReloadPushData']/tbody/tr[3]/td[2]/div[@source='lightstreamer']");

    @Test
    public void test() throws InterruptedException {
        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        capabilities.setCapability("marionette", true);
        driver = new FirefoxDriver(capabilities);

        driver.get(URL_DAX);

        Observable<Float> priceObservable = Observable
                .interval(0, 1, TimeUnit.SECONDS)
                .map(index ->getStockPrice())
                .distinctUntilChanged();

        Observable<Float> changeObservable = Observable
                .zip(priceObservable, priceObservable.skip(1), new Func2<Float, Float, Float>() {
            @Override
            public Float call(final Float price, final Float skippedPrice) {
                //return String.format("price: %.2f skippedPrice: %.2f, change: %.2f", price, skippedPrice, skippedPrice - price);
                return skippedPrice - price;
            }
        });

        Observable<Float> sequenceObservable = changeObservable.scan(0f, new Func2<Float, Float, Float>() {
            @Override
            public Float call(Float aFloat, Float bFloat) {
                return aFloat+bFloat;
            }
        })
                .asObservable();

        Observable.zip(priceObservable, changeObservable, sequenceObservable, new Func3<Float, Float, Float, String>() {
            @Override
            public String call(Float price, Float change, Float sequence) {
                return String.format("price: %.2f change: %.2f, sequence: %.2f", price, change, sequence);
            }
        }).subscribe(LOGGER::info);

        while(true){
        }
        //LOGGER.info("finished");
    }

    private Float getStockPrice(){
        return Float.parseFloat(driver.findElement(XPATH_DAX).getText().replaceAll("\\.", "").replaceAll(",","."));
    }

    @AfterClass
    public void tearDown(){
        driver.quit();
    }
}
