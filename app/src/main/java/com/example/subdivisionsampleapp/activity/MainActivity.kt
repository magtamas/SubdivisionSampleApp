package com.example.subdivisionsampleapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bosphere.verticalslider.VerticalSlider
import com.example.subdivisionsampleapp.R
import com.example.subdivisionsampleapp.databinding.ActivityMainBinding
import com.example.subdivisionsampleapp.utils.round
import kotlin.math.round

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

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
        setWeightLabel(binding.canvasContainer.getPrimaryWeight())
    }

    private fun setWeightLabel(weight: Float) {
        binding.weightLabel.text =
            this.resources.getString(
                R.string.weight,
                weight.round(3)
            )
    }

    private fun getSliderChangeListener(): VerticalSlider.OnProgressChangeListener {
        return VerticalSlider.OnProgressChangeListener { progress ->
            setWeightLabel(progress)
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