package com.kishorebabu.android.samplebranch

import android.app.Application
import io.branch.referral.Branch

/**
 * Created by kishore on 05/07/17.
 */
class SampleBranchApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val branch = Branch.getAutoInstance(this)

        if (BuildConfig.DEBUG) {
            branch.setDebug()
        }
    }
}

