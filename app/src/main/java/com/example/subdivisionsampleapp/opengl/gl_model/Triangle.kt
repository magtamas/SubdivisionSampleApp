package com.example.subdivisionsampleapp.opengl.gl_model

data class Triangle(
    var firstVertexIndex: Int,
    var secondVertexIndex: Int,
    var thirdVertexIndex: Int
) {
    private var neighborhoodList: List<Pair<Triangle, Line>> = listOf()
    private var lineList: List<Line> = listOf(
        Line(
            firstVertexIndex = firstVertexIndex,
            secondVertexIndex = secondVertexIndex
        ),
        Line(
            firstVertexIndex = firstVertexIndex,
            secondVertexIndex = thirdVertexIndex
        ),
        Line(
            firstVertexIndex = secondVertexIndex,
            secondVertexIndex = thirdVertexIndex
        )
    )

    fun provideLineList() = lineList
    fun setNeighborhoodList(list: List<Pair<Triangle, Line>>) {
        this.neighborhoodList = list
    }
    fun getNeighborhoodList(): List<Pair<Triangle, Line>> {
        return this.neighborhoodList
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Triangle

        if (firstVertexIndex != other.firstVertexIndex) return false
        if (secondVertexIndex != other.secondVertexIndex) return false
        if (thirdVertexIndex != other.thirdVertexIndex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = firstVertexIndex
        result = 31 * result + secondVertexIndex
        result = 31 * result + thirdVertexIndex
        return result
    }


}