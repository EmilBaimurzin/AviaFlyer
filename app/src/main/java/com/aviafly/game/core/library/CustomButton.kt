package com.aviafly.game.core.library

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView

class CustomButton(context: Context, attr: AttributeSet) : AppCompatTextView(context, attr) {
    init {
        this.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate()
                        .setDuration(100)
                        .scaleX(0.9f)
                        .scaleY(0.9f)
                        .start()
                    v.invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    v.animate()
                        .setDuration(100)
                        .scaleX(1f)
                        .scaleY(1f)
                        .start()
                    v.invalidate()
                }
            }
            false
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}

class CustomLayoutButton(context: Context, attr: AttributeSet) : LinearLayout(context, attr) {
    init {
        this.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate()
                        .setDuration(100)
                        .scaleX(0.9f)
                        .scaleY(0.9f)
                        .start()
                    v.invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    v.animate()
                        .setDuration(100)
                        .scaleX(1f)
                        .scaleY(1f)
                        .start()
                    v.invalidate()
                }
            }
            false
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}

class CustomImageButton(context: Context, attr: AttributeSet) : AppCompatImageView(context, attr) {
    init {
        this.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate()
                        .setDuration(100)
                        .scaleX(0.9f)
                        .scaleY(0.9f)
                        .start()
                    v.invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    v.animate()
                        .setDuration(100)
                        .scaleX(1f)
                        .scaleY(1f)
                        .start()
                    v.invalidate()
                }
            }
            false
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}

fun View.soundClickListener(function: (view: View) -> Unit) {
    this.setOnClickListener {
        function.invoke(this)
    }
}