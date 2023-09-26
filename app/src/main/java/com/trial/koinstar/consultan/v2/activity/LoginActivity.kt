package com.trial.koinstar.consultan.v2.activity

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.trial.koinstar.consultan.v2.R
import com.trial.koinstar.consultan.v2.api.BaseApiService
import com.trial.koinstar.consultan.v2.api.UtilsApi
import com.trial.koinstar.consultan.v2.databinding.ActivityLoginBinding
import com.trial.koinstar.v2.utils.SharedPreferencesManager
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

private lateinit var binding: ActivityLoginBinding
class LoginActivity : AppCompatActivity() {

    lateinit var dialogBuilder: AlertDialog.Builder
    lateinit var sharedPref: SharedPreferencesManager
    lateinit var customLoading: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.color_secondary)
        val flag = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.decorView.systemUiVisibility = flag
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        dialogBuilder = AlertDialog.Builder(this@LoginActivity)
        sharedPref = SharedPreferencesManager(applicationContext)
        val layoutView = layoutInflater.inflate(R.layout.custom_dialog_loading, null)
        dialogBuilder.setView(layoutView)
        customLoading = dialogBuilder.create()
        customLoading.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.button.setOnClickListener{validate()}
        binding.forgotPassword.setOnClickListener { startActivity(Intent(applicationContext, ForgotPasswordActivity::class.java))}
    }

    private fun validate() {
        if (binding.email.text.isNullOrEmpty()){
            Toast.makeText(applicationContext, getString(R.string.error_null_email_or_nisn), Toast.LENGTH_SHORT).show()
        } else if (binding.password.text.isNullOrEmpty()){
            Toast.makeText(applicationContext, getString(R.string.error_null_password), Toast.LENGTH_SHORT).show()
        } else {
            loginRequest()
        }
    }
    private fun loginRequest() {
        customLoading.show()
        val baseApiService: BaseApiService = UtilsApi.getApiService()
        val login: Call<ResponseBody> = baseApiService.login(binding.email.text.toString(),
            binding.password.text.toString()
        )
        login.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                customLoading.dismiss()
                if (response.code() == 200){
                    try {
                        val jsonResult = JSONObject(response.body()!!.string())
                        if (jsonResult.getString("status").equals("Success")){
                            val token = jsonResult.getString("data")

                            val intent = Intent(applicationContext, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                            sharedPref.createSession(token)
                        }
                    }catch (e: JSONException){
                        e.printStackTrace()
                    }catch (e: IOException){
                        e.printStackTrace()
                    }
                }else if (response.code() == 400){
                    try {
                        val jsonResult = JSONObject(response.errorBody()!!.string())
                        if (jsonResult.getString("status").equals("Error")) {
                            Toast.makeText(
                                applicationContext,
                                jsonResult.getJSONObject("message").getJSONArray("email").getString(0),Toast.LENGTH_SHORT
                            ).show()
                        }
                    }catch (e: JSONException){
                        e.printStackTrace()
                    }
                }else if (response.code() == 401){
                    try {
                        val jsonResult = JSONObject(response.errorBody()!!.string())
                        if (jsonResult.getString("status").equals("Error")) {
                            Toast.makeText(
                                applicationContext,
                                jsonResult.getString("message"),Toast.LENGTH_SHORT
                            ).show()
                        }
                    }catch (e: JSONException){
                        e.printStackTrace()
                    }
                }else{
                    Toast.makeText(applicationContext,getString(R.string.error_default),Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                customLoading.dismiss()
                Toast.makeText(applicationContext,getString(R.string.error_default),Toast.LENGTH_SHORT).show()
            }
        })
    }
}