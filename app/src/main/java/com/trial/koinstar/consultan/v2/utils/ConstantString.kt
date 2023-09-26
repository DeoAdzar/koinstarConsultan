package com.trial.koinstar.v2.utils

class ConstantString {
    val AUTH_TYPE = "Bearer"
    val KEY_SENDER_ID = "senderId"
    val KEY_RECEIVER_ID = "receiverId"

    //    public static final String KEY_MESSAGE = "message";
    val KEY_TIMESTAMP = "timestamp"

    //    public static final String KEY_NAME = "name";
    //    public static final String KEY_EMAIL = "email";
    //    public static final String KEY_PASSWORD = "password";
    //    public static final String KEY_PREFERENCE_NAME = "chatAppPreference";
    //    public static final String KEY_IS_SIGNED_IN = "isSignedIn";
    //    public static final String KEY_USER_ID = "userId";
    //    public static final String KEY_IMAGE = "image";
    //    public static final String KEY_FCM = "fcmToken";
    //    public static final String KEY_USER = "user";
    //    public static final String KEY_COLLECTION_CONVERSATIONS = "conversations";
    val KEY_SENDER_NAME = "senderName"
    val KEY_RECEIVER_NAME = "receiverName"
    val KEY_SENDER_IMAGE = "senderImage"
    val KEY_RECEIVER_IMAGE = "receiverImage"
    val KEY_LAST_MESSAGE = "lastMessage"

    //    public static final String KEY_AVAILABILITY = "availability";
    val REMOTE_MSG_AUTHOR = "Authorization"
    val REMOTE_MSG_CONTENT_TYPE = "Content-Type"
    val REMOTE_MSG_DATA = "data"
    val REMOTE_MSG_REGISTRATION_IDS = "registration_ids"
    var msgHeader: HashMap<String, String> = HashMap<String, String>()
    fun getRemoteMsgHeader(): HashMap<String, String>? {
            msgHeader.put(REMOTE_MSG_AUTHOR, "key=AAAAVaFSY-c:APA91bEuCJKT369VkgST22RlyNAp0tC8bpgEHIVtflP-aSVNrb79iriQezXxTUqEQ-PcqmbYplVSAZPAR6eQeazOrnavgTfPucK7881sy1p9V4kIxuSVD0ID7EyPRvOLW1oPC03Jdjpg")
            msgHeader.put(REMOTE_MSG_CONTENT_TYPE, "application/json")
        return msgHeader
    }
}