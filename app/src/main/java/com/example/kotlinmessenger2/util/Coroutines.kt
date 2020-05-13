package com.example.kotlinmessenger2.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

object Coroutines {

    fun<T: Any> ioThenMain(
        work: suspend (() -> T?),
        callback: ((T?)->Unit)
    ) =
        CoroutineScope(Dispatchers.Main).launch {
            val data = CoroutineScope(Dispatchers.IO).async  rt@{
                return@rt work()
            }.await()   // await() to make the execution

            callback(data)  // we now pass the data from IO to callback or response function
        }
}