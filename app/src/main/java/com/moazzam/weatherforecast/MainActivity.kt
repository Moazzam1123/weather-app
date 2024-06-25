package com.moazzam.weatherforecast

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.moazzam.weatherforecast.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.SearchView

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("sahiwal")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView = binding.searchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(AppInterface::class.java)
        val response = retrofit.getWeatherData(cityName, "6d4f2346f0d8b939aceb1cbd1bf3753a", "metric")
        response.enqueue(object: Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val sunset= responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min

                    binding.weather.text = condition
                    binding.maxTemp.text = "Max Temp: $maxTemp"
                    binding.minTemp.text = "Min Temp: $minTemp"
                    binding.humidity.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.sunRise.text = "${time(sunrise)}"
                    binding.sunSet.text = "${time(sunset)}"
                    binding.sea.text = "$seaLevel hPa"
                    binding.temp.text= "$temperature °C"
                    binding.condition.text = condition
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()
                    binding.cityName.text = "$cityName"
                    changeImageAccordingToWeatherCondition(condition, sunrise, sunset)
                }
            }

            override fun onFailure(p0: Call<WeatherApp>, p1: Throwable) {
            }
        })
    }

    private fun isDay(sunrise: Long, sunset: Long, currentTimeMillis: Long): Boolean {
        val sunriseTime = sunrise * 1000 // Convert seconds to milliseconds
        val sunsetTime = sunset * 1000 // Convert seconds to milliseconds
        return currentTimeMillis in sunriseTime until sunsetTime
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun changeImageAccordingToWeatherCondition(conditions: String, sunrise: Long, sunset: Long) {
        val isDayTime = isDay(sunrise, sunset, System.currentTimeMillis())
        if(isDayTime) {
            when(conditions) {
                "Clear Sky", "Clear", "Sunny" -> {
                    binding.root.setBackgroundResource(R.drawable.day_clear_background)
                    binding.weatherimageView.setImageDrawable(getDrawable(R.drawable.clearsky))
                }

                "Partly Clouds", "Clouds", "Overcast" -> {
                    binding.root.setBackgroundResource(R.drawable.cloudy_background)
                    binding.weatherimageView.setImageDrawable(getDrawable(R.drawable.sunnycloud))
                }

                "Mist", "Foggy" -> {
                    binding.root.setBackgroundResource(R.drawable.foggy_background)
                    binding.weatherimageView.setImageDrawable(getDrawable(R.drawable.mist))
                }

                "Light Rain", "Drizzle", "Moderate Rain", "Rain" -> {
                    binding.root.setBackgroundResource(R.drawable.rain_background)
                    binding.weatherimageView.setImageDrawable(getDrawable(R.drawable.slowrainy))
                }

                "Showers", "Heavy Rain" -> {
                    binding.root.setBackgroundResource(R.drawable.rain_background)
                    binding.weatherimageView.setImageDrawable(getDrawable(R.drawable.showers))
                }

                "Moderate Snow", "Light Snow", "Heavy Snow", "Snow" -> {
                    binding.root.setBackgroundResource(R.drawable.snow_background)
                    binding.weatherimageView.setImageDrawable(getDrawable(R.drawable.cloudsnow))
                }

                "Blizzard" -> {
                    binding.root.setBackgroundResource(R.drawable.snow_background)
                    binding.weatherimageView.setImageDrawable(getDrawable(R.drawable.cloudsnowhail))
                }

                else -> {
                    binding.root.setBackgroundResource(R.drawable.day_clear_background)
                    binding.weatherimageView.setImageDrawable(getDrawable(R.drawable.clearsky))
                }
            }
        }
        else {
            when(conditions) {
                "Clear Sky", "Clear", "Sunny" -> {
                    binding.root.setBackgroundResource(R.drawable.night_clear_background)
                    binding.weatherimageView.setImageDrawable(getDrawable(R.drawable.night))
                }

                "Partly Clouds", "Clouds", "Overcast" -> {
                    binding.root.setBackgroundResource(R.drawable.cloudy_background)
                    binding.weatherimageView.setImageDrawable(getDrawable(R.drawable.nightcloud))
                }

                "Mist", "Foggy" -> {
                    binding.root.setBackgroundResource(R.drawable.foggy_background)
                    binding.weatherimageView.setImageDrawable(getDrawable(R.drawable.nightfoggy))
                }

                "Light Rain", "Drizzle", "Moderate Rain", "Rain" -> {
                    binding.root.setBackgroundResource(R.drawable.rain_background)
                    binding.weatherimageView.setImageDrawable(getDrawable(R.drawable.nightcloudrain))
                }

                "Showers", "Heavy Rain" -> {
                    binding.root.setBackgroundResource(R.drawable.rain_background)
                    binding.weatherimageView.setImageDrawable(getDrawable(R.drawable.nightcloudshowers))
                }

                "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard", "Snow" ->
                {
                    binding.root.setBackgroundResource(R.drawable.snow_background)
                    binding.weatherimageView.setImageDrawable(getDrawable(R.drawable.nightcloudsnow))
                }

                else -> {
                    binding.root.setBackgroundResource(R.drawable.night_clear_background)
                    binding.weatherimageView.setImageDrawable(getDrawable(R.drawable.night))
                }
            }
        }
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }

    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

    fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}