package com.aura.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.aura.R
import com.aura.data.network.ErrorType
import com.aura.data.repository.AccountsRepository
import com.aura.databinding.ActivityHomeBinding
import com.aura.ui.login.LoginActivity
import com.aura.ui.transfer.TransferActivity
import com.aura.viewModel.HomeViewModel
import com.aura.viewModel.viewModelFactory
import kotlinx.coroutines.launch

/**
 * The home activity for the app.
 */
class HomeActivity : AppCompatActivity(), HomeDialogFragment.HomeDialogListener {
    private val TAG = "HomeActivity"
    private lateinit var userId: String

    /**
     * The binding for the home layout.
     */
    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels {
        viewModelFactory {
            HomeViewModel(AccountsRepository())
        }
    }

    /**
     * A callback for the result of starting the TransferActivity.
     */
    private val startTransferActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            //TODO
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        retrieveUserId()
        setupUi()
        viewModel.getUserAccounts(userId)
    }

    private fun retrieveUserId() {
        val userIdFromExtra = intent.getStringExtra("userId")
        userId = userIdFromExtra ?: getString(R.string.unknown_user)
    }

    private fun setupUi() {
        lifecycleScope.launch {
            viewModel.uiState.collect {
                binding.loginLoading.isVisible = it.isViewLoading
                when (it) {
                    is HomeViewModel.HomeUiState.BalanceFoundState -> {
                        binding.balance.text = it.balance.toString()
                    }

                    is HomeViewModel.HomeUiState.ErrorState -> {
                        showHomeDialog(it.errorType)
                    }

                    HomeViewModel.HomeUiState.LoadingState -> {}
                }
            }
        }
        setupListeners()
    }

    private fun setupListeners() {
        binding.transfer.setOnClickListener {
            startTransferActivityForResult.launch(
                Intent(
                    this@HomeActivity,
                    TransferActivity::class.java
                )
            )
        }
    }

    private fun showHomeDialog(errorType: ErrorType) {
        val dialog = HomeDialogFragment(errorType)
        dialog.show(supportFragmentManager, "HOME_DIALOG")
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        viewModel.getUserAccounts(userId)
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        binding.balance.setText(R.string.failed)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.disconnect -> {
                startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}
