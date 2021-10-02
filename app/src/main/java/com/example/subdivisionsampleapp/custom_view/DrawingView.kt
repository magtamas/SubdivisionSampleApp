package com.example.subdivisionsampleapp.custom_view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import com.example.subdivisionsampleapp.R
import com.example.subdivisionsampleapp.model.CanvasPoint
import kotlin.reflect.KProperty


class DrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    val paint: Paint = Paint()
    val path = Path()
    private var pointList = mutableListOf<CanvasPoint>()
    private var currentSubdivisionPointList = mutableListOf<CanvasPoint>()
    private var originalPointList = mutableListOf<CanvasPoint>()
    private var finalizedPoints = false
    private var subdivisionIsStarted = false
    private var subdivisionIsDone = false

    /*private var PRIMARY_WEIGHT: Float = 4f/6f
    private var SECOND_WEIGHT: Float = -(1f/6f)*/
    private var PRIMARY_WEIGHT: Float = 23f/40f
    private var SECOND_WEIGHT: Float = -(3f/40f)

    fun finishPointAddition() {
        finalizedPoints = true
        originalPointList = pointList.toMutableList()
        startLineDrawing()
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
        startSubdivisionDrawing()
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

    private var xPathPosition: Float = 0f
        set(value) {
            field = value
        }

    private var yPathPosition: Float = 0f
        set(value) {
            field = value
            path.lineTo(xPathPosition, yPathPosition)
            invalidate()
        }

    val animPath = Path()
    private fun startSubdivisionDrawing() {
        animPath.reset()
        while(pointList.any { !it.isAlreadyAnimated }) {
            pointList.find { !it.isAlreadyAnimated }?.let { currentPoint ->
                val currentIndex = pointList.indexOf(currentPoint)
                if(currentIndex % 2 != 0) {
                    animPath.moveTo(pointList[currentIndex - 1].xPosition, pointList[currentIndex - 1].yPosition)
                }
                animPath.lineTo(currentPoint.xPosition, currentPoint.yPosition)
                if(currentIndex + 1 < pointList.size) {
                    animPath.lineTo(pointList[currentIndex + 1].xPosition, pointList[currentIndex + 1].yPosition)
                } else {
                    animPath.lineTo(pointList.first().xPosition, pointList.first().yPosition)
                }
                currentPoint.isAlreadyAnimated = true
            }
        }
        path.reset()
        this.path.moveTo(pointList[0].xPosition, pointList[0].yPosition)
        startPathAnim(animPath)
    }

    private fun startLineDrawing() {
        if(finalizedPoints) {
            val path = Path()
            path.moveTo(
                pointList.last().xPosition,
                pointList.last().yPosition
            )
            path.lineTo(
                pointList.first().xPosition,
                pointList.first().yPosition
            )
            pointList.first().isAlreadyAnimated = true
            pointList.last().isAlreadyAnimated = true
            startPathAnim(path)
        } else {
            pointList.findLast { !it.isAlreadyAnimated }?.let { currentPoint ->
                val currentIndex = pointList.indexOf(currentPoint)
                if(currentIndex == 0) {
                    this.path.moveTo(pointList[0].xPosition, pointList[0].yPosition)
                    return
                }
                val path = Path()
                path.moveTo(pointList[currentIndex - 1].xPosition, pointList[currentIndex - 1].yPosition)
                path.lineTo(currentPoint.xPosition, currentPoint.yPosition)
                currentPoint.isAlreadyAnimated = true
                startPathAnim(path)
            }
        }
    }

    private fun startPathAnim(path: Path) {
        ObjectAnimator.ofFloat(this, ::xPathPosition.name, ::yPathPosition.name, path).apply {
            duration = 1000
            start()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawPath(path, paint.apply {
            strokeWidth = 5f
            isAntiAlias = true
            color = ContextCompat.getColor(context, R.color.colorBlue)
            style = Paint.Style.STROKE
        })
        for(point in pointList) {
            if(!finalizedPoints) {
                drawCircle(
                    canvas = canvas,
                    xPosition = point.xPosition,
                    yPosition = point.yPosition
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
                style = Paint.Style.FILL
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
                if(!finalizedPoints) {
                    pointList.add(
                        CanvasPoint(
                            xPosition = x,
                            yPosition = y
                        )
                    )
                    startLineDrawing()
                }
                invalidate()
            }
        }
        return true
    }
}