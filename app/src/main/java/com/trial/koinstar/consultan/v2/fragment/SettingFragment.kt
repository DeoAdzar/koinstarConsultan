package com.trial.koinstar.consultan.v2.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.trial.koinstar.consultan.v2.R
import com.trial.koinstar.consultan.v2.activity.ChangePasswordActivity
import com.trial.koinstar.consultan.v2.activity.ChangeProfileActivity
import com.trial.koinstar.consultan.v2.activity.MainActivity
import com.trial.koinstar.consultan.v2.api.BaseApiService
import com.trial.koinstar.consultan.v2.api.UtilsApi
import com.trial.koinstar.consultan.v2.model.userObject
import com.trial.koinstar.consultan.v2.utils.ImageConverter
import com.trial.koinstar.v2.utils.ConstantString
import com.trial.koinstar.v2.utils.SharedPreferencesManager
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class SettingFragment : Fragment() {
    private lateinit var sessionManager: SharedPreferencesManager
    private var token: String = ""
    private var token_type: String = ""
    val baseApiService: BaseApiService = UtilsApi.getApiService()
    lateinit var loadingContainer: LinearLayout
    lateinit var mainContainer: LinearLayout
    lateinit var updateProfile: LinearLayout
    lateinit var updatePassword: LinearLayout
    lateinit var feedback: LinearLayout
    lateinit var terms: LinearLayout
    lateinit var policy: LinearLayout
    lateinit var tvLogout: TextView
    lateinit var tvNamaProfil: TextView
    lateinit var tvOrigin: TextView
    lateinit var ivProfil: CircleImageView
     var setName:String = ""
     var setOrigin:String = ""
     var setEmail:String = ""
     var setProfession:String = ""
     var setPhone:String = ""
     var setImage:String = ""
    lateinit var mContext: Context
    lateinit var editIcon: CardView

    companion object {
        const val PICK_IMAGE_REQUEST = 1
        const val CHANGE_PROFILE = 101
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_setting, container, false)
        loadingContainer = v.findViewById(R.id.loadingContainer)
        mainContainer = v.findViewById(R.id.mainContainer)
        updateProfile = v.findViewById(R.id.editProfile)
        updatePassword = v.findViewById(R.id.editPassword)
        feedback = v.findViewById(R.id.feedback)
        terms = v.findViewById(R.id.terms)
        policy = v.findViewById(R.id.policy)
        tvLogout = v.findViewById(R.id.tv_logout)
        tvNamaProfil = v.findViewById(R.id.tv_nama_profil)
        ivProfil = v.findViewById(R.id.iv_profil)
        tvOrigin = v.findViewById(R.id.tv_school_origin)
        editIcon = v.findViewById(R.id.editIcon)

        tvLogout.setOnClickListener {
            val builder = AlertDialog.Builder(mContext)
            builder.setTitle(getString(R.string.logout))
            builder.setMessage(getString(R.string.logout_message))
            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                requestLogout()

            }

            builder.setNegativeButton(android.R.string.no) { dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        }

        updatePassword.setOnClickListener{ mContext.startActivity(Intent(mContext, ChangePasswordActivity::class.java))}

        ivProfil.setOnClickListener { openGallery() }
        editIcon.setOnClickListener { openGallery() }
        return v
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        sessionManager = SharedPreferencesManager(mContext)
        val user: HashMap<String, String?> = sessionManager.userDetails
        token = user[SharedPreferencesManager.keyToken].toString()
        token_type = ConstantString().AUTH_TYPE
        getUser()

    }
    private fun getUser() {
        val getUser: Call<ResponseBody> = baseApiService.getUser("$token_type $token")
        getUser.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    loadingContainer.visibility = View.GONE
                    mainContainer.visibility = View.VISIBLE
                    try {
                        val jsonResult = JSONObject(response.body()!!.string())
                        if (jsonResult.getString("status") == "Success") {

                            setEmail = jsonResult.getJSONObject("data").getString("email")
                            setName = jsonResult.getJSONObject("data").getString("name")
                            setProfession = jsonResult.getJSONObject("data").getString("profession")
                            setOrigin = jsonResult.getJSONObject("data").getString("agency_origin")
                            setImage = jsonResult.getJSONObject("data").getString("image")
                            setPhone = jsonResult.getJSONObject("data").getString("num_hp")

                            tvNamaProfil.text = if (setName == "null"||setName.isEmpty()) "-" else setName
                            if (setImage == "null"||setImage.isEmpty()){
                                ivProfil.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.person))
                            }else{
                                Glide.with(requireContext())
                                    .load(setImage)
                                    .placeholder(R.drawable.person) // Placeholder image while loading
                                    .into(ivProfil)
                            }
                            tvOrigin.text = if (setOrigin == "null"||setOrigin.isEmpty()) "" else setOrigin
                            updateProfile.setOnClickListener{ startActivityForResult(Intent(mContext, ChangeProfileActivity::class.java)
                                .putExtra("email",setEmail)
                                .putExtra("name",setName)
                                .putExtra("origin",setOrigin)
                                .putExtra("profession",setProfession)
                                .putExtra("phone",setPhone), CHANGE_PROFILE)}
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()

                    } catch (e: IOException) {
                        e.printStackTrace()

                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.error_default), Toast.LENGTH_SHORT).show()

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data

            // Set the selected image to the ImageView
            if (imageUri!=null) {
                val file = File(getRealPathFromURI(imageUri))
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

                // Panggil method untuk mengirim gambar ke API
                save(imagePart,imageUri)
            }
        }else if (requestCode == CHANGE_PROFILE && resultCode == Activity.RESULT_OK && data != null) {
            if (data.getBooleanExtra("dataInserted", false)) {
                getUser()
            }
        }
    }
    private fun getRealPathFromURI(uri: android.net.Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireContext().contentResolver.query(uri, projection, null, null, null)
        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val result = cursor.getString(column_index)
        cursor.close()
        return result
    }
    private fun save(imagePart: MultipartBody.Part, imageUri: Uri) {
        val update: Call<ResponseBody> =
            baseApiService.updateImageProfile("$token_type $token", imagePart)
        update.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.code() == 200) {
                    try {
                        val jsonResult = JSONObject(response.body()!!.string())
                        if (jsonResult.getString("status").equals("Success")) {
                            Glide.with(requireContext())
                                .load(imageUri)
                                .placeholder(R.drawable.person) // Placeholder image while loading
                                .into(ivProfil)
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.toast_update_image_success),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                jsonResult.getString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else if (response.code() == 400) {
                    val image = "image"
                    val jsonResult: JSONObject?
                    var objectImage: JSONArray? = null
                    try {
                        jsonResult = JSONObject(response.errorBody()!!.string())
                        try {
                            if (jsonResult.getString("status").equals("Error")) {
                                objectImage =
                                    jsonResult.getJSONObject("message").getJSONArray(image);
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    if (objectImage != null) {
                        Toast.makeText(
                            requireContext(),
                            objectImage.getString(0), Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_default),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun requestLogout() {
        val getUser: Call<ResponseBody> = baseApiService.logout("$token_type $token")
        getUser.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        val jsonResult = JSONObject(response.body()!!.string())
                        if (jsonResult.getString("status").equals("Success")){
                            sessionManager.logout()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(context, "gagal", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

}