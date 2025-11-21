package com.aura.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.aura.R
import com.aura.databinding.ActivityLoginBinding
import com.aura.ui.home.HomeActivity
import com.aura.viewModel.LoginViewModel
import kotlinx.coroutines.launch

/**
 * The login activity for the app.
 */
class LoginActivity : AppCompatActivity() {

    /**
     * The binding for the login layout.
     */
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInsets()
        setupUi()
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

    private fun setupUi() {
        lifecycleScope.launch {
            viewModel.uiState.collect {
                binding.loginButton.isEnabled = it.isButtonEnabled
                binding.loginButton.setOnClickListener {
                    viewModel.setLoading()
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                binding.loading.visibility = if (it.isViewLoading) View.VISIBLE else View.INVISIBLE
                binding.identifierText.addTextChangedListener(getWatcher())
                binding.passwordText.addTextChangedListener(getWatcher())
            }
        }
    }

    private fun getWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                    viewModel.setButton(
                        !binding.identifierText.text.isEmpty() && !binding.passwordText.text.isEmpty()
                    )
            }
        }
    }
}
