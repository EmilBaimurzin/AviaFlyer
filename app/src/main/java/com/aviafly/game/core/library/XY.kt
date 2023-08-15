package com.aviafly.game.core.library

interface XY {
    var x: Float
    var y: Float
}

data class XYIMpl(override var x: Float, override var y: Float): XY