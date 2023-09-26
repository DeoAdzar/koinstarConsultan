package com.trial.koinstar.consultan.v2.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.trial.koinstar.consultan.v2.R
import com.trial.koinstar.consultan.v2.api.BaseApiService
import com.trial.koinstar.consultan.v2.api.UtilsApi
import com.trial.koinstar.consultan.v2.databinding.ActivityMainBinding
import com.trial.koinstar.consultan.v2.fragment.ChatFragment
import com.trial.koinstar.consultan.v2.fragment.SettingFragment
import com.trial.koinstar.consultan.v2.model.userObject
import com.trial.koinstar.consultan.v2.utils.Debouncer
import com.trial.koinstar.v2.utils.ConstantString
import com.trial.koinstar.v2.utils.SharedPreferencesManager
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

private lateinit var binding: ActivityMainBinding
@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    lateinit var bottomNav : BottomNavigationView
    private lateinit var sessionManager: SharedPreferencesManager
    private var token: String = ""
    private var token_type: String = ""
    val baseApiService: BaseApiService = UtilsApi.getApiService()
    lateinit var user :HashMap<String, String?>
    private lateinit var myId: String
    lateinit var dialogBuilder: AlertDialog.Builder
    lateinit var sharedPref: SharedPreferencesManager
    lateinit var customLoading: AlertDialog
    private var isClickableChat = true
    private var isClickableSetting = true
    private val delayMillis: Long = 1500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.color_secondary)
        val flag = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.decorView.systemUiVisibility = flag
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        dialogBuilder = AlertDialog.Builder(this)
        val layoutView = layoutInflater.inflate(R.layout.custom_dialog_loading, null)
        dialogBuilder.setView(layoutView)
        customLoading = dialogBuilder.create()
        customLoading.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        sessionManager = SharedPreferencesManager(applicationContext)
        user = sessionManager.userDetails
        token = user[SharedPreferencesManager.keyToken].toString()
        myId = user[SharedPreferencesManager.keyId].toString()
        token_type = ConstantString().AUTH_TYPE

        getUser()
        sessionManager.customAddBool("REFRESH_USER_DETAIL_CHAT",false)
        sessionManager.customAddBool("REFRESH_USER_DETAIL_SETTING",false)

        menuChatSelected()

        binding.menuChat.setOnClickListener{
            if (isClickableChat) {
                // Disable button and set isClickable to false
                isClickableChat = false

                // Enable button after delayMillis
                Handler(Looper.getMainLooper()).postDelayed({
                    isClickableChat = true
                }, delayMillis)

                menuChatSelected()
                // Place your click action here
                // Example: showToast()
            }
        }
        binding.menuSetting.setOnClickListener{
            if (isClickableSetting) {
                // Disable button and set isClickable to false
                isClickableSetting = false

                // Enable button after delayMillis
                Handler(Looper.getMainLooper()).postDelayed({
                    isClickableSetting = true
                }, delayMillis)

                menuSettingSelected()
                // Place your click action here
                // Example: showToast()
            }
        }

    }

    private fun getUser() {
        val getUser: Call<ResponseBody> = baseApiService.getUser("$token_type $token")
        getUser.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    try {
                        val jsonResult = JSONObject(response.body()!!.string())
                        if (jsonResult.getString("status") == "Success") {

                            val jsonObject: JSONObject = jsonResult.getJSONObject("data")
                            if (myId.isEmpty() || myId == "null" || myId == "") {
                                sessionManager.addId(jsonObject.getInt("id").toString())
                            }
                            customLoading.dismiss()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                            customLoading.dismiss()

                    } catch (e: IOException) {
                        e.printStackTrace()
                            customLoading.dismiss()

                    }
                } else {
                    Toast.makeText(applicationContext, getString(R.string.error_default), Toast.LENGTH_SHORT).show()
                        customLoading.dismiss()

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                    customLoading.dismiss()
            }
        })
    }
    private fun menuChatSelected(){
        binding.iconChat.setImageDrawable(ContextCompat.getDrawable(applicationContext,R.drawable.chat_on_icon))
        binding.titleChat.visibility = View.VISIBLE
        binding.iconSetting.setImageDrawable(ContextCompat.getDrawable(applicationContext,R.drawable.setting_off_icon))
        binding.titleSetting.visibility = View.GONE
        loadFragment(ChatFragment())
    }

    private fun menuSettingSelected(){
        binding.iconSetting.setImageDrawable(ContextCompat.getDrawable(applicationContext,R.drawable.setting_on_icon))
        binding.titleSetting.visibility = View.VISIBLE
        binding.iconChat.setImageDrawable(ContextCompat.getDrawable(applicationContext,R.drawable.chat_off_icon))
        binding.titleChat.visibility = View.GONE
        loadFragment(SettingFragment())
    }

    private fun loadFragment(fragment: Fragment){

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_container,fragment)
        transaction.commit()

    }
}