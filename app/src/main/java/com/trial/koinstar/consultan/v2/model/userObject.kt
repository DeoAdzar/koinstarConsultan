package com.trial.koinstar.consultan.v2.model

import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable
import java.util.Date

class userObject : Comparable<userObject>, Serializable {
    var email: String? = null
    var id: Int? = null
    var name: String? = null
    var profession: String? = null
    var agency_origin: String? = null
    var image: String? = null
    var num_hp: String? = null
    var status: Int? = null
    override fun compareTo(other: userObject): Int {
        TODO("Not yet implemented")
    }

    @Throws(JSONException::class)
    fun userObject(jsonObject: JSONObject) {
        this.id = jsonObject.getInt("id")
        this.email = jsonObject.getString("email")
        this.name = jsonObject.getString("name")
        this.profession = jsonObject.getString("profession")
        this.agency_origin = jsonObject.getString("agency_origin")
        this.image = jsonObject.getString("image")
        this.num_hp = jsonObject.getString("num_hp")
        this.status = jsonObject.getInt("status")
    }
}