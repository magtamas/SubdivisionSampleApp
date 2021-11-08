package com.example.subdivisionsampleapp.opengl.gl_model

import android.util.Log
import com.example.subdivisionsampleapp.opengl.gl_utils.LineUtils
import com.example.subdivisionsampleapp.opengl.gl_utils.VertexHolder
import com.example.subdivisionsampleapp.opengl.gl_utils.toBuffer
import java.lang.Exception
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.opengles.GL10

class GLCube() {

    private val colorValues: FloatArray = floatArrayOf(
        1f, 1f, 1f, 0f, //point0 color
        1f, 1f, 1f, 0f, //point1 color
        1f, 1f, 1f, 0f, //point2 color
        1f, 1f, 1f, 0f, //point3 color
        1f, 1f, 1f, 0f, //point4 color
        1f, 1f, 1f, 0f, //point5 color
        1f, 1f, 1f, 0f, //point6 color
        1f, 1f, 1f, 0f, //point7 color
    )

    private var vertices = mutableListOf<Float>(
        1f, 1f, -1f, //point0 - topFrontRight
        1f, -1f, -1f, //point1 - bottomFrontRight
        -1f, -1f, -1f, //point2 - bottomFrontLeft
        -1f, 1f, -1f, //point3 - topFrontLeft
        1f, 1f, 1f, //point4 - topBackRight
        1f, -1f, 1f, //point5 - bottomBackRight
        -1f, -1f, 1f, //point6 - bottomBackLeft
        -1f, 1f, 1f //point7 - topBackLeft
    )

    private var triangleList = mutableListOf<Triangle>(
        Triangle(3, 4, 0),
        Triangle(0, 4, 1),
        Triangle(3, 0, 1),
        Triangle(3, 7, 4),
        Triangle(7, 6, 4),
        Triangle(7, 3, 6),
        Triangle(3, 1, 2),
        Triangle(1, 6, 2),
        Triangle(6, 3, 2),
        Triangle(1, 4, 5),
        Triangle(5, 6, 1),
        Triangle(6, 5, 4)
    )
    private var linePointIndex: ShortArray = shortArrayOf(
        0,3,
        3,7,
        7,4,
        4,0,
        1,2,
        2,6,
        6,5,
        5,1,
        0,1,
        3,2,
        7,6,
        4,5
    )
    private var lineColorValues: FloatArray = floatArrayOf()

    private var vertBuffer: FloatBuffer = vertices.toFloatArray().toBuffer()
    private var colorBuffer: FloatBuffer = colorValues.toBuffer()
    private var pointBuffer: ShortBuffer = getPointIndexShortArray().toBuffer()
    private var linePointBuffer: ShortBuffer = linePointIndex.toBuffer()
    private var lineColorBuffer: FloatBuffer = lineColorValues.toBuffer()

    private fun getPointIndexShortArray(): ShortArray {
        val shortList = mutableListOf<Short>()
        for(triangle in triangleList) {
            shortList.add(triangle.firstVertexIndex.toShort())
            shortList.add(triangle.secondVertexIndex.toShort())
            shortList.add(triangle.thirdVertexIndex.toShort())
        }

        return shortList.toShortArray()
    }

    private fun getVertexIndex(currentLine: Line, triangleLine: Line): Int {
        if(triangleLine.firstVertexIndex != currentLine.firstVertexIndex
            && triangleLine.firstVertexIndex != currentLine.secondVertexIndex
        ) {
            return triangleLine.firstVertexIndex
        }
        if(triangleLine.secondVertexIndex != currentLine.firstVertexIndex
            && triangleLine.secondVertexIndex != currentLine.secondVertexIndex
        ) {
            return triangleLine.secondVertexIndex
        }

        return -1
    }

    private var newPointList = mutableListOf<Point>()

    init {
        generateNeighborhoods()
        calculateLines()
    }

    //HINT: ADD LINES
    fun calculateLines() {
        var testList = mutableListOf<Short>()
        for(triangle in triangleList) {
            triangle.provideLineList().forEach {
                testList.add(it.firstVertexIndex.toShort())
                testList.add(it.secondVertexIndex.toShort())
            }
        }
        linePointIndex = testList.toShortArray()
        linePointBuffer = linePointIndex.toBuffer()
        var testList2 = mutableListOf<Float>()
        for(triangle in triangleList) {
            val test = floatArrayOf(1f, 0f, 0f, 0f)
            test.forEach {
                testList2.add(it)
            }
        }
        lineColorValues = testList2.toFloatArray()
        lineColorBuffer = lineColorValues.toBuffer()
    }

