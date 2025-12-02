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
import androidx.lifecycle.lifecycleScope
import com.aura.R
import com.aura.data.repository.TransferRepository
import com.aura.databinding.ActivityTransferBinding
import com.aura.domain.Transfer
import com.aura.viewModel.TransferViewModel
import com.aura.viewModel.viewModelFactory
import kotlinx.coroutines.launch

/**
 * The transfer activity for the app.
 */
class TransferActivity : AppCompatActivity() {
    private val TAG = "TransferActivity"
    private lateinit var userId: String

    /**
     * The binding for the transfer layout.
     */
    private lateinit var binding: ActivityTransferBinding
    private val viewModel: TransferViewModel by viewModels {
        viewModelFactory {
            TransferViewModel(TransferRepository())
        }
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
                        // TODO success behaviour
                        setResult(Activity.RESULT_OK)
                        finish()
                    }

                    is TransferViewModel.TransferUiState.ErrorState -> {
                        binding.transfer.isEnabled = true
                        // TODO error behaviour, dialog?
                    }

                    else -> {}
                }
            }
        }
        binding.transfer.isEnabled = false
        setupListeners()
    }

    private fun setupListeners() {
        binding.recipient.addTextChangedListener(getWatcher())
        binding.amount.addTextChangedListener(getWatcher())

        binding.transfer.setOnClickListener {
            it.isEnabled = false
            val transfer = Transfer(
                userId,
                binding.recipient.text.toString(),
                // TODO handle string to Double
                2.1,
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
