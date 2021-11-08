package com.example.subdivisionsampleapp.opengl.gl_utils

import android.util.Log
import com.example.subdivisionsampleapp.opengl.gl_model.Line
import com.example.subdivisionsampleapp.opengl.gl_model.Triangle

object LineUtils {
    fun getCommonPointIndex(l1: Line, l2: Line): Int {
        var commonIndex = 0
        when {
            l1.firstVertexIndex == l2.firstVertexIndex -> {
                commonIndex = l1.firstVertexIndex
            }
            l1.secondVertexIndex == l2.secondVertexIndex -> {
                commonIndex = l1.secondVertexIndex
            }
            l1.firstVertexIndex == l2.secondVertexIndex -> {
                commonIndex = l1.firstVertexIndex
            }
            l1.secondVertexIndex == l2.firstVertexIndex -> {
                commonIndex = l1.secondVertexIndex
            }
        }
        return commonIndex
    }
}

object VertexHolder {
    var verticesTEST = mutableListOf<Float>()

    var triangleListTEST = mutableListOf<Triangle>()
    var test = 0
}