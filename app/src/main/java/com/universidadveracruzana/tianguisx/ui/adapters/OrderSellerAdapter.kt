package com.universidadveracruzana.tianguisx.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.DocumentSnapshot
import com.squareup.picasso.Picasso
import com.universidadveracruzana.tianguisx.R
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.entities.MarketUser
import com.universidadveracruzana.tianguisx.entities.Order
import com.universidadveracruzana.tianguisx.entities.Review
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.services.BuyerService
import com.universidadveracruzana.tianguisx.ui.cu06.ShowProductDetailsActivity
import kotlinx.coroutines.runBlocking

class OrderSellerAdapter(
    context : Context,
    orderList : MutableList<Order>,
    seller : Seller?,
    buyer : Buyer?,
    private val onBuyerDetailsClickListener : (Order) -> Unit,
    private val onConfirmClickListener : (Order) -> Unit
) : BaseAdapter() {

    val context = context
    var orderList = orderList
    var currentSeller = seller
    var currentBuyer = buyer
    var consultBuyer = buyer

    var currentOrder : Order? = null

    var sellerOrderRowProductNameTV : TextView? = null
    var sellerOrderRowProductBrandTV : TextView? = null
    var sellerOrderRowProductPriceTV : TextView? = null
    var sellerOrderRowBuyerNameTV : TextView? = null
    var sellerOrderRowOrderCodeTV : TextView? = null
    var sellerOrderRowImageIV : ImageView? = null
    var sellerOrderRowViewBuyerDetailsButton : Button? = null
    var sellerOrderRowConfirmOrderButton : Button? = null


    override fun getCount(): Int {
        return this.orderList.size
    }

    override fun getItem(position: Int): Order {
        return this.orderList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var myConvertView = convertView

        if(myConvertView == null){
            myConvertView = LayoutInflater.from(this.context).inflate(R.layout.seller_order_row, parent, false)
        }

        currentOrder = getItem(position)

        sellerOrderRowProductNameTV = myConvertView?.findViewById<TextView>(R.id.seller_Order_Row_TextView_Name)
        sellerOrderRowProductBrandTV = myConvertView?.findViewById<TextView>(R.id.seller_Order_Row_TextView_Brand)
        sellerOrderRowProductPriceTV = myConvertView?.findViewById<TextView>(R.id.seller_Order_Row_TextView_Price)
        sellerOrderRowBuyerNameTV = myConvertView?.findViewById<TextView>(R.id.seller_Order_Row_TextView_BuyerName)
        sellerOrderRowOrderCodeTV = myConvertView?.findViewById<TextView>(R.id.seller_Order_Row_TextView_Code)
        sellerOrderRowImageIV = myConvertView?.findViewById<ImageView>(R.id.seller_Order_Row_imageView)
        sellerOrderRowViewBuyerDetailsButton = myConvertView?.findViewById<Button>(R.id.seller_Order_row_buyer_details_button)
        sellerOrderRowConfirmOrderButton = myConvertView?.findViewById<Button>(R.id.seller_Order_row_confirm_button)

        Picasso.get().load(currentOrder?.orderProductUrlImage).into(sellerOrderRowImageIV)
        sellerOrderRowProductNameTV?.text = currentOrder?.orderProductName
        sellerOrderRowProductBrandTV?.text = currentOrder?.orderProductBrand
        sellerOrderRowProductPriceTV?.text = currentOrder?.orderProductPrice.toString()
        sellerOrderRowBuyerNameTV?.text = currentOrder?.orderBuyerName
        sellerOrderRowOrderCodeTV?.text = currentOrder?.orderCode


        sellerOrderRowViewBuyerDetailsButton?.setOnClickListener{
            currentOrder = orderList[position]
            if(currentOrder != null){
                onBuyerDetailsClickListener.invoke(currentOrder!!)
            }
        }

        sellerOrderRowConfirmOrderButton?.setOnClickListener {
            currentOrder = orderList[position]
            if(currentOrder != null){
                onConfirmClickListener.invoke(currentOrder!!)
            }
        }

        return myConvertView!!
    }

}