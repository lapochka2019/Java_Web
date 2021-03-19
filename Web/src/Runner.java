
public class Runner implements Runnable{//

    private Pair urlDepthPair;

    public Runner(Pair urlDepthPair){
        this.urlDepthPair = urlDepthPair;
    }

    // [Runnable] interface
    @Override
    public void run() {
        Main_Class.crawlThroughURL(urlDepthPair); // [ИДЕЯ] засунуть этот метод в try catch чтобы потоки никогда не обрывались от ошибок этого метода
        Main_Class.activeThreadsDec();
    }

}
