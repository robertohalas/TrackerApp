package smart.tuke.sk.makac.other;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import smart.tuke.sk.makac.R;
import smart.tuke.sk.makac.data.ForecastItem;

public class WeatherAdapter extends ArrayAdapter<ForecastItem> {

    private Context myContext;

    public WeatherAdapter(Context context, ArrayList<ForecastItem> users) {
        super(context, 0, users);
        this.myContext = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ForecastItem forecast = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.weather_item, parent, false);
        }
        // Lookup view for data population
        ImageView icon = (ImageView) convertView.findViewById(R.id.weatherIconImageView);
        TextView temp = (TextView) convertView.findViewById(R.id.temperatureTextView);
        TextView condition = (TextView) convertView.findViewById(R.id.conditionTextView);
        TextView location = (TextView) convertView.findViewById(R.id.locationTextView);
        // Populate the data into the template view using the data object

        int resourceId = myContext.getResources().getIdentifier("drawable/icon_" + forecast.getCode(), null, myContext.getPackageName());

        icon.setImageResource(resourceId);

        temp.setText(forecast.getTemperature());
        condition.setText(forecast.getText());
        location.setText(forecast.getCity());
        // Return the completed view to render on screen
        return convertView;
    }
}

