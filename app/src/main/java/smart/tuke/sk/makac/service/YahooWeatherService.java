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

import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import smart.tuke.sk.makac.data.ForecastItem;
import smart.tuke.sk.makac.listener.WeatherServiceListener;

public class YahooWeatherService {
    private WeatherServiceListener listener;
    private Exception error;

    public YahooWeatherService(WeatherServiceListener listener) {
        this.listener = listener;
    }

    public void refreshWeather(String location) {

        new AsyncTask<String, Void, ArrayList<ForecastItem>>() {
            @Override
            protected ArrayList<ForecastItem> doInBackground(String[] locations) {

                String location = locations[0];

                ArrayList<ForecastItem> forecast = new ArrayList<>();

                String YQL = String.format("select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s\") and u='c'", location);

                String endpoint = String.format("https://query.yahooapis.com/v1/public/yql?q=%s&format=json", Uri.encode(YQL));

                try {
                    URL url = new URL(endpoint);

                    URLConnection connection = url.openConnection();
                    connection.setUseCaches(false);

                    InputStream inputStream = connection.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    JSONObject data = new JSONObject(result.toString()).optJSONObject("query");

                    int count = data.optInt("count");

                    if (count == 0) {
                        error = new LocationWeatherException("No weather information found for " + location);
                        return null;
                    }

                    data = data.optJSONObject("results").optJSONObject("channel");
                    JSONArray forecastArray = data.optJSONObject("item").getJSONArray("forecast");

                    String city = data.optJSONObject("location").getString("city");

                    data = data.optJSONObject("item").optJSONObject("condition");

                    forecast.add(new ForecastItem(data.getInt("code"), data.getString("temp"), data.getString("text"), city));

                    for (int i = 0; i < forecastArray.length(); i++) {
                        JSONObject objI = forecastArray.getJSONObject(i);

                        forecast.add(new ForecastItem(objI.getInt("code"), String.format("%s - %s", objI.getString("low"), objI.getString("high")), objI.getString("text"), city));
                    }

                    return forecast;

                } catch (Exception e) {
                    error = e;
                }

                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<ForecastItem> forecast) {

                if (forecast == null && error != null) {
                    listener.serviceFailure(error);
                } else {
                    listener.serviceSuccess(forecast);
                }

            }

        }.execute(location);
    }

    public class LocationWeatherException extends Exception {
        public LocationWeatherException(String detailMessage) {
            super(detailMessage);
        }
    }
}
