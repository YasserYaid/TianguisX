package com.universidadveracruzana.tianguisx.ui.cu13

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
import com.universidadveracruzana.tianguisx.databinding.ActivityShowProductReviewsBinding
import com.universidadveracruzana.tianguisx.databinding.ActivityShowSellerReviewsBinding
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.entities.Product
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.ui.adapters.ProductReviewsAdapter
import com.universidadveracruzana.tianguisx.ui.adapters.SellerReviewsAdapter
import com.universidadveracruzana.tianguisx.viewmodels.ProductViewModel
import com.universidadveracruzana.tianguisx.viewmodels.ProductViewModelState
import com.universidadveracruzana.tianguisx.viewmodels.SellerViewModel
import com.universidadveracruzana.tianguisx.viewmodels.SellerViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShowSellerReviewsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityShowSellerReviewsBinding
    private var sellerReviewsAdapter : SellerReviewsAdapter? = null
    private val sellerViewModel : SellerViewModel by viewModels()

    var currentSeller : Seller? = null
    var currentBuyer : Buyer? = null
    var consultSeller : Seller? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowSellerReviewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFlow()
        showSellerReviewsGUIConfig()
    }

    private fun showSellerReviewsGUIConfig(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            currentBuyer = intent.getSerializableExtra("currentBuyer", Buyer::class.java)
            currentSeller = intent.getSerializableExtra("currentSeller", Seller::class.java)
            consultSeller = intent.getSerializableExtra("sellerConsult", Seller::class.java)
        }
        else {
            currentBuyer = intent.getSerializableExtra("currentBuyer") as Buyer
            currentSeller = intent.getSerializableExtra("currentSeller") as Seller
            consultSeller = intent.getSerializableExtra("sellerConsult") as Seller
        }
        recoverReviewsConsultSeller()
    }

    private fun recoverReviewsConsultSeller(){
        sellerViewModel.recoverSellerReviews(consultSeller!!)
    }

    private fun initFlow(){
        lifecycleScope.launch(Dispatchers.Main) {

            whenCreated {
                sellerViewModel.sellerViewModelState.collect{

                    when(it){

                        is SellerViewModelState.RecoverReviewsSuccessFully -> {
                            hideProgress()
                            sellerReviewsAdapter = SellerReviewsAdapter(applicationContext, it.reviewList, null, null)
                            binding.ui13ListView.adapter = sellerReviewsAdapter
                        }

                        is SellerViewModelState.Loading -> {
                            showProgress()
                        }

                        is SellerViewModelState.Empty -> {
                            hideProgress()
                            Log.d("???" , "VACIO")
                            Toast.makeText(this@ShowSellerReviewsActivity, "No se encontraron reseñas" , Toast.LENGTH_SHORT).show()
                        }

                        is SellerViewModelState.Error -> {
                            Log.d("???" , "ERROR : ${it.message}")
                            Toast.makeText(this@ShowSellerReviewsActivity, "ERROR : ${it.message}" , Toast.LENGTH_SHORT).show()
                            hideProgress()
                        }

                        is SellerViewModelState.None -> {
                            Log.d("???" , "NADA")
                        }

                        is SellerViewModelState.Clean-> {
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
        sellerViewModel.cleanViewModelState()
    }

    override fun onRestart() {
        super.onRestart()
        recoverReviewsConsultSeller()
    }
}
