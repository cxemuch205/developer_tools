/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package ua.setcom.developertools.Onscreen;

import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ua.setcom.developertools.Classes.AbstractPreference;

/**
 * User: serso
 * Date: 11/21/12
 * Time: 10:55 PM
 */
public class DevToolsViewState implements Parcelable {

	private static final String TAG = DevToolsViewState.class.getSimpleName();

	public static final Parcelable.Creator<DevToolsViewState> CREATOR = new Parcelable.Creator<DevToolsViewState>() {
		public DevToolsViewState createFromParcel(Parcel in) {
			return DevToolsViewState.fromParcel(in);
		}

		public DevToolsViewState[] newArray(int size) {
			return new DevToolsViewState[size];
		}
	};

	private int width;

	private int height;

	private int x;

	private int y;

	private DevToolsViewState() {
	}

	private static DevToolsViewState fromParcel( Parcel in) {
		final DevToolsViewState result = new DevToolsViewState();
		result.width = in.readInt();
		result.height = in.readInt();
		result.x = in.readInt();
		result.y = in.readInt();
		return result;
	}

	public static DevToolsViewState newDefaultState() {
		return newInstance(200, 400, 0, 0);
	}

	public static DevToolsViewState newInstance(int width, int height, int x, int y) {
		final DevToolsViewState result = new DevToolsViewState();
		result.width = width;
		result.height = height;
		result.x = x;
		result.y = y;
		return result;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(width);
		out.writeInt(height);
		out.writeInt(x);
		out.writeInt(y);
	}

	@Override
	public String toString() {
		return "DevToolsOnscreenViewState{" +
				"y=" + y +
				", x=" + x +
				", height=" + height +
				", width=" + width +
				'}';
	}

    public static class Preference extends AbstractPreference<DevToolsViewState> {

        public Preference( String key, @Nullable DevToolsViewState defaultValue) {
            super(key, defaultValue);
        }

        @Nullable
        @Override
        protected DevToolsViewState getPersistedValue( SharedPreferences preferences) {
            try {
                final DevToolsViewState result = new DevToolsViewState();
                final JSONObject jsonObject = new JSONObject(preferences.getString(getKey(), "{}"));
                result.width = jsonObject.getInt("width");
                result.height = jsonObject.getInt("height");
                result.x = jsonObject.getInt("x");
                result.y = jsonObject.getInt("y");

                Log.d(TAG, "Reading onscreen view state: " + result);

                return result;
            } catch (JSONException e) {
                return getDefaultValue();
            }
        }

        @Override
        protected void putPersistedValue( SharedPreferences.Editor editor, DevToolsViewState value) {
            final Map<String, Object> properties = new HashMap<String, Object>();
            properties.put("width", value.getWidth());
            properties.put("height", value.getHeight());
            properties.put("x", value.getX());
            properties.put("y", value.getY());

            final JSONObject jsonObject = new JSONObject(properties);

            final String json = jsonObject.toString();
            Log.d(TAG, "Persisting onscreen view state: " + json);
            editor.putString(getKey(), json);
        }
    }
}
