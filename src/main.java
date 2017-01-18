import javax.lang.model.util.ElementScanner6;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Created by vnc on 12/18/16.
 */
public class main {

    private static ArrayList<String> listProxy;
    public static int MAX_COUNT_ITERATION = 1999;
    public static int countIteration = 0;
    public static int countSuccess = 0;
    private static String textOfLog = "", logFilePath;
    public static long startTime;
    public static boolean USE_PROXY = false;

    static OS currentOS = OS.Linux;

//    public static int COUNT_ITERATION = 30;

    public static void main(String[] args)
    {
        getCurrentOS();
        logFilePath = createLogFile();

        // http://ценыконкурентов.рф/
//        String generalURL = "https://yandex.ru/search/?msid=1482176371.93869.22866.28043&text=%D1%86%D0%B5%D0%BD%D1%8B%D0%BA%D0%BE%D0%BD%D0%BA%D1%83%D1%80%D0%B5%D0%BD%D1%82%D0%BE%D0%B2.%D1%80%D1%84&lr=213";
        String generalURL = "https://yandex.ru/search/?lr=90&msid=1482652454.05074.22877.5047&text=%D1%86%D0%B5%D0%BD%D1%8B%20%D0%BA%D0%BE%D0%BD%D0%BA%D1%83%D1%80%D0%B5%D0%BD%D1%82%D0%BE%D0%B2%20%D1%80%D1%84&vkbd=1";
        startTime = System.currentTimeMillis();

//            new StartThread(generalURL, proxy, countIteration >= countTreads).start();
        countIteration = 0;
        while (countIteration < MAX_COUNT_ITERATION){
            StartTask newTask = new StartTask();
            if (USE_PROXY){
                for (String proxy:getRandomProxy(1).split(",")
                        ) {
                    countIteration++;
                    newTask.start(generalURL, proxy);
                }
            }else {
                countIteration++;
                newTask.start(generalURL, "");
            }

        }

        addToResultString("Finish work!");
    }

    private static String getRandomProxy(int countOfProxy) {

        String resultString = "", stringOfProxies;
        String pathOfProxyFile = "/home/vnc/Public/proxyList.txt";
        listProxy = readFromProxyFile(pathOfProxyFile);

//            if (!PROP_PROXY.equalsIgnoreCase("FOXTOOLS")) return "";
        if (listProxy.isEmpty()) {
            GetPost getHtmlData = new GetPost();
            try {
//                stringOfProxies = getHtmlData.sendGet("http://api.foxtools.ru/v2/Proxy.txt?cp=UTF-8&lang=&anonymity=All&type=None&available=Yes&free=Yes&limit=100&uptime=15&country=RU");
                stringOfProxies = getHtmlData.sendGet("http://proxymir.com/get2.txt?key=DmMINswuievU3XoDK9l3mDDKuM6RCFt&type=http&level=elite,anonymous&country=RU&servis=all&count=0");
            } catch (Exception e) {
                return "";
            }
            if (!stringOfProxies.isEmpty()) {
                for (String proxyAddress : stringOfProxies.split(";")
                        ) {
                    if (!proxyAddress.trim().isEmpty())listProxy.add(proxyAddress);
                }
                listProxy.remove(0);
            }
        }
        Random r = new Random();
        for (int i = 0; i < countOfProxy & i<listProxy.size(); i++) {
            if (i > listProxy.size()-1 | listProxy.size()==0) break;
            int rand = r.nextInt(listProxy.size()) - 1;
            if (rand<0) rand=0;
            resultString = resultString.concat(listProxy.get(rand)).concat(",");
            listProxy.remove(rand);
        }

        writeToProxyFile(listProxy, pathOfProxyFile);

        return resultString;
    }

    private static void writeToProxyFile(ArrayList<String> givenProxyList, String givenProxyFileName) {

        try {
            OutputStream f_ = new FileOutputStream(givenProxyFileName, false);
            OutputStreamWriter writer_ = new OutputStreamWriter(f_);
            BufferedWriter out_ = new BufferedWriter(writer_);
            out_.write(""); out_.flush(); out_.close();

            OutputStream f = new FileOutputStream(givenProxyFileName, true);
            OutputStreamWriter writer = new OutputStreamWriter(f);
            BufferedWriter out = new BufferedWriter(writer);
            if (givenProxyList.size()==0) {
                out.write(""); out.flush();
            }else {
                for (String proxyAddress : givenProxyList) {
                    out.write(proxyAddress.concat("\n"));
                    out.flush();
                }
            }
        } catch (IOException ex) {
            main.addToResultString(ex.getMessage());
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
            main.addToResultString(e.getMessage());
            e.printStackTrace();
        }
        finally {
            try { fstream.close(); } catch ( Exception ignore ) {}
        }
        return listOfProxy;
    }

    public static void addToResultString(String addedString) {

        Long currentMilliseconds = System.currentTimeMillis();
        Long elapsedTime = (currentMilliseconds - startTime) / 1000;
        String secondsElapse = String.format("%02d", elapsedTime % 60);
        String minutsElapse = String.format("%02d", elapsedTime / 60 % 60);
        String hoursElapse = String.format("%02d", elapsedTime / 3600 % 24);
        String daysElapse = String.format("%02d", elapsedTime / (3600 * 24));
        String timeForResult = daysElapse.concat(",").concat(hoursElapse).concat(":").concat(minutsElapse).concat(":").concat(secondsElapse);
        String stringToLog = timeForResult + " -> " + addedString + System.getProperty("line.separator");
        textOfLog = textOfLog.concat(stringToLog);

        writeToLogFile(textOfLog);

        System.out.println(addedString);
    }

    private static void getCurrentOS(){
        if (System.getProperty("os.name").startsWith("Windows")) {
            currentOS = OS.Windows;
        } else currentOS = OS.Linux;
    }

    private static String createLogFile(){

        String dateToName = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String fileName_Result = "Result_SeoTraffic_".concat(dateToName).concat(".txt");
        String fullPathOfLogs = getCurrentPath() + "logs".concat(currentOS == OS.Windows ? "\\" : "/").concat(fileName_Result);

        File logFile = new File(fullPathOfLogs);

        if(!logFile.exists()){
            File path = new File(logFile.getParent());
            if (!path.exists()) {
                if (path.mkdir()) System.out.println("Directory is creating :".concat(path.getParent()));
                else System.out.println("Error creating of directory: ".concat(path.toString()));
            }
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Can't create file.");
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }

        return fullPathOfLogs;

    }

    public static String getCurrentPath(){

        String pathStartingApp = "";
        byte prefixException = 0;
        byte suffixException = 1;

        try {
            if (currentOS == OS.Windows){
                pathStartingApp = new File(".").getAbsolutePath();
            }else {
                pathStartingApp = "/" + new File(".").getAbsolutePath();
                prefixException = 0;
                suffixException = 2;
            }
        }catch (Exception e){
            System.out.println("Error getting path.");
            e.printStackTrace();
        }

        pathStartingApp = pathStartingApp.substring(prefixException, pathStartingApp.length() - suffixException);

        if (currentOS != OS.Windows)pathStartingApp = pathStartingApp + "/";

//        System.out.println(" => " + pathStartingApp);

        return pathStartingApp;

    }

    private static void writeToLogFile(String fullTextOfLog){

        try {
            FileWriter out = new FileWriter(logFilePath, false);
            out.write(fullTextOfLog);
            out.close();
        } catch (Exception e) {
            System.out.println("Error write to logfile.");
            e.printStackTrace();
        }
    }

}
