package com.universidadveracruzana.tianguisx.ui.cu01

import android.content.Intent
import android.net.Uri
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.swein.easyphotopicker.SystemPhotoPickManager
import com.universidadveracruzana.tianguisx.R
import com.universidadveracruzana.tianguisx.databinding.ActivityLoginBinding
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.entities.MarketUser
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.ui.BuyerMainMenuActivity
import com.universidadveracruzana.tianguisx.ui.SellerMainMenuActivity
import com.universidadveracruzana.tianguisx.ui.cu02.RegisterSellerActivity
import com.universidadveracruzana.tianguisx.ui.cu03.RegisterBuyerActivity
import com.universidadveracruzana.tianguisx.viewmodels.MarketUserViewModel
import com.universidadveracruzana.tianguisx.viewmodels.MarketUserViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val marketUserViewModel : MarketUserViewModel by viewModels()
    private lateinit var marketsPaths : List<String>
    private lateinit var typeUser : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        marketsPaths = resources.getStringArray(R.array.Xalapa_Markets).toList()
        fillSpinner()
        initFlow()
        loguinGUIConfig()
    }

    private fun fillSpinner(){
        typeUser = "Comprador"
        binding.ui01SpinUsetype.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.UserTypes))
        typeUser = binding.ui01SpinUsetype.selectedItem.toString()
    }

    private fun loguinGUIConfig(){
        ///////////////////////////////////LISTENERS///////////////////////////////////////////////
        binding.ui01TvBuyerLink.setOnClickListener{
            goToRegisterBuyer()
        }

        binding.ui01TvSellerLink.setOnClickListener{
            goToRegisterSeller()
        }

        binding.ui01BtnLogin.setOnClickListener{
            if(validateInformation()){
                marketUserViewModel.sigInMarketUser(binding.ui01EtEmail.text.toString(), binding.ui01EtPassword.text.toString(), typeUser, marketsPaths)
            }
        }

        binding.ui01SpinUsetype.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                typeUser = binding.ui01SpinUsetype.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun validateInformation() : Boolean{
        var isValid = false
        if (binding.ui01EtEmail.text.toString().isNullOrBlank() || binding.ui01EtEmail.text.toString().isNullOrEmpty() || binding.ui01EtPassword.text.toString().isNullOrBlank() || binding.ui01EtPassword.text.toString().isNullOrEmpty())
            Toast.makeText(this, "No puede haber campos vacios", Toast.LENGTH_SHORT).show()
        else isValid = true
        return isValid
    }

    private fun goToRegisterSeller(){
        val intent = Intent(applicationContext, RegisterSellerActivity::class.java)
        startActivity(intent)
    }

    private fun goToRegisterBuyer(){
        val intent = Intent(applicationContext, RegisterBuyerActivity::class.java)
        startActivity(intent)
    }

    private fun goToSellerMainMenu(sellerFound : Seller){
        val intent = Intent(applicationContext, SellerMainMenuActivity::class.java)
        intent.putExtra("currentSeller", sellerFound)
        startActivity(intent)
        finish()
    }

    private fun goToBuyerMainMenu(buyer : Buyer){
        val intent = Intent(applicationContext, BuyerMainMenuActivity::class.java)
        intent.putExtra("currentBuyer", buyer)
        startActivity(intent)
        finish()
    }

    private fun initFlow(){
        lifecycleScope.launch(Dispatchers.Main) {

            whenCreated {
                marketUserViewModel.marketUserViewModelState.collect{

                    when(it){

                        is MarketUserViewModelState.SignInBuyerSuccessFully -> {
                            hideProgress()
                            Log.d("???" , "${it.buyer}")
                            Toast.makeText(this@LoginActivity, "Buyer In Success" , Toast.LENGTH_SHORT).show()
                            goToBuyerMainMenu(it.buyer)
                        }

                        is MarketUserViewModelState.SignInSellerSuccessFully -> {
                            hideProgress()
                            Log.d("???" , "${it.seller}")
                            Toast.makeText(this@LoginActivity, "Seller In Success" , Toast.LENGTH_SHORT).show()
                            goToSellerMainMenu(it.seller)
                        }

                        is MarketUserViewModelState.Loading -> {
                            showProgress()
                        }

                        is MarketUserViewModelState.Empty -> {
                            hideProgress()
                            Log.d("???" , "VACIO")
                            Toast.makeText(this@LoginActivity, "No se encontraron las credenciales" , Toast.LENGTH_SHORT).show()
                        }

                        is MarketUserViewModelState.Error -> {
                            Log.d("???" , "ERROR : ${it.message}")
                            Toast.makeText(this@LoginActivity, "ERROR : ${it.message}" , Toast.LENGTH_SHORT).show()
                            hideProgress()
                        }

                        is MarketUserViewModelState.None -> {
                            Log.d("???" , "NADA")
                        }

                        is MarketUserViewModelState.Clean-> {
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
        marketUserViewModel.cleanViewModelState()
    }

}