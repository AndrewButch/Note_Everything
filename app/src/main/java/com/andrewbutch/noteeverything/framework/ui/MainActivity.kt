package com.andrewbutch.noteeverything.framework.ui

import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.input.input
import com.andrewbutch.noteeverything.R
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class MainActivity : DaggerAppCompatActivity() {
    private var dialogInView: MaterialDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun showDialog(title: String, callback: DialogCallback) {
        dialogInView = MaterialDialog(this).show {
            title(text = title)
            input(
                allowEmpty = false,
                waitForPositiveButton = true,
                inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD,
                maxLength = 40
            ) { _, text -> callback.onDoneClick(text = text.toString())}
            positiveButton(R.string.dialog_done)
            onDismiss {
                dialogInView = null
            }
            cancelable(true)

        }
    }

    fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        if(dialogInView != null){
            (dialogInView as MaterialDialog).dismiss()
            dialogInView = null
        }
    }

    interface DialogCallback {
        fun onDoneClick(text: String)
    }
}