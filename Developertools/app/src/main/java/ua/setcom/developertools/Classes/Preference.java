package ua.setcom.developertools.Classes;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;

/**
 * Created by daniil on 10/23/14.
 */
public interface Preference<T> {

    String getKey();

    /**
     * @return default preference value, may be null
     */
    T getDefaultValue();

    /**
     * NOTE: this method can throw runtime exceptions if errors occurred while extracting preferences values
     *
     * @param preferences application preferences
     * @return value from preference, default value if no value in preference was found
     */
    T getPreference( SharedPreferences preferences);

    /**
     * NOTE: this method SHOULD not throw any runtime exceptions BUT return default value if any error occurred
     *
     * @param preferences application preferences
     * @return value from preference, default value if no value in preference was found or error occurred
     */
    T getPreferenceNoError( SharedPreferences preferences);

    /**
     * Method puts (saves) preference represented by <code>value</code> in <code>preferences</code> container
     *
     * @param preferences preferences container
     * @param value       value to be saved
     */
    void putPreference( SharedPreferences preferences, @Nullable T value);

    /**
     * Method saves default value in <code>preferences</code> container.
     * Should behave exactly as <code>p.putPreference(preferences, p.getDefaultValue())</code>
     *
     * @param preferences preferences container
     */
    void putDefault( SharedPreferences preferences);

    /**
     * @param preferences preferences container
     * @return true if any value is saved in preferences container, false - otherwise
     */
    boolean isSet( SharedPreferences preferences);

    /**
     * Method applies default value to preference only if explicit value is not set
     *
     * @param preferences preferences container
     * @return true if default values have been applied, false otherwise
     */
    boolean tryPutDefault( SharedPreferences preferences);

    /**
     * @param key preference key
     * @return true if current preferences has the same key
     */
    boolean isSameKey( String key);
}
