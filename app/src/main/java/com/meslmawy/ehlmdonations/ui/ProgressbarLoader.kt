package com.meslmawy.ehlmdonations.ui

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.meslmawy.ehlmdonations.R

class ProgressbarLoader(private val myactivity: Activity) {
    private var dialog: AlertDialog? = null
    fun showloader() {
        val builder =
            AlertDialog.Builder(myactivity)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_bar)
        dialog = builder.create()
        dialog!!.show()
    }

    fun dismissloader() {
        dialog!!.dismiss()
    }

}