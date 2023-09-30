package com.trial.koinstar.consultan.v2.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.trial.koinstar.consultan.v2.R
import com.trial.koinstar.consultan.v2.activity.ChangeProfileActivity
import com.trial.koinstar.consultan.v2.adapter.RecentAdapter
import com.trial.koinstar.consultan.v2.api.BaseApiService
import com.trial.koinstar.consultan.v2.api.UtilsApi
import com.trial.koinstar.consultan.v2.model.chatObject
import com.trial.koinstar.consultan.v2.model.userObject
import com.trial.koinstar.v2.utils.ConstantString
import com.trial.koinstar.v2.utils.SharedPreferencesManager
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.Collections

class ChatFragment : Fragment() {
    private lateinit var sessionManager: SharedPreferencesManager
    private var chatMessageList: ArrayList<chatObject> = ArrayList<chatObject>()
    private var recentAdapter: RecentAdapter? = null
    private var database: FirebaseFirestore? = null
    private lateinit var idUser: String
    lateinit var recyclerView: RecyclerView
    lateinit var loadingContainer: LinearLayout
    lateinit var header: LinearLayout
    lateinit var displayPicture: CircleImageView
    lateinit var displayName: TextView
    lateinit var displayAgencyOrigin: TextView
    lateinit var displayStatus: TextView
    lateinit var switch: Switch
    lateinit var mContext: Context
    private var token: String = ""
    private var token_type: String = ""
    private lateinit var myId: String
    private var status: Boolean = false
    val baseApiService: BaseApiService = UtilsApi.getApiService()
    lateinit var user :HashMap<String, String?>
     var setName:String = ""
     var setOrigin:String = ""
//    lateinit var setId:String
     var setImage:String = ""
    var setStts:Int = 0
    lateinit var emptyContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_chat, container, false)
        loadingContainer = v.findViewById(R.id.loadingContainer)
        emptyContainer = v.findViewById(R.id.emptyContainer)
        header = v.findViewById(R.id.header)
        recyclerView = v.findViewById(R.id.recyclerView)
        displayPicture = v.findViewById(R.id.picture)
        displayName = v.findViewById(R.id.name)
        displayAgencyOrigin = v.findViewById(R.id.agencyOrigin)
        displayStatus = v.findViewById(R.id.displayStatus)
        switch = v.findViewById(R.id.status)

        return v
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        sessionManager = SharedPreferencesManager(mContext)
        user = sessionManager.userDetails
        token = user[SharedPreferencesManager.keyToken].toString()
        myId = user[SharedPreferencesManager.keyId].toString()
        token_type = ConstantString().AUTH_TYPE
        database = FirebaseFirestore.getInstance()
        getUser()
    }

    private fun listenerConversion() {
        database!!.collection("conversations").whereEqualTo("senderId",idUser).addSnapshotListener(eventListener)
        database!!.collection("conversations").whereEqualTo("receiverId",idUser).addSnapshotListener(eventListener)
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
                            setName = jsonResult.getJSONObject("data").getString("name")
                            setOrigin = jsonResult.getJSONObject("data").getString("agency_origin")
                            setImage = jsonResult.getJSONObject("data").getString("image")
                            setStts = jsonResult.getJSONObject("data").getInt("status")
                            initialize()

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
    private fun initialize() {
        chatMessageList = java.util.ArrayList()
        recentAdapter = RecentAdapter(chatMessageList, mContext)
        recyclerView.adapter = recentAdapter
        idUser = "C${user[SharedPreferencesManager.keyId].toString()}"
        displayName.text = setName
        displayAgencyOrigin.text = setOrigin
        switch.isChecked = setStts == 1
        status = setStts == 1
        displayStatus.text = if (setStts == 1) "Online" else "Offline"
        if (setImage.isEmpty() || setImage == "null" || setImage=="") {
            displayPicture.setImageDrawable(
                ContextCompat.getDrawable(
                    mContext,
                    R.drawable.person
                )
            )
        } else {
            Glide.with(requireContext())
                .load(setImage)
                .placeholder(R.drawable.person) // Placeholder image while loading
                .into(displayPicture)
        }
        switch.setOnClickListener {
            if (status){
                setStatus(false)
            }else{
                setStatus(true)
            }
        }
        listenerConversion()

    }

    private fun setStatus (boolean: Boolean){
        val update: Call<ResponseBody> = baseApiService.updateStatus("$token_type $token", if (boolean) "1" else "0" )
        update.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    try {
                        val jsonResult = JSONObject(response.body()!!.string())
                        if (jsonResult.getString("status") == "Success") {
                            switch.isChecked = boolean
                            status = boolean
                            displayStatus.text = if (boolean) "Online" else "Offline"
                        }else{
                            Toast.makeText(mContext, getString(R.string.error_default), Toast.LENGTH_SHORT).show()
                        }
                    }catch (e: JSONException){
                        e.printStackTrace()
                        Toast.makeText(mContext, getString(R.string.error_default), Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(mContext, getString(R.string.error_default), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private val eventListener =
        EventListener<QuerySnapshot> { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
            if (error != null) {
                return@EventListener
            }
            if (value != null) {
                for (documentChange in value.documentChanges) {
                    if (documentChange.type == DocumentChange.Type.ADDED) {
                        val senderId =
                            documentChange.document.getString("senderId")
                        val receiverId =
                            documentChange.document.getString("receiverId")
                        val chatMessage = chatObject()
                        chatMessage.senderId = senderId
                        chatMessage.receiverId = receiverId
                        if (idUser == senderId) {
                            chatMessage.conversionImage =
                                documentChange.document.getString(ConstantString().KEY_RECEIVER_IMAGE)
                            chatMessage.conversionName =
                                documentChange.document.getString(ConstantString().KEY_RECEIVER_NAME)
                            chatMessage.conversionId =
                                documentChange.document.getString(ConstantString().KEY_RECEIVER_ID)
                        } else {
                            chatMessage.conversionImage =
                                documentChange.document.getString(ConstantString().KEY_SENDER_IMAGE)
                            chatMessage.conversionName =
                                documentChange.document.getString(ConstantString().KEY_SENDER_NAME)
                            chatMessage.conversionId =
                                documentChange.document.getString(ConstantString().KEY_SENDER_ID)
                        }
                        chatMessage.message =
                            documentChange.document.getString(ConstantString().KEY_LAST_MESSAGE)
                        chatMessage.dateObject =
                            documentChange.document.getDate(ConstantString().KEY_TIMESTAMP)
                        chatMessageList.add(chatMessage)
                    } else if (documentChange.type == DocumentChange.Type.MODIFIED) {
                        for (i in chatMessageList.indices) {
                            val senderId =
                                documentChange.document.getString(ConstantString().KEY_SENDER_ID)
                            val receiverId =
                                documentChange.document.getString(ConstantString().KEY_RECEIVER_ID)
                            if (chatMessageList[i].senderId.equals(senderId) && chatMessageList[i].receiverId.equals(
                                    receiverId
                                )
                            ) {
                                chatMessageList[i].message = documentChange.document
                                    .getString(ConstantString().KEY_LAST_MESSAGE)
                                chatMessageList[i].dateObject =
                                    documentChange.document.getDate(ConstantString().KEY_TIMESTAMP)
                                break
                            }
                        }
                    }
                }
                Log.d("conversationSize", "eventListener: ${chatMessageList.size}")
                if (chatMessageList.size>0){
                    recyclerView.visibility = View.VISIBLE
                    emptyContainer.visibility = View.GONE
                }else{
                    recyclerView.visibility = View.GONE
                    emptyContainer.visibility = View.VISIBLE
                }
                Collections.sort(chatMessageList,
                    java.util.Comparator<chatObject> { obj1: chatObject, obj2: chatObject ->
                        obj2.dateObject!!.compareTo(
                            obj1.dateObject
                        )
                    })

                loadingContainer.visibility = View.GONE
                header.visibility = View.VISIBLE
                recentAdapter!!.notifyDataSetChanged()
                recyclerView.smoothScrollToPosition(0)
            }
        }

}