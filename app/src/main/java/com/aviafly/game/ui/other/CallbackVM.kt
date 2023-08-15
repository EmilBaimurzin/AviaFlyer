package com.aviafly.game.ui.other

import androidx.lifecycle.ViewModel

class CallbackVM : ViewModel() {
    var pauseCallback: (() -> Unit)? = null
    var buttonCallback: (() -> Unit)? = null
}