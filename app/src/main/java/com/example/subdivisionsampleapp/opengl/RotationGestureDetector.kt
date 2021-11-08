package com.example.subdivisionsampleapp.opengl

import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent


enum class Axis {
    X,
    Y
}

class RotationGestureDetector(private val mListener: OnRotationGestureListener?) {

    private val SWIPE_THRESHOLD = 100
    private val SWIPE_VELOCITY_THRESHOLD = 20

    private var fX = 0f
    private var fY = 0f
    private var sX = 0f
    private var sY = 0f
    private var ptrID1: Int
    var angle = 0f
        private set

    var axis: Axis = Axis.X

    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                ptrID1 = event.getPointerId(event.actionIndex)
                sX = event.getX(event.findPointerIndex(ptrID1))
                sY = event.getY(event.findPointerIndex(ptrID1))
            }
            MotionEvent.ACTION_MOVE -> if (ptrID1 != INVALID_POINTER_ID) {
                try {
                    val nsX: Float = event.getX(event.findPointerIndex(ptrID1))
                    val nsY: Float = event.getY(event.findPointerIndex(ptrID1))
                    angle = angleBetweenLines(fX, fY, sX, sY, nsX, nsY)
                    mListener?.OnRotation(this)
                } catch (ex: Exception) { }
            }
            MotionEvent.ACTION_UP -> ptrID1 = INVALID_POINTER_ID
            MotionEvent.ACTION_CANCEL -> {
                ptrID1 = INVALID_POINTER_ID
            }
            else -> {

            }
        }
        return true
    }

    private fun angleBetweenLines(
        fX: Float,
        fY: Float,
        sX: Float,
        sY: Float,
        nsX: Float,
        nsY: Float
    ): Float {

        try {
            val diffY = sY - nsY
            val diffX = sX - nsX
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD) {
                    if (diffX > 0) {
                        angle = (nsX - sX) / 2
                        axis = Axis.X
                        //onSwipeRight()
                    } else {
                        angle = (nsX - sX) / 2
                        axis = Axis.X
                        //onSwipeLeft()
                    }
                }
            } else if (Math.abs(diffY) > SWIPE_THRESHOLD) {
                if (diffY > 0) {
                    angle = (nsY - sY) / 2
                    axis = Axis.Y
                } else {
                    angle = (nsY - sY) / 2
                    axis = Axis.Y
                    //onSwipeTop()
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }


        return angle
    }

    interface OnRotationGestureListener {
        fun OnRotation(rotationDetector: RotationGestureDetector?)
    }

    companion object {
        private const val INVALID_POINTER_ID = -1
    }

    init {
        ptrID1 = INVALID_POINTER_ID
    }
}