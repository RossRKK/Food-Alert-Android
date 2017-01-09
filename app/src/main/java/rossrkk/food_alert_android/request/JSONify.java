package rossrkk.food_alert_android.request;

import rossrkk.food_alert_android.MainActivity;
import rossrkk.food_alert_android.Reference;

/**
 * Created by rossrkk on 15/11/16.
 */

public class JSONify {

    // produce an int array from a json string
    public static DataObj fromJSON(String json) {
        String nameField = "name";

        String name = valueOfField(nameField, json);
        name = name.substring(1, name.length()-1);

        // declare an integer array with an element for each field
        int[] data = new int[Reference.tertiaryFieldNames.length + Reference.binaryFieldNames.length];
        int dataIndex = 0;
        // loop through each field
        for (int i = 0; i < Reference.binaryFieldNames.length; i++) {
            // get the substring of the json that is relevan
            // parse the string to an integer
            data[dataIndex] = Integer.parseInt(valueOfField(Reference.binaryFieldNames[i], json));
            dataIndex++;
        }

        // loop through each field
        for (int i = 0; i < Reference.tertiaryFieldNames.length; i++) {
            // get the substring of the json that is relevant

            // parse the string to an integer
            data[dataIndex] = Integer.parseInt(valueOfField(Reference.tertiaryFieldNames[i], json));
            dataIndex++;
        }
        return new DataObj(data, name);
    }

    private static String valueOfField(String fieldName, String json) {
        int start = json.indexOf(fieldName) + fieldName.length();
        int colon = json.indexOf(":", start);
        int end = json.indexOf(",", colon);
        if (end == -1) {
            end = json.indexOf("}");
        }
        String subStr = json.substring(colon+1, end);
        subStr = subStr.trim();
        return subStr;
    }

    public static String encode(String name, int[] data) {
        String out = "?name=" + name;

        int index = 0;
        for (int i = 0; i < Reference.binaryFieldNames.length; i++) {
            out += "&";
            out += Reference.binaryFieldNames[i] + "=" + data[index];
            index++;
        }

        for (int i = 0; i < Reference.tertiaryFieldNames.length && i < data.length; i++) {
            out += "&";
            out += Reference.tertiaryFieldNames[i] + "=" + data[index];
            index++;
        }
        System.out.println(out);
        return out;
    }
}
