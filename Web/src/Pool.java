import java.util.LinkedList;


 //Потокобезопасный класс для работы с двумя LinkedList (checkedURL и uncheckedURL) класса URLDepthPair
 //до определённой глубины
 // Note: реализованы только методы, необходимые для 8-ой лабораторной

public class Pool {
        //листы проверено, непроверено
    private LinkedList<Pair> checkedURL;
    private LinkedList<Pair> uncheckedURL;
    public int MAXDepth = 0;//константа гнлубины поиска


     // Инициализирует две пустых коллекции.
    // @param i Максимальная глубина поиска
     //
    public Pool(int i){//конструктор
        checkedURL = new LinkedList<>();
        uncheckedURL = new LinkedList<>();
        MAXDepth = i;
    }

    public synchronized void addToUnchecked (Pair urlDepthPair){//synchronized-синхронизируем объекты, если рабтает поток, то другие не работаеют
        if (urlDepthPair.getDepth() >= MAXDepth) checkedURL.add(urlDepthPair);//если глубина достигнута, то чек
        else uncheckedURL.add(urlDepthPair);
    }


     //Добавляет url в коллекцию проверенных url и убирает из коллекции непроверенных url
     // @param urlDepthPair

    public synchronized void addToChecked (Pair urlDepthPair){
        checkedURL.add(urlDepthPair);//в чек
        uncheckedURL.remove(urlDepthPair);//удаляем из не чек
    }

    public synchronized boolean uncheckedIsEmpty (){
        return uncheckedURL.isEmpty();
    }//пустой ли не проверенный

    public synchronized Pair getUncheckedURL (int i){//возвращает непроверенную ссылку на позиции И (синхронизированный вариант метода гет класса линкд лист)
        return uncheckedURL.get(i);
    }
    public synchronized void removeUncheckedURL (Pair urlDepthPair){
        uncheckedURL.remove(urlDepthPair);
    }//синхронизированный метод ремув клласса линкд лист




    public synchronized boolean poolContains (Pair urlDepthPair) {
//проверяет содержится ли урл в обоих листах
        if (checkedURL.isEmpty() && uncheckedURL.isEmpty()) return false;

        for (Pair ur: checkedURL) {
            if (ur.getURLAddress().equals(urlDepthPair.getURLAddress())) return true;
        }
        for (Pair ur: uncheckedURL) {
            if (ur.getURLAddress().equals(urlDepthPair.getURLAddress())) return true;
        }
        return false;
    }

    public synchronized void printAllUnchecked (){//выводим все непроверенные на консоль
        if (uncheckedURL.isEmpty()) System.out.println("Empty");
        for (Pair urlDepthPair : uncheckedURL) {
            System.out.println(urlDepthPair.getStringFormat());
        }
    }
    public synchronized void printAllChecked (){//выводим все проверенные на консоль
        if (checkedURL.isEmpty()) System.out.println("Empty");
        for (Pair urlDepthPair : checkedURL) {
            System.out.println(urlDepthPair.getStringFormat());
        }
    }
}
