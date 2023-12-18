package com.universidadveracruzana.tianguisx.ui.cu08

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import com.universidadveracruzana.tianguisx.R
import com.universidadveracruzana.tianguisx.databinding.ActivityCheckInventoryBinding
import com.universidadveracruzana.tianguisx.databinding.ActivityShowProductDetailsBinding
import com.universidadveracruzana.tianguisx.databinding.ActivityShowProductReviewsBinding
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.entities.Product
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.ui.adapters.ProductReviewsAdapter
import com.universidadveracruzana.tianguisx.ui.adapters.ProductsAdapter
import com.universidadveracruzana.tianguisx.ui.cu04.RegisterProductActivity
import com.universidadveracruzana.tianguisx.viewmodels.ProductViewModel
import com.universidadveracruzana.tianguisx.viewmodels.ProductViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShowProductReviewsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityShowProductReviewsBinding
    private var productReviewsAdapter : ProductReviewsAdapter? = null
    private val productViewModel : ProductViewModel by viewModels()

    var currentSeller : Seller? = null
    var currentBuyer : Buyer? = null
    var currentProduct : Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowProductReviewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFlow()
        showProductReviewsGUIConfig()
    }

    private fun showProductReviewsGUIConfig(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            currentBuyer = intent.getSerializableExtra("currentBuyer", Buyer::class.java)
            currentSeller = intent.getSerializableExtra("currentSeller", Seller::class.java)
            currentProduct = intent.getSerializableExtra("productConsult", Product::class.java)
        }
        else {
            currentBuyer = intent.getSerializableExtra("currentBuyer") as Buyer
            currentSeller = intent.getSerializableExtra("currentSeller") as Seller
            currentProduct = intent.getSerializableExtra("productConsult") as Product
        }
        recoverReviewsCurrentProduct()
    }

    private fun recoverReviewsCurrentProduct(){
        productViewModel.recoverProductReviews(currentProduct!!)
    }

    private fun initFlow(){
        lifecycleScope.launch(Dispatchers.Main) {

            whenCreated {
                productViewModel.productViewModelState.collect{

                    when(it){

                        is ProductViewModelState.RecoverReviewsSuccessFully -> {
                            hideProgress()
                            productReviewsAdapter = ProductReviewsAdapter(applicationContext, it.reviewList, null, null)
                            binding.ui08ListView.adapter = productReviewsAdapter
                        }

                        is ProductViewModelState.Loading -> {
                            showProgress()
                        }

                        is ProductViewModelState.Empty -> {
                            hideProgress()
                            Log.d("???" , "VACIO")
                            Toast.makeText(this@ShowProductReviewsActivity, "No se encontraron reseñas" , Toast.LENGTH_SHORT).show()
                        }

                        is ProductViewModelState.Error -> {
                            Log.d("???" , "ERROR : ${it.message}")
                            Toast.makeText(this@ShowProductReviewsActivity, "ERROR : ${it.message}" , Toast.LENGTH_SHORT).show()
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

    override fun onRestart() {
        super.onRestart()
        recoverReviewsCurrentProduct()
    }

}