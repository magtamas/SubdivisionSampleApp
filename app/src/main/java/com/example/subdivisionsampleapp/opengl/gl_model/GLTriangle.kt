package com.example.subdivisionsampleapp.opengl.gl_model

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.opengles.GL10

class GLTriangle() {

    private val colorValues: FloatArray = floatArrayOf(
        1f, 1f, 0f, 0.5f,
        0.25f, 0f, 0.85f, 1f,
        0f, 1f, 1f, 1f,
    )

    private val vertices: FloatArray = floatArrayOf(
        0f, 1f, //point0
        1f, -1f, //point1
        -1f, -1f //point2
    )

    private var vertBuffer: FloatBuffer
    private var colorBuffer: FloatBuffer
    private var pointIndex: ShortArray = shortArrayOf(0,1,2)
    private var pointBuffer: ShortBuffer

    init {
        ByteBuffer.allocateDirect(vertices.size * 4)
            .apply {
                order(ByteOrder.nativeOrder())
            }
            .let {
                vertBuffer = it.asFloatBuffer()
            }
        vertBuffer.put(vertices)
        vertBuffer.position(0)

        ByteBuffer.allocateDirect(colorValues.size * 4)
            .apply {
                order(ByteOrder.nativeOrder())
            }
            .let {
                colorBuffer = it.asFloatBuffer()
            }
        colorBuffer.put(colorValues)
        colorBuffer.position(0)

        ByteBuffer.allocateDirect(pointIndex.size * 2)
            .apply {
                order(ByteOrder.nativeOrder())
            }
            .let {
                pointBuffer = it.asShortBuffer()
            }
        pointBuffer.put(pointIndex)
        pointBuffer.position(0)
    }

    fun draw(gl: GL10) {
        gl.apply {
            glFrontFace(GL10.GL_CW)
            glEnableClientState(GL10.GL_VERTEX_ARRAY)
            glEnableClientState(GL10.GL_COLOR_ARRAY)
            glVertexPointer(2, GL10.GL_FLOAT, 0, vertBuffer)
            glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer)
            glDrawElements(GL10.GL_TRIANGLES, pointIndex.size, GL10.GL_UNSIGNED_SHORT, pointBuffer)
            glDisableClientState(GL10.GL_COLOR_ARRAY)
            glDisableClientState(GL10.GL_VERTEX_ARRAY)
        }
    }

}