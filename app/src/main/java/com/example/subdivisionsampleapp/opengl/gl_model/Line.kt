package com.example.subdivisionsampleapp.opengl.gl_model

data class Line(
    var firstVertexIndex: Int,
    var secondVertexIndex: Int
) {
    private var isActiveLine = true

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Line

        if((firstVertexIndex == other.firstVertexIndex
                    && secondVertexIndex == other.secondVertexIndex)
            || (firstVertexIndex == other.secondVertexIndex
                    && secondVertexIndex == other.firstVertexIndex)
            || (secondVertexIndex == other.firstVertexIndex
                    && firstVertexIndex == other.secondVertexIndex)
        ) {
            return true
        }

        return false
    }

    override fun hashCode(): Int {
        var result = firstVertexIndex
        result = 31 * result + secondVertexIndex
        return result
    }

    fun deactivate() {
        this.isActiveLine = false
    }
}