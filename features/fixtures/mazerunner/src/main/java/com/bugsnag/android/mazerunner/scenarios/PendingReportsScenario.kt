package com.bugsnag.android.mazerunner.scenarios

import com.bugsnag.android.Bugsnag
import com.bugsnag.android.Client
import com.bugsnag.android.Configuration

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.HandlerThread

internal class PendingReportsScenario(config: Configuration,
                                      context: Context) : Scenario(config, context) {

    init {
        config.setAutoCaptureSessions(false)
        if (context is Activity) {
            eventMetaData = context.intent.getStringExtra("EVENT_METADATA")
            if (eventMetaData != "online") {
                disableAllDelivery(config)
            }
        }
    }

    override fun run() {
        super.run()
        val client = Bugsnag.getClient()
        var first = true

        // notify bugsnag of 10 exceptions during the first callback (the handled error)
        client.beforeNotify {
            if (first) {
                first = false
                postHandledExceptions(client)
            }
            true
        }

        if (eventMetaData != "online") {
            // throw a fatal exception that kills the process
            throw RuntimeException("PendingReportsScenario")
        }
    }

    private fun postHandledExceptions(client: Client) {
        val thread = HandlerThread("HandlerThread")
        thread.start()

        for (k in 1 .. 10) {
            Handler(thread.looper).post {
                // send 10 large reports which will take time to write to disk asynchronously
                // due to blocking beforeNotify call
                client.notify(generateException())
            }
        }
    }

}
