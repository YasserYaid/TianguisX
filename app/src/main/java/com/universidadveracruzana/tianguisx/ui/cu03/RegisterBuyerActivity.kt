package com.universidadveracruzana.tianguisx.ui.cu03

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
import com.swein.easyphotopicker.SystemPhotoPickManager
import com.universidadveracruzana.tianguisx.R
import com.universidadveracruzana.tianguisx.databinding.ActivityRegisterBuyerBinding
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.ui.cu01.LoginActivity
import com.universidadveracruzana.tianguisx.viewmodels.BuyerViewModel
import com.universidadveracruzana.tianguisx.viewmodels.BuyerViewModelState
import com.universidadveracruzana.tianguisx.viewmodels.SellerViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterBuyerActivity : AppCompatActivity() {

    private val provider = "com.universidadveracruzana.tianguisx.provider"
    private val systemPhotoPickManager = SystemPhotoPickManager(this, provider)
    private lateinit var binding: ActivityRegisterBuyerBinding
    private lateinit var state : String
    private lateinit var city : String
    private lateinit var firstDay : String
    private lateinit var lastDay : String
    private lateinit var firstHour : String
    private lateinit var lastHour : String
    private var currenBuyerImageUri : Uri? = null
    var buyerRegistered : Buyer? = null

    private val buyerViewModel : BuyerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBuyerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFlow()
        registerBuyerGUIConfig()
    }

    private fun registerBuyerGUIConfig(){
        fillSpinner()
        spinnerListennersConfig()
        binding.ui03BtnSelectImage.setOnClickListener {
            selectImageFromGallery()
        }
        binding.ui03BtnRegisterAccount.setOnClickListener{
            if(validateInformation()){
                registerBuyerProcess()
            }
        }
    }

    private fun selectImageFromGallery(){
        systemPhotoPickManager.requestPermission {
            it.selectPicture {uri ->
                currenBuyerImageUri = uri
            }
        }
    }

    private fun registerBuyerProcess(){
        var buyerToRegister = Buyer(
            name = binding.ui03RegisterBuyerEtName.text.toString(),
            lastName = binding.ui03RegisterBuyerEtLastName.text.toString(),
            email = binding.ui03RegisterBuyerEtEmail.text.toString(),
            phoneNumber = binding.ui03RegisterBuyerEtPhone.text.toString(),
            password = binding.ui03RegisterBuyerEtPassword.text.toString(),
            profileImageURL = null,
            profileImageFileCloudPath = null,
            typeUser = "Buyer",
            country = null,
            state = this.state,
            city = this.city,
            streetAddress = binding.ui03RegisterBuyerEtStreet.text.toString(),
            numberAddress = binding.ui03RegisterBuyerEtNumberStreet.text.toString().toInt(),
            descriptionAddress = binding.ui03RegisterBuyerEtAditionalStreetDescription.text.toString(),
            initialReceptionDay = firstDay,
            finalReceptionDay = lastDay,
            initialReceptionHour = firstHour,
            finalReceptionHour = lastHour,
        )
        buyerViewModel.signUp(buyerToRegister, currenBuyerImageUri!!)
    }

    private fun validateInformation() : Boolean {
        var isValid = false
        if(binding.ui03RegisterBuyerEtName.text.toString().isNullOrBlank() || binding.ui03RegisterBuyerEtLastName.text.toString().isNullOrBlank() || binding.ui03RegisterBuyerEtEmail.text.toString().isNullOrBlank() || binding.ui03RegisterBuyerEtPhone.text.toString().isNullOrBlank() || binding.ui03RegisterBuyerEtPassword.text.toString().isNullOrBlank() || binding.ui03RegisterBuyerEtConfirmPassword.text.toString().isNullOrBlank())
            Toast.makeText(this, applicationContext.getResources().getString(R.string.Code_Message_EmptyFields), Toast.LENGTH_SHORT).show()
        else if(!binding.ui03RegisterBuyerEtPassword.text.toString().equals(binding.ui03RegisterBuyerEtConfirmPassword.text.toString()))
            Toast.makeText(this, applicationContext.getResources().getString(R.string.Code_Message_DiferentPassword), Toast.LENGTH_SHORT).show()
        else if(currenBuyerImageUri == null)
            Toast.makeText(this, applicationContext.getResources().getString(R.string.Code_Message_SelectImageFirst), Toast.LENGTH_SHORT).show()
        else isValid = true
        return isValid
    }

    private fun fillSpinner(){
        binding.ui03RegisterBuyerSpinnerFirstDay.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Days))
        firstDay = binding.ui03RegisterBuyerSpinnerFirstDay.selectedItem.toString()
        binding.ui03RegisterBuyerSpinnerLastDay.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Days))
        lastDay = binding.ui03RegisterBuyerSpinnerLastDay.selectedItem.toString()
        binding.ui03RegisterBuyerSpinnerFirstHour.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Hours))
        firstHour = binding.ui03RegisterBuyerSpinnerFirstHour.selectedItem.toString()
        binding.ui03RegisterBuyerSpinnerLastHour.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Hours))
        lastHour = binding.ui03RegisterBuyerSpinnerLastHour.selectedItem.toString()
        binding.ui03RegisterBuyerSpinnerState.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Mexico_States))
        state = binding.ui03RegisterBuyerSpinnerState.selectedItem.toString()
        binding.ui03RegisterBuyerSpinnerCity.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Veraruz_Citys))
        city = binding.ui03RegisterBuyerSpinnerCity.selectedItem.toString()
    }

    private fun spinnerListennersConfig(){

        binding.ui03RegisterBuyerSpinnerFirstDay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                firstDay = binding.ui03RegisterBuyerSpinnerFirstDay.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.ui03RegisterBuyerSpinnerLastDay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                lastDay = binding.ui03RegisterBuyerSpinnerLastDay.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.ui03RegisterBuyerSpinnerFirstHour.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                firstHour = binding.ui03RegisterBuyerSpinnerFirstHour.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.ui03RegisterBuyerSpinnerLastHour.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                lastHour = binding.ui03RegisterBuyerSpinnerLastHour.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.ui03RegisterBuyerSpinnerState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                state = binding.ui03RegisterBuyerSpinnerState.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.ui03RegisterBuyerSpinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                city = binding.ui03RegisterBuyerSpinnerCity.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

    }

    private fun initFlow(){
        lifecycleScope.launch(Dispatchers.Main) {

            whenCreated {
                buyerViewModel.buyerViewModelState.collect{

                    when(it){

                        is BuyerViewModelState.RegisterSuccessFully -> {
                            hideProgress()
                            Log.d("???" , "${it.buyer}")
                            Toast.makeText(this@RegisterBuyerActivity, "Registro Exitoso" , Toast.LENGTH_SHORT).show()
                            buyerRegistered = it.buyer
                            goToLogin()
                        }

                        is BuyerViewModelState.Loading -> {
                            showProgress()
                        }

                        is BuyerViewModelState.Empty -> {
                            hideProgress()
                            Log.d("???" , "VACIO")
                        }

                        is BuyerViewModelState.Error -> {
                            Log.d("???" , "ERROR : ${it.message}")
                            Toast.makeText(this@RegisterBuyerActivity, "ERROR : ${it.message}" , Toast.LENGTH_SHORT).show()
                            hideProgress()
                        }

                        is BuyerViewModelState.None -> {
                            Log.d("???" , "NADA")
                        }

                        is BuyerViewModelState.Clean-> {
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
        buyerViewModel.cleanViewModelState()
    }

    private fun goToLogin(){
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}