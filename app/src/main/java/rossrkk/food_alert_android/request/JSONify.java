package rossrkk.food_alert_android.request;

import rossrkk.food_alert_android.Reference;

/**
 * Created by rossrkk on 15/11/16.
 */

public class JSONify {

    // produce an int array from a json string
    public static int[] fromJSON(String json) {
        // declare an integer array with an element for each field
        int[] data = new int[Reference.fieldNames.length];

        // loop through each field
        for (int i = 0; i < Reference.fieldNames.length; i++) {
            // get the substring of the json that is relevant

            int index = json.indexOf(Reference.fieldNames[i]) + Reference.fieldNames[i].length() + 3;
            String subStr = json.substring(index, index + 1);
            if (subStr.contains("-")) {
                subStr = json.substring(index, index + 2);
            }
            // parse the string to an integer
            data[i] = Integer.parseInt(subStr);
        }
        return data;
    }

    public static String toJSON(int[] data) {
        String out = "{";

        for (int i = 0; i < Reference.fieldNames.length && i < data.length; i++) {
            out += "\"" + Reference.fieldNames[i] + "\": " + data[i];

            if (i != Reference.fieldNames.length - 1) {
                out += ", ";
            }
        }

        out += "}";

        return out;
    }

    public static String encode(int[] data) {
        String out = "?";

        for (int i = 0; i < Reference.fieldNames.length && i < data.length; i++) {
            out += Reference.fieldNames[i] + "=" + data[i];

            if (i != Reference.fieldNames.length - 1) {
                out += "&";
            }
        }

        //out += "}";

        return out;
    }

    public static String formatData(int[] data) {
        String out = "";
        for (int i = 0; i < Reference.fieldNames.length; i++) {
            out += Reference.fieldNamesFormatted[i] + ": ";
            switch (data[i]) {
                case -1: out += "Unknown"; break;
                case 0: out += "Yes"; break;
                case 1: out += "No"; break;
                default: out += "Error"; break;
            }

            if (i < Reference.fieldNames.length - 1) {
                out += "\n";
            }
        }
        return out;
    }
}
