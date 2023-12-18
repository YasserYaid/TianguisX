package com.universidadveracruzana.tianguisx.ui.cu02

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
import com.universidadveracruzana.tianguisx.databinding.ActivityRegisterProductBinding
import com.universidadveracruzana.tianguisx.databinding.ActivityRegisterSellerBinding
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.ui.cu01.LoginActivity
import com.universidadveracruzana.tianguisx.viewmodels.SellerViewModel
import com.universidadveracruzana.tianguisx.viewmodels.SellerViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterSellerActivity : AppCompatActivity() {

    private val provider = "com.universidadveracruzana.tianguisx.provider"
    private val systemPhotoPickManager = SystemPhotoPickManager(this, provider)
    private lateinit var binding: ActivityRegisterSellerBinding
    private lateinit var state : String
    private lateinit var city : String
    private lateinit var market : String
    private lateinit var sellerType : String
    private lateinit var firstDay : String
    private lateinit var lastDay : String
    private lateinit var firstHour : String
    private lateinit var lastHour : String
    private lateinit var bankName : String
    private var currenSellerImageUri : Uri? = null
    var sellerRegistered : Seller? = null

    private val sellerViewModel : SellerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterSellerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFlow()
        registerSellerGUIConfig()
    }

    private fun registerSellerGUIConfig(){
        fillSpinner()
        spinnerListennersConfig()
        binding.ui02BtnSelectImage.setOnClickListener {
            selectImageFromGallery()
        }
        binding.ui02BtnRegisterAccount.setOnClickListener{
            if(validateInformation()){
                registerSellerProcess()
            }
        }
    }

    private fun selectImageFromGallery(){
        systemPhotoPickManager.requestPermission {
            it.selectPicture {uri ->
                currenSellerImageUri = uri
            }
        }
    }

    private fun registerSellerProcess(){
        var sellerToregister = Seller(
            name = binding.ui02EtName.text.toString(),
            lastName = binding.ui02EtLastName.text.toString(),
            email = binding.ui02EtEmail.text.toString(),
            phoneNumber = binding.ui02EtPhone.text.toString(),
            password = binding.ui02EtPassword.text.toString(),
            profileImageURL = null,
            profileImageFileCloudPath = null,
            typeUser = "Seller",
            country = null,
            state = this.state,
            city = this.city,
            qrImageURL = "",
            qrImageFileCloudPath = "",
            localDescription = binding.ui02EtLocalplace.text.toString(),
            typeSeller = sellerType,
            market = this.market,
            initialWorkDay = this.firstDay,
            finalWorkDay = this.lastDay,
            initialWorkHour = this.firstHour,
            finalWorkHour = this.lastHour,
            bankName = this.bankName,
            accountBank = binding.ui02EtAccountNumber.text.toString()
        )
        sellerViewModel.signUp(sellerToregister,currenSellerImageUri!!)
    }

    private fun validateInformation() : Boolean{
        var isValid = false
        if(binding.ui02EtName.text.toString().isNullOrBlank() || binding.ui02EtLastName.text.toString().isNullOrBlank() || binding.ui02EtEmail.text.toString().isNullOrBlank() || binding.ui02EtPhone.text.toString().isNullOrBlank() || binding.ui02EtPassword.text.toString().isNullOrBlank() || binding.ui02EtConfirmPassword.text.toString().isNullOrBlank() || binding.ui02EtLocalplace.text.toString().isNullOrBlank() || binding.ui02EtAccountNumber.text.toString().isNullOrBlank())
            Toast.makeText(this, applicationContext.getResources().getString(R.string.Code_Message_EmptyFields), Toast.LENGTH_SHORT).show()
        else if(!binding.ui02EtPassword.text.toString().equals(binding.ui02EtConfirmPassword.text.toString()))
            Toast.makeText(this, applicationContext.getResources().getString(R.string.Code_Message_DiferentPassword), Toast.LENGTH_SHORT).show()
        else if(currenSellerImageUri == null)
            Toast.makeText(this, applicationContext.getResources().getString(R.string.Code_Message_SelectImageFirst), Toast.LENGTH_SHORT).show()
        else isValid = true
        return isValid
    }

    private fun fillSpinner(){
        binding.ui02SpinFirstDay.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Days))
        firstDay = binding.ui02SpinFirstDay.selectedItem.toString()
        binding.ui02SpinLastDay.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Days))
        lastDay = binding.ui02SpinLastDay.selectedItem.toString()
        binding.ui02SpinFirstHour.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Hours))
        firstHour = binding.ui02SpinFirstHour.selectedItem.toString()
        binding.ui02SpinLastHour.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Hours))
        lastHour = binding.ui02SpinLastHour.selectedItem.toString()
        binding.ui02SpinSellerType.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.SellerTyper))
        sellerType = binding.ui02SpinSellerType.selectedItem.toString()
        binding.ui02SpinState.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Mexico_States))
        state = binding.ui02SpinState.selectedItem.toString()
        binding.ui02SpinCity.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Veraruz_Citys))
        city = binding.ui02SpinCity.selectedItem.toString()
        binding.ui02SpinMarket.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Xalapa_Markets))
        market = binding.ui02SpinMarket.selectedItem.toString()
        binding.ui02SpinBank.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Banks))
        bankName = binding.ui02SpinBank.selectedItem.toString()
    }

    private fun spinnerListennersConfig(){

        binding.ui02SpinFirstDay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                firstDay = binding.ui02SpinFirstDay.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.ui02SpinLastDay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                lastDay = binding.ui02SpinLastDay.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.ui02SpinFirstHour.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                firstHour = binding.ui02SpinFirstHour.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.ui02SpinLastHour.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                lastHour = binding.ui02SpinLastHour.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.ui02SpinSellerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                sellerType = binding.ui02SpinSellerType.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.ui02SpinState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                state = binding.ui02SpinState.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.ui02SpinCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                city = binding.ui02SpinCity.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.ui02SpinMarket.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                market = binding.ui02SpinMarket.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.ui02SpinBank.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                bankName = binding.ui02SpinBank.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun initFlow(){
        lifecycleScope.launch(Dispatchers.Main) {

            whenCreated {
                sellerViewModel.sellerViewModelState.collect{

                    when(it){

                        is SellerViewModelState.RegisterSuccessFully -> {
                            hideProgress()
                            Log.d("???" , "${it.seller}")
                            Toast.makeText(this@RegisterSellerActivity, "Registro Exitoso" , Toast.LENGTH_SHORT).show()
                            sellerRegistered = it.seller
                            goToLogin()
                        }

                        is SellerViewModelState.Loading -> {
                            showProgress()
                        }

                        is SellerViewModelState.Empty -> {
                            hideProgress()
                            Log.d("???" , "VACIO")
                        }

                        is SellerViewModelState.Error -> {
                            Log.d("???" , "ERROR : ${it.message}")
                            Toast.makeText(this@RegisterSellerActivity, "ERROR : ${it.message}" , Toast.LENGTH_SHORT).show()
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

    private fun goToLogin(){
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}