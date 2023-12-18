package com.universidadveracruzana.tianguisx.entities

import java.util.Date

class Review {
    var uuIdReview : String? = null
    var uuIdProduct : String? = null
    var uuIdBuyer : String? = null
    var uuIdSeller : String? = null
    var reviewDate : Date? = null
    var reviewAuthor : String? = null
    var reviewRating : Float? = null
    var reviewDescription : String? = null

    constructor()

    constructor(
        uuIdReview: String?,
        uuIdProduct: String?,
        uuIdBuyer: String?,
        uuIdSeller: String?,
        reviewDate: Date?,
        reviewAuthor: String?,
        reviewRating: Float?,
        reviewDescription: String?,
    ){
        this.uuIdReview = uuIdReview
        this.uuIdProduct = uuIdProduct
        this.uuIdBuyer = uuIdBuyer
        this.uuIdSeller = uuIdSeller
        this.reviewDate = reviewDate
        this.reviewAuthor = reviewAuthor
        this.reviewRating = reviewRating
        this.reviewDescription = reviewDescription
    }

    companion object{
        const val UUID_REVIEW_KEY = "uuIdReview"
        const val UUID_PRODUCT_KEY = "uuIdProduct"
        const val UUID_BUYER_KEY = "uuIdBuyer"
        const val UUID_SELLER_KEY = "uuIdSeller"
        const val REVIEW_DATE_KEY = "reviewDate"
        const val REVIEW_AUTHOR_KEY = "reviewAuthor"
        const val REVIEW_RATING_KEY = "reviewRating"
        const val REVIEW_DESCRIPTION_KEY = "reviewDescription"
    }

    fun toDictionary() : MutableMap<String, Any>{
        val map = mutableMapOf<String , Any>()
        if(this.uuIdReview != null) map[Review.UUID_REVIEW_KEY] = this.uuIdReview!!
        if(this.uuIdProduct != null) map[Review.UUID_PRODUCT_KEY] = this.uuIdProduct!!
        if(this.uuIdBuyer != null) map[Review.UUID_BUYER_KEY] = this.uuIdBuyer!!
        if(this.uuIdSeller != null) map[Review.UUID_SELLER_KEY] = this.uuIdSeller!!
        if(this.reviewDate != null) map[Review.REVIEW_DATE_KEY] = this.reviewDate!!
        if(this.reviewAuthor != null) map[Review.REVIEW_AUTHOR_KEY] = this.reviewAuthor!!
        if(this.reviewRating != null) map[Review.REVIEW_RATING_KEY] = this.reviewRating!!
        if(this.reviewDescription != null) map[Review.REVIEW_DESCRIPTION_KEY] = this.reviewDescription!!
        return map
    }

    fun parsing(map : MutableMap<String , Any>){
        if(map[Review.UUID_REVIEW_KEY] != null) this.uuIdReview = map[Review.UUID_REVIEW_KEY] as String
        if(map[Review.UUID_PRODUCT_KEY] != null) this.uuIdProduct = map[Review.UUID_PRODUCT_KEY] as String
        if(map[Review.UUID_BUYER_KEY] != null) this.uuIdBuyer = map[Review.UUID_BUYER_KEY] as String
        if(map[Review.UUID_SELLER_KEY] != null) this.uuIdSeller = map[Review.UUID_SELLER_KEY] as String
        if(map[Review.REVIEW_DATE_KEY] != null) this.reviewDate = map[Review.REVIEW_DATE_KEY] as Date
        if(map[Review.REVIEW_AUTHOR_KEY] != null) this.reviewAuthor = map[Review.REVIEW_AUTHOR_KEY] as String
        if(map[Review.REVIEW_RATING_KEY] != null) this.reviewRating = map[Review.REVIEW_RATING_KEY] as Float
        if(map[Review.REVIEW_DESCRIPTION_KEY] != null) this.reviewDescription = map[Review.REVIEW_DESCRIPTION_KEY] as String
    }

}