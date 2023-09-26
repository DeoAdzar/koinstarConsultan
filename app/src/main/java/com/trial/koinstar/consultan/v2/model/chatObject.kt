package com.trial.koinstar.consultan.v2.model

import java.util.Date

class chatObject : Comparable<chatObject> {
    var senderId:String? = null
    var receiverId: String? = null
    var message: String? = null
    var dateTime: String? = null
    var dateObject: Date? = null
    var conversionId: String? = null
    var conversionName: String? = null
    var conversionImage: String? = null
    override fun compareTo(other: chatObject): Int {
        TODO("Not yet implemented")
    }

}