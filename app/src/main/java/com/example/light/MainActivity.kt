package com.example.light

import android.graphics.Color.*
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import okio.IOException
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .callTimeout(3, TimeUnit.SECONDS)
        .build()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hueBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                hue_label.text = getString(R.string.HueWithValue, i)
                hue_label.setTextColor(HSVToColor(floatArrayOf(i.toFloat(), 1F, 1F)))
            }

            override fun onStartTrackingTouch(seek: SeekBar) {}
            override fun onStopTrackingTouch(seek: SeekBar) {}
        })

        valueBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                value_label.text = getString(R.string.ValueWithValue, i)
            }

            override fun onStartTrackingTouch(seek: SeekBar) {}
            override fun onStopTrackingTouch(seek: SeekBar) {}
        })

        whiteValueBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                white_value_label.text = getString(R.string.WhiteValueWithValue, i)
            }

            override fun onStartTrackingTouch(seek: SeekBar) {}
            override fun onStopTrackingTouch(seek: SeekBar) {}
        })

        set_button.setOnClickListener {
            val hsvHue = hueBar.progress.toFloat()
            val hsvValue = valueBar.progress.toFloat() * (1F / 128F)
            val whiteValue = whiteValueBar.progress
            val colour = HSVToColor(floatArrayOf(hsvHue, 1F, hsvValue))
            val colourString =
                "%02x%02x%02x%02x".format(red(colour), green(colour), blue(colour), whiteValue)

            val url = HttpUrl.Builder()
                .scheme("http")
                .host("192.168.0.13")
                .addPathSegment("setLEDState")
                .addQueryParameter("colour", colourString)
                .addQueryParameter("action", "1")
                .build()

            val request = Request.Builder().url(url).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    this@MainActivity.runOnUiThread(Runnable {
                        val succToast = Toast.makeText(
                            this@MainActivity,
                            response.body?.string(),
                            Toast.LENGTH_SHORT
                        )
                        succToast.show()
                    })
                }
            })

        }

    }

}
