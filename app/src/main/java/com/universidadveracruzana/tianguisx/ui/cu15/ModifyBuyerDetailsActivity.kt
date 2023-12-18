package com.universidadveracruzana.tianguisx.ui.cu15

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
import com.google.firestore.bundle.BundleElement
import com.swein.easyphotopicker.SystemPhotoPickManager
import com.universidadveracruzana.tianguisx.R
import com.universidadveracruzana.tianguisx.databinding.ActivityModifyBuyerDetailsBinding
import com.universidadveracruzana.tianguisx.databinding.ActivityModifySellerDetailsBinding
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.viewmodels.BuyerViewModel
import com.universidadveracruzana.tianguisx.viewmodels.BuyerViewModelState
import com.universidadveracruzana.tianguisx.viewmodels.SellerViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ModifyBuyerDetailsActivity : AppCompatActivity() {

    private val provider = "com.universidadveracruzana.tianguisx.provider"
    private lateinit var binding : ActivityModifyBuyerDetailsBinding
    private val systemPhotoPickManager = SystemPhotoPickManager(this, provider)
    var currentBuyer : Buyer? = null

    private lateinit var firstDay : String
    private lateinit var lastDay : String
    private lateinit var firstHour : String
    private lateinit var lastHour : String

    private val buyerViewModel : BuyerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModifyBuyerDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFlow()
        modifyBuyerDetailsGUIConfig()
    }

    private fun modifyBuyerDetailsGUIConfig(){
        /////////////Start////////////
        currentBuyer = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            intent.getSerializableExtra("currentBuyer", Buyer::class.java)
        }
        else{
            intent.getSerializableExtra("currentBuyer") as Buyer
        }
        ///////////////Fill Spinners///////////
        binding.ui15SpinFirstDay.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Days))
        firstDay = binding.ui15SpinFirstDay.selectedItem.toString()
        binding.ui15SpinLastDay.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Days))
        lastDay = binding.ui15SpinLastDay.selectedItem.toString()
        binding.ui15SpinFirstHour.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Hours))
        firstHour = binding.ui15SpinFirstHour.selectedItem.toString()
        binding.ui15SpinLastHour.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Hours))
        lastHour = binding.ui15SpinLastHour.selectedItem.toString()
        //////Load information to components

//        if(currentSeller?.profileImageURL != null) Picasso.get().load(currentSeller?.profileImageURL ).into(binding.ui07ModifyProductIvImage)
        binding.ui15EtName.setText(currentBuyer?.name)
        binding.ui15EtLastName.setText(currentBuyer?.lastName)
        binding.ui15EtPhone.setText(currentBuyer?.phoneNumber.toString())
        binding.ui15EtAddress.setText(currentBuyer?.streetAddress)
        binding.ui15EtNumberHouse.setText(currentBuyer?.numberAddress.toString())
        binding.ui15EtAditionalStreetAddress.setText(currentBuyer?.descriptionAddress)
        ////////////////Listeners/////////////////////
        setListeners()
    }

    private fun setListeners(){

        binding.ui15ModifyButton.setOnClickListener {
            if(validateInformation()){
                Toast.makeText(this@ModifyBuyerDetailsActivity, "El proceso de modificacion de cuenta puede tomar un tiempo, sea paciente" , Toast.LENGTH_SHORT).show()
                modifyBuyerProccess()
            }
        }


        binding.ui15SpinFirstDay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                firstDay = binding.ui15SpinFirstDay.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.ui15SpinLastDay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                lastDay = binding.ui15SpinLastDay.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.ui15SpinFirstHour.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                firstHour = binding.ui15SpinFirstHour.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.ui15SpinLastHour.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                lastHour = binding.ui15SpinLastHour.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun validateInformation() : Boolean{
        var isValid = false
        if(binding.ui15EtName.text.toString().isNullOrBlank() || binding.ui15EtLastName.text.toString().isNullOrBlank() || binding.ui15EtPhone.text.toString().isNullOrBlank() || binding.ui15EtAditionalStreetAddress.text.toString().isNullOrBlank() || binding.ui15EtNumberHouse.text.toString().isNullOrBlank() || binding.ui15EtAddress.text.toString().isNullOrBlank())
            Toast.makeText(this, applicationContext.getResources().getString(R.string.Code_Message_EmptyFields), Toast.LENGTH_SHORT).show()
        else isValid = true
        return isValid
    }

    private fun modifyBuyerProccess(){
        currentBuyer!!.name = binding.ui15EtName.text.toString()
        currentBuyer!!.lastName = binding.ui15EtLastName.text.toString()
        currentBuyer!!.phoneNumber = binding.ui15EtPhone.text.toString()
        currentBuyer!!.streetAddress = binding.ui15EtAddress.text.toString()
        currentBuyer!!.numberAddress = binding.ui15EtNumberHouse.text.toString().toInt()
        currentBuyer!!.descriptionAddress = binding.ui15EtAditionalStreetAddress.text.toString()
        currentBuyer!!.initialReceptionHour = firstHour
        currentBuyer!!.finalReceptionHour = lastHour
        currentBuyer!!.initialReceptionDay = firstDay
        currentBuyer!!.finalReceptionDay = lastDay
        buyerViewModel.modifyBuyer(currentBuyer!! /*, productImageUriSelected*/)
    }

    private fun initFlow(){
        lifecycleScope.launch(Dispatchers.Main) {

            whenCreated {
                buyerViewModel.buyerViewModelState.collect{

                    when(it){

                        is BuyerViewModelState.UpdateSuccessFully -> {
                            hideProgress()
                            Log.d("???" , "${it.buyer}")
                            currentBuyer = it.buyer
                            Toast.makeText(this@ModifyBuyerDetailsActivity, "Comprador actualizado Exitosamente" , Toast.LENGTH_SHORT).show()
                            finish()
                        }

                        is BuyerViewModelState.Loading -> {
                            showProgress()
                        }

                        is BuyerViewModelState.Empty -> {
                            hideProgress()
                            Log.d("???" , "VACIO")
                            Toast.makeText(this@ModifyBuyerDetailsActivity, "No se encontro comprador" , Toast.LENGTH_SHORT).show()
                        }

                        is BuyerViewModelState.Error -> {
                            Log.d("???" , "ERROR : ${it.message}")
                            Toast.makeText(this@ModifyBuyerDetailsActivity, "ERROR : ${it.message}" , Toast.LENGTH_SHORT).show()
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
}