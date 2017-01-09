package rossrkk.food_alert_android.request;

/**
 * Created by rossrkk on 09/01/17.
 */

public class DataObj {
    private int[] data;
    private String name;

    public DataObj(int[] data, String name) {
        this.data = data;
        this.name = name;
    }

    public int[] getData() {
        return data;
    }

    public String getName() {
        return name;
    }
}
