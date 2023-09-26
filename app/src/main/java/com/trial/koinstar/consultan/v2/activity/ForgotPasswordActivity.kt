package com.trial.koinstar.consultan.v2.activity

import android.app.AlertDialog
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
import com.trial.koinstar.consultan.v2.databinding.ActivityForgotPasswordBinding
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

private lateinit var binding: ActivityForgotPasswordBinding
class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var customLoading: AlertDialog
    lateinit var dialogBuilder: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.color_secondary)
        val flag = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.decorView.systemUiVisibility = flag
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        dialogBuilder = AlertDialog.Builder(this)
        val layoutView = layoutInflater.inflate(R.layout.custom_dialog_loading, null)
        dialogBuilder.setView(layoutView)
        customLoading = dialogBuilder.create()
        customLoading.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.btnBackTest.setOnClickListener { onBackPressed() }
        binding.button.setOnClickListener {
            if (binding.email.text.isNullOrEmpty()){
                Toast.makeText(applicationContext,getString(R.string.error_null_email), Toast.LENGTH_SHORT).show()
            }else{
                sendEmail()
            }
        }
    }
    private fun sendEmail() {
        customLoading.show()
        val baseApiService: BaseApiService = UtilsApi.getApiService()
        val login: Call<ResponseBody> = baseApiService.forgetPassword(binding.email.text.toString())
        login.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful){
                    try {
                        val jsonResult = JSONObject(response.body()!!.string())
                        customLoading.dismiss()
                        if (jsonResult.getString("status").equals("Success")){
                            binding.email.setText("")
                            Toast.makeText(applicationContext,getString(R.string.send_email_reset),Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(applicationContext,getString(R.string.error_default),Toast.LENGTH_SHORT).show()
                        }
                    }catch (e: JSONException){
                        e.printStackTrace()
                    }catch (e: IOException){
                        e.printStackTrace()
                    }
                }else{
                    customLoading.dismiss()
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