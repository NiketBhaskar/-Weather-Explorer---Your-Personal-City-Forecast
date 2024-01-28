package com.revia.weatherstatus

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import com.revia.weatherstatus.databinding.ActivityMainBinding
import retrofit2.Response
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//7ecfd237d98575091394192dfef2afb2
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Jaipur")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.svSearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                binding.svSearchView.hideKeyboard()
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }
    fun View.hideKeyboard() {
        val context = this.context ?: return
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(this.windowToken, 0)
    }
    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response =
            retrofit.getWeatherData(cityName, "7ecfd237d98575091394192dfef2afb2", "metric")
        response.enqueue(/* callback = */ object : Callback<Weatherapp> {
            override fun onResponse(call: Call<Weatherapp>, response: Response<Weatherapp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity.toString()
                    val windSpeed = responseBody.wind.speed.toString()
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure.toString()
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    val maxTemp = responseBody.main.temp_max.toString()
                    val minTemp = responseBody.main.temp_min.toString()

                    //      Log.d("LOGTEMP", "onResponse: $temperature")
                    binding.tvTemp.text = "$temperature °C"
                    binding.tvWeather.text = condition
                    binding.tvMaxTemp.text = "Max Temp: $maxTemp °C"
                    binding.tvMinTemp.text = "Max Temp: $minTemp °C"
                    binding.tvHumidity.text = "$humidity %"
                    binding.tvWind.text = "$windSpeed m/s"
                    binding.tvSunrise.text = "${time(sunRise)}"
                    binding.tvSunset.text = "${time(sunSet)}"
                    binding.tvSea.text = "$seaLevel hPa"
                    binding.tvConditions.text = condition
                    binding.tvDay.text = dayName(System.currentTimeMillis())
                    binding.tvDate.text = date()
                    binding.tvCity.text = cityName

                    changeUIAccordingToWeather(condition)
                }
            }

            override fun onFailure(call: Call<Weatherapp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun changeUIAccordingToWeather(conditions: String) {
        when(conditions){
            "Clear Sky", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Partly com.revia.weatherstatus.Clouds", "com.revia.weatherstatus.Clouds", "Overcast", "Mist", "Foggy" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain", "Drizzle", "Moderate rain", "Showers", "Heavy Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))

    }
    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

    fun dayName(timestamp: Long): String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}