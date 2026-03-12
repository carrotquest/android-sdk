package io.carrotquest.sample.input_data.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import io.carrotquest.sample.R
import io.carrotquest.sample.constants.USER_AUTH_KEY_SP
import io.carrotquest.sample.databinding.ActivityInputDataBinding
import io.carrotquest.sample.input_data.presenter.InputDataPresenter
import io.carrotquest.sample.main.view.MainActivity
import io.carrotquest.sample.utils.SharedPreferencesUtil
import kotlin.system.exitProcess

class InputDataActivity: AppCompatActivity(), IInputDataView {
    private lateinit var binding: ActivityInputDataBinding
    private val presenter = InputDataPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.connectBtn.setOnClickListener {
            presenter.onTryConnect(
                this,
                binding.apiKeyEt.text.toString(),
                binding.userAuthKeyEt.text.toString()
            )
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                presenter.onBack()
            }
        })
    }

    override fun close() {
        presenter.detachView()
        finishAffinity()
        exitProcess(0)
    }

    override fun showConnectError() {
        Toast.makeText(this, getString(R.string.connect_error_str), Toast.LENGTH_SHORT).show()
    }

    override fun openMainActivity() {
        val userAuthKey = SharedPreferencesUtil.getString(this, USER_AUTH_KEY_SP)
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.USER_AUTH_KEY_ARG, userAuthKey)
        startActivity(intent)
    }

    override fun showFieldsIsEmptyError() {
        Toast.makeText(this, getString(R.string.fil_all_fields_warning_str), Toast.LENGTH_SHORT).show()
    }

    override fun showProgress() {
        binding.connectPb.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        binding.connectPb.visibility = View.GONE
    }
}
