package com.universidadveracruzana.tianguisx.ui.cu05

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
import com.universidadveracruzana.tianguisx.databinding.ActivityCheckInventoryBinding
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.ui.adapters.ProductsAdapter
import com.universidadveracruzana.tianguisx.ui.cu04.RegisterProductActivity
import com.universidadveracruzana.tianguisx.viewmodels.ProductViewModel
import com.universidadveracruzana.tianguisx.viewmodels.ProductViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CheckInventoryActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCheckInventoryBinding
    private var productsAdapter : ProductsAdapter? = null
    private val productViewModel : ProductViewModel by viewModels()

    var currentSeller : Seller? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFlow()
        checkInventoryGUIConfig()
    }

    private fun checkInventoryGUIConfig(){
        ///////////Start/////////////

        currentSeller = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            intent.getSerializableExtra("currentSeller", Seller::class.java)
        }else{
            intent.getSerializableExtra("currentSeller") as Seller
        }

        recoverInventoryCurrentSeller()

        //////////////Listener/////////

        binding.ui06BtnAddProduct.setOnClickListener{
            goToRegisterProduct()
        }
    }

    private fun recoverInventoryCurrentSeller(){
        productViewModel.recoverSellerInventory(currentSeller!!)
    }

    private fun initFlow(){
        lifecycleScope.launch(Dispatchers.Main) {

            whenCreated {
                productViewModel.productViewModelState.collect{

                    when(it){

                        is ProductViewModelState.RecoverProductsSuccessFully -> {
                            hideProgress()
                            productsAdapter = ProductsAdapter(applicationContext, it.productList, currentSeller, null)
                            binding.ui05ListView.adapter = productsAdapter
                        }

                        is ProductViewModelState.Loading -> {
                            showProgress()
                        }

                        is ProductViewModelState.Empty -> {
                            hideProgress()
                            Log.d("???" , "VACIO")
                            Toast.makeText(this@CheckInventoryActivity, "No se encontraron productos" , Toast.LENGTH_SHORT).show()
                        }

                        is ProductViewModelState.Error -> {
                            Log.d("???" , "ERROR : ${it.message}")
                            Toast.makeText(this@CheckInventoryActivity, "ERROR : ${it.message}" , Toast.LENGTH_SHORT).show()
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

    private fun goToRegisterProduct(){
        val intent = Intent(applicationContext, RegisterProductActivity::class.java)
        intent.putExtra("currentSeller", currentSeller)
        startActivity(intent)
    }

    override fun onRestart() {
        super.onRestart()
        recoverInventoryCurrentSeller()
    }
}