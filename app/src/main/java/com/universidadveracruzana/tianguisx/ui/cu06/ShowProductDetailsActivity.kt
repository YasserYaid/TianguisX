package com.universidadveracruzana.tianguisx.ui.cu06

import android.content.Intent
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
import com.universidadveracruzana.tianguisx.databinding.ActivityShowProductDetailsBinding
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.entities.Product
import com.universidadveracruzana.tianguisx.entities.Review
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.ui.adapters.ProductReviewsAdapter
import com.universidadveracruzana.tianguisx.ui.cu07.ModifyProductActivity
import com.universidadveracruzana.tianguisx.ui.cu08.ShowProductReviewsActivity
import com.universidadveracruzana.tianguisx.viewmodels.ProductViewModel
import com.universidadveracruzana.tianguisx.viewmodels.ProductViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShowProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowProductDetailsBinding

    var currentSeller : Seller? = null
    var currentProduct : Product? = null
    var currentBuyer : Buyer? = null
    var averageRating : Float? = null

    private val productViewModel : ProductViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFlow()
        showProductDetailsGUIConfig()
    }

    fun showProductDetailsGUIConfig(){
        ////////////////START ////////////////////////////
        currentSeller = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            intent.getSerializableExtra("currentSeller", Seller::class.java)
        }
        else{
            intent.getSerializableExtra("currentSeller") as Seller
        }
        currentBuyer = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            intent.getSerializableExtra("currentBuyer", Buyer::class.java)
        }
        else{
            intent.getSerializableExtra("currentBuyer") as Buyer
        }
        currentProduct = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            intent.getSerializableExtra("productConsult", Product::class.java)
        }
        else{
            intent.getSerializableExtra("productConsult") as Product
        }

        productViewModel.recoverProductReviews(currentProduct!!)

        Picasso.get().load(currentProduct?.productImageURL).into(binding.ui06IvProductImage)
        binding.ui06TvProductName.text = " " + currentProduct?.name
        binding.ui06TvProductBrand.text = " " + currentProduct?.brand
        binding.ui06TvProductPrice.text = " " + currentProduct?.price.toString()
        binding.ui06TvProductCategory.text = " " + currentProduct?.category
        binding.ui06TvProductDescription.text = " " + currentProduct?.description
        binding.ui06TvProductQuantity.text = " " + currentProduct?.availableQuantity.toString()
        if(currentProduct?.rating != null) binding.ui06RatingbarProductEvaluation.rating = currentProduct?.rating!!

        ////////////////Listeners/////////////////////
        binding.ui06ButtonModifyProduct.setOnClickListener{
            goToModifyProduct()
        }
        binding.ui06ButtonShowEvaluations.setOnClickListener{
            goToShowProductReviews()
        }
        binding.ui06ButtonDeleteProduct.setOnClickListener {
            deleteProductProcess()
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

                        is ProductViewModelState.DeleteSuccessFully -> {
                            hideProgress()
                            Toast.makeText(this@ShowProductDetailsActivity, "El producto se elimino correctamente" , Toast.LENGTH_SHORT).show()
//                            this@ShowProductDetailsActivity.onBackPressedDispatcher.onBackPressed()
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
                            Toast.makeText(this@ShowProductDetailsActivity, "ERROR : ${it.message}" , Toast.LENGTH_SHORT).show()
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

    private fun goToModifyProduct(){
        val intent = Intent(applicationContext, ModifyProductActivity::class.java)
        intent.putExtra("productConsult", currentProduct)
        intent.putExtra("currentSeller", currentSeller)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun goToShowProductReviews(){
        val intent = Intent(applicationContext, ShowProductReviewsActivity::class.java)
        intent.putExtra("productConsult", currentProduct)
        intent.putExtra("currentSeller", currentSeller)
        intent.putExtra("currentBuyer", currentBuyer)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun deleteProductProcess(){
        //AlertDialog etc validation method etc
        productViewModel.deleteProduct(currentSeller!! , currentProduct!!)
    }

    private fun getAverageRatingProduct(productReviewList : MutableList<Review>){
        var totalRating = 0.0f
        for (review in productReviewList){
            totalRating += review.reviewRating!!
        }
        averageRating = totalRating / productReviewList.count()
        currentProduct?.rating = averageRating
        binding.ui06RatingbarProductEvaluation.rating = currentProduct?.rating!!
        binding.ui06ShowProductDetailsEtProductAverage.text = (currentProduct?.rating!! * 2).toString()
    }

}