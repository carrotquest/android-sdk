package io.carrotquest.sample.input_data.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.carrotquest.sample.R
import io.carrotquest.sample.constants.USER_AUTH_KEY_SP
import io.carrotquest.sample.input_data.presenter.InputDataPresenter
import io.carrotquest.sample.main.view.MainActivity
import io.carrotquest.sample.utils.SharedPreferencesUtil
import kotlinx.android.synthetic.main.activity_input_data.*
import kotlin.system.exitProcess

class InputDataActivity: AppCompatActivity(), IInputDataView {
    private val presenter = InputDataPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_data)

        connect_btn.setOnClickListener {
            presenter.onTryConnect(
                this,
                app_id_et.text.toString(),
                api_key_et.text.toString(),
                user_auth_key_et.text.toString()
            )
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        presenter.onBack()
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
        connect_pb.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        connect_pb.visibility = View.GONE
    }
}