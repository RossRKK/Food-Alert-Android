package rossrkk.food_alert_android.request;

import javax.json.JsonArray;
import javax.json.JsonReader;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonObject;

import rossrkk.food_alert_android.Reference;

/**
 * Created by rossrkk on 15/11/16.
 */

public class JSONify {

    public static ArrayList<Service> parseResults(String jsonStr) {
        ArrayList<Service> results = new ArrayList<Service>();

        //this removes non standard characters and prevents parsing errors
        jsonStr = jsonStr.replaceAll("[^\\x00-\\x7F]", "");

        JsonReader jsonReader = Json.createReader(new StringReader(jsonStr));
        JsonObject root = jsonReader.readObject();

        if (root.containsKey("results")) {
            JsonArray resultsJson = root.getJsonArray("results");

            for (int i = 0; i < resultsJson.size(); i++) {
                JsonObject result = resultsJson.getJsonObject(i);
                String name = result.getString("name");
                String description = result.getString("description");

                JsonArray menuJson = result.getJsonArray("items");

                ArrayList<Item> menu = new ArrayList<Item>();
                for (int j = 0; j < menuJson.size(); j++) {
                    JsonObject item = menuJson.getJsonObject(j);

                    int[] data = new int[Reference.binaryFieldNames.length + Reference.tertiaryFieldNames.length];

                    String itemName = item.getString("name");
                    String itemDescription = item.getString("description");
                    String category = item.getString("category");
                    double price= 0;

                    int index = 0;
                    for (String field:Reference.tertiaryFieldNames) {
                        data[index] = item.getInt(field);
                        index++;
                    }
                    for (String field:Reference.binaryFieldNames) {
                        data[index] = item.getInt(field);
                        index++;
                    }

                    menu.add(new Item(itemName, itemDescription, category, price, data));
                }

                results.add(new Service(name, description, menu));
            }
        }

        return results;
    }

    // produce an int array from a json string
    public static DataObj fromJSON(String json) {
        String nameField = "name";

        String name = valueOfField(nameField, json);
        try {
            name = URLDecoder.decode(name.substring(1, name.length() - 1), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("Error decoding name");
        }

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

        String reconfirmStr = valueOfField("reconfirm", json);
        reconfirmStr = reconfirmStr.substring(1, reconfirmStr.length()-1);
        boolean reconfirm = reconfirmStr.equalsIgnoreCase("true");
        System.out.println(reconfirmStr);

        return new DataObj(data, name, reconfirm);
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
