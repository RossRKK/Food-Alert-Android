package rossrkk.food_alert_android.profile;

import android.content.SharedPreferences;

import java.util.ArrayList;

import rossrkk.food_alert_android.Reference;

import static rossrkk.food_alert_android.Reference.binaryFieldNames;
import static rossrkk.food_alert_android.Reference.tertiaryFieldNames;

/**
 * Created by rossrkk on 19/01/17.
 */

public class ProfileManager {
    private static final String LAST_ID_FIELD = "rossrkk.food_alert_android.profile.LAST_ID";
    static final String FIELD_BASE = "rossrkk.food_alert_android.profile.";
    private static ArrayList<Profile> profiles = new ArrayList<Profile>();
    private static int defaultValue = Reference.UNKNOWN;
    private static String defaultValueName = "Unknown Name";

    /**
     * Saves all profiles
     * @param sharedPref The shared preferences object to be used
     */
    public static void saveProfiles(SharedPreferences sharedPref) {
        SharedPreferences.Editor editor = sharedPref.edit();

        System.out.println("Setting number of profiles to: " + profiles.size());
        editor.putInt(LAST_ID_FIELD, profiles.size());
        editor.commit();

        for (int i = 0; i < profiles.size(); i++) {
            profiles.get(i).save(i, editor);
        }
    }

    /**
     * Loads all profiles into memory
     * @param sharedPref The shared preferences to be used
     */
    public static void loadProfiles(SharedPreferences sharedPref) {
        if (profiles.isEmpty()) {
            int length = sharedPref.getInt(LAST_ID_FIELD, 0);
            for (int i = 0; i < length; i++) {
               loadProfile(i, sharedPref);
            }
        }
    }

    /**
     * Loads a profile based on an id
     * @param id The id of the profile to be loaded
     * @param sharedPref The shared preferences to be used
     * @return The profile that was loaded, null if no profile of that ID could be found
     */
    private static Profile loadProfile(int id, SharedPreferences sharedPref) {
        try {
            String name = sharedPref.getString(Reference.NAME_FIELD + id, defaultValueName);

            int[] settings = new int[tertiaryFieldNames.length + binaryFieldNames.length];
            int index = 0;
            for (int i = 0; i < binaryFieldNames.length; i++) {
                settings[index] = sharedPref.getInt(FIELD_BASE + id + Reference.binaryFieldNames[i], defaultValue);
                index++;
            }

            for (int i = 0; i < tertiaryFieldNames.length; i++) {
                settings[index] = sharedPref.getInt(FIELD_BASE + id + Reference.tertiaryFieldNames[i], defaultValue);
                index++;
            }
            return new Profile(name, settings);
        } catch (Exception e) {
            System.out.println("There was an error loading the profile with ID: " + id);
            return null;
        }
    }

    /**
     * Return a profile with a given id
     * @param id The id of the profile you are looking for
     * @return The profile with that ID
     */
    public static Profile getProfile(int id) {
        return profiles.get(id);
    }

    /**
     * Figure out whether this food is compatible with this profile
     *
     * @return 1 if compatible, 0 if not and -1 if unsure
     */
    public static int compareToProfiles() {
        int result = Reference.UNKNOWN;
        for (int i = 0; i < profiles.size(); i++) {
            int cur = profiles.get(i).comapreToData();

            if (cur == Reference.INCOMPATIBLE) {
                return Reference.INCOMPATIBLE;
            } else  if (cur == Reference.UNKNOWN) {
                return Reference.UNKNOWN;
            } else if (cur == Reference.COMPATIBLE) {
                    result = Reference.COMPATIBLE;
            }
        }
        return result;
    }

    /**
     * Add a profile to the profile manager
     * @param p The profle to be added
     */
    public static void addProfile(Profile p) {
        profiles.add(p);
    }

    public static int getLength() {
        return profiles.size();
    }
}
