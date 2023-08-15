package com.aviafly.game.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.aviafly.game.R
import com.aviafly.game.core.library.ViewBindingDialog
import com.aviafly.game.databinding.DialogGameOverBinding
import com.aviafly.game.ui.other.CallbackVM
import com.aviafly.game.ui.root.FragmentRootDirections

class DialogGameOver: ViewBindingDialog<DialogGameOverBinding>(DialogGameOverBinding::inflate) {
    private val args: DialogGameOverArgs by navArgs()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Dialog_No_Border)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setCancelable(false)
        dialog!!.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                findNavController().popBackStack(R.id.fragmentRoot, false, false)
                true
            } else {
                false
            }
        }
        binding.home.setOnClickListener {
            findNavController().popBackStack(R.id.fragmentRoot, false, false)
        }

        binding.balance.text = args.coins.toString()

        val minutes = (args.time % 3600) / 60
        val seconds = args.time % 60

        binding.time.text = String.format("%02d:%02d", minutes, seconds)

        binding.restart.setOnClickListener {
            findNavController().popBackStack(R.id.fragmentRoot, false, false)
            findNavController().navigate(FragmentRootDirections.actionFragmentRootToFragmentAviaFly(0))
        }
    }
}