package com.aviafly.game.domain

import android.content.Context

class Prefs(context: Context) {
    private val sp = context.getSharedPreferences("SP", Context.MODE_PRIVATE)

    fun getBalance(): Int = sp.getInt("BALANCE", 0)
    fun setBalance(value: Int) = sp.edit().putInt("BALANCE", getBalance() + value).apply()

    fun getBonusAmount(value: Int): Int = when (value) {
        1 -> sp.getInt("MAGNET", 0)
        2 -> sp.getInt("SHIELD", 0)
        else -> sp.getInt("ATTACK", 0)
    }

    fun setBonusAmount(bonus: Int, value: Int) {
        when (bonus) {
            1 -> sp.edit().putInt("MAGNET", getBonusAmount(value) + value).apply()
            2 -> sp.edit().putInt("SHIELD", getBonusAmount(value) + value).apply()
            else -> sp.edit().putInt("ATTACK", getBonusAmount(value) + value).apply()
        }
    }

    fun setTime(time: Int) {
        sp.edit().putInt("TIME", time).apply()
    }

    fun getTime(): Int = sp.getInt("TIME", 0)
}