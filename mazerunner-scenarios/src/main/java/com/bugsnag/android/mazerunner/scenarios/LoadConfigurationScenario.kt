package com.bugsnag.android.mazerunner.scenarios

import com.bugsnag.android.Bugsnag
import com.bugsnag.android.Configuration
import com.bugsnag.android.flushAllSessions
import com.bugsnag.android.mazerunner.IgnorableException
import com.bugsnag.android.OnError;

import android.content.Context
import android.content.Intent

import android.os.Handler
import android.os.HandlerThread

/**
 * Sends a handled exception to Bugsnag, which does not include session data.
 */
internal class LoadConfigurationScenario(config: Configuration,
                                        context: Context) : Scenario(config, context) {

    companion object {
        private const val SLEEP_MS: Long = 300
    }

    override fun run() {
        super.run()
        val thread = HandlerThread("HandlerThread")
        thread.start()

        Handler(thread.looper).post(Runnable {
            context.startActivity(Intent("com.bugsnag.android.mazerunner.UPDATE_CONTEXT"))

            Bugsnag.addOnError(OnError { error ->
                error.addMetadata("test", "redacted", "foo")
                error.addMetadata("test", "present", "bar")
                true
            })

            flushAllSessions()
            Thread.sleep(SLEEP_MS)

            Bugsnag.notify(IgnorableException("Ignore me"))

            Bugsnag.notify(RuntimeException("LoadConfigException"))
        })
    }

}
