package io.carrotquest.testsdkapplication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import io.carrotquest.testsdkapplication.user_property.UserPropertyDialogFragment
import io.carrotquest_sdk.android.Carrot
import kotlinx.android.synthetic.main.activity_auth.*

private const val APP_ID = ""
private const val API_KEY = ""
private const val USER_AUTH_KEY = ""

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        setup()
        initViews()
        initFirebase()
    }

    private fun setup() {
        val callbackSetup = object : Carrot.Callback<Boolean> {
            override fun onResponse(result: Boolean?) {
                if (!result!!) {
                    Toast.makeText(this@AuthActivity, "Setup is failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(t: Throwable?) {
                logInButton.isEnabled = true
                logOutButton.isEnabled = false

                Toast.makeText(this@AuthActivity, "Setup error: " + t?.message, Toast.LENGTH_SHORT).show()
            }
        }

        Carrot.setup(applicationContext, API_KEY, APP_ID, callbackSetup)
    }

    private fun logIn() {
        val userId = userIdEditText.text?.toString()?.trim() ?: ""

        if (userId.isEmpty()) {
            return
        }

        val callbackAuth = object : Carrot.Callback<Boolean> {
            override fun onResponse(result: Boolean?) {
                if (result!!) {
                    logInButton.isEnabled = false
                    logOutButton.isEnabled = true
                } else {
                    Toast.makeText(this@AuthActivity, "Auth is failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(t: Throwable?) {
                logInButton.isEnabled = true
                logOutButton.isEnabled = false

                Toast.makeText(this@AuthActivity, "Auth error: " + t?.message, Toast.LENGTH_SHORT).show()
            }
        }

        val callbackSetup = object : Carrot.Callback<Boolean> {
            override fun onResponse(result: Boolean?) {
                if (result!!) {
                    Carrot.auth(userId, USER_AUTH_KEY, callbackAuth)
                } else {
                    Toast.makeText(this@AuthActivity, "Setup is failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(t: Throwable?) {
                logInButton.isEnabled = true
                logOutButton.isEnabled = false

                Toast.makeText(this@AuthActivity, "Setup error: " + t?.message, Toast.LENGTH_SHORT).show()
            }
        }

        if (Carrot.isInit()) {
            Carrot.deInit()
        }
        Carrot.setup(applicationContext, API_KEY, APP_ID, callbackSetup)
    }

    private fun logOut() {
        Carrot.deInit()
        userIdEditText.setText("")
        logOutButton.isEnabled = false
    }

    private fun openSetUserPropertyDialog() {
        val fragment = UserPropertyDialogFragment()
        fragment.show(supportFragmentManager, "user_property_fragment")
    }

    private fun initViews() {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                logInButton.isEnabled = userIdEditText.text.isNotEmpty()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
        }
        userIdEditText.addTextChangedListener(watcher)

        logInButton.setOnClickListener { logIn() }
        logOutButton.setOnClickListener { logOut() }

        setPropertyButton.setOnClickListener { openSetUserPropertyDialog() }
    }

    private fun initFirebase() {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
        })
    }
}
