package com.trial.koinstar.consultan.v2.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.github.drjacky.imagepicker.ImagePicker
import com.trial.koinstar.consultan.v2.R
import com.trial.koinstar.consultan.v2.activity.ChangePasswordActivity
import com.trial.koinstar.consultan.v2.activity.ChangeProfileActivity
import com.trial.koinstar.consultan.v2.api.BaseApiService
import com.trial.koinstar.consultan.v2.api.UtilsApi
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

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
    var setName: String = ""
    var setOrigin: String = ""
    var setEmail: String = ""
    var setProfession: String = ""
    var setPhone: String = ""
    var setImage: String = ""
    lateinit var mContext: Context
    lateinit var editIcon: CardView
    private val REQUEST_WRITE_PERMISSION = 786
    var mediaPath: String? = null
    var postPath: String? = null

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val CHANGE_PROFILE = 102
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

        updatePassword.setOnClickListener {
            mContext.startActivity(
                Intent(
                    mContext,
                    ChangePasswordActivity::class.java
                )
            )
        }

        ivProfil.setOnClickListener {
            launcher.launch(
                ImagePicker.with(requireActivity())
                    .galleryOnly()
                    .cropOval()
                    .setMultipleAllowed(false)
                    .galleryMimeTypes(mimeTypes = arrayOf(
                        "image/png",
                        "image/jpg",
                        "image/jpeg"
                    ))
                    .createIntent()
            )
        }
        editIcon.setOnClickListener {
            launcher.launch(
                ImagePicker.with(requireActivity())
                    .galleryOnly()
                    .cropOval()
                    .setMultipleAllowed(false)
                    .galleryMimeTypes(mimeTypes = arrayOf(
                        "image/png",
                        "image/jpg",
                        "image/jpeg"
                    ))
                    .createIntent()
            )
        }
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

                            tvNamaProfil.text =
                                if (setName == "null" || setName.isEmpty()) "-" else setName
                            if (setImage == "null" || setImage.isEmpty()) {
                                ivProfil.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        mContext,
                                        R.drawable.person
                                    )
                                )
                            } else {
                                Glide.with(requireContext())
                                    .load(setImage)
                                    .placeholder(R.drawable.person) // Placeholder image while loading
                                    .into(ivProfil)
                            }
                            tvOrigin.text =
                                if (setOrigin == "null" || setOrigin.isEmpty()) "" else setOrigin
                            updateProfile.setOnClickListener {
                                startActivityForResult(
                                    Intent(mContext, ChangeProfileActivity::class.java)
                                        .putExtra("email", setEmail)
                                        .putExtra("name", setName)
                                        .putExtra("origin", setOrigin)
                                        .putExtra("profession", setProfession)
                                        .putExtra("phone", setPhone), CHANGE_PROFILE
                                )
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()

                    } catch (e: IOException) {
                        e.printStackTrace()

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
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                ivProfil.setImageURI(uri)
                save(createMultipartBodyPart(requireContext(),uri))
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CHANGE_PROFILE && resultCode == Activity.RESULT_OK && data != null) {
            if (data.getBooleanExtra("dataInserted", false)) {
                getUser()
            }
        }
    }
    private fun createMultipartBodyPart(context: Context, uri: Uri): MultipartBody.Part? {
        val file: File? = getFileFromUri(context, uri)
        if (file != null) {
            // Create RequestBody instance from file
            val requestFile: RequestBody = RequestBody.create(getMimeType(context, uri).toMediaTypeOrNull(), file)
            // Create MultipartBody.Part using the file request body and provided part name
            return MultipartBody.Part.createFormData("image", file.name, requestFile)
        }
        return null
    }

    private fun getFileFromUri(context: Context, uri: Uri): File? {
        val contentResolver = context.contentResolver
        val fileName = System.currentTimeMillis().toString() + "." + getMimeType(context, uri)
        val tempFile = File(context.cacheDir, fileName)
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            return tempFile
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun getMimeType(context: Context, uri: Uri): String {
        val extension: String?
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val mime = MimeTypeMap.getSingleton()
            extension = mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
        } else {
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path!!)).toString())
        }
        return extension ?: "application/octet-stream"
    }

    private fun save(createMultipartBodyPart: MultipartBody.Part?) {
        if (createMultipartBodyPart!=null) {
            val update: Call<ResponseBody> =
                baseApiService.updateImageProfile("$token_type $token", createMultipartBodyPart)
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
                    Log.d("changeProfile", "onFailure: ${t.message}")
                    Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }else{
            Log.d("multipart", "null")
        }
    }

    private fun requestLogout() {
        val getUser: Call<ResponseBody> = baseApiService.logout("$token_type $token")
        getUser.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        val jsonResult = JSONObject(response.body()!!.string())
                        if (jsonResult.getString("status").equals("Success")) {
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