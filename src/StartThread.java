
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
    private static ArrayList<String> listProxy;
    private static boolean USE_GUI = true;

    @Override
    public void run()    //Этот метод будет выполнен в побочном потоке
    {
        listProxy = new ArrayList<String>();
        String url_1 = "https://yandex.ru/search/?msid=1482176371.93869.22866.28043&text=%D1%86%D0%B5%D0%BD%D1%8B%D0%BA%D0%BE%D0%BD%D0%BA%D1%83%D1%80%D0%B5%D0%BD%D1%82%D0%BE%D0%B2.%D1%80%D1%84&lr=213";
//            startingWebDriver("xn--b1afataqbcfet0aj9a7e.xn--p1ai");
        startingWebDriver(url_1);
        List<WebElement> listLinks;
        listLinks = driver.findElements(By.cssSelector("a.link.organic__url.link.link_cropped_no"));
        if (listLinks.size() > 0) listLinks.get(0).click();
        driver.close();
        driver.quit();
        System.out.println("Привет из побочного потока!");

    }

    // Start new WebDriver.
    private static void startingWebDriver(String givenURL) {

        String proxyString = getRandomProxy();

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

    private static String getRandomProxy() {

        String resultString, stringOfProxies;
        String pathOfProxyFile = "/home/vnc/Public/proxyList.txt";
        listProxy = readFromProxyFile(pathOfProxyFile);

//            if (!PROP_PROXY.equalsIgnoreCase("FOXTOOLS")) return "";
        if (listProxy.isEmpty()) {
            GetPost getHtmlData = new GetPost();
            try {
                stringOfProxies = getHtmlData.sendGet("http://api.foxtools.ru/v2/Proxy.txt?cp=UTF-8&lang=RU&type=HTTPS&available=Yes&free=Yes&limit=10&uptime=2&country=RU");
            } catch (Exception e) {
                return "";
            }
            if (!stringOfProxies.isEmpty()) {
                for (String proxyAddress : stringOfProxies.split(";")
                        ) {
                    listProxy.add(proxyAddress);
                }
                listProxy.remove(0);
            }
        }
        Random r = new Random();
        int i = r.nextInt(listProxy.size() - 1);
        resultString = listProxy.get(i);
        listProxy.remove(i);

        writeToProxyFile(listProxy, pathOfProxyFile);

        return resultString;
    }

    private static void writeToProxyFile(ArrayList<String> givenProxyList, String givenProxyFileName) {

        try {
            OutputStream f = new FileOutputStream(givenProxyFileName, true);
            OutputStreamWriter writer = new OutputStreamWriter(f);
            BufferedWriter out = new BufferedWriter(writer);
            for (String proxyAddress : givenProxyList) {
                out.write(proxyAddress);
                out.flush();
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    private static ArrayList<String> readFromProxyFile(String givenProxyFileName) {

        ArrayList<String> listOfProxy = new ArrayList<String>();

        File f = new File(givenProxyFileName);
        if(!f.exists()) return listOfProxy;

        FileInputStream fstream = null;

        try
        {
            fstream = new FileInputStream(givenProxyFileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine = "";
            while ((strLine = br.readLine()) != null)   {
                listOfProxy.add(strLine);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try { fstream.close(); } catch ( Exception ignore ) {}
        }
        return listOfProxy;
    }
}









