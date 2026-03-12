package io.carrotquest.sample.auth.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import io.carrotquest.sample.auth.presenter.AuthPresenter
import io.carrotquest.sample.databinding.AuthDialogBinding
import androidx.core.graphics.drawable.toDrawable

class AuthDialog : DialogFragment(), IAuthView {

    private val presenter = AuthPresenter(this)

    private var _binding: AuthDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AuthDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        binding.closeAuthIbtn.setOnClickListener { presenter.onTapClose() }

        binding.doneAuthBtn.setOnClickListener {
            val name = binding.nameEt.text.toString()
            val email = binding.emailEt.text.toString()
            val phone = binding.phoneEt.text.toString()

            presenter.onTapDone(requireContext(), name, email, phone)
        }

        setupFocusListener(binding.nameEt) { presenter.onChangeName(it, requireContext()) }
        setupFocusListener(binding.emailEt) { presenter.onChangeEmail(it, requireContext()) }
        setupFocusListener(binding.phoneEt) { presenter.onChangePhone(it, requireContext()) }
    }

    private fun setupFocusListener(editText: EditText, action: (String) -> Unit) {
        editText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) action(editText.text.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        
        val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        dialog?.window?.apply {
            setLayout(
                width,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
        
        presenter.onStart(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun close() {
        presenter.detachView()
        dismiss()
    }

    override fun showName(name: String) {
        binding.nameEt.setText(name)
    }

    override fun showEmail(email: String) {
        binding.emailEt.setText(email)
    }

    override fun showPhone(phone: String) {
        binding.phoneEt.setText(phone)
    }
}
