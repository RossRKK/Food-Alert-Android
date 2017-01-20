package rossrkk.food_alert_android.profile;

import android.content.SharedPreferences;

import rossrkk.food_alert_android.Reference;

import static rossrkk.food_alert_android.Reference.profile;

/**
 * Created by rossrkk on 19/01/17.
 */

public class Profile {
    private int[] settings;
    private String name;
    private int id;

    /**
     * Create a new profile
     * @param id The id of the new profile
     * @param name The name of the new profile
     * @param settings The settings array of the new profile
     */
    public Profile(int id, String name, int[] settings) {
        this.id = id;
        this.name = name;
        this.settings = settings;
        ProfileManager.addProfile(this);
    }

    /**
     * Return the settings of the profile
     * @return
     */
    public int[] getSettings() {
        return settings;
    }

    /**
     * Return the name of the profile
     * @return The name of the profile
     */
    public String getName() {
        return name;
    }

    /**
     * Get the ID of the profile
     * @return The id of the profile
     */
    public int getId() {
        return id;
    }

    /**
     * Return whether the person this profile describes can eat the product
     * @return The code relevant to whether this person can eat the current product
     */
    public int comapreToData() {
        if (Reference.data != null && !Reference.reconfirm) {
            for (int i = 0; i < settings.length; i++) {
                //if the person is intolerant and the data is unknown return unknown
                if ((settings[i] == Reference.NONE || settings[i] == Reference.TRACE) && Reference.data[i] == Reference.UNKNOWN) {
                    return Reference.UNKNOWN;
                }

                //if the data matches the profiles tolerances
                if (settings[i] == Reference.NONE && (Reference.data[i] == Reference.TRACE || Reference.data[i] == Reference.ANY)) {
                    return Reference.INCOMPATIBLE;
                } else if (settings[i] == Reference.TRACE && Reference.data[i] == Reference.ANY) {
                    return Reference.INCOMPATIBLE;
                }
            }
            return Reference.COMPATIBLE;
        } else {
            if (Reference.reconfirm) {
                return Reference.UNKNOWN;
            } else {
                return -1;
            }
        }
    }

    /**
     * Save the profile to the disk
     * @param sharedPref The sharedpref to be used to save the profile
     */
    public void save(SharedPreferences sharedPref) {
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(id + Reference.NAME_FIELD, name);

        //submit the fields to the thing
        int index = 0;

        for (int i = 0; i < Reference.binaryFieldNames.length; i++) {
            editor.putInt(id + Reference.binaryFieldNames[i], profile[index]);
            editor.commit();
            index++;
        }

        for (int i = 0; i < Reference.tertiaryFieldNames.length; i++) {
            editor.putInt(id + Reference.tertiaryFieldNames[i], profile[index]);
            editor.commit();
            index++;
        }
    }
}
