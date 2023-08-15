package com.aviafly.game.ui.aviafly

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aviafly.game.core.library.XY
import com.aviafly.game.core.library.XYIMpl
import com.aviafly.game.core.library.random
import com.aviafly.game.domain.Bonus
import com.aviafly.game.domain.Lightning
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Random

class AviaFlyViewModel : ViewModel() {
    var gameState = true
    var pauseState = false

    var isMagnet = false
    var isShield = false
    var isAttack = false

    var coinsCounter = 0
    var canCoin = true

    var bonusCallback: ((Bonus) -> Unit)? = null
    var coinCallback: (() -> Unit)? = null
    var endCallback: (() -> Unit)? = null

    private var gameScope = CoroutineScope(Dispatchers.Default)
    private val _playerXY = MutableLiveData(XYIMpl(0f, 0f))
    val playerXY: LiveData<XYIMpl> = _playerXY

    private val _isGoingUp = MutableLiveData(true)
    val isGoingUp: LiveData<Boolean> = _isGoingUp

    private val _rockets = MutableLiveData<List<XYIMpl>>(emptyList())
    val rockets: LiveData<List<XYIMpl>> = _rockets

    private val _coins = MutableLiveData<List<XYIMpl>>(emptyList())
    val coins: LiveData<List<XYIMpl>> = _coins

    private val _bombs = MutableLiveData<List<XYIMpl>>(emptyList())
    val bombs: LiveData<List<XYIMpl>> = _bombs

    private val _lightning = MutableLiveData<List<Lightning>>(emptyList())
    val lightning: LiveData<List<Lightning>> = _lightning

    private val _bonuses = MutableLiveData<List<Bonus>>(emptyList())
    val bonuses: LiveData<List<Bonus>> = _bonuses

    private val _timer = MutableLiveData(0)
    val timer: LiveData<Int> = _timer

    fun setTimer(time: Int) {
        _timer.postValue(time)
    }

    fun movePlayerUp() {
        _isGoingUp.postValue(true)
        val xy = _playerXY.value!!
        if (xy.y - 2 > 0) {
            _playerXY.postValue(XYIMpl(xy.x, xy.y - 2))
        }
    }

    fun movePlayerDown(limit: Int) {
        _isGoingUp.postValue(false)
        val xy = _playerXY.value!!
        if (xy.y + 2 < limit) {
            _playerXY.postValue(XYIMpl(xy.x, xy.y + 2))
        }
    }

    fun initPlayer(x: Float, y: Float) {
        _playerXY.postValue(XYIMpl(x, y))
    }

    fun start(
        maxY: Int,
        rocketHeight: Int,
        maxX: Int,
        rocketWidth: Int,
        playerWidth: Int,
        playerHeight: Int,
        bonusSize: Int,
        lightningHeight: Int,
        lightningWidth: Int,
        coinSize: Int,
        bombHeight: Int,
        bombWidth: Int
    ) {
        gameScope = CoroutineScope(Dispatchers.Default)

        generateRockets(maxY, rocketHeight, maxX)
        letRocketsMove(rocketHeight, rocketWidth, playerWidth, playerHeight)

        generateBonuses(maxY, bonusSize, maxX)
        letBonusesMove(bonusSize, playerWidth, playerHeight)

        generateLightning(maxY, lightningHeight, maxX)
        letLightningsMove(lightningHeight, lightningWidth, playerWidth, playerHeight)

        generateCoins(maxY, coinSize, maxX)
        letCoinsMove(coinSize, playerWidth, playerHeight)

        generateBombs(maxY, bombHeight, maxX)
        letBombsMove(bombHeight, bombWidth, playerWidth, playerHeight)

        startTimer()
    }

    private fun startTimer() {
        gameScope.launch {
            while (true) {
                delay(1000)
                _timer.postValue(_timer.value!! + 1)
            }
        }
    }

    fun stop() {
        gameScope.cancel()
    }

    private fun generateRockets(maxY: Int, rocketHeight: Int, maxX: Int) {
        gameScope.launch {
            while (true) {
                delay(3000)
                if (!isAttack) {
                    val rocketY = 0 random (maxY - rocketHeight)
                    val currentList = _rockets.value!!.toMutableList()
                    currentList.add(XYIMpl(maxX.toFloat(), rocketY.toFloat()))
                    _rockets.postValue(currentList)
                }
            }
        }
    }