    fun startSubdivision() {
        val VERTICES_LIST_SIZE = vertices.size / 3
        Log.d("tag","LOGMAG VERTICES_LIST_SIZE: " + VERTICES_LIST_SIZE)
        val doneLineList = mutableListOf<Line>()


        for(currentTriangle in triangleList) {
            for(currentLine in currentTriangle.provideLineList()) {

                if(doneLineList.contains(currentLine)) {
                    continue
                }

                val trianglesByLine = triangleList.filter {
                    it.provideLineList().contains(currentLine)
                }

                val indexOf1per2Edges = trianglesByLine[0].provideLineList().find {
                    it == currentLine
                }?.let {
                    listOf(
                        it.firstVertexIndex,
                        it.secondVertexIndex
                    )
                } ?: listOf()


                val indexOf1per8Edges = mutableListOf<Int>()
                for(triangleByLine in trianglesByLine) {
                    for(triangleLine in triangleByLine.provideLineList()) {
                        val vertexIndex = getVertexIndex(
                            triangleLine = triangleLine,
                            currentLine = currentLine
                        )
                        if(vertexIndex != -1) {
                            indexOf1per8Edges.add(vertexIndex)
                            break
                        }
                    }
                }

                val indexOfMinus1per16Edges = mutableListOf<Int>()
                trianglesByLine.forEach { rootTriangle ->
                    rootTriangle.getNeighborhoodList().forEach {
                        if(it.first != trianglesByLine[0] && it.first != trianglesByLine[1]) {
                            for(line in it.first.provideLineList()) {
                                if(line.firstVertexIndex != it.second.firstVertexIndex
                                    && line.firstVertexIndex != it.second.secondVertexIndex
                                ) {
                                    indexOfMinus1per16Edges.add(line.firstVertexIndex)
                                    break
                                }
                                if(line.secondVertexIndex != it.second.firstVertexIndex
                                    && line.secondVertexIndex != it.second.secondVertexIndex
                                ) {
                                    indexOfMinus1per16Edges.add(line.secondVertexIndex)
                                    break
                                }
                            }
                        }
                    }
                }

                var newX = 0f
                var newY = 0f
                var newZ = 0f

                indexOf1per2Edges.forEach {
                    newX += vertices[it * 3] * 1/2
                    newY += vertices[it * 3 + 1] * 1/2
                    newZ += vertices[it * 3 + 2] * 1/2
                }
                indexOf1per8Edges.forEach {
                    newX += vertices[it * 3] * 1/8
                    newY += vertices[it * 3 + 1] * 1/8
                    newZ += vertices[it * 3 + 2] * 1/8
                }
                indexOfMinus1per16Edges.forEach {
                    newX += vertices[it * 3] * -1/16
                    newY += vertices[it * 3 + 1] * -1/16
                    newZ += vertices[it * 3 + 2] * -1/16
                }

                val newPoint = Point(
                    x = newX,
                    y = newY,
                    z = newZ,
                    parentLine = currentLine
                )

                vertices.add(newX)
                vertices.add(newY)
                vertices.add(newZ)

                newPointList.add(newPoint)

                doneLineList.add(currentLine)
            }
        }

        val processedPoints = mutableListOf<Point>()
        val newTriangles = mutableListOf<Triangle>()
        for(currentNewPoint in newPointList) {
            processedPoints.add(currentNewPoint)

            val currentParentLine = currentNewPoint.parentLine
            val triangleListOfCurrentNewPoint = triangleList.filter {
                it.provideLineList().contains(currentParentLine)
            }

            for(triangle in triangleListOfCurrentNewPoint) {
                val newPointsInCurrentTriangle = newPointList.filter {
                    triangle.provideLineList().contains(it.parentLine)
                            && !processedPoints.contains(it)
                }

                newPointList.forEach {
                    if(triangle.provideLineList().contains(it.parentLine)) {
                        (newPointList.indexOf(it) + 8)
                    }
                }

                val newVertexIndexList = mutableListOf<Int>()
                newPointsInCurrentTriangle.forEach {
                    newVertexIndexList.add(newPointList.indexOf(it) + VERTICES_LIST_SIZE)
                }

                newTriangles.add(
                    Triangle(
                        newPointList.indexOf(currentNewPoint) + VERTICES_LIST_SIZE,
                        newVertexIndexList[0],
                        newVertexIndexList[1]
                    )
                )
                for(point in newPointsInCurrentTriangle) {
                    newTriangles.add(
                        Triangle(
                            firstVertexIndex = newPointList.indexOf(currentNewPoint) + VERTICES_LIST_SIZE,
                            secondVertexIndex = newPointList.indexOf(point) + VERTICES_LIST_SIZE,
                            thirdVertexIndex = LineUtils.getCommonPointIndex(
                                currentNewPoint.parentLine,
                                point.parentLine
                            )
                        )
                    )
                }
            }
            processedPoints.clear()
        }

        /*for(point in newPointList) {
            vertices.add(point.x)
            vertices.add(point.y)
            vertices.add(point.z)
        }*/

        triangleList.clear()

        for(triangle in newTriangles) {
            triangleList.add(triangle)
        }

        vertBuffer = vertices.toFloatArray().toBuffer()
        pointBuffer = getPointIndexShortArray().toBuffer()
        calculateLines()
        generateNeighborhoods()
        newPointList.clear()
    }

