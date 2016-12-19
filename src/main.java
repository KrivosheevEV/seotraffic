/**
 * Created by vnc on 12/18/16.
 */
public class main {
    static StartThread mSecondThread;

    public static void main(String[] args)
    {
//        mSecondThread = new StartThread();	//Создание потока
        new StartThread().start();	//Создание потока
        new StartThread().start();	//Создание потока
        new StartThread().start();	//Создание потока
//        new StartThread().start();	//Создание потока

        System.out.println("Главный поток завершён...");
    }


}