    private fun generateCoins(maxY: Int, coinSize: Int, maxX: Int) {
        gameScope.launch {
            while (true) {
                delay(3000)
                val coinY = 0 random (maxY - coinSize)
                val currentList = _coins.value!!.toMutableList()
                currentList.add(XYIMpl(maxX.toFloat(), coinY.toFloat()))
                _coins.postValue(currentList)
            }
        }
    }

    private fun generateBombs(maxY: Int, bombHeight: Int, maxX: Int) {
        gameScope.launch {
            while (true) {
                delay(10000)
                if (!isAttack) {
                    val bombY = 0
                    val currentList = _bombs.value!!.toMutableList()
                    currentList.add(XYIMpl(maxX.toFloat(), bombY.toFloat()))
                    _bombs.postValue(currentList)
                }
            }
        }
    }

    private fun generateLightning(maxY: Int, lightningHeight: Int, maxX: Int) {
        gameScope.launch {
            while (true) {
                delay(8000)
                val randomValue = Random().nextBoolean()
                val rocketY = if (randomValue) 0 else maxY - lightningHeight
                val currentList = _lightning.value!!.toMutableList()
                currentList.add(Lightning(maxX.toFloat(), rocketY.toFloat(), randomValue))
                _lightning.postValue(currentList)
            }
        }
    }

    private fun generateBonuses(maxY: Int, bonusSize: Int, maxX: Int) {
        gameScope.launch {
            while (true) {
                delay(25000)
                val bonusY = 0 random (maxY - bonusSize)
                val currentList = _bonuses.value!!.toMutableList()
                currentList.add(Bonus(maxX.toFloat(), bonusY.toFloat(), 1 random 3))
                _bonuses.postValue(currentList)
            }
        }
    }

    private fun letRocketsMove(
        rocketHeight: Int,
        rocketWidth: Int,
        playerWidth: Int,
        playerHeight: Int
    ) {
        gameScope.launch {
            while (true) {
                delay(10)
                _rockets.postValue(
                    moveSomethingLeft(
                        _rockets.value!!,
                        rocketHeight,
                        rocketWidth,
                        8,
                        _playerXY.value!!,
                        playerWidth,
                        playerHeight,
                        {
                            if (!isShield) {
                                endCallback?.invoke()
                            }
                        }) as List<XYIMpl>
                )
            }
        }
    }

    private fun letBonusesMove(
        bonusSize: Int,
        playerWidth: Int,
        playerHeight: Int
    ) {
        gameScope.launch {
            while (true) {
                delay(16)
                _bonuses.postValue(
                    moveSomethingLeft(
                        _bonuses.value!!,
                        bonusSize,
                        bonusSize,
                        5,
                        _playerXY.value!!,
                        playerWidth,
                        playerHeight,
                        {
                            bonusCallback?.invoke(it as Bonus)
                        }) as List<Bonus>
                )
            }
        }
    }

    private fun letCoinsMove(
        coinSize: Int,
        playerWidth: Int,
        playerHeight: Int
    ) {
        gameScope.launch {
            while (true) {
                delay(16)
                _coins.postValue(
                    moveCoinsLeft(
                        _coins.value!!,
                        coinSize,
                        coinSize,
                        5,
                        _playerXY.value!!,
                        playerWidth,
                        playerHeight,
                        {
                            if (canCoin) {
                                viewModelScope.launch {
                                    canCoin = false
                                    coinCallback?.invoke()
                                    coinsCounter += 1
                                    delay(100)
                                    canCoin = true
                                }
                            }
                        }) as List<XYIMpl>
                )
            }
        }
    }

    private fun letLightningsMove(
        lightningHeight: Int,
        lightningWidth: Int,
        playerWidth: Int,
        playerHeight: Int
    ) {
        gameScope.launch {
            while (true) {
                delay(16)
                _lightning.postValue(
                    moveSomethingLeft(
                        _lightning.value!!,
                        lightningHeight,
                        lightningWidth,
                        8,
                        _playerXY.value!!,
                        playerWidth,
                        playerHeight,
                        {
                            if (!isShield) {
                                endCallback?.invoke()
                            }
                        }) as List<Lightning>
                )
            }
        }
    }

    private fun letBombsMove(
        bombHeight: Int,
        bombWidth: Int,
        playerWidth: Int,
        playerHeight: Int
    ) {
        gameScope.launch {
            while (true) {
                delay(16)
                _bombs.postValue(
                    moveBombLeft(
                        _bombs.value!!,
                        bombHeight,
                        bombWidth,
                        8,
                        _playerXY.value!!,
                        playerWidth,
                        playerHeight,
                        {
                            if (!isShield) {
                                endCallback?.invoke()
                            }
                        }) as List<XYIMpl>
                )
            }
        }
    }

