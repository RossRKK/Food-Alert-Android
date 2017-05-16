package rossrkk.food_alert_android;

import android.app.Service;
import android.graphics.Color;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by rossrkk on 25/11/16.
 */

public class Reference {
    public static final String[] tertiaryFieldNames = {"containsMilk", "containsEggs", "containsPeanuts", "containsTreeNuts", "containsSoy", "containsWheatGluten", "containsFish", "containsShellFish"};
    public static final String[] binaryFieldNames = {"isVegetarian", "isVegan"};
    public static final String[] tertiaryFieldNamesFormatted = {"Milk", "Eggs", "Peanuts", "Tree Nuts", "Soy", "Wheat or Gluten", "Fish", "Shell Fish"};
    public static final String[] binaryFieldNamesFormatted = {"Vegetarian", "Vegan"};

    public static final int ANY = 3; //also used to indicate FALSE for isVegetarian etc.
    public static final int TRACE = 2;
    public static final int NONE = 1; //also used to indicate TRUE for isVegetarian etc.
    public static final int UNKNOWN = 0;

    public static final int COMPATIBLE = 2;
    public static final int INCOMPATIBLE = 1;

    public static final int GREEN = Color.rgb(155, 192, 102);
    public static final int YELLOW = Color.rgb(248, 205, 111);
    public static final int RED = Color.rgb(247, 117, 177);

    public static final String BASE_URL = "http://food-alert.herokuapp.com/";
    public static final String NAME_FIELD = "name";

    public static int canEat = -1;
    public static String ean;
    public static String name;
    public static int[] data;
    public static boolean reconfirm = false;

    public static void updateBackground(LinearLayout layout) {
        switch (Reference.canEat) {
            case Reference.COMPATIBLE:
                layout.setBackgroundColor(Reference.GREEN);
                break;
            case Reference.INCOMPATIBLE:
                layout.setBackgroundColor(Reference.RED);
                break;
            case Reference.UNKNOWN:
                layout.setBackgroundColor(Reference.YELLOW);
                break;
        }
    }
    //public static final String BASE_URL = "http://138.251.249.141:8080/";
}
