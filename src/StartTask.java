import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.javascript.background.JavaScriptExecutor;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static sun.net.www.protocol.http.HttpURLConnection.userAgent;

/**
 * Created by vnc on 12/18/16.extends Thread
 */
public class StartTask {
    private static HtmlUnitDriver driver_noGUI;
    private static WebDriver driver;
    private static boolean USE_GUI = true;
    private String givenURL, givenProxy;
    private Boolean itsLastTread;

    StartTask(){
//        this.givenURL = givenURL;
//        this.givenProxy = givenProxy;
//        this.itsLastTread = itsLastTread;

    }

//    @Override
    public void start(String givenURL, String givenProxy)    //Этот метод будет выполнен в побочном потоке
    {
        startingWebDriver(givenURL, givenProxy);
        if(driver==null) return;
        driver.manage().window().maximize();
        List<WebElement> listLinks;
        Random r = new Random();
        try {
            listLinks = driver.findElements(By.cssSelector("a.link.organic__url.link.link_cropped_no"));
            if (listLinks.size() > 0) {
                listLinks.get(0).click();
                driver.switchTo().window(String.valueOf(driver.getWindowHandles().toArray()[1]));
                Thread.sleep(2000);
                try {
                    (new WebDriverWait(driver, 310, 2))
                            .until(ExpectedConditions.visibilityOfElementLocated(By.id("fh5co-clients")));
                    int delay1 = r.nextInt(15000);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", driver.findElement(By.id("fh5co-clients")));
                    Thread.sleep(delay1);
                    int delay2 = r.nextInt(15000);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", driver.findElement(By.id("fh5co-features-3")));
                    Thread.sleep(delay2);
                    int delay3 = r.nextInt(15000);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", driver.findElement(By.id("fh5co-features-2")));
                    Thread.sleep(delay3);
                    int delay4 = r.nextInt(15000);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", driver.findElement(By.id("fh5co-features")));
                    Thread.sleep(delay4);
                    int delay5 = r.nextInt(15000);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", driver.findElement(By.id("fh5co-pricing")));
                    Thread.sleep(delay5);
                    int delay6 = r.nextInt(15000);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", driver.findElement(By.id("fh5co-faqs")));
                    Thread.sleep(delay6);
                    int delay7 = 0;
                    for (WebElement we : driver.findElements(By.cssSelector("div.faq-accordion.to-animate.fadeInUp.animated h3"))
                            ) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", we);
//                    we.click();
                        int d = r.nextInt(10000);
                        delay7 = delay7+d;
                        Thread.sleep(d);
                    }
                    main.addToResultString("Elements for scrolling reading for ".concat(String.valueOf((delay1+delay2+delay3+delay4+delay5+delay6+delay7)/1000)).concat("+ seconds."));
                } catch (Exception e) {
                    main.addToResultString("Elements for scrolling NOT found.");
                }
                ++main.countSuccess;
            }
        } catch (Exception e) {
            main.addToResultString(e.getMessage());
            e.printStackTrace();
        }
        main.addToResultString(getElaspedTime().concat(" -> Iteration: ").concat(String.valueOf(main.countIteration)).concat(", Success: ").concat(String.valueOf(main.countSuccess)).concat(", Proxy: ").concat(String.valueOf(givenProxy)));
        try {
            driver.close();
            driver.quit();
        } catch(Exception e) {main.addToResultString("Driver not found! (".concat(getElaspedTime()).concat(")"));};
//        System.out.println("Привет из побочного потока!");
    }

    // Start new WebDriver.
    private static void startingWebDriver(String givenURL, String proxyString) {

        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/msword,application/csv,text/csv,image/png ,image/jpeg");
        profile.setPreference("browser.download.manager.showWhenStarting", false);
        profile.setPreference("browser.download.manager.focusWhenStarting", false);
        //profile.setPreference("browser.download.useDownloadDir",true);
        profile.setPreference("browser.helperApps.alwaysAsk.force", false);
        profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
        profile.setPreference("browser.download.manager.closeWhenDone", false);
        profile.setPreference("browser.download.manager.showAlertOnComplete", false);
        profile.setPreference("browser.download.manager.useWindow", false);
        profile.setPreference("browser.download.manager.showWhenStarting", false);
        if (!proxyString.isEmpty() | proxyString.split(":").length>2) {
            String[] proxy = proxyString.split(":");
            profile.setPreference("network.proxy.type", 1);
            profile.setPreference("network.proxy.http", proxy[0]);
            profile.setPreference("network.proxy.http_port", Integer.valueOf(proxy[1]));
            profile.setPreference("network.proxy.ssl", proxy[0]);
            profile.setPreference("network.proxy.ssl_port", Integer.valueOf(proxy[1]));
        }
        profile.setPreference("services.sync.prefs.sync.browser.download.manager.showWhenStarting", false);
        profile.setPreference("pdfjs.disabled", true);

//        userAgent = "Mozilla/5.0 (Linux; U; Android 2.3.3; en-us; sdk Build/GRI34) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";

        try {

            if (USE_GUI) {
                if (driver_noGUI != null) driver_noGUI.quit();
//                    addToResultString("Trying start new FirefoxDriver", addTo.LogFileAndConsole);
                driver = new FirefoxDriver(profile);
//                driver = new ChromeDriver(profile);
                driver.manage().timeouts().implicitlyWait(5000, TimeUnit.MILLISECONDS);
                driver.manage().timeouts().pageLoadTimeout(185000, TimeUnit.MILLISECONDS);
                try {
                    if (!givenURL.isEmpty()) driver.get(givenURL);
                } catch(org.openqa.selenium.TimeoutException te){
                    main.addToResultString("!! Timeout 185 sec.");
                    ((JavascriptExecutor)driver).executeScript("window.stop();");
                }
            } else {
                if (driver != null) driver.quit();
//                    addToResultString("Trying start new WebDriver(HtmlUnit)", addTo.LogFileAndConsole);
                driver_noGUI = new HtmlUnitDriver(BrowserVersion.FIREFOX_38);
                driver_noGUI.getBrowserVersion().setUserAgent(userAgent);
                driver_noGUI.manage().timeouts().implicitlyWait(5000, TimeUnit.MILLISECONDS);
//                driver_noGUI.setJavascriptEnabled(true);
                if (!givenURL.isEmpty()) driver_noGUI.get(givenURL);
            }

        } catch (Exception e) {
            if(USE_GUI) driver = null;
            else driver_noGUI = null;
            main.addToResultString(e.getMessage());
//            e.printStackTrace();
            System.out.println(e.getMessage());
//                addToResultString(e.toString(), addTo.LogFileAndConsole);
//            return;
        }
    }

    private static String getElaspedTime(){

        Long currentMilliseconds = System.currentTimeMillis();
        Long elapsedTime = (currentMilliseconds - main.startTime) / 1000;
        String secondsElapse = String.format("%02d", elapsedTime % 60);
        String minutsElapse = String.format("%02d", elapsedTime / 60 % 60);
        String hoursElapse = String.format("%02d", elapsedTime / 3600 % 24);
        String daysElapse = String.format("%02d", elapsedTime / (3600 * 24));
        return daysElapse.concat(",").concat(hoursElapse).concat(":").concat(minutsElapse).concat(":").concat(secondsElapse);
    }



}









