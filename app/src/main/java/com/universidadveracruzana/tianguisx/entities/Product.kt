package com.universidadveracruzana.tianguisx.entities

import com.google.firebase.firestore.Exclude
import java.io.Serializable

class Product : Serializable {

    var uuIdProduct : String? = null
    var uuIdSeller : String? = null
    var name : String? = null
    var brand : String? = null
    var category : String? = null
    var availableQuantity : Int? = null
    var description : String? = null
    var productImageURL : String? = null
    var productImageFileCloudPath : String? = null
    var qrImageURL : String? = null
    var qrImageFileCloudPath : String? = null
    var price : Double? = null
    var productState : String? = null
    var outboundOrderQuantity : Int? = null
    var reviews : MutableList<Review>? = null
    @set: Exclude @get: Exclude var rating : Float? = null


    constructor()

    constructor(
        uuIdProduct : String,
        uuIdSeller : String,
        name : String,
        brand : String,
        category : String,
        availableQuantity : Int,
        description : String,
        productImageURL : String?,
        productImageFileCloudPath : String?,
        price : Double,
        productState : String?
    ){
        this.uuIdProduct = uuIdProduct
        this.uuIdSeller = uuIdSeller
        this.name = name
        this.brand = brand
        this.category = category
        this.availableQuantity = availableQuantity
        this.description = description
        this.productImageURL = productImageURL
        this.productImageFileCloudPath = productImageFileCloudPath
        this.price = price
        this.productState = productState
        this.outboundOrderQuantity = 0
    }

    companion object{
        const val UUID_PRODUCT_KEY = "uuIdProduct"
        const val UUID_SELLER_KEY = "uuIdSeller"
        const val NAME_KEY = "name"
        const val BRAND_KEY = "brand"
        const val CATEGORY_KEY = "category"
        const val AVAILABLE_QUANTITY_KEY = "availableQuantity"
        const val DESCRIPTION_KEY = "description"
        const val PRODUCT_IMAGE_URL_KEY = "productImageURL"
        const val PRODUCT_IMAGE_FILE_CLOUD_PATH_KEY = "productImageFileCloudPath"
        const val QR_IMAGE_URL_KEY = "qrImageURL"
        const val QR_IMAGE_FILE_CLOUD_PATH_KEY = "qrImageFileCloudPath"
        const val PRICE_KEY = "price"
        const val PRODUCT_STATE_KEY = "productState"
        const val OUTBOUND_ORDER_QUANTITY_KEY = "outboundOrderQuantity"
    }

    fun toDictionary() : MutableMap<String, Any>{
        val map = mutableMapOf<String , Any>()
        if(this.uuIdProduct != null) map[UUID_PRODUCT_KEY] = this.uuIdProduct!!
        if(this.uuIdSeller != null) map[UUID_SELLER_KEY] = this.uuIdSeller!!
        if(this.name != null) map[NAME_KEY] = this.name!!
        if(this.brand != null) map[BRAND_KEY] = this.brand!!
        if(this.category != null) map[CATEGORY_KEY] = this.category!!
        if(this.availableQuantity != null) map[AVAILABLE_QUANTITY_KEY] = this.availableQuantity!!
        if(this.description != null) map[DESCRIPTION_KEY] = this.description!!
        if(this.productImageURL != null) map[PRODUCT_IMAGE_URL_KEY] = this.productImageURL!!
        if(this.productImageFileCloudPath != null) map[PRODUCT_IMAGE_FILE_CLOUD_PATH_KEY] = this.productImageFileCloudPath!!
        if(this.qrImageURL != null) map[QR_IMAGE_URL_KEY] = this.qrImageURL!!
        if(this.qrImageFileCloudPath != null) map[QR_IMAGE_FILE_CLOUD_PATH_KEY] = this.qrImageFileCloudPath!!
        if(this.price != null) map[PRICE_KEY] = this.price!!
        if(this.productState != null) map[PRODUCT_STATE_KEY] = this.productState!!
        if(this.outboundOrderQuantity != null) map[OUTBOUND_ORDER_QUANTITY_KEY] = this.outboundOrderQuantity!!
        return map
    }

    fun parsing(map : MutableMap<String , Any>){
        if(map[UUID_PRODUCT_KEY] != null) this.uuIdProduct = map[UUID_PRODUCT_KEY] as String
        if(map[UUID_SELLER_KEY] != null) this.uuIdSeller = map[UUID_SELLER_KEY] as String
        if(map[NAME_KEY] != null) this.name = map[NAME_KEY] as String
        if(map[BRAND_KEY] != null) this.brand = map[BRAND_KEY] as String
        if(map[CATEGORY_KEY] != null) this.category = map[CATEGORY_KEY] as String
        if(map[AVAILABLE_QUANTITY_KEY] != null) this.availableQuantity = map[AVAILABLE_QUANTITY_KEY] as Int
        if(map[DESCRIPTION_KEY] != null) this.description = map[DESCRIPTION_KEY] as String
        if(map[PRODUCT_IMAGE_URL_KEY] != null) this.productImageURL = map[PRODUCT_IMAGE_URL_KEY] as String
        if(map[PRODUCT_IMAGE_FILE_CLOUD_PATH_KEY] != null) this.productImageFileCloudPath = map[PRODUCT_IMAGE_FILE_CLOUD_PATH_KEY] as String
        if(map[QR_IMAGE_URL_KEY] != null) this.qrImageURL = map[QR_IMAGE_URL_KEY] as String
        if(map[QR_IMAGE_FILE_CLOUD_PATH_KEY] != null) this.qrImageFileCloudPath = map[QR_IMAGE_FILE_CLOUD_PATH_KEY] as String
        if(map[PRICE_KEY] != null) this.price = map[PRICE_KEY] as Double
        if(map[PRODUCT_STATE_KEY] != null) this.productState = map[PRODUCT_STATE_KEY] as String
        if(map[OUTBOUND_ORDER_QUANTITY_KEY] != null) this.outboundOrderQuantity = map[OUTBOUND_ORDER_QUANTITY_KEY] as Int
    }

}