package com.example.subdivisionsampleapp.opengl.gl_utils

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

fun FloatArray.toBuffer(): FloatBuffer {
    ByteBuffer.allocateDirect(this.size * 4)
        .apply {
            order(ByteOrder.nativeOrder())
        }
        .let { byteBuffer ->
            val floatBuffer = byteBuffer.asFloatBuffer()
            floatBuffer.put(this)
            floatBuffer.position(0)
            return floatBuffer
        }
}