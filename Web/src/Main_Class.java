import jdk.jfr.internal.tool.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class Main_Class {

    public static final String  BEFORE_URL = "a href=";

    private static int     MAXT = 0;
    public  static Pool urlPool;

    private static int activeT = 0;




    private static final Main m = new Main();
    public static void main(String[] args) {

        //https://www.nytimes.com/
        //https://slashdot.org/

        // активируем прогу с помощью ввода пользователя
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try { // Ввод и проверка MAXDepth
                if (urlPool == null) {
                    System.out.println("Введите максимальную глубину поиска:");
                    String writingPoolDepth = scanner.nextLine();
                    int i = Integer.parseInt(writingPoolDepth);
                    if (i <= 0) {
                        System.out.print("Ошибка ввода! ");
                        continue;
                    }
                    urlPool = new Pool(i);
                }
            }
            catch (NumberFormatException numberFormatException) {
                System.out.print("Ошибка ввода! ");
                continue;
            }
            try { // Ввод и проверка MAXThreads
                if (MAXT == 0) {
                    System.out.println("Введите количество потоков");
                    String writingMAXThreads = scanner.nextLine();
                    int i = Integer.parseInt(writingMAXThreads);
                    if (i <= 0){
                        System.out.print("Ошибка ввода! ");
                        continue;
                    }
                    MAXT = i;
                }
            }
            catch (NumberFormatException numberFormatException) {
                System.out.print("Ошибка ввода! ");
                continue;
            }
            try { // Ввод и проверка url
                System.out.println("Введите сайт для начала поиска");
                String writingURL = scanner.nextLine();
                (new java.net.URL(writingURL)).openStream().close();
                urlPool.addToUnchecked(new Pair(writingURL, 0));
                break;
            } catch (Exception ex) {
                System.out.print("Ошибка ввода! ");
            }
        }





        //Нормальный способ активировать программу
//        urlPool = new URLPool(3);
//        MAXThreads = 8;
//        urlPool.addToUnchecked(new URLDepthPair("https://slashdot.org/", 0));


        while (!urlPool.uncheckedIsEmpty() || activeT != 0) { // работает пока не закончатся непроверенные ссылки и все потоки
            try {
                synchronized (m) {// чтобы была возможность усыпить main поток
                    while (activeT >= MAXT) { // спим пока потоков >= максимально установленных потоков
                        System.out.println("waiting... threads working = " + activeT); //[ДЛЯ НАГЛЯДНОСТИ]
                        m.wait(1000);
                    }

                    if (!urlPool.uncheckedIsEmpty()) {//лист непроверенных url не пуст -> начинаем новый поток
                        startThread(urlPool.getUncheckedURL(0));
                    } else m.wait(1000);//иначе спим
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        System.out.println("UNCHECKED");
        urlPool.printAllUnchecked();
        
        System.out.println("CHECKED");
        urlPool.printAllChecked();


    }

    public static void startThread (Pair urlDepthPair){
        System.out.println(urlDepthPair.getStringFormat()); //[ДЛЯ НАГЛЯДНОСТИ]
        Main_Class.activeThreadsInc();//увеличиваем колво потоков
        Main_Class.urlPool.addToChecked(urlDepthPair);//добавляем в проверенные
        new Thread(new Runner(urlDepthPair), "thread №" + activeT).start();//новый поток с новым объектом
    }

    public static void crawlThroughURL (Pair urlDepthPair){
        try {
            URLConnection urlConnection;

            // устанавливаем соединение
            urlConnection = new URL(urlDepthPair.getURLAddress()).openConnection();//соединяемся с сайтом
            urlConnection.setConnectTimeout(10_1000);//если нет соединения в течении 10 секунд

            // создаём Reader для чтения
            BufferedReader in = null;
            InputStreamReader inputStreamReader = null;
            try {
                inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
                in = new BufferedReader(inputStreamReader);
            } catch (ConnectException connectException) {
                //connectException.printStackTrace();
            }

            // читаем сайт
            String s;
            if (in != null) {
                while ((s = in.readLine()) != null) {

                    //System.out.println(s); //[ДЛЯ НАГЛЯДНОСТИ]

                    if (s.contains(Main_Class.BEFORE_URL + "\"" + Pair.PREFIX) && urlDepthPair.getDepth() < urlPool.MAXDepth) { // содержит a href="http://
                        try {
                            String url = s.substring(s.indexOf(Main_Class.BEFORE_URL + "\"" + Pair.PREFIX) + Main_Class.BEFORE_URL.length() + 1); // обрезаем url адресс от лишнего слева
                            url = url.substring(0, url.indexOf("\"")); //обрезаем url адресс от лишнего справа


                            // url = url.replace(URLDepthPair.URL_PREFIX, URLDepthPair.URL_PREFIX_S); // костыль, потому что некоторые ссылки http работают только, если они https


                            Pair foundURL = new Pair(url, urlDepthPair.getDepth() + 1);
                            if (!urlPool.poolContains(foundURL) ) {
                                urlPool.addToUnchecked(foundURL);
                            }
                        } catch (StringIndexOutOfBoundsException e){
                            //e.printStackTrace();
                        }
                    }
                    if (s.contains(Main_Class.BEFORE_URL + "\"" + Pair.PREFIX_S) && urlDepthPair.getDepth() < urlPool.MAXDepth) {
                        try {

                            String url = s.substring(s.indexOf(Main_Class.BEFORE_URL + "\"" + Pair.PREFIX_S) + Main_Class.BEFORE_URL.length() + 1); // обрезаем url адресс от лишнего слева
                            url = url.substring(0, url.indexOf("\"")); //обрезаем url адресс от лишнего справа

                            // url = url.replace(URLDepthPair.URL_PREFIX, URLDepthPair.URL_PREFIX_S); // костыль, потому что некоторые ссылки http работают только, если они https


                            Pair foundURL = new Pair(url, urlDepthPair.getDepth() + 1);
                            if (!urlPool.poolContains(foundURL)) {
                                urlPool.addToUnchecked(foundURL);
                            }
                        } catch (StringIndexOutOfBoundsException e){
                            //e.printStackTrace();
                        }
                    }

                    if (s.contains("301 Moved Permanently") && !urlDepthPair.getURLAddress().contains(Pair.PREFIX_S)){
                        crawlThroughURL(new Pair(urlDepthPair.getURLAddress().replace(Pair.PREFIX, Pair.PREFIX_S), urlDepthPair.getDepth()));
                        break;
                    }

                }

                in.close();
                inputStreamReader.close();
                urlConnection.getInputStream().close();

            }
        } catch (IOException ioException) {
            //ioException.printStackTrace();
        }


    }


    public static synchronized void activeThreadsInc (){
        activeT++;
    }
    public static synchronized void activeThreadsDec (){
        activeT--;
    }







}
