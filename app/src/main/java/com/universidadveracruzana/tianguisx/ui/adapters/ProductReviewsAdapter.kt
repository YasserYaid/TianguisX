package com.universidadveracruzana.tianguisx.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.universidadveracruzana.tianguisx.R
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.entities.Product
import com.universidadveracruzana.tianguisx.entities.Review
import com.universidadveracruzana.tianguisx.entities.Seller

class ProductReviewsAdapter(context : Context, reviewList : MutableList<Review>, seller : Seller?, buyer : Buyer?) : BaseAdapter() {

    val context = context
    var reviewList = reviewList
    var currentSeller = seller
    var currentBuyer = buyer

    var currentReview : Review? = null

    var reviewRowAuthorNameTV : TextView? = null
    var reviewRowDescriptionTV : TextView? = null
    var reviewRowRatingBar : RatingBar? = null

    override fun getCount(): Int {
        return this.reviewList.size
    }

    override fun getItem(position: Int): Review {
        return this.reviewList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var myConvertView = convertView

        if(myConvertView == null){
            myConvertView = LayoutInflater.from(this.context).inflate(R.layout.review_row, parent, false)
        }
        currentReview = getItem(position)

        reviewRowAuthorNameTV = myConvertView?.findViewById<TextView>(R.id.reviewRow_TextView_AuthorName)
        reviewRowDescriptionTV = myConvertView?.findViewById<TextView>(R.id.reviewRow_TextView_Description)
        reviewRowRatingBar = myConvertView?.findViewById<RatingBar>(R.id.reviewRow_RatingBar)

        reviewRowAuthorNameTV?.text = currentReview?.reviewAuthor
        reviewRowDescriptionTV?.text = currentReview?.reviewDescription
        reviewRowRatingBar?.rating = currentReview?.reviewRating!!

        return myConvertView!!
    }

}