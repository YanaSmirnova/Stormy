package yanasmirnova.com.stormy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private CurrentWeather mCurrentWeather;

    @InjectView(R.id.timeLabel) TextView mTimeLabel;
    @InjectView(R.id.temperatureLabel) TextView mTemperatureLabel;
    @InjectView(R.id.humidityValue) TextView mHumidityValue;
    @InjectView(R.id.precipValue) TextView mPrecipValue;
    @InjectView(R.id.summaryLabel) TextView mSummaryLabel;
    @InjectView(R.id.iconImageView) ImageView mIconImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        String apiKey = "5c868a1f47e78ac86297b36dcfde4609";
        double latitude = 37.8267;
        double longitude = -122.423;

        String forecastUrl = "https://api.forecast.io/forecast/"+apiKey+"/"+latitude+","+longitude;
        // https://api.forecast.io/forecast/5c868a1f47e78ac86297b36dcfde4609/37.8267,-122.423

        if (isNetworkAvailable()) {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(forecastUrl)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mCurrentWeather = getCurrentDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });
                        }
                        else {
                            alertUserAboutError();
                        }
                    }
                    catch (IOException e) {
                        Log.i(TAG, "Exception caught", e);
                    }
                    catch (JSONException e) {
                        Log.i(TAG, "Exception caught", e);
                    }
                }
            });
        }
        else {
            // TODO make it a dialog
            Toast.makeText(this, getString(R.string.network_unavailable_msg),
                    Toast.LENGTH_LONG).show();
        }
        Log.d(TAG, "Main UI code is running");

    }

    private void updateDisplay() {
        mTemperatureLabel.setText(mCurrentWeather.getTemperature() + "");
        mTimeLabel.setText("At " + mCurrentWeather.getFormattedTime() + " it will be");
        mHumidityValue.setText(mCurrentWeather.getHumidity() + "");
        mPrecipValue.setText(mCurrentWeather.getPrecipChance() + "%");
        mSummaryLabel.setText(mCurrentWeather.getSummary());
        mIconImageView.setImageResource(mCurrentWeather.getIconId());
    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timeZone = forecast.getString("timezone");
        Log.i(TAG, "TimeZone: " + timeZone);

        JSONObject currently = forecast.getJSONObject("currently");

        String icon = currently.getString("icon");
        long time = currently.getLong("time");
        double temperature = currently.getDouble("temperature");
        double humidity = currently.getDouble("humidity");
        double precipChance = currently.getDouble("precipProbability");
        String summary = currently.getString("summary");

        Log.i(TAG, "Icon" + icon + "; Time: " + time + "; Temperature: " + temperature
                + "; Humidity: " + humidity + "; PrecipChance: " + precipChance + "; Summary: " + summary);

        CurrentWeather currentWeather = new CurrentWeather();

        currentWeather.setIcon(icon);
        currentWeather.setTime(time);
        currentWeather.setTemperature(temperature);
        currentWeather.setHumidity(humidity);
        currentWeather.setPrecipChance(precipChance);
        currentWeather.setSummary(summary);
        currentWeather.setTimeZone(timeZone);

        Log.d(TAG, "Formatted time: " + currentWeather.getFormattedTime());

        return currentWeather;
    }

    private boolean isNetworkAvailable() {
        boolean isAvailable = false;

        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return  isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

}
