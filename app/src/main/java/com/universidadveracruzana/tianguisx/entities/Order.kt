package com.universidadveracruzana.tianguisx.entities

import java.io.Serializable
import java.util.Date

class Order : Serializable {
    var uuIdOrder : String? = null
    var uuIdProduct : String? = null
    var uuIdBuyer : String? = null
    var uuIdSeller : String? = null
    var orderBuyerName : String? = null
    var orderCode : String? = null
    var orderProductQuantity : Int? = null
    var orderProductName : String? = null
    var orderProductBrand : String? = null
    var orderProductPrice : Double? = null
    var orderProductUrlImage : String? = null
    var orderState : String? = null
    var orderCity : String? = null
    var orderMarket : String? = null
    var orderDate : Date? = null
    var orderStatus : String? = null

    constructor()

    constructor(
        uuIdOrder: String,
        uuIdProduct: String,
        uuIdBuyer: String,
        uuIdSeller: String,
        orderBuyerName : String,
        orderCode : String,
        orderProductQuantity: Int,
        orderProductName : String,
        orderProductBrand : String,
        orderProductPrice : Double,
        orderProductUrlImage : String,
        orderState: String?,
        orderCity: String?,
        orderMarket: String?,
        orderDate: Date
    ){
        this.uuIdOrder = uuIdOrder
        this.uuIdProduct = uuIdProduct
        this.uuIdBuyer = uuIdBuyer
        this.uuIdSeller = uuIdSeller
        this.orderBuyerName = orderBuyerName
        this.orderCode = orderCode
        this.orderProductQuantity = orderProductQuantity
        this.orderProductName = orderProductName
        this.orderProductBrand = orderProductBrand
        this.orderProductPrice = orderProductPrice
        this.orderProductUrlImage = orderProductUrlImage
        this.orderState = orderState
        this.orderCity = orderCity
        this.orderMarket = orderMarket
        this.orderDate = orderDate
        this.orderStatus = "Apartado"
    }

    companion object{
        const val UUID_ORDER_KEY = "uuIdOrder"
        const val UUID_PRODUCT_KEY = "uuIdProduct"
        const val UUID_BUYER_KEY = "uuIdBuyer"
        const val UUID_SELLER_KEY = "uuIdSeller"
        const val ORDER_BUYER_NAME_KEY = "orderBuyerName"
        const val ORDER_CODE_KEY = "orderCode"
        const val ORDER_PRODUCT_QUANTITY_KEY = "orderProductQuantity"
        const val ORDER_PRODUCT_NAME_KEY = "orderProductName"
        const val ORDER_PRODUCT_BRAND_KEY = "orderProductBrand"
        const val ORDER_PRODUCT_PRICE_KEY = "orderProductPrice"
        const val ORDER_PRODUCT_URL_IMAGE_KEY = "orderProductUrlImage"
        const val ORDER_STATE_KEY = "orderState"
        const val ORDER_CITY_KEY = "orderCity"
        const val ORDER_MARKET_KEY = "orderMarket"
        const val ORDER_DATE_KEY = "orderDate"
        const val ORDER_STATUS_KEY = "orderStatus"
    }

    fun toDictionary() : MutableMap<String, Any>{
        val map = mutableMapOf<String , Any>()
        if(this.uuIdOrder != null) map[Order.UUID_ORDER_KEY] = this.uuIdOrder!!
        if(this.uuIdProduct != null) map[Order.UUID_PRODUCT_KEY] = this.uuIdProduct!!
        if(this.uuIdBuyer != null) map[Order.UUID_BUYER_KEY] = this.uuIdBuyer!!
        if(this.uuIdSeller != null) map[Order.UUID_SELLER_KEY] = this.uuIdSeller!!
        if(this.orderBuyerName != null) map[Order.ORDER_BUYER_NAME_KEY] = this.orderBuyerName!!
        if(this.orderCode != null) map[Order.ORDER_CODE_KEY] = this.orderCode!!
        if(this.orderProductQuantity != null) map[Order.ORDER_PRODUCT_QUANTITY_KEY] = this.orderProductQuantity!!
        if(this.orderProductName != null) map[Order.ORDER_PRODUCT_NAME_KEY] = this.orderProductName!!
        if(this.orderProductBrand != null) map[Order.ORDER_PRODUCT_BRAND_KEY] = this.orderProductBrand!!
        if(this.orderProductPrice != null) map[Order.ORDER_PRODUCT_PRICE_KEY] = this.orderProductPrice!!
        if(this.orderProductUrlImage != null) map[Order.ORDER_PRODUCT_URL_IMAGE_KEY] = this.orderProductUrlImage!!
        if(this.orderState != null) map[Order.ORDER_STATE_KEY] = this.orderState!!
        if(this.orderCity != null) map[Order.ORDER_CITY_KEY] = this.orderCity!!
        if(this.orderMarket != null) map[Order.ORDER_MARKET_KEY] = this.orderMarket!!
        if(this.orderDate != null) map[Order.ORDER_DATE_KEY] = this.orderDate!!
        if(this.orderStatus != null) map[Order.ORDER_STATUS_KEY] = this.orderStatus!!
        return map
    }

    fun parsing(map : MutableMap<String , Any>){
        if(map[Order.UUID_ORDER_KEY] != null) this.uuIdOrder = map[Order.UUID_ORDER_KEY] as String
        if(map[Order.UUID_PRODUCT_KEY] != null) this.uuIdProduct = map[Order.UUID_PRODUCT_KEY] as String
        if(map[Order.UUID_BUYER_KEY] != null) this.uuIdBuyer = map[Order.UUID_BUYER_KEY] as String
        if(map[Order.UUID_SELLER_KEY] != null) this.uuIdSeller = map[Order.UUID_SELLER_KEY] as String
        if(map[Order.ORDER_BUYER_NAME_KEY] != null) this.orderBuyerName = map[Order.ORDER_BUYER_NAME_KEY] as String
        if(map[Order.ORDER_CODE_KEY] != null) this.orderCode = map[Order.ORDER_CODE_KEY] as String
        if(map[Order.ORDER_PRODUCT_QUANTITY_KEY] != null) this.orderProductQuantity = map[Order.ORDER_PRODUCT_QUANTITY_KEY] as Int
        if(map[Order.ORDER_PRODUCT_NAME_KEY] != null) this.orderProductName = map[Order.ORDER_PRODUCT_NAME_KEY] as String
        if(map[Order.ORDER_PRODUCT_BRAND_KEY] != null) this.orderProductBrand = map[Order.ORDER_PRODUCT_BRAND_KEY] as String
        if(map[Order.ORDER_PRODUCT_PRICE_KEY] != null) this.orderProductPrice = map[Order.ORDER_PRODUCT_PRICE_KEY] as Double
        if(map[Order.ORDER_PRODUCT_URL_IMAGE_KEY] != null) this.orderProductUrlImage = map[Order.ORDER_PRODUCT_URL_IMAGE_KEY] as String
        if(map[Order.ORDER_STATE_KEY] != null) this.orderState = map[Order.ORDER_STATE_KEY] as String
        if(map[Order.ORDER_CITY_KEY] != null) this.orderCity = map[Order.ORDER_CITY_KEY] as String
        if(map[Order.ORDER_MARKET_KEY] != null) this.orderMarket = map[Order.ORDER_MARKET_KEY] as String
        if(map[Order.ORDER_DATE_KEY] != null) this.orderDate = map[Order.ORDER_DATE_KEY] as Date
        if(map[Order.ORDER_STATUS_KEY] != null) this.orderStatus = map[Order.ORDER_STATUS_KEY] as String
    }
}