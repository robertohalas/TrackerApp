/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2015 Yoel Nunez <dev@nunez.guru>
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package smart.tuke.sk.makac.service;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import smart.tuke.sk.makac.data.ForecastItem;
import smart.tuke.sk.makac.listener.WeatherServiceListener;

public class WeatherCacheService {
    private Context context;
    private Exception error;
    private final String CACHED_WEATHER_FILE = "weather.data";

    public WeatherCacheService(Context context) {
        this.context = context;
    }

    public void save(ArrayList<ForecastItem> forecast) {
        new AsyncTask<ArrayList<ForecastItem>, Void, Void>() {

            @Override
            protected Void doInBackground(ArrayList<ForecastItem>... list) {

                FileOutputStream outputStream;

                try {
                    outputStream = context.openFileOutput(CACHED_WEATHER_FILE, Context.MODE_PRIVATE);
                    JSONArray jsArray = new JSONArray();
                    int i = 0;
                    for(ForecastItem item: list[0]) {
                        JSONObject iObj = new JSONObject();
                        try {
                            iObj.put("code", item.getCode());
                            iObj.put("temp", item.getTemperature());
                            iObj.put("text", item.getText());
                            iObj.put("city", item.getCity());
                            jsArray.put(i);
                            i++;
                        }
                        catch (JSONException e) {
                        }
                    }
                    outputStream.write(jsArray.toString().getBytes());
                    outputStream.close();

                } catch (IOException e) {
                    // TODO: file save operation failed
                }

                return null;
            }
        }.execute(forecast);
    }

    public void load(final WeatherServiceListener listener) {

        new AsyncTask<WeatherServiceListener, Void, ArrayList<ForecastItem>>() {
            private WeatherServiceListener weatherListener;

            @Override
            protected ArrayList<ForecastItem> doInBackground(WeatherServiceListener[] serviceListeners) {
                weatherListener = serviceListeners[0];
                try {
                    FileInputStream inputStream = context.openFileInput(CACHED_WEATHER_FILE);

                    StringBuilder cache = new StringBuilder();
                    int content;
                    while ((content = inputStream.read()) != -1) {
                        cache.append((char) content);
                    }

                    inputStream.close();

                    JSONObject jsonCache = new JSONObject(cache.toString());

                    ArrayList<ForecastItem> list = new ArrayList<>();
                    list.add(new ForecastItem(jsonCache.getInt("code"), jsonCache.getString("temp"), jsonCache.getString("text"), jsonCache.getString("city")));

                    return list;

                } catch (Exception e) {
                    error = e;
                }

                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<ForecastItem> list) {
                if (list == null && error != null) {
                    weatherListener.serviceFailure(error);
                } else {
                    weatherListener.serviceSuccess(list);
                }
            }
        }.execute(listener);
    }
}
