package rossrkk.food_alert_android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.Layout;
import android.widget.RelativeLayout;

/**
 * Created by rossrkk on 25/11/16.
 */

public class Reference {
    public static final String[] tertiaryFieldNames = {"containsMilk", "containsEggs", "containsPeanuts", "containsTreeNuts", "containsSoy", "containsWheatGluten", "containsFish", "containsShellFish"};
    public static final String[] binaryFieldNames = {"isVegetarian", "isVegan"};
    //public static final String[] contiuousFieldNames = { "sugar", "fat", "calories" };
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

    public static final String BASE_URL = "http://food-alert.herokuapp.com";

    public static int[] profile = new int[tertiaryFieldNames.length + binaryFieldNames.length];
    public static int canEat = -1;
    public static String ean;
    public static String name;
    public static int[] data;
    public static boolean reconfirm = false;

    /**
     * Figure out whether this food is compatible with this profile
     *
     * @return 1 if compatible, 0 if not and -1 if unsure
     */
    public static int compareToProfile() {
        if (data != null && !reconfirm) {
            for (int i = 0; i < profile.length; i++) {
                //if the person is intolerant and the data is unknown return unknown
                if ((profile[i] == NONE || profile[i] == TRACE) && data[i] == UNKNOWN) {
                    return UNKNOWN;
                }

                //if the data matches the profiles tolerances
                if (profile[i] == NONE && (data[i] == TRACE || data[i] == ANY)) {
                    return INCOMPATIBLE;
                } else if (profile[i] == TRACE && data[i] == ANY) {
                    return INCOMPATIBLE;
                }
            }
            return COMPATIBLE;
        } else {
            if (reconfirm) {
                return UNKNOWN;
            } else {
                return -1;
            }
        }
    }

    public static void updateBackground(RelativeLayout layout) {
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
    //public static final String BASE_URL = "http://192.168.1.174:8080";
}
