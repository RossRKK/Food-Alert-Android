package rossrkk.food_alert_android;

import android.graphics.Color;

/**
 * Created by rossrkk on 25/11/16.
 */

public class Reference {
    public static final String[] fieldNames = { "containsNuts", "containsDairy" };
    public static final String[] fieldNamesFormatted = { "Nuts", "Dairy"};

    public static final int ANY = 3;
    public static final int TRACE = 2;
    public static final int NONE = 1;
    public static final int UNKNOWN = 0;

    public static final int COMPATIBLE = 2;
    public static final int INCOMPATIBLE = 1;

    public static final int RED = Color.rgb(255, 84, 104);
    public static final int GREEN = Color.rgb(77, 250, 144);
    public static final int YELLOW = Color.rgb(250, 190, 77);

    //public static final String BASE_URL = "http://food-alert.herokuapp.com";
    public static final String BASE_URL = "http://192.168.1.174:8080";
}
