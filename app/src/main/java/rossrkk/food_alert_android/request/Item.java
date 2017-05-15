package rossrkk.food_alert_android.request;

/**
 * Created by rossrkk on 15/05/17.
 */

public class Item {
    private String name;
    private String description;
    private String category;
    private double price;

    private int[] data;

    public Item(String name, String description, String category, double price, int[] data) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int[] getData() {
        return data;
    }
}
