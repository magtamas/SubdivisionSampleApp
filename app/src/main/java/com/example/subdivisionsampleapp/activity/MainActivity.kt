package com.example.subdivisionsampleapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bosphere.verticalslider.VerticalSlider
import com.example.subdivisionsampleapp.custom_view.DrawingView
import com.example.subdivisionsampleapp.databinding.ActivityMainBinding
import com.example.subdivisionsampleapp.model.CanvasPoint
import kotlin.math.round

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var pointList: MutableList<CanvasPoint> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.finishButton.setOnClickListener(getFinishButtonAction())
        binding.startButton.setOnClickListener(getStartButtonAction())
        binding.resetButton.setOnClickListener(getResetButtonAction())
        binding.restartButton.setOnClickListener(getRestartButtonAction())
        binding.weightSlider.setOnSliderProgressChangeListener(getSliderChangeListener())
        binding.weightSlider.setProgress(
            binding.canvasContainer.getPrimaryWeight()
        )
    }

    private fun getSliderChangeListener(): VerticalSlider.OnProgressChangeListener {
        return VerticalSlider.OnProgressChangeListener { progress ->
            /*if((progress % 0.1f).round(2) == 0f) {
                binding.canvasContainer.changeWeights(progress)
            }*/
            binding.canvasContainer.changeWeights(progress)
        }
    }

    private fun getFinishButtonAction(): View.OnClickListener {
        return View.OnClickListener {
            binding.canvasContainer.finishPointAddition()
        }
    }

    private fun getStartButtonAction(): View.OnClickListener {
        return View.OnClickListener {
            binding.canvasContainer.startSubdivision()
        }
    }

    private fun getResetButtonAction(): View.OnClickListener {
        return View.OnClickListener {
            binding.canvasContainer.restartPointAddition()
        }
    }

    private fun getRestartButtonAction(): View.OnClickListener {
        return View.OnClickListener {
            binding.canvasContainer.restartSubdivision()
        }
    }

}

fun Float.round(decimals: Int): Float {
    var multiplier = 1.0f
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}