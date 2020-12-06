package com.andrewbutch.noteeverything.business.domain.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import timber.log.Timber
import javax.inject.Singleton

@Singleton
class MessageStack {
    private val _stateMessage = MutableLiveData<StateMessage?>()
    val stateMessage: LiveData<StateMessage?>
        get() = _stateMessage

    private val messages: ArrayList<StateMessage> = ArrayList()

    fun addMessage(message: StateMessage) {
        if (messages.contains(message)) {
            Timber.d("Adding fail ${message.message}")
            return
        }
        messages.add(message)
        Timber.d("Adding success ${message.message}")
        setStateMessage(message)
    }

    fun removeMessage() {
        val message = messages.removeFirstOrNull()
        Timber.d("Removed ${message?.message}")

        val newFirst = messages.firstOrNull()
        setStateMessage(newFirst)
    }

    private fun setStateMessage(stateMessage: StateMessage?) {
        _stateMessage.value = stateMessage
        Timber.d("Set state message: ${stateMessage?.message}")
    }
}