package com.example.weatherapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.SearchView
import android.widget.Toast

import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


//f189f5bfef45fb5dc06a02b4b54ea6d1
//https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}

class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        fetchWeatherData("Bhubaneshwar")
       searchCity()

    }

    private fun searchCity() {
        val searchView = binding.searchBar
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query != null){
                    fetchWeatherData(query)

                }

                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }


    private fun fetchWeatherData(cityName : String){
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityName,"f189f5bfef45fb5dc06a02b4b54ea6d1","metric")
        response.enqueue(object : Callback<weatherData> {
            override fun onResponse(
                call: Call<weatherData?>,
                response: Response<weatherData?>
            ) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null){
                    val temp = responseBody.main.temp.toString()
                    val min = responseBody.main.temp_min.toString()
                    val max = responseBody.main.temp_max.toString()
                    val humidity =  responseBody.main.humidity.toString()
                    val wind =  responseBody.wind.speed.toString()
                    val sunrise =  responseBody.sys.sunrise.toString()
                    val sunset =  responseBody.sys.sunset.toString()
                    val seaLevel = responseBody.main.pressure.toString()
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"


                    binding.temperatureBox.text = temp+"°C"
                    binding.minMAxBox.text="Min: ${min}\nMax: ${max}"
                    binding.wetherTypeBox.text = condition
                    binding.humidity.text = humidity+"%"
                    binding.winds.text = wind+"km/h"
                    binding.sunriseTime.text= setTime(sunrise.toLong())
                    binding.sunsetTime.text = setTime(sunset.toLong())
                    binding.pressurebox.text = seaLevel+" hPa"
                    binding.conditionBox.text=condition
                    binding.dayBox.text=dayName(System.currentTimeMillis())
                    binding.dateBox.text=date()
                    binding.cityBox.text = "${cityName}"


                    changeBackgrounds(condition)
                }
            }




            override fun onFailure(call: Call<weatherData?>, t: Throwable) {

            }
        })


    }
    fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun date() : String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun changeBackgrounds(conditions: String) {
        when (conditions) {
            "Clear Sky", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Light Rain","Thunderstorm" ,"Drizzle", "Moderate Rain", "Showers", "Heavy Rain","Rain" -> {
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

    fun setTime(timestamp: Long): String {
        val date = Date(timestamp * 1000) // Convert seconds to milliseconds if needed
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(date)
    }

}


