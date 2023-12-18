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
import com.squareup.picasso.Picasso
import com.universidadveracruzana.tianguisx.R
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.entities.Order
import com.universidadveracruzana.tianguisx.entities.Product
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.ui.cu06.ShowProductDetailsActivity

class OrderBuyerAdapter(
    context : Context,
    orderList : MutableList<Order>,
    seller : Seller?,
    buyer : Buyer?,
    private val onOrderDetailsClickListener : (Order) -> Unit,
    private val onTicketClickListener : (Order) -> Unit,
    private val onCancelClickListener : (Order) -> Unit
) : BaseAdapter() {

    val context = context
    var orderList = orderList
    var currentSeller = seller
    var currentBuyer = buyer

    var currentOrder : Order? = null

    var buyerOrderRowProductNameTV : TextView? = null
    var buyerOrderRowBrandTV : TextView? = null
    var buyerOrderRowPriceTV : TextView? = null
    var buyerOrderRowCodeTV : TextView? = null
    var buyerOrderRowImageIV : ImageView? = null
    var buyerOrderRowViewDetailsButton : Button? = null
    var buyerOrderRowSendTicketButton : Button? = null
    var buyerOrderRowCancelOrderButton : Button? = null


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
            myConvertView = LayoutInflater.from(this.context).inflate(R.layout.buyer_order_row, parent, false)
        }

        currentOrder = getItem(position)

        buyerOrderRowProductNameTV = myConvertView?.findViewById<TextView>(R.id.buyer_Order_Row_TextView_Name)
        buyerOrderRowBrandTV = myConvertView?.findViewById<TextView>(R.id.buyer_Order_Row_TextView_Brand)
        buyerOrderRowPriceTV = myConvertView?.findViewById<TextView>(R.id.buyer_Order_Row_TextView_Price)
        buyerOrderRowCodeTV = myConvertView?.findViewById<TextView>(R.id.buyer_Order_Row_TextView_Code)
        buyerOrderRowImageIV = myConvertView?.findViewById<ImageView>(R.id.buyer_Order_Row_imageView)
        buyerOrderRowViewDetailsButton = myConvertView?.findViewById<Button>(R.id.buyer_Order_row_details_button)
        buyerOrderRowSendTicketButton = myConvertView?.findViewById<Button>(R.id.buyer_Order_row_send_ticket_button)
        buyerOrderRowCancelOrderButton = myConvertView?.findViewById<Button>(R.id.buyer_Order_row_cancel_button)

        Picasso.get().load(currentOrder?.orderProductUrlImage).into(buyerOrderRowImageIV)
        buyerOrderRowProductNameTV?.text = currentOrder?.orderProductName
        buyerOrderRowBrandTV?.text = currentOrder?.orderProductBrand
        buyerOrderRowPriceTV?.text = currentOrder?.orderProductPrice.toString()
        buyerOrderRowCodeTV?.text = currentOrder?.orderCode


        buyerOrderRowViewDetailsButton?.setOnClickListener{
            currentOrder = orderList[position]
            if(currentOrder != null){
                onOrderDetailsClickListener.invoke(currentOrder!!)
            }
        }


        buyerOrderRowSendTicketButton?.setOnClickListener {
            currentOrder = orderList[position]
            if(currentOrder != null){
                onTicketClickListener.invoke(currentOrder!!)
            }
        }


        buyerOrderRowCancelOrderButton?.setOnClickListener {
            currentOrder = orderList[position]
            if(currentOrder != null){
                onCancelClickListener.invoke(currentOrder!!)
            }
        }

        return myConvertView!!
    }

}