package io.carrotquest.sample.start.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import io.carrotquest.sample.R
import io.carrotquest.sample.constants.USER_AUTH_KEY_SP
import io.carrotquest.sample.input_data.view.InputDataActivity
import io.carrotquest.sample.main.view.MainActivity
import io.carrotquest.sample.start.presenter.StartPresenter
import io.carrotquest.sample.utils.SharedPreferencesUtil

class StartActivity : AppCompatActivity(), IStartView {

    private val presenter = StartPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
    }

    override fun onResume() {
        super.onResume()
        presenter.onStart(this)
    }

    override fun openMainActivity() {
        val userAuthKey = SharedPreferencesUtil.getString(this, USER_AUTH_KEY_SP)
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.USER_AUTH_KEY_ARG, userAuthKey)
        startActivity(intent)
    }

    override fun openSetApiKey() {
        val intent = Intent(this, InputDataActivity::class.java)
        startActivity(intent)
    }

    override fun showConnectError() {
        Toast.makeText(this, "Не удалось установить соединение", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}
