package com.example.testweatherapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText editTextCity;
    private Button buttonSearch;
    private TextView textViewResult;

    private final OkHttpClient client = new OkHttpClient();
    private final String apiKey = "1cf0a465fd6cbb518f880cf5e08de973"; // Replace with your actual OpenWeatherMap API key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Make sure this layout file exists

        editTextCity = findViewById(R.id.editTextCity);
        buttonSearch = findViewById(R.id.buttonSearch);
        textViewResult = findViewById(R.id.textViewResult);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = editTextCity.getText().toString().trim();
                if (!city.isEmpty()) {
                    fetchWeather(city);
                } else {
                    textViewResult.setText("Please enter a city name.");
                }
            }
        });
    }

    private void fetchWeather(String city) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city +
                "&appid=" + apiKey + "&units=metric";

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> textViewResult.setText("Failed to load data."));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> textViewResult.setText("City not found or server error."));
                    return;
                }

                String responseData = response.body().string();
                try {
                    JSONObject json = new JSONObject(responseData);
                    JSONArray weatherArray = json.getJSONArray("weather");
                    String description = weatherArray.getJSONObject(0).getString("description");

                    JSONObject main = json.getJSONObject("main");
                    double temperature = main.getDouble("temp");

                    String resultText = "Weather: " + description + "\nTemperature: " + temperature + "°C";
                    runOnUiThread(() -> textViewResult.setText(resultText));

                } catch (Exception e) {
                    runOnUiThread(() -> textViewResult.setText("Error parsing weather data."));
                }
            }
        });
    }
}