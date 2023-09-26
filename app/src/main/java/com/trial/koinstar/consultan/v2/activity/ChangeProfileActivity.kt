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
import com.trial.koinstar.consultan.v2.databinding.ActivityChangeProfileBinding
import com.trial.koinstar.v2.utils.ConstantString
import com.trial.koinstar.v2.utils.SharedPreferencesManager
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

private lateinit var binding: ActivityChangeProfileBinding
class ChangeProfileActivity : AppCompatActivity() {
    var email: String = ""
    var name: String = ""
    var origin: String = ""
    var profession: String = ""
    var phone: String = ""
    private lateinit var sessionManager: SharedPreferencesManager
    private var token: String = ""
    private var token_type: String = ""
    private var mApiService: BaseApiService = UtilsApi.getApiService()
    lateinit var customLoading: AlertDialog
    lateinit var dialogBuilder: AlertDialog.Builder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this,R.color.color_secondary)
        val flag = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.decorView.systemUiVisibility = flag
        binding = ActivityChangeProfileBinding.inflate(layoutInflater)
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
        email = intent.getStringExtra("email").toString()
        name = intent.getStringExtra("name").toString()
        origin = intent.getStringExtra("origin").toString()
        profession = intent.getStringExtra("profession").toString()
        phone = intent.getStringExtra("phone").toString()
        binding.email.setText(email)
        binding.name.setText(name)
        binding.profession.setText( if (profession == "null") "" else profession)
        binding.schoolOrigin.setText(origin)

        binding.btnBack.setOnClickListener { onBackPressed() }

        binding.save.setOnClickListener { checkAnyUpdate() }
    }
    private fun checkAnyUpdate(){
        if (binding.name.text.toString() == name && binding.email.text.toString() == email && binding.schoolOrigin.text.toString() == origin && binding.profession.text.toString() == profession){
            dataUpdated(false)
            Toast.makeText(applicationContext, "No Data Updated", Toast.LENGTH_SHORT).show()
        }else{
            updateProfile()
        }
    }
    private fun updateProfile() {
        val update: Call<ResponseBody> = mApiService.updateProfile("$token_type $token"
            , binding.email.text.toString(), binding.name.text.toString(), binding.schoolOrigin.text.toString(),binding.profession.text.toString(),phone)
        update.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200){
                    try {
                        val jsonResult = JSONObject(response.body()!!.string())
                        if (jsonResult.getString("status").equals("Success")){
                            dataUpdated(true);
                            sessionManager.customAddBool("REFRESH_USER_DETAIL_SETTING",false)
                            Toast.makeText(applicationContext,getString(R.string.toast_update_profile_success),
                                Toast.LENGTH_SHORT).show()
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
                    val email = "email"
                    val profession = "profession"
                    val phone = "num_hp"
                    val jsonResult : JSONObject?
                    var objectEmail: JSONArray? = null
                    var objectProfession: JSONArray? = null
                    var objectPhone: JSONArray? = null
                    try {
                        jsonResult = JSONObject(response.errorBody()!!.string())
                        try {
                            customLoading.dismiss()
                            if (jsonResult.getString("status").equals("Error")) {
                                objectEmail= jsonResult.getJSONObject("message").getJSONArray(email);
                            }
                        }catch (e: JSONException){
                            e.printStackTrace()
                            try {
                                if (jsonResult.getString("status").equals("Error")) {
                                    objectPhone= jsonResult.getJSONObject("message").getJSONArray(phone);
                                }
                            }catch (e: JSONException){
                                e.printStackTrace()
                                try {
                                    if (jsonResult.getString("status").equals("Error")) {
                                        objectProfession= jsonResult.getJSONObject("message").getJSONArray(profession);
                                    }
                                }catch (e: JSONException){
                                    e.printStackTrace()
                                }
                            }
                        }
                    }catch (e: JSONException){
                        e.printStackTrace()
                    }

                    if (objectEmail!=null){
                        Toast.makeText(
                            applicationContext,
                            objectEmail.getString(0), Toast.LENGTH_SHORT
                        ).show()
                    } else if (objectPhone!=null){
                        Toast.makeText(
                            applicationContext,
                            objectPhone.getString(0), Toast.LENGTH_SHORT
                        ).show()
                    }else if (objectProfession!=null){
                        Toast.makeText(
                            applicationContext,
                            objectProfession.getString(0), Toast.LENGTH_SHORT
                        ).show()
                    }
                }else{
                    Toast.makeText(applicationContext,getString(R.string.error_default), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(applicationContext,getString(R.string.error_default), Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun dataUpdated (isUpdated: Boolean){
        val intent = Intent()
        intent.putExtra("dataInserted",isUpdated)
        setResult(RESULT_OK,intent)
        finish()
    }

    override fun onBackPressed() {
        dataUpdated(false)
        super.onBackPressed()
    }
}