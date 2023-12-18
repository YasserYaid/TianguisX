package com.universidadveracruzana.tianguisx.ui.cu18

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import com.squareup.picasso.Picasso
import com.universidadveracruzana.tianguisx.databinding.ActivityShowProductInformationCardBinding
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.entities.Order
import com.universidadveracruzana.tianguisx.entities.Product
import com.universidadveracruzana.tianguisx.entities.Review
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.ui.commonmessages.ReviewDialog
import com.universidadveracruzana.tianguisx.ui.cu08.ShowProductReviewsActivity
import com.universidadveracruzana.tianguisx.viewmodels.ProductViewModel
import com.universidadveracruzana.tianguisx.viewmodels.ProductViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

class ShowProductInformationCardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowProductInformationCardBinding
    var currentProduct : Product? = null
    var currentBuyer : Buyer? = null
    var averageRating : Float? = null
//    var currentSeller : Seller? = null

    private val productViewModel : ProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowProductInformationCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFlow()
        showProductInformationCardGUIConfig()
    }

    fun showProductInformationCardGUIConfig(){

        ////////////////START ////////////////////////////
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            currentBuyer = intent.getSerializableExtra("currentBuyer", Buyer::class.java)
            currentProduct = intent.getSerializableExtra("productConsult", Product::class.java)
        }
        else {
            currentBuyer = intent.getSerializableExtra("currentBuyer") as Buyer
            currentProduct = intent.getSerializableExtra("productConsult") as Product
        }

        if(currentProduct != null){
            productViewModel.recoverProductReviews(currentProduct!!)
            Picasso.get().load(currentProduct?.productImageURL).into(binding.ui18ShowProductInformationCardIvProductImage)
            binding.ui18ShowProductInformationCardEtProductName.text = " " + currentProduct?.name
            binding.ui18ShowProductInformationCardEtProductBrand.text = " " + currentProduct?.brand
            binding.ui18ShowProductInformationCardEtProductPrice.text = " " + currentProduct?.price.toString()
            binding.ui18ShowProductInformationCardEtProductCategory.text = " " + currentProduct?.category
            binding.ui18ShowProductInformationCardEtProductDesciption.text = " " + currentProduct?.description
            binding.ui18ShowProductInformationCardEtProductQuantity.text = " " + currentProduct?.availableQuantity.toString()
            if(currentProduct?.rating != null) binding.ui18ShowProductInformationCardEtProductRating.rating = currentProduct?.rating!!

            ////////////////Listeners/////////////////////
            binding.ui18ShowProductInformationCardSelectToBuyButton.setOnClickListener{
                orderProductProcess()
            }
            binding.ui18ShowProductInformationCardRegisterEvaluationButton.setOnClickListener{
                registerProductEvaluationProcess()
            }
            binding.ui18ShowProductInformationCardShowEvaluationsButton.setOnClickListener{
                goToShowProductReviews()
            }
            binding.ui18ShowProductInformationCardShareQRButton.setOnClickListener {
                shareQRProcess()
            }
        }
        else{
            Toast.makeText(this@ShowProductInformationCardActivity, "No se pudo encontrar el producto" , Toast.LENGTH_SHORT).show()
        }
    }

    private fun initFlow(){
        lifecycleScope.launch(Dispatchers.Main) {

            whenCreated {
                productViewModel.productViewModelState.collect{

                    when(it){

                        is ProductViewModelState.RecoverReviewsSuccessFully -> {
                            hideProgress()
                            getAverageRatingProduct(it.reviewList)
                        }

                        is ProductViewModelState.RegisterOrderSuccesFully -> {
                            hideProgress()
                            Toast.makeText(this@ShowProductInformationCardActivity, "Producto apartado exitosamente" , Toast.LENGTH_SHORT).show()
                            finish()
                        }

                        is ProductViewModelState.RegisterReviewSuccesFully -> {
                            hideProgress()
                            Toast.makeText(this@ShowProductInformationCardActivity, "Reseña registrada exitosamente" , Toast.LENGTH_SHORT).show()
                            finish()
                        }

                        is ProductViewModelState.Loading -> {
                            showProgress()
                        }

                        is ProductViewModelState.Empty -> {
                            hideProgress()
                            Log.d("???" , "VACIO")
                        }

                        is ProductViewModelState.Error -> {
                            Log.d("???" , "ERROR : ${it.message}")
                            Toast.makeText(this@ShowProductInformationCardActivity, "ERROR : ${it.message}" , Toast.LENGTH_SHORT).show()
                            hideProgress()
                        }

                        is ProductViewModelState.None -> {
                            Log.d("???" , "NADA")
                        }

                        is ProductViewModelState.Clean-> {
                            Log.d("???", "ViewModel State Renovado")
                        }

                        else -> {
                            Log.d("???", "¿Como paso?")
                        }
                    }
                }
            }
        }
    }

    private fun showProgress(){
        binding.progress.visibility = View.VISIBLE
    }

    private fun hideProgress(){
        binding.progress.visibility = View.GONE
    }

    private fun changeViewModelStateToClean(){
        productViewModel.cleanViewModelState()
    }

    private fun orderProductProcess(){
//        val simpleDateFormat = SimpleDateFormat("dd/M/yyyy")
        val currentDate = Date()

        var order = Order(
            uuIdOrder = getRandomUUIDString(),
            uuIdProduct = currentProduct!!.uuIdProduct!!,
            uuIdBuyer = currentBuyer!!.uuId!!,
            uuIdSeller = currentProduct!!.uuIdSeller!!,
            orderBuyerName = currentBuyer?.name + " " + currentBuyer?.lastName,
            orderCode = currentBuyer?.name + "_" + currentBuyer?.lastName + "_" + currentDate,
            orderProductQuantity = 1,
            orderProductName = currentProduct!!.name!!,
            orderProductBrand = currentProduct!!.brand!!,
            orderProductPrice = currentProduct!!.price!!,
            orderProductUrlImage = currentProduct!!.productImageURL!!,
            orderState = currentBuyer!!.state!!,
            orderCity = currentBuyer!!.city!!,
            orderMarket = null,
            orderDate = currentDate
        )
        productViewModel.registerOrderToBuy(currentBuyer!!, order)
    }

    private fun registerProductEvaluationProcess(){
        var submitReview : Review?
        ReviewDialog(
            onSubmitClickListener = {actualReview ->
                if(actualReview.reviewRating == null || actualReview.reviewDescription.isNullOrBlank()) {
                    Toast.makeText(this@ShowProductInformationCardActivity, "Se debe ingresar comentarios y un rating", Toast.LENGTH_LONG).show()
                }else if(actualReview.reviewRating!! < 1 || actualReview.reviewDescription!!.length < 10){
                    Toast.makeText(this@ShowProductInformationCardActivity, "Se debe ingresar el rating y la longitud de la descripcion debe ser mayor o igual a diez caracteres", Toast.LENGTH_LONG).show()
                }else{
                    submitReview = actualReview
                    submitReview?.uuIdReview = getRandomUUIDString()
                    submitReview?.uuIdProduct = currentProduct?.uuIdProduct
                    submitReview?.uuIdBuyer = currentBuyer?.uuId
                    submitReview?.uuIdSeller = currentProduct?.uuIdSeller
                    submitReview?.reviewDate = Date()
                    submitReview?.reviewAuthor = currentBuyer?.name + " " + currentBuyer?.lastName
                    productViewModel.registerProductReview(currentBuyer!!, submitReview!!)
                }
            }
        ).show(supportFragmentManager, "dialog")
    }

    private fun getRandomUUIDString() : String {
        return UUID.randomUUID().toString().replace("-", "")
    }

    private fun goToShowProductReviews(){
        val intent = Intent(applicationContext, ShowProductReviewsActivity::class.java)
        intent.putExtra("productConsult", currentProduct)
        intent.putExtra("currentBuyer", currentBuyer)
        intent.putExtra("currentSeller", Seller())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private  fun shareQRProcess(){
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, currentProduct?.qrImageURL)
            type = "text/plain"
        }
        var shareIntent = Intent.createChooser(intent, null)
        startActivity(shareIntent)
    }

    private fun getAverageRatingProduct(productReviewList : MutableList<Review>){
        var totalRating = 0.0f
        for (review in productReviewList){
            totalRating += review.reviewRating!!
        }
        averageRating = totalRating / productReviewList.count()
        currentProduct?.rating = averageRating
        binding.ui18ShowProductInformationCardEtProductRating.rating = currentProduct?.rating!!
        binding.ui18ShowProductInformationCardEtProductAverage.text = (currentProduct?.rating!! * 2).toString()
    }
}