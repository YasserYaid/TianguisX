package com.universidadveracruzana.tianguisx.ui.cu20

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import com.universidadveracruzana.tianguisx.R
import com.universidadveracruzana.tianguisx.databinding.ActivityExploreProductsBinding
import com.universidadveracruzana.tianguisx.databinding.ActivityRegisterSellerBinding
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.entities.Product
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.ui.cu01.LoginActivity
import com.universidadveracruzana.tianguisx.ui.cu07.ModifyProductActivity
import com.universidadveracruzana.tianguisx.viewmodels.ProductViewModel
import com.universidadveracruzana.tianguisx.viewmodels.ProductViewModelState
import com.universidadveracruzana.tianguisx.viewmodels.SellerViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExploreProductsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExploreProductsBinding
    private lateinit var state : String
    private lateinit var city : String
    private lateinit var market : String
    private lateinit var category : String
    var currentBuyer : Buyer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExploreProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        exploreProductsGUIConfig()
    }

    private fun exploreProductsGUIConfig(){
        ///////////START/////////////////
        currentBuyer = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            intent.getSerializableExtra("currentBuyer", Buyer::class.java)
        }
        else{
            intent.getSerializableExtra("currentBuyer") as Buyer
        }

        fillSpinner()
        spinnerListennersConfig()

        //////////LISTENER///////////////7
        binding.ui20ExploreProductsExploreButton.setOnClickListener{
            goToExploreProductList()
        }
    }

    private fun fillSpinner(){
        binding.ui20ExploreProductsSpinnerState.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Mexico_States))
        binding.ui20ExploreProductsSpinnerState.setSelection(0)
        state = binding.ui20ExploreProductsSpinnerState.selectedItem.toString()
        binding.ui20ExploreProductsSpinnerCity.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Veraruz_Citys))
        binding.ui20ExploreProductsSpinnerCity.setSelection(0)
        city = binding.ui20ExploreProductsSpinnerCity.selectedItem.toString()
        binding.ui20ExploreProductsSpinnerMarket.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Xalapa_Markets))
        binding.ui20ExploreProductsSpinnerMarket.setSelection(0)
        market = binding.ui20ExploreProductsSpinnerMarket.selectedItem.toString()
        binding.ui20ExploreProductsSpinnerProductCategory.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.ProductCategory))
        binding.ui20ExploreProductsSpinnerProductCategory.setSelection(0)
        category = binding.ui20ExploreProductsSpinnerProductCategory.selectedItem.toString()
    }

    private fun spinnerListennersConfig(){
        binding.ui20ExploreProductsSpinnerState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                state = binding.ui20ExploreProductsSpinnerState.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.ui20ExploreProductsSpinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                city = binding.ui20ExploreProductsSpinnerCity.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.ui20ExploreProductsSpinnerMarket.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                market = binding.ui20ExploreProductsSpinnerMarket.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.ui20ExploreProductsSpinnerProductCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                category = binding.ui20ExploreProductsSpinnerProductCategory.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun goToExploreProductList(){
        var productName = ""
        if(!binding.ui20ExploreProductsProductName.text.toString().isNullOrBlank())
            productName = binding.ui20ExploreProductsProductName.text.toString()

        val intent = Intent(applicationContext, ExploreProductsFoundActivity::class.java)
        intent.putExtra("stateSelected", state)
        intent.putExtra("citySelected", city)
        intent.putExtra("marketSelected", market)
        intent.putExtra("categorySelected", category)
        intent.putExtra("productName", productName)
        intent.putExtra("currentBuyer", currentBuyer)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

}