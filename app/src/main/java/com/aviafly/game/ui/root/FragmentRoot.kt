package com.aviafly.game.ui.root

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aviafly.game.R
import com.aviafly.game.core.library.safe
import com.aviafly.game.databinding.FragmentRootBinding
import com.aviafly.game.domain.Prefs
import com.aviafly.game.ui.other.CallbackVM
import com.aviafly.game.ui.other.ViewBindingFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentRoot : ViewBindingFragment<FragmentRootBinding>(FragmentRootBinding::inflate) {
    private val sp by lazy {
        Prefs(requireContext())
    }
    private val callbackViewModel: CallbackVM by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callbackViewModel.buttonCallback = {
            lifecycleScope.launch(Dispatchers.Main) {
                delay(50)
                safe {
                    if (sp.getTime() != 0) {
                        binding.coontinue.setImageResource(R.drawable._continue)
                        binding.coontinue.isEnabled = true
                    } else {
                        binding.coontinue.setImageResource(R.drawable.continue02)
                        binding.coontinue.isEnabled = false
                    }
                }
            }
        }

        binding.newGame.setOnClickListener {
            findNavController().navigate(FragmentRootDirections.actionFragmentRootToFragmentAviaFly(0))
        }

        if (sp.getTime() != 0) {
            binding.coontinue.setImageResource(R.drawable._continue)
            binding.coontinue.isEnabled = true
        } else {
            binding.coontinue.setImageResource(R.drawable.continue02)
            binding.coontinue.isEnabled = false
        }

        binding.coontinue.setOnClickListener {
            findNavController().navigate(FragmentRootDirections.actionFragmentRootToFragmentAviaFly(sp.getTime()))
        }

        binding.exit.setOnClickListener {
            requireActivity().finish()
        }
        binding.privacyText.setOnClickListener {
            requireActivity().startActivity(
                Intent(
                    ACTION_VIEW,
                    Uri.parse("https://www.google.com")
                )
            )
        }
    }
}