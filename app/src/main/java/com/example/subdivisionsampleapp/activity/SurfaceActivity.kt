package com.example.subdivisionsampleapp.activity

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.subdivisionsampleapp.databinding.ActivityMainBinding
import com.example.subdivisionsampleapp.databinding.ActivitySurfaceBinding
import com.example.subdivisionsampleapp.opengl.GLCustomRenderer

class SurfaceActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySurfaceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurfaceBinding.inflate(layoutInflater)
        binding.surfaceView.setRenderer(GLCustomRenderer())
        setContentView(binding.root)
    }

    override fun onPause() {
        super.onPause()
        binding.surfaceView.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.surfaceView.onResume()
    }
}