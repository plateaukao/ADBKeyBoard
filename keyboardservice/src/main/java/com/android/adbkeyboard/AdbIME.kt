package com.android.adbkeyboard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View

class AdbIME : InputMethodService() {
    private var mReceiver: BroadcastReceiver = AdbReceiver()

    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter(IME_MESSAGE).apply {
            addAction(IME_CHARS)
            addAction(IME_KEYCODE)
            addAction(IME_EDITOR_CODE)
        }
        registerReceiver(mReceiver, filter)
    }

    override fun onCreateInputView(): View =
            layoutInflater.inflate(R.layout.view, null)

    override fun onDestroy() =
        unregisterReceiver(mReceiver)

    internal inner class AdbReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                IME_MESSAGE ->
                    intent.getStringExtra("msg")?.let {
                        currentInputConnection?.commitText(it, 1)
                    }
                IME_CHARS ->
                    intent.getIntArrayExtra("chars")?.let {
                        val msg = String(it, 0, it.size)
                        currentInputConnection?.commitText(msg, 1)
                    }
                IME_KEYCODE -> {
                    val code = intent.getIntExtra("code", -1)
                    if (code != -1) {
                        currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, code))
                    }
                }

                IME_EDITOR_CODE -> {
                    val code = intent.getIntExtra("code", -1)
                    if (code != -1) {
                        currentInputConnection?.performEditorAction(code)
                    }
                }
                else -> {}
            }
        }
    }

    companion object {
        private const val IME_MESSAGE = "ADB_INPUT_TEXT"
        private const val IME_CHARS = "ADB_INPUT_CHARS"
        private const val IME_KEYCODE = "ADB_INPUT_CODE"
        private const val IME_EDITOR_CODE = "ADB_EDITOR_CODE"

    }
}
