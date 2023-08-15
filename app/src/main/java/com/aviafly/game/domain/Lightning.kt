package com.aviafly.game.domain

import com.aviafly.game.core.library.XY

data class Lightning(
    override var x: Float,
    override var y: Float,
    val isTop: Boolean
): XY
