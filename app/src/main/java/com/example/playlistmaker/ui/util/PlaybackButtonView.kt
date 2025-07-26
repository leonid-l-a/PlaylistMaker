package com.example.playlistmaker.ui.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import com.example.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var iconPlay: Drawable? = null
    private var iconPause: Drawable? = null
    private val boundsRect = RectF()

    var isPlaying: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var playbackControlCallback: (() -> Unit)? = null

    init {
        context.obtainStyledAttributes(attrs, R.styleable.PlaybackButtonView, defStyleAttr, defStyleRes).apply {
            iconPlay = getDrawable(R.styleable.PlaybackButtonView_playIcon)
            iconPause = getDrawable(R.styleable.PlaybackButtonView_pauseIcon)
            recycle()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val left = paddingLeft.toFloat()
        val top = paddingTop.toFloat()
        val right = (w - paddingRight).toFloat()
        val bottom = (h - paddingBottom).toFloat()
        boundsRect.set(left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val drawable = if (isPlaying) iconPause else iconPlay
        drawable?.setBounds(
            boundsRect.left.toInt(),
            boundsRect.top.toInt(),
            boundsRect.right.toInt(),
            boundsRect.bottom.toInt()
        )
        drawable?.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                Log.d("PlaybackButtonView", "onTouchEvent: ACTION_UP")
                val x = event.x
                val y = event.y
                if (x in 0f..width.toFloat() && y in 0f..height.toFloat()) {
                    playbackControlCallback?.invoke()
                    performClick()
                    return true
                }
            }
        }
        return false
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }


}

