package com.example.subdivisionsampleapp.opengl

import android.opengl.GLSurfaceView
import android.opengl.GLU
import android.os.SystemClock
import com.example.subdivisionsampleapp.opengl.gl_model.GLCube
import com.example.subdivisionsampleapp.opengl.gl_model.GLTriangle
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLCustomRenderer: GLSurfaceView.Renderer {

    private var triangle: GLTriangle = GLTriangle()
    private var cube: GLCube = GLCube()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        gl?.apply {
            glDisable(GL10.GL_DITHER)
            glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST)
            glClearColor(0.8f,0f,0.2f,1f)
            glClearDepthf(1f)
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        gl?.apply {
            glViewport(0,0, width, height)
            glMatrixMode(GL10.GL_PROJECTION)
            glLoadIdentity()
            (width.toFloat()/height.toFloat()).let { ratio ->
                glFrustumf(-ratio, ratio, -1f, 0.5f, 1f, 25f)
            }
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        gl?.apply {
            glDisable(GL10.GL_DITHER)
            glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)
            glMatrixMode(GL10.GL_MODELVIEW)
            glLoadIdentity()

            //CAMERA LOOK AT
            GLU.gluLookAt(gl, 0f,0f,-5f,0f,0f,0f,0f,2f,0f)

            /*val time = SystemClock.uptimeMillis() % 4000L
            val angle = 0.090f * time.toInt()

            glRotatef(angle, 1f, 0f, 0f)
            glRotatef(angle, 0f, 0f, 1f)*/

            triangle.draw(gl)
        }
    }
}