
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.htmlunit.HtmlUnitWebElement;

import static sun.net.www.protocol.http.HttpURLConnection.userAgent;


/**
 * Created by vnc on 12/18/16.
 */
public class StartThread extends Thread {
    private static HtmlUnitDriver driver_noGUI;
    private static WebDriver driver;
    private static boolean USE_GUI = true;
    private String givenURL, givenProxy;
    private Boolean itsLastTread;

    StartThread(String givenURL, String givenProxy, Boolean itsLastTread){
        this.givenURL = givenURL;
        this.givenProxy = givenProxy;
        this.itsLastTread = itsLastTread;
    }

    @Override
    public void run()    //Этот метод будет выполнен в побочном потоке
    {
        startingWebDriver(givenURL, givenProxy);
        List<WebElement> listLinks;
        try {
            listLinks = driver.findElements(By.cssSelector("a.link.organic__url.link.link_cropped_no"));
            if (listLinks.size() > 0) listLinks.get(0).click();
        } catch (Exception e) {/**/}
//        driver.manage().window().maximize();
        main.addToResultString(driver.getWindowHandle());
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        driver.close();
//        if (itsLastTread)
            driver.close(); driver.quit();
        main.addToResultString("Привет из побочного потока!");

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
                driver.manage().timeouts().implicitlyWait(5000, TimeUnit.MILLISECONDS);
                if (!givenURL.isEmpty()) driver.get(givenURL);
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
            e.printStackTrace();
//                addToResultString(e.toString(), addTo.LogFileAndConsole);
//            return;
        }
    }


}









