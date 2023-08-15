package com.aviafly.game.core.library

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun main() {

}

infix fun Int.random(to: Int): Int = (this..to).random()

fun shortToast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

fun longToast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}

fun l(text: String, tag: String = "myLog") {
    Log.e(tag, text)
}

fun safe(function: () -> Unit) {
    try {
        function()
    } catch (t: Throwable) {
        Log.e("error", "", t)
    }
}

fun drawable(context: Context, @DrawableRes id: Int): Drawable {
    val drawable = ResourcesCompat.getDrawable(context.resources, id, null)
    return drawable ?: throw NullPointerException()
}

fun balance(s: SharedPreferences): Long = s.getLong("BALANCE", 5000)

fun increaseBalance(s: SharedPreferences, value: Long) {
    val balance = balance(s)
    s.edit().putLong("BALANCE", balance + value).apply()
}

fun getVolumeState(sharedPrefs: SharedPreferences): Boolean {
    return sharedPrefs.getBoolean("SOUND", true)
}

fun bidListeners(
    bid: Long,
    sharedPrefs: SharedPreferences,
    context: Context,
    editText: EditText,
    lessButton: View,
    moreButton: View,
    betButton: View,
    onBidPlaced: () -> Unit,
    onBidChanged: (bid: Long) -> Unit
) {
    editText.setText(bid.toString())
    lessButton.setOnClickListener {
        val text = editText.text.toString().toInt()
        if (text < 100) {
            editText.setText("0")
        } else {
            editText.setText((text - 100).toString())
        }
    }

    moreButton.setOnClickListener {
        val text = editText.text.toString().toInt()
        editText.setText((text + 100).toString())
    }

    betButton.setOnClickListener {
        val text = editText.text.toString().toLong()
        val balance = sharedPrefs.getLong("BALANCE", 5000);
        if (text != 0L) {
            if (text < balance) {
                sharedPrefs.edit().putLong("BALANCE", balance - text).apply()
                onBidPlaced.invoke()
            } else {
                Toast.makeText(context, "Not enough money", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(context, "Place a bet", Toast.LENGTH_SHORT).show()
        }
    }

    editText.doOnTextChanged { text, _, _, _ ->
        if (text != null) {
            if (text.isBlank()) {
                editText.setText("0")
            } else {
                onBidChanged.invoke(text.toString().toLong())
            }
        }
    }
}

fun <T>getAmountOfSameValuesInList(list: Collection<T?>, value: T?): Int {
    return Collections.frequency(list, value)
}

suspend fun <T>checkMaxAmountOfSameValuesInLists(lists: List<Collection<T?>>, value: T?): Int {
    return suspendCoroutine { continuation ->
        CoroutineScope(Dispatchers.Default).launch {
            val list = mutableListOf<Int>()
            lists.forEach {
                list.add(Collections.frequency(it, value))
            }
            continuation.resume(list.max())
        }
    }
}

suspend fun <T>checkMinAmountOfSameValuesInLists(lists: List<Collection<T?>>, value: T?): Int {
    return suspendCoroutine { continuation ->
        CoroutineScope(Dispatchers.Default).launch {
            val list = mutableListOf<Int>()
            lists.forEach {
                list.add(Collections.frequency(it, value))
            }
            continuation.resume(list.min())
        }
    }
}

private fun flipImage(
    imgView: ImageView,
    @DrawableRes img: Int,
    onFinish: () -> Unit = {},
) {
    CoroutineScope(Dispatchers.Main).launch {
        imgView.animate()
            .setDuration(1000)
            .rotationY(180F)
        delay(500)
        imgView.scaleX = -1f
        imgView.setImageResource(img)
        delay(500)
        onFinish.invoke()
    }
}
