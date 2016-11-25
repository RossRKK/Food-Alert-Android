package rossrkk.food_alert_android;

/**
 * Created by rossrkk on 25/11/16.
 */

public class Database {
    public static final String[] fieldNames = { "containsNuts", "containsDairy" };
    public static final String[] fieldNamesFormatted = { "Nuts", "Dairy"};

    public static final int ANY = 2;
    public static final int TRACE = 1;
    public static final int NONE = 0;
    public static final int UNKNOWN = -1;

    public static final int COMPATIBLE = 1;
    public static final int INCOMPATIBLE = 0;
}
