package com.universidadveracruzana.tianguisx.ui.cu20

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import com.universidadveracruzana.tianguisx.R
import com.universidadveracruzana.tianguisx.databinding.ActivityExploreProductsFoundBinding
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.entities.Product
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.ui.adapters.ExploreProductsFoundAdapter
import com.universidadveracruzana.tianguisx.viewmodels.ProductViewModel
import com.universidadveracruzana.tianguisx.viewmodels.ProductViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExploreProductsFoundActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExploreProductsFoundBinding
    private lateinit var state : String
    private lateinit var city : String
    private lateinit var market : String
    private lateinit var category : String
    private lateinit var productName : String

    private var exploreProductsFoundAdapter : ExploreProductsFoundAdapter? = null
    private val productViewModel : ProductViewModel by viewModels()

    private var productsFound : MutableList<Product>? = null
    var currentBuyer : Buyer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExploreProductsFoundBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFlow()
        exploreProductsFoundGUIConfig()
    }

    private fun exploreProductsFoundGUIConfig(){
        ///////////Start/////////////
        currentBuyer = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            intent.getSerializableExtra("currentBuyer", Buyer::class.java)
        }
        else{
            intent.getSerializableExtra("currentBuyer") as Buyer
        }

        state = intent.getStringExtra("stateSelected").toString()
        city = intent.getStringExtra("citySelected").toString()
        market = intent.getStringExtra("marketSelected").toString()
        category = intent.getStringExtra("categorySelected").toString()
        productName = intent.getStringExtra("productName").toString()

        searchProductProcess()
    }


    private fun searchProductProcess(){
        productViewModel.exploreProducts(state, city, market, category)
    }

    private fun initFlow(){
        lifecycleScope.launch(Dispatchers.Main) {

            whenCreated {
                productViewModel.productViewModelState.collect{

                    when(it){

                        is ProductViewModelState.RecoverProductsSuccessFully -> {
                            hideProgress()
                            productsFound = it.productList
                            configureProductFoundList()
                        }

                        is ProductViewModelState.Loading -> {
                            showProgress()
                        }

                        is ProductViewModelState.Empty -> {
                            hideProgress()
                            Log.d("???" , "VACIO")
                            Toast.makeText(this@ExploreProductsFoundActivity, "No se encontraron productos" , Toast.LENGTH_SHORT).show()
                        }

                        is ProductViewModelState.Error -> {
                            Log.d("???" , "ERROR : ${it.message}")
                            Toast.makeText(this@ExploreProductsFoundActivity, "ERROR : ${it.message}" , Toast.LENGTH_SHORT).show()
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

    private fun configureProductFoundList(){
        if(productsFound == null){
            Toast.makeText(this@ExploreProductsFoundActivity, "No se encontraron productos" , Toast.LENGTH_SHORT).show()
        }else{
            if(productName.isNullOrBlank()){
                exploreProductsFoundAdapter = ExploreProductsFoundAdapter(applicationContext, productsFound!!, currentBuyer)
                binding.ui20ListView.adapter = exploreProductsFoundAdapter
            }
            else{
                var productListToShow = mutableListOf<Product>()
                for (product in productsFound!!){// AQUI SE PUEDE CONFIGURAR DE TAL MANERA QUE BUSQUE EN UNA PARTE DE LA CADENA
                    if(product.name?.uppercase().equals(productName.uppercase()))
                        productListToShow.add(product)
                }
                if(productListToShow.isEmpty())
                    Toast.makeText(this@ExploreProductsFoundActivity, "No se encontraron productos con ese nombre" , Toast.LENGTH_SHORT).show()
                else
                    exploreProductsFoundAdapter = ExploreProductsFoundAdapter(applicationContext, productListToShow, currentBuyer)
                binding.ui20ListView.adapter = exploreProductsFoundAdapter
            }
        }
    }
}