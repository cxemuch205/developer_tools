package ua.setcom.developertools.Classes;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;

/**
 * Created by daniil on 10/23/14.
 */
public abstract class AbstractPreference<T> implements Preference<T> {
    private final String key;

    private final T defaultValue;

    protected AbstractPreference(String key, @Nullable T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public final T getPreference(SharedPreferences preferences) {
        if (isSet(preferences)) {
            return getPersistedValue(preferences);
        } else {
            return this.defaultValue;
        }
    }

    @Override
    public T getPreferenceNoError(SharedPreferences preferences) {
        if (isSet(preferences)) {
            try {
                return getPersistedValue(preferences);
            } catch (RuntimeException e) {
                return this.defaultValue;
            }
        } else {
            return this.defaultValue;
        }
    }

    @Override
    public void putDefault(SharedPreferences preferences) {
        putPreference(preferences, this.defaultValue);
    }

    @Override
    public void putPreference(SharedPreferences preferences, @Nullable T value) {
        if (value != null) {
            final SharedPreferences.Editor editor = preferences.edit();
            putPersistedValue(editor, value);
            editor.commit();
        }
    }

    @Override
    public boolean isSet(SharedPreferences preferences) {
        return preferences.contains(this.key);
    }

    @Override
    public final boolean tryPutDefault(SharedPreferences preferences) {
        final boolean result;

        if (isSet(preferences)) {
            result = false;
        } else {
            putDefault(preferences);
            result = true;
        }

        return result;
    }

    @Override
    public final boolean isSameKey(String key) {
        return this.key.equals(key);
    }

    /*
    **********************************************************************
    *
    *                           ABSTRACT METHODS
    *
    **********************************************************************
    */

    /**
     * @param preferences preferences container
     * @return preference value from preferences with key defined by {@link #getKey()} method
     */
    @Nullable
    protected abstract T getPersistedValue(SharedPreferences preferences);

    /**
     * Method saved preference to preferences container editor
     *
     * @param editor editor in which value must be saved
     * @param value  value to be saved
     */
    protected abstract void putPersistedValue(SharedPreferences.Editor editor, T value);

}
