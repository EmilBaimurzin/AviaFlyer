package com.aviafly.game.domain

import com.aviafly.game.core.library.XY

data class Bonus(
    override var x: Float,
    override var y: Float,
    val item: Int
): XY