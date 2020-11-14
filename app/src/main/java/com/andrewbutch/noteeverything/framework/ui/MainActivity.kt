package com.andrewbutch.noteeverything.framework.ui

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.inputmethod.InputMethodManager
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
class MainActivity : DaggerAppCompatActivity(), UIController {
    private var dialogInView: MaterialDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun showDialog(title: String, callback: DialogCallback) {

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

    override fun displayProgressBar(isDisplayed: Boolean) {
    }

    override fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager
                .hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    override fun displayInputDialog(
        title: String,
        callback: UIController.Companion.InputDialogCallback
    ) {
        dialogInView = MaterialDialog(this).show {
            title(text = title)
            input(
                allowEmpty = false,
                waitForPositiveButton = true,
                inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD,
                maxLength = 40
            ) { _, text -> callback.onInputComplete(text = text.toString())}
            positiveButton(R.string.dialog_done)
            onDismiss {
                dialogInView = null
            }
            cancelable(true)

        }
    }

    override fun displayColorDialog(
        title: String,
        callback: UIController.Companion.ColorDialogCallback
    ) {
        TODO("Not yet implemented")
    }
}