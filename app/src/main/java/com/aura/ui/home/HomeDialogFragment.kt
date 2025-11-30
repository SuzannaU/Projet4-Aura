package com.aura.ui.home

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.aura.R
import com.aura.data.network.ErrorType

class HomeDialogFragment(val errorType: ErrorType) : DialogFragment() {

    private lateinit var listener: HomeDialogListener

    interface HomeDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message = when (errorType) {
            ErrorType.SERVER -> getString(R.string.server_error)
            ErrorType.NETWORK -> getString(R.string.network_error)
            ErrorType.BAD_REQUEST -> getString(R.string.bad_request)
            ErrorType.NO_ACCOUNT -> getString(R.string.no_account)
            ErrorType.UNKNOWN -> getString(R.string.unknown_error)
            else -> getString(R.string.something_wrong)
        }

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder
                .setMessage(message)
                .setPositiveButton(getString(R.string.retry), DialogInterface.OnClickListener { dialog, id ->
                    listener.onDialogPositiveClick(this)
                })
                .setNegativeButton(getString(R.string.cancel), DialogInterface.OnClickListener { dialog, id ->
                    listener.onDialogNegativeClick(this)
                })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as HomeDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(("$context must implement HomeDialogListener"))
        }

    }
}