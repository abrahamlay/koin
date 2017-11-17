package org.koin.sampleapp.view.main

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.contextaware.ContextAwareActivity
import org.koin.android.ext.android.bindProperty
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.property
import org.koin.android.ext.android.releaseProperties
import org.koin.sampleapp.R
import org.koin.sampleapp.di.WeatherModule
import org.koin.sampleapp.view.weather.PROPERTY_WEATHER_DATE
import org.koin.sampleapp.view.weather.PROPERTY_WEATHER_DETAIL
import org.koin.sampleapp.view.weather.WeatherResultActivity

/**
 * Weather View
 */
class MainActivity : ContextAwareActivity(WeatherModule.CTX_WEATHER_ACTIVITY), MainContract.View {

    // Presenter
    override val presenter by inject<MainContract.Presenter>()

    // Get last address or default
    private val defaultAddress by property(PROPERTY_ADDRESS, "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchEditText.setText(defaultAddress)
        searchEditText.setOnFocusChangeListener { _, _ ->
            // save address
            bindProperty(PROPERTY_ADDRESS, searchText())
        }

        // Start search weather
        searchButton.setOnClickListener {
            presenter.getWeather(searchText())
        }

        // release any of those properties (if previously used)
        releaseProperties(PROPERTY_WEATHER_DATE, PROPERTY_WEATHER_DETAIL)
    }

    private fun searchText() = searchEditText.text.trim().toString()

    override fun onResume() {
        super.onResume()
        presenter.view = this
    }

    override fun onPause() {
        presenter.stop()
        super.onPause()
    }

    override fun displayNormal() {
        searchProgress.visibility = View.GONE
        searchButton.visibility = View.VISIBLE
    }

    override fun displayProgress() {
        searchProgress.visibility = View.VISIBLE
        searchButton.visibility = View.GONE
    }

    override fun onWeatherSuccess() {
        // save address
        bindProperty(PROPERTY_ADDRESS, searchText())
        startActivity(Intent(this, WeatherResultActivity::class.java))
    }

    override fun onWeatherFailed(error: Throwable) {

        Snackbar.make(this.currentFocus, "Got error : $error", Snackbar.LENGTH_LONG).show()
    }
}