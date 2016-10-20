package eu.laramartin.weather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.laramartin.weather.data.CurrentWeatherResponse;
import eu.laramartin.weather.data.Forecast;
import eu.laramartin.weather.data.ForecastResponse;
import eu.laramartin.weather.data.WeatherService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private static final String LOG_TAG = MainActivity.class.getCanonicalName();
    @BindView(R.id.cityTextView) TextView city;
    @BindView(R.id.temperatureTextView) TextView temperature;
    @BindView(R.id.descriptionTextView) TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherService weatherService = retrofit.create(WeatherService.class);

        String location = "berlin";
        String appid = "";
        Call<ForecastResponse> callForecast = weatherService.getForecasts(location, appid);
        callForecast.enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                for (Forecast forecast : response.body().getForecasts()) {
                    Log.v("MainActivity", "forecast temp: " + String.valueOf(forecast.getTemperature().getTempDay()));
                    Log.v("MainActivity", "forecast descript: " + forecast.getWeather().get(0).getDescription());
                    Log.v("MainActivity", "forecast humidity: " + String.valueOf(forecast.getHumidity()));
                }
                Log.v("Mainactivity", "day 1: " );
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                Log.e("Mainactivity", "error in ForecastResponse: " + t.getLocalizedMessage(), t);
            }
        });

        Call<CurrentWeatherResponse> callCurrentWeather = weatherService.getWeather(location, appid);
        callCurrentWeather.enqueue(new Callback<CurrentWeatherResponse>() {
            @Override
            public void onResponse(Call<CurrentWeatherResponse> call, Response<CurrentWeatherResponse> response) {
                city.setText(response.body().getName());
                temperature.setText(String.valueOf(response.body().getMain().getTemperature()) +
                        getString(R.string.degree_fahrenheit));
                description.setText(response.body().getWeather().get(0).getDescription());
                Log.v(LOG_TAG, "Current temperature: " + String.valueOf(response.body().getMain().getTemperature()));
            }

            @Override
            public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {
                Log.e("Mainactivity", "error in CurrentWeatherResponse: " + t.getLocalizedMessage(), t);
            }
        });


    }
}
