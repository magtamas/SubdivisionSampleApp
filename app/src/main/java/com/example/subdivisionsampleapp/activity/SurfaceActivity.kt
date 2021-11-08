package com.example.subdivisionsampleapp.activity

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.example.subdivisionsampleapp.databinding.ActivitySurfaceBinding
import com.example.subdivisionsampleapp.opengl.GLCustomRenderer
import com.example.subdivisionsampleapp.opengl.RotationGestureDetector


class SurfaceActivity : AppCompatActivity(), RotationGestureDetector.OnRotationGestureListener {

    private lateinit var binding: ActivitySurfaceBinding

    private var rotationDetector = RotationGestureDetector(this)
    private var renderer = GLCustomRenderer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurfaceBinding.inflate(layoutInflater)
        binding.surfaceView.setRenderer(renderer)
        setContentView(binding.root)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        binding.startButton.setOnClickListener {
            renderer.startRotate()
        }

        binding.restartButton.setOnClickListener {
            renderer.startSubdivision()
        }

        Log.d("tag","LOGMAG WIDTH: " + width)
        Log.d("tag","LOGMAG HEIGHT: " + height)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let { rotationDetector.onTouchEvent(it) }
        return super.onTouchEvent(event)
    }

    override fun onPause() {
        super.onPause()
        binding.surfaceView.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.surfaceView.onResume()
    }

    override fun OnRotation(rotationDetector: RotationGestureDetector?) {
        rotationDetector?.angle?.let {
            renderer.rotate(it, rotationDetector.axis)
        }
    }
}