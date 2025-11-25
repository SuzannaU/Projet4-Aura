package com.aura.ui.login

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.aura.data.network.ErrorType

class LoginDialogFragment(val errorType: ErrorType) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message = when (errorType) {
            ErrorType.SERVER -> "Server error"
            ErrorType.NETWORK -> "No connection"
            ErrorType.BAD_REQUEST -> "Error with the request"
            ErrorType.BAD_CREDENTIALS -> "Bad Credentials"
            ErrorType.UNKNOWN -> "Unknown error"
        }

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder
                .setMessage(message)
                .setNeutralButton("OK") { dialog, id ->

                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}