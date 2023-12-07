package com.app.mybase.views.weather.adaptor

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.mybase.R
import com.app.mybase.databinding.ForecastCardViewBinding
import com.app.mybase.helper.Utils
import com.app.mybase.model.WeatherDataModel
import kotlin.math.roundToInt

class ForecastListAdapter(
    private var context: Context
) : RecyclerView.Adapter<ForecastListAdapter.ViewHolder>() {

    var forecastList = ArrayList<WeatherDataModel>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateForecastList(
        forecastList: ArrayList<WeatherDataModel>
    ) {
        this.forecastList.clear()
        this.forecastList.addAll(forecastList)
        notifyDataSetChanged()
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = DataBindingUtil.inflate<ForecastCardViewBinding>(
            LayoutInflater.from(parent.context),
            R.layout.forecast_card_view,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.timeText.text = Utils.convertToTime(forecastList[position].dt_txt)
        holder.degreeText.text =
            Utils.kelvinToCelsius(forecastList[position].main.temp).roundToInt().toString()
        setImage(forecastList[position].weather[0].main, holder.climateImg)
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return forecastList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(itemView: ForecastCardViewBinding) : RecyclerView.ViewHolder(itemView.root) {
        var timeText: TextView = itemView.timeText
        var degreeText: TextView = itemView.degreeText
        var climateImg: ImageView = itemView.climateImg
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setImage(main: String, climateImg: ImageView) {
        when (main) {
            "Thunderstorm", "Drizzle", "Rain" -> {
                Utils.setLocalImage(
                    context.applicationContext,
                    context.getDrawable(R.drawable.rain),
                    climateImg
                )
            }
            "Snow" -> {
                Utils.setLocalImage(
                    context.applicationContext,
                    context.getDrawable(R.drawable.snowy),
                    climateImg
                )
            }
            "Clouds" -> {
                Utils.setLocalImage(
                    context.applicationContext,
                    context.getDrawable(R.drawable.cloudy),
                    climateImg
                )
            }
            "Clear" -> {
                Utils.setLocalImage(
                    context.applicationContext,
                    context.getDrawable(R.drawable.sun),
                    climateImg
                )
            }
            else -> {
                Utils.setLocalImage(
                    context.applicationContext,
                    context.getDrawable(R.drawable.cloudy),
                    climateImg
                )
            }
        }
    }

}