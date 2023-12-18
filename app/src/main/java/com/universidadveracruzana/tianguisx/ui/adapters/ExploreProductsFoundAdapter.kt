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
import com.universidadveracruzana.tianguisx.entities.Product
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.ui.cu06.ShowProductDetailsActivity
import com.universidadveracruzana.tianguisx.ui.cu18.ShowProductInformationCardActivity

class ExploreProductsFoundAdapter (context : Context, productList : MutableList<Product>, buyer : Buyer?) : BaseAdapter() {
    val context = context
    var productList = productList
    var currentBuyer = buyer

    var currentProduct : Product? = null

    var productRowImageIV : ImageView? = null
    var productRowNameTV : TextView? = null
    var productRowBrandTV : TextView? = null
    var productRowPriceTV : TextView? = null
    var productRowAvailableQuantityTV : TextView? = null
    var productRowDescriptionTV : TextView? = null
    var productRowDetailsBTN : Button? = null

    override fun getCount(): Int {
        return this.productList.size
    }

    override fun getItem(position: Int): Product {
        return this.productList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var myConvertView = convertView

        if(myConvertView == null){
            myConvertView = LayoutInflater.from(this.context).inflate(R.layout.product_row, parent, false)
        }
        currentProduct = getItem(position)

        productRowImageIV = myConvertView?.findViewById<ImageView>(R.id.productRow_imageView)
        productRowNameTV = myConvertView?.findViewById<TextView>(R.id.productRow_TextView_Name)
        productRowBrandTV = myConvertView?.findViewById<TextView>(R.id.productRow_TextView_Brand)
        productRowPriceTV = myConvertView?.findViewById<TextView>(R.id.productRow_TextView_Price)
        productRowAvailableQuantityTV = myConvertView?.findViewById<TextView>(R.id.productRow_TextView_AvailableQuantity)
        productRowDescriptionTV = myConvertView?.findViewById<TextView>(R.id.productRow_TextView_Description)
        productRowDetailsBTN = myConvertView?.findViewById<Button>(R.id.product_row_details_button)

        Picasso.get().load(currentProduct?.productImageURL).into(productRowImageIV)
        productRowNameTV?.text = currentProduct?.name
        productRowBrandTV?.text = currentProduct?.brand
        productRowPriceTV?.text = currentProduct?.price.toString()
        productRowAvailableQuantityTV?.text = currentProduct?.availableQuantity.toString()
        productRowDescriptionTV?.text = currentProduct?.description

        productRowDetailsBTN?.setOnClickListener{
            currentProduct = productList[position]
            goToShowProductInformationCardActivity(currentProduct)
        }

        return myConvertView!!
    }

    fun goToShowProductInformationCardActivity(product : Product?){
        val intent = Intent(context, ShowProductInformationCardActivity::class.java)
        intent.putExtra("productConsult", product)
        intent.putExtra("currentBuyer", currentBuyer)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

}