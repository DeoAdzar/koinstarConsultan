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
import com.trial.koinstar.consultan.v2.databinding.ActivityChangePasswordBinding
import com.trial.koinstar.v2.utils.ConstantString
import com.trial.koinstar.v2.utils.SharedPreferencesManager

import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

private lateinit var binding: ActivityChangePasswordBinding
class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var sessionManager: SharedPreferencesManager
    private var token: String = ""
    private var token_type: String = ""
    private var mApiService: BaseApiService = UtilsApi.getApiService()
    lateinit var customLoading: AlertDialog
    lateinit var dialogBuilder: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.color_secondary)
        val flag = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.decorView.systemUiVisibility = flag
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        dialogBuilder = AlertDialog.Builder(this)
        val layoutView = layoutInflater.inflate(R.layout.custom_dialog_loading, null)
        dialogBuilder.setView(layoutView)
        customLoading = dialogBuilder.create()
        customLoading.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        sessionManager = SharedPreferencesManager(applicationContext)
        val user: HashMap<String, String?> = sessionManager.userDetails
        token = user[SharedPreferencesManager.keyToken].toString()
        token_type = ConstantString().AUTH_TYPE

        binding.btnBack.setOnClickListener { onBackPressed() }
        binding.save.setOnClickListener { updatePassword() }
    }
    private fun updatePassword() {
        val update: Call<ResponseBody> = mApiService.updatePasswordProfile("$token_type $token"
            , binding.passwordOld.text.toString(), binding.passwordNew.text.toString(), binding.confirmPassword.text.toString())
        update.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200){
                    try {
                        val jsonResult = JSONObject(response.body()!!.string())
                        customLoading.dismiss()
                        if (jsonResult.getString("status").equals("Success")){
                            Toast.makeText(applicationContext,jsonResult.getString("message"),
                                Toast.LENGTH_SHORT).show()
                            finish()
                        }else{
                            Toast.makeText(applicationContext,jsonResult.getString("message"),
                                Toast.LENGTH_SHORT).show()
                        }
                    }catch (e: JSONException){
                        e.printStackTrace()
                    }catch (e: IOException){
                        e.printStackTrace()
                    }
                }else if (response.code() == 400){
                    val jsonResult : JSONObject?
                    try {
                        jsonResult = JSONObject(response.errorBody()!!.string())
                        customLoading.dismiss()
                        if (jsonResult.getString("status").equals("Error")) {
                            val message = jsonResult.getString("message")
                            Toast.makeText(applicationContext,message, Toast.LENGTH_SHORT).show()
                        }
                    }catch (e: JSONException){
                        e.printStackTrace()
                    }
                }else{
                    customLoading.dismiss()
                    Toast.makeText(applicationContext,getString(R.string.error_default), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(applicationContext,getString(R.string.error_default), Toast.LENGTH_SHORT).show()
            }
        })
    }
}