/**
 * Хранит URL адресс и его глубину
 */
public class Pair {

    public static final String PREFIX = "http://";
    public static final String PREFIX_S = "https://";

    private String URLAddress;
    private int depth;

    Pair(String URLAddress, int depth){
        this.URLAddress = URLAddress;
        this.depth = depth;
    }


    public String getStringFormat (){
        return "URL = " + URLAddress + ", depth = " + depth;
    }


    public void setURLAddress(String URLAddress) {
        this.URLAddress = URLAddress;
    }
    public void setDepth(int depth) {
        this.depth = depth;
    }
    public int getDepth() {
        return depth;
    }
    public String getURLAddress() {
        return URLAddress;
    }
}
