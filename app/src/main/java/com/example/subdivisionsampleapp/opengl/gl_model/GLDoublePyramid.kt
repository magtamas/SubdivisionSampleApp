package com.example.subdivisionsampleapp.opengl.gl_model

import android.util.Log
import com.example.subdivisionsampleapp.opengl.gl_utils.LineUtils
import com.example.subdivisionsampleapp.opengl.gl_utils.VertexHolder
import com.example.subdivisionsampleapp.opengl.gl_utils.toBuffer
import com.google.gson.Gson
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.lang.StringBuilder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.opengles.GL10

class GLDoublePyramid() {

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
        1f, -1f, -1f,
        -1f, -1f, -1f,
        0f, 1f, 0f,
        1f, -1f, 1f,
        -1f, -1f, 1f,
        0f, -3f, 0f,
    )

    private var triangleList = mutableListOf<Triangle>(
        Triangle(0,2,3),
        Triangle(1,2,0),
        Triangle(4,2,1),
        Triangle(3,2,4),

        Triangle(3,5,0),
        Triangle(0,5,1),
        Triangle(1,5,4),
        Triangle(4,5,3),
        /*Triangle(0,3,4),
        Triangle(4,1,0)*/
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
        val test2 = floatArrayOf(0f, 1f, 0f, 0f)

        for((index, triangle) in triangleList.withIndex()) {
            val test = floatArrayOf(1f, 0f, 0f, 0f)
            test.forEach {
                testList2.add(it)
            }
        }

        for((index, triangle) in triangleList.withIndex()) {
            if(index == 4) {
                testList2[triangle.firstVertexIndex * 4] = test2[0]
                testList2[triangle.firstVertexIndex * 4 + 1] = test2[1]
                testList2[triangle.firstVertexIndex * 4 + 2] = test2[2]

                testList2[triangle.secondVertexIndex * 4] = test2[0]
                testList2[triangle.secondVertexIndex * 4 + 1] = test2[1]
                testList2[triangle.secondVertexIndex * 4 + 2] = test2[2]

                testList2[triangle.thirdVertexIndex * 4] = test2[0]
                testList2[triangle.thirdVertexIndex * 4 + 1] = test2[1]
                testList2[triangle.thirdVertexIndex * 4 + 2] = test2[2]
            }
        }

        lineColorValues = testList2.toFloatArray()
        lineColorBuffer = lineColorValues.toBuffer()
    }

    fun startSubdivision() {
        val VERTICES_LIST_SIZE = vertices.size / 3
        val doneLineList = mutableListOf<Line>()

        for((index, currentTriangle) in triangleList.withIndex()) {

            for(currentLine in currentTriangle.provideLineList()) {

                if(doneLineList.contains(currentLine)) {
                    continue
                }

                val trianglesByLine = currentTriangle.getNeighborhoodList()
                    .find {
                        it.second == currentLine
                    }?.first!!.let {
                        listOf(
                            currentTriangle,
                            it
                        )
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
                    newX += vertices[it * 3] * 1f/2f
                    newY += vertices[it * 3 + 1] * 1f/2f
                    newZ += vertices[it * 3 + 2] * 1f/2f
                }
                indexOf1per8Edges.forEach {
                    newX += vertices[it * 3] * 1f/8f
                    newY += vertices[it * 3 + 1] * 1f/8f
                    newZ += vertices[it * 3 + 2] * 1f/8f
                }
                indexOfMinus1per16Edges.forEach {
                    newX += vertices[it * 3] * -1f/16f
                    newY += vertices[it * 3 + 1] * -1f/16f
                    newZ += vertices[it * 3 + 2] * -1f/16f
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
        Log.d("tag","LOGMAG NEW POINTS: " + newPointList.size)
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

                val newVertexIndexList = mutableListOf<Int>()
                newPointsInCurrentTriangle.forEach {
                    newVertexIndexList.add(newPointList.indexOf(it) + VERTICES_LIST_SIZE)
                }

                var newTriangle = Triangle(
                    newPointList.indexOf(currentNewPoint) + VERTICES_LIST_SIZE,
                    newVertexIndexList[0],
                    newVertexIndexList[1]
                )
                if(!newTriangles.contains(newTriangle)) {
                    newTriangles.add(newTriangle)
                }

                for(point in newPointsInCurrentTriangle) {
                    newTriangle = Triangle(
                        firstVertexIndex = newPointList.indexOf(currentNewPoint) + VERTICES_LIST_SIZE,
                        secondVertexIndex = newPointList.indexOf(point) + VERTICES_LIST_SIZE,
                        thirdVertexIndex = LineUtils.getCommonPointIndex(
                            currentNewPoint.parentLine,
                            point.parentLine
                        )
                    )
                    if(!newTriangles.contains(newTriangle)) {
                        newTriangles.add(newTriangle)
                    }
                }
            }
            processedPoints.clear()
        }

        triangleList.clear()
        triangleList.addAll(newTriangles)

        /*val sb = StringBuilder()
        var counter = 0
        for(vertex in vertices) {
            when(counter) {
                0 -> {
                    sb.append("v " + vertex)
                    counter++
                }
                1 -> {
                    sb.append(" " + vertex)
                    counter++
                }
                2 -> {
                    sb.append(" " + vertex + "\n")
                    counter = 0
                }
            }
        }*/
        /*Log.d("tag","LOGMAG VERTEX JSON: " + sb.toString())
        Log.d("tag","LOGMAG VERTEX --------------")*/


        /*val first = triangleList.map { it.firstVertexIndex }
        val second = triangleList.map { it.secondVertexIndex }
        val third = triangleList.map { it.thirdVertexIndex }

        val stringBuilder = StringBuilder()
        for(index in 0 until triangleList.size) {
            stringBuilder.append("f ")
            stringBuilder.append(first[index]+1)
            stringBuilder.append(" ")
            stringBuilder.append(second[index]+1)
            stringBuilder.append(" ")
            stringBuilder.append(third[index]+1)
            stringBuilder.append("\n")
        }*/
        /*Log.d("tag","LOGMAG TRIANGLE JSON: " + stringBuilder.toString())
        Log.d("tag","LOGMAG TRIANGLE --------------")*/

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
                    if(commonLines.isNotEmpty()) {
                        if(neighborhoodList.find { it.second == commonLines.first()} == null) {
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