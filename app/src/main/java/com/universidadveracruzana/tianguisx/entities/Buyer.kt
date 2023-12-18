package com.universidadveracruzana.tianguisx.entities

import java.io.Serializable

class Buyer : MarketUser, Serializable{

    var streetAddress : String? = null
    var numberAddress : Int? = null
    var descriptionAddress : String? = null
    var initialReceptionDay : String? = null
    var finalReceptionDay : String? = null
    var initialReceptionHour : String? = null
    var finalReceptionHour : String? = null

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
        city : String,
        streetAddress : String?,
        numberAddress : Int?,
        descriptionAddress : String?,
        initialReceptionDay : String?,
        finalReceptionDay : String?,
        initialReceptionHour : String?,
        finalReceptionHour : String?
    ) : super(
        name,
        lastName,
        email,
        phoneNumber,
        password,
        profileImageURL,
        profileImageFileCloudPath,
        typeUser,
        country,
        state,
        city
        )
    {
        this.streetAddress = streetAddress
        this.numberAddress = numberAddress
        this.descriptionAddress = descriptionAddress
        this.initialReceptionDay = initialReceptionDay
        this.finalReceptionDay = finalReceptionDay
        this.initialReceptionHour = initialReceptionHour
        this.finalReceptionHour = finalReceptionHour
    }

    override fun toDictionary() : MutableMap<String , Any> {
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

        if(streetAddress != null) map[STREET_ADDRESS_KEY] = streetAddress!!
        if(numberAddress != null) map[NUMBER_ADDRESS_KEY] = numberAddress!!
        if(descriptionAddress != null) map[DESCRIPTION_ADDRESS_KEY] = descriptionAddress!!
        if(initialReceptionDay != null) map[INITIAL_RECEPTION_DAY_KEY] = initialReceptionDay!!
        if(finalReceptionDay != null) map[FINAL_RECEPTION_DAY_KEY] = finalReceptionDay!!
        if(initialReceptionHour != null) map[INITIAL_RECEPTION_HOUR_KEY] = initialReceptionHour!!
        if(finalReceptionHour != null) map[FINAL_RECEPTION_HOUR_KEY] = finalReceptionHour!!

        return map
    }

    override fun parsing(map : MutableMap<String , Any>){
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

        if(map[STREET_ADDRESS_KEY] != null) streetAddress = map[STREET_ADDRESS_KEY] as String
        if(map[NUMBER_ADDRESS_KEY] != null) numberAddress = map[NUMBER_ADDRESS_KEY] as Int
        if(map[DESCRIPTION_ADDRESS_KEY] != null) descriptionAddress = map[DESCRIPTION_ADDRESS_KEY] as String
        if(map[INITIAL_RECEPTION_DAY_KEY] != null) initialReceptionDay = map[INITIAL_RECEPTION_DAY_KEY] as String
        if(map[FINAL_RECEPTION_DAY_KEY] != null) finalReceptionDay = map[FINAL_RECEPTION_DAY_KEY] as String
        if(map[INITIAL_RECEPTION_HOUR_KEY] != null) initialReceptionHour = map[INITIAL_RECEPTION_HOUR_KEY] as String
        if(map[FINAL_RECEPTION_HOUR_KEY] != null) finalReceptionHour = map[FINAL_RECEPTION_HOUR_KEY] as String
    }

    companion object{
        const val STREET_ADDRESS_KEY = "streetAddress"
        const val NUMBER_ADDRESS_KEY = "numberAddress"
        const val DESCRIPTION_ADDRESS_KEY = "descriptionAddress"
        const val INITIAL_RECEPTION_DAY_KEY = "initialReceptionDay"
        const val FINAL_RECEPTION_DAY_KEY = "finalReceptionDay"
        const val INITIAL_RECEPTION_HOUR_KEY = "initialReceptionHour"
        const val FINAL_RECEPTION_HOUR_KEY = "finalReceptionHour"
    }
}