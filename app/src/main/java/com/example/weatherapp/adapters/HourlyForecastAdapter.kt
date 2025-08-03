package com.example.weatherapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.models.HourlyForecastModel

class HourlyForecastAdapter(private val context: Context, private val hourlyList: List<HourlyForecastModel>) :
    RecyclerView.Adapter<HourlyForecastAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_hourly_forecast, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hourlyItem = hourlyList[position]
        
        holder.timeTextView.text = hourlyItem.time
        holder.tempTextView.text = hourlyItem.temperature
        holder.precipTextView.text = hourlyItem.precipitationProbability
        
        // Load weather icon using Glide
        val iconUrl = "https://openweathermap.org/img/wn/${hourlyItem.icon}@2x.png"
        Glide.with(context)
            .load(iconUrl)
            .into(holder.iconImageView)
    }

    override fun getItemCount(): Int = hourlyList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeTextView: TextView = itemView.findViewById(R.id.tvHourlyTime)
        val iconImageView: ImageView = itemView.findViewById(R.id.ivHourlyIcon)
        val tempTextView: TextView = itemView.findViewById(R.id.tvHourlyTemp)
        val precipTextView: TextView = itemView.findViewById(R.id.tvHourlyPrecip)
    }
}