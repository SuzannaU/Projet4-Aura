package com.aura.ui.transfer

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.aura.AuraApplication
import com.aura.R
import com.aura.data.repository.TransferRepository
import com.aura.databinding.ActivityTransferBinding
import com.aura.domain.ErrorType
import com.aura.domain.Transfer
import com.aura.viewModel.TransferViewModel
import com.aura.viewModel.viewModelFactory
import kotlinx.coroutines.launch

/**
 * The transfer activity for the app.
 */
class TransferActivity : AppCompatActivity(), TransferDialogFragment.TransferDialogListener {
    private val TAG = "TransferActivity"
    private lateinit var userId: String
    private lateinit var transfer: Transfer

    /**
     * The binding for the transfer layout.
     */
    private lateinit var binding: ActivityTransferBinding
    private val viewModel: TransferViewModel by viewModels {
        AuraApplication.appModule.transferViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        retrieveUserId()
        setupInsets()
        setupUi()
    }

    private fun retrieveUserId() {
        val userIdFromExtra = intent.getStringExtra("userId")
        userId = userIdFromExtra ?: getString(R.string.unknown_user)
    }

    private fun setupUi() {
        lifecycleScope.launch {
            viewModel.uiState.collect {
                binding.loading.isVisible = it is TransferViewModel.TransferUiState.LoadingState
                when (it) {
                    is TransferViewModel.TransferUiState.TransferSuccessfulState -> {
                        binding.transfer.isEnabled = false
                        setResult(Activity.RESULT_OK)
                        finish()
                    }

                    is TransferViewModel.TransferUiState.ErrorState -> {
                        binding.transfer.isEnabled = true
                        showTransferDialog(it.errorType)
                    }

                    is TransferViewModel.TransferUiState.LoadingState -> {
                        binding.transfer.isEnabled = false
                    }

                    else -> {}
                }
            }
        }
        binding.transfer.isEnabled = false
        setupListeners()
    }

    private fun showTransferDialog(errorType: ErrorType) {
        val dialog = TransferDialogFragment(errorType)
        dialog.show(supportFragmentManager, "TRANSFER_DIALOG")
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        viewModel.transfer(transfer)
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
    }

    private fun setupListeners() {
        binding.recipient.addTextChangedListener(getWatcher())
        binding.amount.addTextChangedListener(getWatcher())

        binding.transfer.setOnClickListener {
            it.isEnabled = false

            val recipient = binding.recipient.text.toString().trim()
            val amount = binding.amount.text.toString().trim().toDouble()
            transfer = Transfer(
                userId,
                recipient,
                amount,
            )

            viewModel.transfer(transfer)
        }
    }

    private fun getWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                binding.transfer.isEnabled =
                    !binding.recipient.text.isEmpty() && !binding.amount.text.isEmpty()
            }
        }
    }

    private fun setupInsets() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            val baseLeft = resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin)
            val baseTop = resources.getDimensionPixelSize(R.dimen.activity_vertical_margin)
            val baseRight = resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin)
            val baseBottom = resources.getDimensionPixelSize(R.dimen.activity_vertical_margin)

            v.setPadding(
                baseLeft + systemBars.left,
                baseTop + systemBars.top,
                baseRight + systemBars.right,
                baseBottom + systemBars.bottom
            )
            insets
        }
    }

}
