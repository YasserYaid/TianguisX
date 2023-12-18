package com.universidadveracruzana.tianguisx.entities

import com.google.firebase.firestore.Exclude
import java.io.Serializable

class Seller : MarketUser, Serializable {

    var qrImageURL : String? = null
    var qrImageFileCloudPath : String? = null
    var localDescription : String? = null
    var typeSeller : String? = null
    var market : String? = null
    var initialWorkDay : String? = null
    var finalWorkDay : String? = null
    var initialWorkHour : String? = null
    var finalWorkHour : String? = null
    var bankName : String? = null
    var accountBank : String? = null
    @set: Exclude @get: Exclude var productsList : MutableList<Product>? = null
    @set: Exclude @get: Exclude var rating : Float? = null

    constructor()

    constructor(
        name : String,
        lastName : String,
        email : String,
        phoneNumber : String,
        password : String,
        profileImageURL : String?,
        profileImageFileCloudPath : String?,
        typeUser : String,
        country : String?,
        state : String,
        city : String,
        qrImageURL : String,
        qrImageFileCloudPath : String,
        localDescription : String,
        typeSeller : String,
        market : String,
        initialWorkDay : String?,
        finalWorkDay : String?,
        initialWorkHour : String?,
        finalWorkHour : String?,
        bankName : String,
        accountBank : String
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
        this.qrImageURL = qrImageURL
        this.qrImageFileCloudPath = qrImageFileCloudPath
        this.localDescription = localDescription
        this.typeSeller = typeSeller
        this.market = market
        this.initialWorkDay = initialWorkDay
        this.finalWorkDay = finalWorkDay
        this.initialWorkHour = initialWorkHour
        this.finalWorkHour = finalWorkHour
        this.bankName = bankName
        this.accountBank = accountBank
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

        if(qrImageURL != null) map[QR_IMAGE_URL_KEY] = qrImageURL!!
        if(qrImageFileCloudPath != null) map[QR_IMAGE_FILE_CLOUD_PATH_KEY] = qrImageFileCloudPath!!
        if(localDescription != null) map[LOCAL_DESCRIPTION_KEY] = localDescription!!
        if(typeSeller != null) map[TYPE_SELLER_KEY] = typeSeller!!
        if(market != null) map[MARKET_KEY] = market!!
        if(initialWorkDay != null) map[INITIAL_WORK_DAY_KEY] = initialWorkDay!!
        if(finalWorkDay != null) map[FINAL_WORK_DAY_KEY] = finalWorkDay!!
        if(initialWorkHour != null) map[INITIAL_WORK_HOUR_KEY] = initialWorkHour!!
        if(finalWorkHour != null) map[FINAL_WORK_HOUR_KEY] = finalWorkHour!!
        if(bankName != null) map[BANK_NAME_KEY] = bankName!!
        if(accountBank != null) map[ACCOUNT_BANK_KEY] = accountBank!!

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

        if(map[QR_IMAGE_URL_KEY] != null) qrImageURL = map[QR_IMAGE_URL_KEY] as String
        if(map[QR_IMAGE_FILE_CLOUD_PATH_KEY] != null) qrImageFileCloudPath = map[QR_IMAGE_FILE_CLOUD_PATH_KEY] as String
        if(map[LOCAL_DESCRIPTION_KEY] != null) localDescription = map[LOCAL_DESCRIPTION_KEY] as String
        if(map[TYPE_SELLER_KEY] != null) typeSeller = map[TYPE_SELLER_KEY] as String
        if(map[MARKET_KEY] != null) market = map[MARKET_KEY] as String
        if(map[INITIAL_WORK_DAY_KEY] != null) initialWorkDay = map[INITIAL_WORK_DAY_KEY] as String
        if(map[FINAL_WORK_DAY_KEY] != null) finalWorkDay = map[FINAL_WORK_DAY_KEY] as String
        if(map[INITIAL_WORK_HOUR_KEY] != null) initialWorkHour = map[INITIAL_WORK_HOUR_KEY] as String
        if(map[FINAL_WORK_HOUR_KEY] != null) finalWorkHour = map[FINAL_WORK_HOUR_KEY] as String
        if(map[BANK_NAME_KEY] != null) bankName = map[BANK_NAME_KEY] as String
        if(map[ACCOUNT_BANK_KEY] != null) accountBank = map[ACCOUNT_BANK_KEY] as String
    }

    companion object{
        const val BANK_NAME_KEY = "bankName"
        const val ACCOUNT_BANK_KEY = "accountBank"
        const val QR_IMAGE_URL_KEY = "qrImageURL"
        const val QR_IMAGE_FILE_CLOUD_PATH_KEY = "qrImageFileCloudPath"
        const val LOCAL_DESCRIPTION_KEY = "localDescription"
        const val TYPE_SELLER_KEY = "typeSeller"
        const val MARKET_KEY = "market"
        const val INITIAL_WORK_DAY_KEY = "initialWorkDay"
        const val FINAL_WORK_DAY_KEY = "finalWorkDay"
        const val INITIAL_WORK_HOUR_KEY = "initialWorkHour"
        const val FINAL_WORK_HOUR_KEY = "finalWorkHour"
    }

}