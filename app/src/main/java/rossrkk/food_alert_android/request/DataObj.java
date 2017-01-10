package rossrkk.food_alert_android.request;

/**
 * Created by rossrkk on 09/01/17.
 */

public class DataObj {
    private int[] data;
    private String name;
    private boolean reconfirm;

    public DataObj(int[] data, String name, boolean reconfirm) {
        this.data = data;
        this.name = name;
        this.reconfirm = reconfirm;
    }

    public int[] getData() {
        return data;
    }

    public String getName() {
        return name;
    }

    public boolean getReconfirm() {
        return reconfirm;
    }
}