    private fun generateNeighborhoods() {
        for(firstIndex in triangleList.indices) {
            val neighborhoodList = mutableListOf<Pair<Triangle, Line>>()
            for(secondIndex in triangleList.indices) {
                if(firstIndex == secondIndex) {
                    continue
                }
                triangleList[firstIndex].provideLineList().intersect(
                    triangleList[secondIndex].provideLineList()
                ).let { commonLines ->
                    commonLines.isNotEmpty().let { isNotEmpty ->
                        if (isNotEmpty) {
                            neighborhoodList.add(
                                triangleList[secondIndex] to commonLines.first()
                            )
                        }
                    }
                }
            }
            if(neighborhoodList.isNotEmpty()) {
                triangleList[firstIndex].setNeighborhoodList(neighborhoodList)
            }
        }
    }

    fun draw(gl: GL10) {
        gl.apply {
            glFrontFace(GL10.GL_CW)
            glEnable(GL10.GL_CULL_FACE)
            glCullFace(GL10.GL_BACK)


            try {
                glEnableClientState(GL10.GL_VERTEX_ARRAY)
                //glEnableClientState(GL10.GL_COLOR_ARRAY)
                glVertexPointer(3, GL10.GL_FLOAT, 0, vertBuffer)
                //glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer)
                glDrawElements(GL10.GL_TRIANGLES, triangleList.size * 3, GL10.GL_UNSIGNED_SHORT, pointBuffer)
                //glDisableClientState(GL10.GL_COLOR_ARRAY)
                glDisableClientState(GL10.GL_VERTEX_ARRAY)
            } catch (ex: Exception) {

            }

            /*glEnableClientState(GL10.GL_VERTEX_ARRAY)
            glEnableClientState(GL10.GL_COLOR_ARRAY)
            glVertexPointer(3, GL10.GL_FLOAT, 0, vertBuffer)
            glColorPointer(4, GL10.GL_FLOAT, 0, lineColorBuffer)
            glDrawElements(GL10.GL_LINE_STRIP, pointIndex.size, GL10.GL_UNSIGNED_SHORT, pointBuffer)
            glDisableClientState(GL10.GL_COLOR_ARRAY)
            glDisableClientState(GL10.GL_VERTEX_ARRAY)*/


            glEnableClientState(GL10.GL_VERTEX_ARRAY)
            glEnableClientState(GL10.GL_COLOR_ARRAY)
            glVertexPointer(3, GL10.GL_FLOAT, 0, vertBuffer)
            glColorPointer(4, GL10.GL_FLOAT, 0, lineColorBuffer)
            glDrawElements(GL10.GL_LINES, linePointIndex.size, GL10.GL_UNSIGNED_SHORT, linePointBuffer)
            glDisableClientState(GL10.GL_COLOR_ARRAY)
            glDisableClientState(GL10.GL_VERTEX_ARRAY)


            glDisable(GL10.GL_CULL_FACE)
        }
    }

}