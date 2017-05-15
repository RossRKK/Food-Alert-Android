package rossrkk.food_alert_android.request;

import java.util.ArrayList;

/**
 * Created by rossrkk on 15/05/17.
 */

public class Service {
    private String name;
    private String description;

    private ArrayList<Item> menu;

    public Service(String name, String description, ArrayList<Item> menu) {
        this.name = name;
        this.description = description;
        this.menu = menu;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Item> getMenu() {
        return menu;
    }

    public String getDescription() {
        return description;
    }
}
