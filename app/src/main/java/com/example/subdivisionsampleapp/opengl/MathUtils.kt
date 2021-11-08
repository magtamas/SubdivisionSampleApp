package com.example.subdivisionsampleapp.opengl

import com.example.subdivisionsampleapp.opengl.gl_model.Point
import kotlin.math.pow
import kotlin.math.sqrt

object MathUtils {
    fun distance(p1: Point, p2: Point): Float {
        val sum = (p1.x + p2.x).toDouble().pow(2.0) +
        (p1.y + p2.y).toDouble().pow(2.0) +
        (p1.z + p2.z).toDouble().pow(2.0)

        return sqrt(sum).toFloat()
    }
}