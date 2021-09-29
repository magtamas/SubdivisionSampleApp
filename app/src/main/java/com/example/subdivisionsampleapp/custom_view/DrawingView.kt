package com.example.subdivisionsampleapp.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import android.view.MotionEvent
import com.example.subdivisionsampleapp.R
import com.example.subdivisionsampleapp.model.CanvasPoint
import android.view.ScaleGestureDetector
import java.lang.Thread.sleep

class DrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    val paint: Paint = Paint()
    private var pointList = mutableListOf<CanvasPoint>()
    private var currentSubdivisionPointList = mutableListOf<CanvasPoint>()
    private var originalPointList = mutableListOf<CanvasPoint>()
    private var finalizedPoints = false
    private var subdivisionIsStarted = false

    private var PRIMARY_WEIGHT: Float = 4f/6f
    private var SECOND_WEIGHT: Float = -(1f/6f)

    init {
        paint.apply {
            style = Paint.Style.FILL
        }
    }

    fun finishPointAddition() {
        finalizedPoints = true
        originalPointList = pointList.toMutableList()
        requestLayout()
    }

    fun restartPointAddition() {
        pointList.clear()
        finalizedPoints = false
        requestLayout()
    }

    fun restartSubdivision() {
        pointList = originalPointList.toMutableList()
        requestLayout()
    }

    fun startSubdivision() {
        subdivisionIsStarted = true
        calculateNewPoints()
    }

    fun getPrimaryWeight(): Float {
        return PRIMARY_WEIGHT
    }

    fun changeWeights(percent: Float) {
        PRIMARY_WEIGHT = percent
        SECOND_WEIGHT = 0.5f - PRIMARY_WEIGHT
        refreshNewPoints()
    }

    private fun refreshNewPoints() {
        val newPointList = currentSubdivisionPointList.toMutableList()
        for((index, point) in currentSubdivisionPointList.withIndex()) {
            val newPointCoordinates = when(index) {
                0 -> {
                    calculateCoordinates(listOf(
                        point,
                        currentSubdivisionPointList[index + 1],
                        currentSubdivisionPointList[index + 2],
                        currentSubdivisionPointList.last()
                    ))
                }
                currentSubdivisionPointList.lastIndex - 1 -> {
                    calculateCoordinates(listOf(
                        point,
                        currentSubdivisionPointList.last(),
                        currentSubdivisionPointList[1],
                        currentSubdivisionPointList[index - 1]
                    ))
                }
                currentSubdivisionPointList.lastIndex -> {
                    calculateCoordinates(listOf(
                        point,
                        currentSubdivisionPointList.first(),
                        currentSubdivisionPointList[1],
                        currentSubdivisionPointList[index - 1]
                    ))
                }
                else -> {
                    calculateCoordinates(listOf(
                        point,
                        currentSubdivisionPointList[index + 1],
                        currentSubdivisionPointList[index + 2],
                        currentSubdivisionPointList[index - 1]
                    ))
                }
            }
            newPointList.add(
                2 * index + 1,
                CanvasPoint(
                    xPosition = newPointCoordinates.first,
                    yPosition = newPointCoordinates.second
                )
            )
        }
        pointList = newPointList.toMutableList()
        requestLayout()
    }

    private fun calculateNewPoints() {
        val newPointList = pointList.toMutableList()
        for((index, point) in pointList.withIndex()) {
            val newPointCoordinates = when(index) {
                0 -> {
                    calculateCoordinates(listOf(
                        point,
                        pointList[index + 1],
                        pointList[index + 2],
                        pointList.last()
                    ))
                }
                pointList.lastIndex - 1 -> {
                    calculateCoordinates(listOf(
                        point,
                        pointList.last(),
                        pointList[1],
                        pointList[index - 1]
                    ))
                }
                pointList.lastIndex -> {
                    calculateCoordinates(listOf(
                        point,
                        pointList.first(),
                        pointList[1],
                        pointList[index - 1]
                    ))
                }
                else -> {
                    calculateCoordinates(listOf(
                        point,
                        pointList[index + 1],
                        pointList[index + 2],
                        pointList[index - 1]
                    ))
                }
            }
            newPointList.add(
                2 * index + 1,
                CanvasPoint(
                    xPosition = newPointCoordinates.first,
                    yPosition = newPointCoordinates.second
                )
            )
        }
        currentSubdivisionPointList = pointList.toMutableList()
        pointList = newPointList.toMutableList()
        requestLayout()
    }

    private fun calculateCoordinates(
        points: List<CanvasPoint>
    ): Pair<Float, Float> {
        var newX = 0f
        var newY = 0f
        for((index, point) in points.withIndex()) {
            when(index) {
                0,1 -> {
                    newX += point.xPosition * PRIMARY_WEIGHT
                    newY += point.yPosition * PRIMARY_WEIGHT
                }
                2,3 -> {
                    newX += point.xPosition * SECOND_WEIGHT
                    newY += point.yPosition * SECOND_WEIGHT
                }
            }
        }

        return (newX to newY)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for((index, point) in pointList.withIndex()) {
            /*drawCircle(
                canvas = canvas,
                xPosition = point.xPosition,
                yPosition = point.yPosition
            )*/
            if(index != 0) {
                drawLine(
                    canvas = canvas,
                    startX = pointList[index - 1].xPosition,
                    startY = pointList[index - 1].yPosition,
                    toX = point.xPosition,
                    toY = point.yPosition,
                )
            }
            if(index == pointList.lastIndex && finalizedPoints) {
                drawLine(
                    canvas = canvas,
                    startX = pointList.first().xPosition,
                    startY = pointList.first().yPosition,
                    toX = pointList.last().xPosition,
                    toY = pointList.last().yPosition,
                )
            }
        }
    }

    private fun drawCircle(
        canvas: Canvas?,
        xPosition: Float,
        yPosition: Float
    ) {
        canvas?.drawCircle(
            xPosition,
            yPosition,
            10f,
            paint.apply {
                color = ContextCompat.getColor(context, R.color.colorRed)
            }
        )
    }

    private fun drawLine(
        canvas: Canvas?,
        startX: Float,
        startY: Float,
        toX: Float,
        toY: Float,
    ) {
        canvas?.drawLine(
            startX,
            startY,
            toX,
            toY,
            paint.apply {
                strokeWidth = 5f
                isAntiAlias = true
                color = ContextCompat.getColor(context, R.color.colorBlue)
            }
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pointList.add(
                    CanvasPoint(
                        xPosition = x,
                        yPosition = y
                    )
                )
                invalidate()
            }
        }
        return true
    }
}