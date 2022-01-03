package com.example.subdivisionsampleapp.opengl

import android.opengl.GLSurfaceView
import android.opengl.GLU
import android.os.SystemClock
import com.example.subdivisionsampleapp.opengl.gl_model.GLCube
import com.example.subdivisionsampleapp.opengl.gl_model.GLDoublePyramid
import com.example.subdivisionsampleapp.opengl.gl_model.GLPyramid
import com.example.subdivisionsampleapp.opengl.gl_model.GLTriangle
import com.example.subdivisionsampleapp.opengl.gl_utils.VertexHolder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLCustomRenderer: GLSurfaceView.Renderer {

    private var triangle: GLTriangle = GLTriangle()
    private var cube: GLCube = GLCube()
    private var pyramid: GLPyramid = GLPyramid()
    private var doublePyramid: GLDoublePyramid = GLDoublePyramid()

    private val lightDiffuse = floatArrayOf(0.75f, 0.75f, 0.75f, 1f)
    private val lightPosition = floatArrayOf(-5f, -5f, 0f)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        gl?.apply {
            glDisable(GL10.GL_DITHER)
            glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST)
            glClearColor(0.83f,0.83f,0.83f,1f)
            glClearDepthf(1f)
            enableLight(this)
        }
    }

    private fun enableLight(gl: GL10) {
        if(false) {
            gl.apply {
                glDepthFunc(GL10.GL_LEQUAL)
                glEnable(GL10.GL_COLOR_MATERIAL)
                glEnable(GL10.GL_NORMALIZE)
                glEnable(GL10.GL_LIGHTING)
                glEnable(GL10.GL_LIGHT0)
                glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse, 0)
                glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPosition, 0)
            }
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        gl?.apply {
            glViewport(0,0, width, height)
            glMatrixMode(GL10.GL_PROJECTION)
            glLoadIdentity()
            (width.toFloat()/height.toFloat()).let { ratio ->
                glFrustumf(ratio, -ratio, -1f, 1f, 1f, 25f)
            }
        }
    }
    private var xAngle: Float = 0f
    private var yAngle: Float = 0f
    var axis: Axis = Axis.X

    private var currentAngle = 0f
    private var needRotate = true
    override fun onDrawFrame(gl: GL10?) {
        gl?.apply {
            glDisable(GL10.GL_DITHER)
            glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)
            glMatrixMode(GL10.GL_MODELVIEW)


            glLoadIdentity()

            //CAMERA LOOK AT
            GLU.gluLookAt(gl, 0f,0f,-5f,0f,0f,0f,0f,2f,0f)

            val time = SystemClock.uptimeMillis() % 4000L
            val angle = .09f * time.toInt()

            //glRotatef(20f,1f,0f,0f)


            glRotatef(yAngle, 1f, 0f, 0f)
            glRotatef(xAngle, 0f, 1f, 0f)
            /*if(needRotate) {
                currentAngle = angle
                glRotatef(angle, 1f, 1f, 0f)
            } else {
                glRotatef(currentAngle, 1f, 1f, 0f)
            }*/

            if(needRotate) {
                cube.draw(gl)
            } else {
                pyramid.draw(gl)
            }
        }
    }

    fun startSubdivision() {
        if(needRotate) {
            cube.startSubdivision()
        } else {
            pyramid.startSubdivision()
        }
    }

    fun startRotate() {
        if(needRotate) {
            pyramid = GLPyramid()
        } else {
            cube = GLCube()
        }
        needRotate = !needRotate
    }

    fun rotate(angle: Float, axis: Axis) {
        when(axis) {
            Axis.X -> {
                this.xAngle = angle
            }
            Axis.Y -> {
                this.yAngle = angle
            }
        }
        this.axis = axis
    }
}