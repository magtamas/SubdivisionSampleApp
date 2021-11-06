package com.example.subdivisionsampleapp.opengl.gl_model

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.opengles.GL10

class GLCube() {

    private val vertices: FloatArray = floatArrayOf(
        1f, 1f, -1f, //point0 - topFrontRight
        1f, -1f, -1f, //point1 - bottomFrontRight
        -1f, -1f, -1f, //point2 - bottomFrontLeft
        -1f, 1f, -1f, //point3 - topFrontLeft
        1f, 1f, 1f, //point4 - topBackRight
        1f, -1f, 1f, //point5 - bottomBackRight
        -1f, -1f, 1f, //point6 - bottomBackLeft
        -1f, 1f, 1f, //point7 - topBackLeft
    )

    private var vertBuffer: FloatBuffer
    private var pointIndex: ShortArray = shortArrayOf(
        3, 4, 0,    0, 4, 1,    3, 0, 1,
        3, 7, 4,    7, 6, 4,    7, 3, 6,
        3, 1, 2,    1, 6, 2,    6, 3, 2,
        1, 4, 5,    5, 6, 1,    6, 5, 4
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
            glVertexPointer(3, GL10.GL_FLOAT, 0, vertBuffer)
            glDrawElements(GL10.GL_TRIANGLES, pointIndex.size, GL10.GL_UNSIGNED_SHORT, pointBuffer)
            glDisableClientState(GL10.GL_VERTEX_ARRAY)
            glDisable(GL10.GL_CULL_FACE)
        }
    }

}