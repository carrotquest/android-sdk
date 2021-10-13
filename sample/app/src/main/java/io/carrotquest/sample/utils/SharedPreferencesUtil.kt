package io.carrotquest.sample.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesUtil {
    companion object {
        private const val NAME_SHARED_PREFERENCES = "io.carrotquest.sample"
        fun saveString(
            context: Context, key: String, value: String?
        ) {
            val editor = getSharedEditor(context, key)
            editor?.putString(key, value)
            editor?.apply()
        }

        fun getString(context: Context, key: String?): String {
            val sharedPreferences = context.getSharedPreferences(NAME_SHARED_PREFERENCES, 0)
            return sharedPreferences.getString(key, "") ?: ""
        }

        private fun getSharedEditor(context: Context, key: String): SharedPreferences.Editor? {
            val sharedPreferences = context.getSharedPreferences(NAME_SHARED_PREFERENCES, 0)
            val editor = sharedPreferences.edit()
            if (sharedPreferences.contains(key)) {
                editor.remove(key)
            }
            return editor
        }

    }
}