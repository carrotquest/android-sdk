package io.carrotquest.testsdkapplication.user_property

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatDialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import io.carrotquest.testsdkapplication.R
import kotlinx.android.synthetic.main.user_property_df_layout.*
import android.widget.ArrayAdapter
import io.carrotquest_sdk.android.Carrot
import io.carrotquest_sdk.android.util.UserProperty

class UserPropertyDialogFragment: AppCompatDialogFragment() {

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_property_df_layout, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }


    private fun initViews() {
        val items = Array(3){""}
        items[0] = "User name"
        items[1] = "Email"
        items[2] = "Phone"
        val adapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_spinner_item, items
        )
        user_property_spinner.adapter = adapter

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                set_user_prop_button.isEnabled = s.toString().isNotEmpty()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
        }
        user_property_value_edit_text.addTextChangedListener(watcher)


        set_user_prop_button.setOnClickListener {
            val property = when(user_property_spinner.selectedItemPosition) {
                0 -> "\$name"
                1 -> "\$email"
                2 -> "\$phone"
                else -> ""
            }

            val value = user_property_value_edit_text.text.toString()

            if(Carrot.isInit() && property.isNotEmpty() && value.isNotEmpty()) {
                Carrot.setUserProperty(UserProperty(property, value))
            }
        }
    }
}