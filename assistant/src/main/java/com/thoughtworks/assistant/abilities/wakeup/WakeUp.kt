package com.thoughtworks.assistant.abilities.wakeup

interface WakeUpListener {
    fun onSuccess(word: String)
    fun onError(errorCode: Int, errorMessage: String)
    fun onStop()
}

interface WakeUp {
    fun setWakeUpListener(wakeUpListener: WakeUpListener?)
    fun start()
    fun stop()
    fun release()
}