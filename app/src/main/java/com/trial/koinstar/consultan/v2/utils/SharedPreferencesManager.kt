package com.trial.koinstar.v2.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.trial.koinstar.consultan.v2.activity.MainActivity
import com.trial.koinstar.consultan.v2.activity.OnBoardActivity

class SharedPreferencesManager (var context: Context){
    var pref: SharedPreferences
    var editor: SharedPreferences.Editor
    var mode = 0

    init {
        pref = context.getSharedPreferences(prefName, mode)
        editor = pref.edit()
    }

    fun createSession(token: String?) {
        editor.putBoolean(isLogin, true)
        editor.putString(keyToken, token)
//        editor.putString(keyTokenType, tokenType)
        editor.commit()
    }
    fun addId(id:String?) {
        editor.putString(keyId, id)
//        editor.putString(keyTokenType, tokenType)
        editor.commit()
    }
    fun customAddString(key:String,value:String?) {
        editor.putString(key, value)
//        editor.putString(keyTokenType, tokenType)
        editor.commit()
    }
    fun customAddInt(key:String,value:Int) {
        editor.putInt(key, value)
//        editor.putString(keyTokenType, tokenType)
        editor.commit()
    }
    fun customAddBool(key:String,value:Boolean) {
        editor.putBoolean(key, value)
//        editor.putString(keyTokenType, tokenType)
        editor.commit()
    }
    fun checkLogin() {
        if (!isUserLogin()) {
            val i = Intent(context, OnBoardActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
        } else {
            val i = Intent(context, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
        }
    }

    private fun isUserLogin(): Boolean {
        return pref.getBoolean(isLogin, false)
    }

    fun logout() {
        editor.clear()
        editor.commit()
        val i = Intent(context, OnBoardActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(i)
    }

    val userDetails: HashMap<String, String?>
        get() {
            val user = HashMap<String, String?>()
            user[prefName] = pref.getString(prefName, null)
            user[keyToken] = pref.getString(keyToken, null)
            user[keyId] = pref.getString(keyId, null)
            return user
        }
    fun getCustomPrefString(key: String) : String{
        return pref.getString(key,null).toString()
    }
    fun getCustomPrefInt(key: String,default: Int) : Int{
        return pref.getInt(key,default)
    }
    fun getCustomPrefBool(key: String,default: Boolean) : Boolean{
        return pref.getBoolean(key, default)
    }
    companion object {
        private const val prefName = "crudprefteacher"
        private const val isLogin = "isLoginTeacer"
        const val keyId = "keyTeacherid"
        const val keyToken = "keyTeachertoken"
//        const val keyTokenType = "keytokentype"
    }
}