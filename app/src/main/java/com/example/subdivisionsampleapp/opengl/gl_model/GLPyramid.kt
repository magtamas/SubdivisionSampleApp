package com.example.subdivisionsampleapp.opengl.gl_model

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.opengles.GL10

class GLPyramid() {

    /*private val colorValues: FloatArray = floatArrayOf(
        1f, 0f, 0f, 0f, //point0 color
        0f, 0f, 1f, 0f, //point1 color
        0f, 0f, 1f, 0f, //point2 color
        1f, 0f, 0f, 0f, //point3 color
    )*/

    private val colorValues: FloatArray = floatArrayOf(
        1f, 1f, 1f, 0f, //point0 color
        1f, 1f, 1f, 0f, //point1 color
        1f, 1f, 1f, 0f, //point2 color
        1f, 1f, 1f, 0f, //point3 color
    )

    /*private val vertices: FloatArray = floatArrayOf(
        0f, 1f, 0f, //point0 - topFrontRight 0
        0f, -1f, 1f, //point1 - bottomFrontRight 1
        1f, -1f, -1f, //point5 - bottomBackRight 2
        -1f, -1f, -1f, //point6 - bottomBackLeft 3
    )*/

    private val vertices: FloatArray = floatArrayOf(
        1f, 0f, 0f,
        0f, 1f, 0f,
        0f, 0f, 1f,
        0f, 0f, 0f
    )

    private var vertBuffer: FloatBuffer
    private var colorBuffer: FloatBuffer
    private var pointIndex: ShortArray = shortArrayOf(
        1, 3, 2,
        3, 1, 0,
        2, 0, 1,
        0, 2, 3
    )
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
            glEnable(GL10.GL_CULL_FACE)
            glCullFace(GL10.GL_BACK)
            glEnableClientState(GL10.GL_VERTEX_ARRAY)
            glEnableClientState(GL10.GL_COLOR_ARRAY)
            glVertexPointer(3, GL10.GL_FLOAT, 0, vertBuffer)
            glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer)
            glDrawElements(GL10.GL_TRIANGLES, pointIndex.size, GL10.GL_UNSIGNED_SHORT, pointBuffer)
            glDisableClientState(GL10.GL_COLOR_ARRAY)
            glDisableClientState(GL10.GL_VERTEX_ARRAY)
            glDisable(GL10.GL_CULL_FACE)
        }
    }

}