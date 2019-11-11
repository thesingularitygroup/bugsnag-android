package com.bugsnag.android.mazerunner.scenarios

import com.bugsnag.android.Bugsnag
import com.bugsnag.android.Configuration
import com.bugsnag.android.mazerunner.IgnorableException

import android.content.Context
import android.content.Intent

/**
 * Sends a handled exception to Bugsnag, which does not include session data.
 */
internal class LoadConfigurationScenario(config: Configuration,
                                        context: Context) : Scenario(config, context) {

    override fun run() {
        super.run()
        context.startActivity(Intent("com.bugsnag.android.mazerunner.UPDATE_CONTEXT"))

        Bugsnag.notify(IgnorableException("Ignore me"))

        Bugsnag.notify(RuntimeException("LoadConfigException"))
    }

}
