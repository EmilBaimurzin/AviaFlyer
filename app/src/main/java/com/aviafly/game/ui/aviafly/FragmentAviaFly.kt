package com.aviafly.game.ui.aviafly

import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.aviafly.game.R
import com.aviafly.game.core.library.safe
import com.aviafly.game.core.library.shortToast
import com.aviafly.game.databinding.FragmentAviaflyBinding
import com.aviafly.game.domain.Prefs
import com.aviafly.game.ui.other.CallbackVM
import com.aviafly.game.ui.other.ViewBindingFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class FragmentAviaFly :
    ViewBindingFragment<FragmentAviaflyBinding>(FragmentAviaflyBinding::inflate) {
    private val callbackViewModel: CallbackVM by activityViewModels()
    private val sp by lazy {
        Prefs(requireContext())
    }
    private val args: FragmentAviaFlyArgs by navArgs()
    private val viewModel: AviaFlyViewModel by viewModels()
    private val xy by lazy {
        val display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        Pair(size.x, size.y)
    }
    var canDeath = true
    private var moveScope = CoroutineScope(Dispatchers.Default)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAmounts()
        setBalance()

        if (args.time != 0 && savedInstanceState == null) {
            viewModel.setTimer(sp.getTime())
        }

        binding.home.setOnClickListener {
            findNavController().popBackStack()
        }

        callbackViewModel.pauseCallback = {
            viewModel.pauseState = false
            viewModel.start(
                xy.second,
                dpToPx(36),
                xy.first,
                dpToPx(100),
                binding.player.width,
                binding.player.height,
                dpToPx(30),
                dpToPx(180),
                dpToPx(50),
                dpToPx(30),
                dpToPx(140),
                dpToPx(110),
            )
        }

        binding.pause.setOnClickListener {
            viewModel.stop()
            moveScope.cancel()
            viewModel.pauseState = true
            findNavController().navigate(R.id.action_fragmentAviaFly_to_dialogPause)
        }

        binding.magnet.setOnClickListener {
            if (sp.getBonusAmount(1) > 0) {
                viewModel.turnOnMagnet()
                sp.setBonusAmount(1, -1)
                shortToast(requireContext(), "Magnet activated for 15 seconds")
            } else if (sp.getBalance() >= 30) {
                sp.setBalance(-30)
                viewModel.turnOnMagnet()
                shortToast(requireContext(), "Magnet activated for 15 seconds")
            } else {
                shortToast(requireContext(), "Not enough money")
            }
            setAmounts()
            setBalance()
        }

        binding.shield.setOnClickListener {
            if (sp.getBonusAmount(2) > 0) {
                viewModel.turnOnShield()
                sp.setBonusAmount(2, -1)
                shortToast(requireContext(), "Shield activated for 15 seconds")
            } else if (sp.getBalance() >= 20) {
                sp.setBalance(-20)
                viewModel.turnOnShield()
                shortToast(requireContext(), "Shield activated for 15 seconds")
            } else {
                shortToast(requireContext(), "Not enough money")
            }
            setAmounts()
            setBalance()
        }

        binding.attack.setOnClickListener {
            if (sp.getBonusAmount(3) > 0) {
                viewModel.turnOnAttack()
                sp.setBonusAmount(3, -1)
                shortToast(requireContext(), "Bombs and rockets won't spawn for 15 seconds")
            } else if (sp.getBalance() >= 10) {
                sp.setBalance(-10)
                viewModel.turnOnAttack()
                shortToast(requireContext(), "Bombs and rockets won't spawn for 15 seconds")
            } else {
                shortToast(requireContext(), "Not enough money")
            }
            setAmounts()
            setBalance()
        }

        viewModel.bonusCallback = {
            sp.setBonusAmount(it.item, 1)
            setAmounts()
        }

        viewModel.endCallback = {
            if (canDeath) {
                canDeath = false
                lifecycleScope.launch(Dispatchers.Main) {
                    viewModel.gameState = false
                    sp.setTime(0)
                    moveScope.cancel()
                    binding.root.setOnTouchListener { view, motionEvent -> false }
                    viewModel.stop()
                    findNavController().navigate(
                        FragmentAviaFlyDirections.actionFragmentAviaFlyToDialogEnd(
                            viewModel.timer.value!!,
                            viewModel.coinsCounter
                        )
                    )
                }
            }
        }

        viewModel.coinCallback = {
            sp.setBalance(1)
            setBalance()
        }

        viewModel.playerXY.observe(viewLifecycleOwner) {
            binding.player.apply {
                x = it.x
                y = it.y
            }
        }

        viewModel.isGoingUp.observe(viewLifecycleOwner) {
            binding.player.rotation = if (it) 0f else 20f
        }

        viewModel.rockets.observe(viewLifecycleOwner) {
            binding.rocketsLayout.removeAllViews()
            it.forEach { rocket ->
                val rocketView = ImageView(requireContext())
                rocketView.layoutParams = ViewGroup.LayoutParams(dpToPx(100), dpToPx(36))
                rocketView.x = rocket.x
                rocketView.y = rocket.y
                rocketView.setImageResource(R.drawable.rocket)
                binding.rocketsLayout.addView(rocketView)
            }
        }

        viewModel.coins.observe(viewLifecycleOwner) {
            binding.coinsLayout.removeAllViews()
            it.forEach { coin ->
                val coinView = ImageView(requireContext())
                coinView.layoutParams = ViewGroup.LayoutParams(dpToPx(30), dpToPx(30))
                coinView.x = coin.x
                coinView.y = coin.y
                coinView.setImageResource(R.drawable.coin)
                binding.coinsLayout.addView(coinView)
            }
        }

        viewModel.bombs.observe(viewLifecycleOwner) {
            binding.bombsLayout.removeAllViews()
            it.forEach { bomb ->
                val bombView = ImageView(requireContext())
                bombView.layoutParams = ViewGroup.LayoutParams(dpToPx(110), dpToPx(140))
                bombView.x = bomb.x
                bombView.y = bomb.y
                bombView.setImageResource(R.drawable.bomb)
                binding.bombsLayout.addView(bombView)
            }
        }

        viewModel.lightning.observe(viewLifecycleOwner) {
            binding.thundersLayout.removeAllViews()
            it.forEach { lightning ->
                val lightningView = ImageView(requireContext())
                lightningView.layoutParams = ViewGroup.LayoutParams(dpToPx(50), dpToPx(180))
                lightningView.x = lightning.x
                lightningView.y = lightning.y
                lightningView.setImageResource(R.drawable.lightning01)
                lightningView.rotationX = if (lightning.isTop) 0f else 180f
                binding.thundersLayout.addView(lightningView)
            }
        }

        viewModel.bonuses.observe(viewLifecycleOwner) {
            binding.bonusesLayout.removeAllViews()
            it.forEach { bonus ->
                val bonusView = ImageView(requireContext())
                bonusView.layoutParams = ViewGroup.LayoutParams(dpToPx(30), dpToPx(30))
                bonusView.x = bonus.x
                bonusView.y = bonus.y
                val img = when (bonus.item) {
                    1 -> R.drawable.magnet02
                    2 -> R.drawable.shield01
                    else -> R.drawable.shield02
                }
                bonusView.setImageResource(img)
                binding.bonusesLayout.addView(bonusView)
            }
        }

        viewModel.timer.observe(viewLifecycleOwner) {
            val minutes = (it % 3600) / 60
            val seconds = it % 60

            binding.time.text = String.format("%02d:%02d", minutes, seconds)
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            delay(20)
            setTouchListener()
            if (viewModel.playerXY.value!!.x == 0f) {
                viewModel.initPlayer(dpToPx(60).toFloat(), (xy.second / 2 - dpToPx(40)).toFloat())
            }

            if (viewModel.gameState && !viewModel.pauseState) {
                viewModel.start(
                    xy.second,
                    dpToPx(36),
                    xy.first,
                    dpToPx(100),
                    binding.player.width,
                    binding.player.height,
                    dpToPx(30),
                    dpToPx(180),
                    dpToPx(50),
                    dpToPx(30),
                    dpToPx(140),
                    dpToPx(110),
                )
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setTouchListener() {
        binding.root.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    moveScope.launch {
                        while (true) {
                            safe {
                                viewModel.movePlayerUp()
                            }
                            delay(2)
                        }
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    moveScope.launch {
                        while (true) {
                            safe {
                                viewModel.movePlayerUp()
                            }
                            delay(2)
                        }
                    }
                    true
                }

                else -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    moveScope.launch {
                        while (true) {
                            safe {
                                viewModel.movePlayerDown(xy.second - binding.player.height)
                            }
                            delay(2)
                        }
                    }
                    false
                }
            }
        }
    }

    private fun setAmounts() {
        binding.apply {
            magnetAmount.text = sp.getBonusAmount(1).toString()
            shieldAmount.text = sp.getBonusAmount(2).toString()
            attackAmount.text = sp.getBonusAmount(3).toString()
        }
    }

    private fun setBalance() {
        binding.balance.text = sp.getBalance().toString()
    }

    private fun dpToPx(dp: Int): Int {

        val displayMetrics = resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putShort("2", 1)
    }

    override fun onPause() {
        super.onPause()
        binding.root.setOnTouchListener { view, motionEvent ->  false}
        moveScope.cancel()
        viewModel.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        callbackViewModel.buttonCallback?.invoke()
        if (viewModel.gameState) {
            sp.setTime(viewModel.timer.value!!)
        } else {
            sp.setTime(0)
        }
    }
}