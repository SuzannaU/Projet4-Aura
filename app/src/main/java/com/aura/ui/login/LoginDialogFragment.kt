package com.aura.ui.login

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.aura.R
import com.aura.data.network.ErrorType

class LoginDialogFragment(val errorType: ErrorType) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message = when (errorType) {
            ErrorType.SERVER -> getString(R.string.server_error)
            ErrorType.NETWORK -> getString(R.string.network_error)
            ErrorType.BAD_REQUEST -> getString(R.string.bad_request)
            ErrorType.BAD_CREDENTIALS -> getString(R.string.bad_credentials)
            ErrorType.UNKNOWN -> getString(R.string.unknown_error)
            else -> getString(R.string.something_wrong)
        }

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder
                .setMessage(message)
                .setNeutralButton(getString(R.string.ok)) { dialog, id ->
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}