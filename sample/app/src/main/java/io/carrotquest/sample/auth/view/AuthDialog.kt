package io.carrotquest.sample.auth.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import io.carrotquest.sample.R
import io.carrotquest.sample.auth.presenter.AuthPresenter
import kotlinx.android.synthetic.main.auth_dialog.*

class AuthDialog: DialogFragment(), IAuthView {

    private val presenter = AuthPresenter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.auth_dialog, null)
    }

    override fun onStart() {
        super.onStart()
        presenter.onStart(context!!)

        close_auth_ibtn.setOnClickListener { presenter.onTapClose() }
        done_auth_btn.setOnClickListener {

            val name = name_et.text.toString()
            val email = email_et.text.toString()
            val phone = phone_et.text.toString()

            presenter.onTapDone(context = context!!, name = name, email = email, phone = phone)
        }

        name_et.setOnFocusChangeListener { _, hasFocus ->
            run {
                if (!hasFocus) {
                    presenter.onChangeName(name = name_et.text.toString(), context = context!!)
                }
            }
        }

        email_et.setOnFocusChangeListener { _, hasFocus ->
            run {
                if (!hasFocus) {
                    presenter.onChangeEmail(email = email_et.text.toString(), context = context!!)
                }
            }
        }

        phone_et.setOnFocusChangeListener { _, hasFocus ->
            run {
                if (!hasFocus) {
                    presenter.onChangePhone(phone = phone_et.text.toString(), context = context!!)
                }
            }
        }
    }

    override fun close() {
        presenter.detachView()
        dismiss()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun showName(name: String) {
        name_et.setText(name)
    }

    override fun showEmail(email: String) {
        email_et.setText(email)
    }

    override fun showPhone(phone: String) {
        phone_et.setText(phone)
    }
}