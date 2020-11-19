package com.andrewbutch.noteeverything.framework.ui.main

interface UIController {

    fun displayProgressBar(isDisplayed: Boolean)

    fun hideSoftKeyboard()

    fun displayInputDialog(title: String, callback: InputDialogCallback)

    fun displayColorDialog(initColor: Int?, callback: ColorDialogCallback)


    companion object {
        interface InputDialogCallback {
            fun onInputComplete(text: String)
        }

        interface ColorDialogCallback {
            fun onColorChoose(color: Int)
        }
    }

}