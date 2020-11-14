package com.andrewbutch.noteeverything.framework.ui

interface UIController {

    fun displayProgressBar(isDisplayed: Boolean)

    fun hideSoftKeyboard()

    fun displayInputDialog(title: String, callback: InputDialogCallback)

    fun displayColorDialog(title: String, callback: ColorDialogCallback)


    companion object {
        interface InputDialogCallback {
            fun onInputComplete(text: String)
        }

        interface ColorDialogCallback {
            fun onColorChoose(color: String)
        }
    }

}