package com.universidadveracruzana.tianguisx.entities

import com.google.firebase.firestore.Exclude
import java.io.Serializable

open class MarketUser : Serializable{

    var uuId : String? = null
    var name : String? = null
    var lastName : String? = null
    var email : String? = null
    var phoneNumber : String? = null
    var password : String? = null
    var profileImageURL : String? = null
    var profileImageFileCloudPath : String? = null
    var typeUser : String? = null
    var country : String? = null
    var state : String? = null
    var city : String? = null
    @set: Exclude @get: Exclude var orderList : MutableList<Order>? = null

    constructor()

    constructor(
        name : String,
        lastName: String,
        email: String,
        phoneNumber: String,
        password : String,
        profileImageURL : String?,
        profileImageFileCloudPath : String?,
        typeUser : String,
        country : String?,
        state : String,
        city : String
    ){
        this.name = name
        this.lastName = lastName
        this.email = email
        this.phoneNumber = phoneNumber
        this.password = password
        this.profileImageURL = profileImageURL
        this.profileImageFileCloudPath = profileImageFileCloudPath
        this.typeUser = typeUser
        this.country = country
        this.state = state
        this.city = city
    }

    open fun toDictionary() : MutableMap<String , Any> {
        val map = mutableMapOf<String , Any>()

        if(uuId != null) map[UUID_KEY] = uuId!!
        if(name != null) map[NAME_KEY] = name!!
        if(lastName != null) map[LAST_NAME_KEY] = lastName!!
        if(email != null) map[EMAIL_KEY] = email!!
        if(phoneNumber !=null) map[PHONE_NUMBER_KEY] = phoneNumber!!
        if(password != null) map[PASSWORD_KEY] = password!!
        if(profileImageURL != null) map[PROFILE_IMAGE_URL_KEY] = profileImageURL!!
        if(profileImageFileCloudPath != null) map[PROFILE_IMAGE_FILE_CLOUD_PATH_KEY] = profileImageFileCloudPath!!
        if(typeUser != null) map[TYPE_USER_KEY] = typeUser!!
        if(country != null) map[COUNTRY_KEY] = country!!
        if(state != null) map[STATE_KEY] = state!!
        if(city != null) map[CITY_KEY] = city!!
        return map
    }

    open fun parsing(map : MutableMap<String , Any>){
        if(map[UUID_KEY] != null) uuId = map[UUID_KEY] as String
        if(map[NAME_KEY] != null) name = map[NAME_KEY] as String
        if(map[LAST_NAME_KEY] != null) lastName = map[LAST_NAME_KEY] as String
        if(map[EMAIL_KEY] != null) email = map[EMAIL_KEY] as String
        if(map[PHONE_NUMBER_KEY] != null) phoneNumber = map[PHONE_NUMBER_KEY] as String
        if(map[PASSWORD_KEY] != null) password = map[PASSWORD_KEY] as String
        if(map[PROFILE_IMAGE_URL_KEY] != null) profileImageURL = map[PROFILE_IMAGE_URL_KEY] as String
        if(map[PROFILE_IMAGE_FILE_CLOUD_PATH_KEY] != null) profileImageFileCloudPath = map[PROFILE_IMAGE_FILE_CLOUD_PATH_KEY] as String
        if(map[TYPE_USER_KEY] != null) typeUser = map[TYPE_USER_KEY] as String
        if(map[COUNTRY_KEY] != null) country = map[COUNTRY_KEY] as String
        if(map[STATE_KEY] != null) state = map[STATE_KEY] as String
        if(map[CITY_KEY] != null) city = map[CITY_KEY] as String
    }

    companion object{

        //Same as column name of db and mapping use
        const val UUID_KEY = "uuId"
        const val NAME_KEY = "name"
        const val LAST_NAME_KEY = "lastName"
        const val EMAIL_KEY = "email"
        const val PHONE_NUMBER_KEY = "phoneNumber"
        const val PASSWORD_KEY = "password"
        const val PROFILE_IMAGE_URL_KEY = "profileImageURL"
        const val PROFILE_IMAGE_FILE_CLOUD_PATH_KEY = "profileImageFileCloudPath"

        const val TYPE_USER_KEY = "typeUser"

        const val COUNTRY_KEY = "country"
        const val STATE_KEY = "state"
        const val CITY_KEY = "city"
    }

}