package com.thoughtworks.assistant.utils

import android.content.Context
import androidx.core.content.edit

class SpUtils(val context: Context) {
    private val sp = context.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE)

    fun saveStr(key: String, value: String) {
        sp.edit {
            putString(key, value)
        }
    }

    fun saveLong(key: String, value: Long) {
        sp.edit {
            putLong(key, value)
        }
    }

    fun getStr(key: String): String {
        return sp.getString(key, "") ?: ""
    }

    fun getLong(key: String): Long {
        return sp.getLong(key, 0L)
    }

    companion object {
        const val SP_FILE = "share_preference"
    }
}