    private fun moveSomethingLeft(
        oldList: List<XY>,
        objHeight: Int,
        objWidth: Int,
        distance: Int,
        playerXY: XY,
        playerWidth: Int,
        playerHeight: Int,
        onIntersect: (XY) -> Unit = {},
        onOutOf: (XY) -> Unit = {}
    ): List<XY> {
        val newList = mutableListOf<XY>()
        oldList.forEachIndexed { index, xy ->
            if (xy.x + objWidth >= 0) {
                xy.x = xy.x - distance

                val objX = xy.x.toInt()..(xy.x.toInt() + objWidth)
                val objY = xy.y.toInt()..(xy.y.toInt() + objHeight)

                val playerX = playerXY.x.toInt()..(playerXY.x.toInt() + playerWidth)
                val playerY = playerXY.y.toInt()..(playerXY.y.toInt() + playerHeight)

                if (playerX.any { it in objX } && playerY.any { it in objY }) {
                    onIntersect.invoke(xy)
                } else {
                    newList.add(xy)
                }
            } else {
                onOutOf.invoke(xy)
            }
        }
        return newList
    }

    private fun moveBombLeft(
        oldList: List<XY>,
        objHeight: Int,
        objWidth: Int,
        distance: Int,
        playerXY: XY,
        playerWidth: Int,
        playerHeight: Int,
        onIntersect: (XY) -> Unit = {},
        onOutOf: (XY) -> Unit = {}
    ): List<XY> {
        val newList = mutableListOf<XY>()
        oldList.forEachIndexed { index, xy ->
            if (xy.x + objWidth >= 0) {
                xy.x = xy.x - distance
                xy.y = xy.y + distance / 6

                val objX = xy.x.toInt() + (objWidth / 3)..(xy.x.toInt() + objWidth - (objWidth / 3))
                val objY = xy.y.toInt() + objHeight / 3 * 2..(xy.y.toInt() + objHeight)

                val playerX = playerXY.x.toInt()..(playerXY.x.toInt() + playerWidth)
                val playerY = playerXY.y.toInt()..(playerXY.y.toInt() + playerHeight)

                if (playerX.any { it in objX } && playerY.any { it in objY }) {
                    onIntersect.invoke(xy)
                } else {
                    newList.add(xy)
                }
            } else {
                onOutOf.invoke(xy)
            }
        }
        return newList
    }

    private fun moveCoinsLeft(
        oldList: List<XY>,
        objHeight: Int,
        objWidth: Int,
        distance: Int,
        playerXY: XY,
        playerWidth: Int,
        playerHeight: Int,
        onIntersect: (XY) -> Unit = {},
        onOutOf: (XY) -> Unit = {}
    ): List<XY> {
        val newList = mutableListOf<XY>()
        oldList.forEachIndexed { index, xy ->
            if (xy.x + objWidth >= 0) {
                if (isMagnet) {
                    if (playerXY.x + ((playerWidth - objWidth) / 2) > xy.x) {
                        xy.x = xy.x + distance * 2
                    } else if (playerXY.x + ((playerWidth - objWidth) / 2) < xy.x) {
                        xy.x = xy.x - distance * 2
                    }

                    if (playerXY.y + ((playerWidth - objHeight) / 2) > xy.y) {
                        xy.y = xy.y + distance * 2
                    } else if (playerXY.y + ((playerWidth - objHeight) / 2) < xy.y) {
                        xy.y = xy.y - distance * 2
                    }
                } else {
                    xy.x = xy.x - distance
                }

                val objX = xy.x.toInt()..(xy.x.toInt() + objWidth)
                val objY = xy.y.toInt()..(xy.y.toInt() + objHeight)

                val playerX = playerXY.x.toInt()..(playerXY.x.toInt() + playerWidth)
                val playerY = playerXY.y.toInt()..(playerXY.y.toInt() + playerHeight)

                if (playerX.any { it in objX } && playerY.any { it in objY }) {
                    onIntersect.invoke(xy)
                } else {
                    newList.add(xy)
                }
            } else {
                onOutOf.invoke(xy)
            }
        }
        return newList
    }

    fun turnOnShield() {
        viewModelScope.launch {
            isShield = true
            delay(15000)
            isShield = false
        }
    }

    fun turnOnMagnet() {
        viewModelScope.launch {
            isMagnet = true
            delay(15000)
            isMagnet = false
        }
    }

    fun turnOnAttack() {
        viewModelScope.launch {
            isAttack = true
            delay(15000)
            isAttack = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        stop()
    }
}