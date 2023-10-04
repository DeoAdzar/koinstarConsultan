package com.trial.koinstar.consultan.v2.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.trial.koinstar.consultan.v2.R
import com.trial.koinstar.consultan.v2.adapter.ChatAdapter
import com.trial.koinstar.consultan.v2.api.BaseApiService
import com.trial.koinstar.consultan.v2.api.UtilsApi
import com.trial.koinstar.consultan.v2.databinding.ActivityChatBinding
import com.trial.koinstar.consultan.v2.model.chatObject
import com.trial.koinstar.v2.utils.ConstantString
import com.trial.koinstar.v2.utils.SharedPreferencesManager
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date
import java.util.Locale


private lateinit var binding: ActivityChatBinding
class ChatActivity : AppCompatActivity() {

    private var chatMessageList: ArrayList<chatObject> = ArrayList<chatObject>()
    private var chatAdapter: ChatAdapter? = null
    private lateinit var sessionManager: SharedPreferencesManager
    private var database: FirebaseFirestore? = null
    val baseApiService: BaseApiService = UtilsApi.getApiService()
    private var idKonsultan: String? = null
    private var namaKonsultan:String? =null
    private var imageKonsultan:String? = null
    private var idUser: String? = null
    private var namaUser:String? = null
    private var imageUser:String? = null
    private var conversionId: String? = null
    private val isReceiverAvailable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        val flag = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.decorView.systemUiVisibility = flag
        binding = ActivityChatBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initialize()
        listenMessage()
        setListener()
    }
    private fun setListener() {
        binding.buttonSend.setOnClickListener { sendMessage() }
        binding.btnBack.setOnClickListener{onBackPressed()}

    }
    private fun sendMessage() {
        val message = java.util.HashMap<String, Any>()
        message["senderId"] = idUser!!
        message["receiverId"] = idKonsultan!!
        message["message"] = binding.chat.getText().toString()
        message["timestamp"] = Date()
        database!!.collection("chat").add(message)
        if (conversionId != null) {
            updateConversion(binding.chat.getText().toString())
        } else {
            val conversion = java.util.HashMap<String, Any>()
            conversion[ConstantString().KEY_SENDER_ID] = idUser!!
            conversion[ConstantString().KEY_SENDER_NAME] = namaUser!!
            conversion[ConstantString().KEY_SENDER_IMAGE] = imageUser!!
            conversion[ConstantString().KEY_RECEIVER_ID] = idKonsultan!!
            conversion[ConstantString().KEY_RECEIVER_NAME] = namaKonsultan!!
            conversion[ConstantString().KEY_RECEIVER_IMAGE] = imageKonsultan!!
            conversion[ConstantString().KEY_LAST_MESSAGE] = binding.chat.getText().toString()
            conversion[ConstantString().KEY_TIMESTAMP] = Date()
            addConversion(conversion)
        }
        binding.chat.setText(null)
    }

    private fun listenMessage() {
        database!!.collection("chat")
            .whereEqualTo("senderId", idUser)
            .whereEqualTo("receiverId", idKonsultan)
            .addSnapshotListener(eventListener)
        database!!.collection("chat")
            .whereEqualTo("senderId", idKonsultan)
            .whereEqualTo("receiverId", idUser)
            .addSnapshotListener(eventListener)
    }

    private val eventListener =
        EventListener<QuerySnapshot> { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
            if (error != null) {
                return@EventListener
            }
            if (value != null) {
                val count = chatMessageList.size
                for (documentChange in value.documentChanges) {
                    if (documentChange.type == DocumentChange.Type.ADDED) {
                        val chatMessage = chatObject()
                        chatMessage.senderId = documentChange.document.getString("senderId")
                        chatMessage.receiverId =
                            documentChange.document.getString("receiverId")
                        chatMessage.message = documentChange.document.getString("message")
                        chatMessage.dateObject = documentChange.document.getDate("timestamp")
                        chatMessage.dateTime =
                            getReadableDateTime(documentChange.document.getDate("timestamp"))
                        chatMessageList.add(chatMessage)
                    }
                }
                Collections.sort(
                    chatMessageList,
                    java.util.Comparator<chatObject> { obj1: chatObject, obj2: chatObject ->
                        obj1.dateObject!!.compareTo(
                            obj2.dateObject
                        )
                    })
                if (count == 0) {
                    chatAdapter!!.notifyDataSetChanged()
                } else {
                    chatAdapter!!.notifyItemRangeInserted(
                        chatMessageList.size,
                        chatMessageList.size
                    )
                    binding.recyclerView.smoothScrollToPosition(chatMessageList.size - 1)
                }
                binding.recyclerView.setVisibility(View.VISIBLE)
            }
            if (conversionId == null) {
                checkForConversion()
            }
        }

    private fun getReadableDateTime(date: Date?): String {
        return SimpleDateFormat("dd MMM yyyy - hh.mm", Locale.getDefault()).format(date)
    }

    private fun addConversion(conversion: java.util.HashMap<String, Any>) {
        database!!.collection("conversations")
            .add(conversion)
            .addOnSuccessListener { documentReference: DocumentReference ->
                conversionId = documentReference.id
            }
    }

    private fun updateConversion(message: String) {
        val documentReference = database!!.collection("conversations").document(
            conversionId!!
        )
        documentReference.update(
            "lastMessage", message,
            "timestamp", Date()
        )
    }

    private fun checkForConversion() {
        if (chatMessageList.size != 0) {
            checkForConversionRemotely(
                idUser!!,
                idKonsultan!!
            )
            checkForConversionRemotely(
                idKonsultan!!,
                idUser!!
            )
        }
    }

    private fun checkForConversionRemotely(senderId: String, receiverId: String) {
        database!!.collection("conversations")
            .whereEqualTo("senderId", senderId)
            .whereEqualTo("receiverId", receiverId)
            .get()
            .addOnCompleteListener(conversionOnCompleteListener)
    }

    private val conversionOnCompleteListener =
        OnCompleteListener { task: Task<QuerySnapshot?> ->
            if (task.isSuccessful && task.result != null && task.result!!
                    .documents.size > 0
            ) {
                val documentSnapshot = task.result!!.documents[0]
                conversionId = documentSnapshot.id
            }
        }

    private fun initialize() {
        sessionManager = SharedPreferencesManager(applicationContext)
        val user: HashMap<String, String?> = sessionManager.userDetails
        chatMessageList = java.util.ArrayList()
        idUser = "C${user[SharedPreferencesManager.keyId].toString()}"
        chatAdapter = ChatAdapter(chatMessageList, idUser!!)
        binding.recyclerView.setAdapter(chatAdapter)
        database = FirebaseFirestore.getInstance()
        namaUser = intent.getStringExtra("namaUser")
        imageUser = intent.getStringExtra("imageUser")
        val subString = intent.getStringExtra("idKonsultan")?.substring(0,1)
        idKonsultan = if (subString=="S")intent.getStringExtra("idKonsultan") else "S${intent.getStringExtra("idKonsultan")}"
        namaKonsultan = intent.getStringExtra("namaKonsultan")
        imageKonsultan = intent.getStringExtra("imageKonsultan")
        binding.displayName.setText(namaKonsultan)
        if (imageKonsultan.isNullOrEmpty()){
            binding.picture.setImageDrawable(ContextCompat.getDrawable(applicationContext,R.drawable.person))
        }else{
            Glide.with(applicationContext)
                .load(imageKonsultan)
                .placeholder(R.drawable.person)
                .centerInside()
                .into(binding.picture)
        }
    }
}