package com.universidadveracruzana.tianguisx.ui.cu12

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
import com.squareup.picasso.Picasso
import com.swein.easyphotopicker.SystemPhotoPickManager
import com.universidadveracruzana.tianguisx.R
import com.universidadveracruzana.tianguisx.databinding.ActivityModifyProductBinding
import com.universidadveracruzana.tianguisx.databinding.ActivityModifySellerDetailsBinding
import com.universidadveracruzana.tianguisx.entities.Product
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.viewmodels.ProductViewModelState
import com.universidadveracruzana.tianguisx.viewmodels.SellerViewModel
import com.universidadveracruzana.tianguisx.viewmodels.SellerViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ModifySellerDetailsActivity : AppCompatActivity() {

    private val provider = "com.universidadveracruzana.tianguisx.provider"
    private lateinit var binding : ActivityModifySellerDetailsBinding
    private val systemPhotoPickManager = SystemPhotoPickManager(this, provider)
    var currentSeller : Seller? = null

    private lateinit var sellerType : String
    private lateinit var firstDay : String
    private lateinit var lastDay : String
    private lateinit var firstHour : String
    private lateinit var lastHour : String
    private lateinit var bankName : String

    private val sellerViewModel : SellerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModifySellerDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFlow()
        modifySellerProfileGUIConfig()
    }

    private fun modifySellerProfileGUIConfig(){
        /////////////Start////////////
        currentSeller = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            intent.getSerializableExtra("currentSeller", Seller::class.java)
        }
        else{
            intent.getSerializableExtra("currentSeller") as Seller
        }
        ///////////////Fill Spinners///////////
        binding.ui12SpinFirstDay.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Days))
        firstDay = binding.ui12SpinFirstDay.selectedItem.toString()
        binding.ui12SpinLastDay.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Days))
        lastDay = binding.ui12SpinLastDay.selectedItem.toString()
        binding.ui12SpinFirstHour.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Hours))
        firstHour = binding.ui12SpinFirstHour.selectedItem.toString()
        binding.ui12SpinLastHour.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Hours))
        lastHour = binding.ui12SpinLastHour.selectedItem.toString()
        binding.ui12SpinSellerType.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.SellerTyper))
        sellerType = binding.ui12SpinSellerType.selectedItem.toString()
        binding.ui12SpinBank.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Banks))
        bankName = binding.ui12SpinBank.selectedItem.toString()

        //////Load information to components

//        if(currentSeller?.profileImageURL != null) Picasso.get().load(currentSeller?.profileImageURL ).into(binding.ui07ModifyProductIvImage)
        binding.ui12EtName.setText(currentSeller?.name)
        binding.ui12EtLastName.setText(currentSeller?.lastName)
        binding.ui12EtPhone.setText(currentSeller?.phoneNumber.toString())
        binding.ui12EtLocalplace.setText(currentSeller?.localDescription)
        binding.ui12EtAccountNumber.setText(currentSeller?.accountBank)
        ////////////////Listeners/////////////////////
        setListeners()
    }

    private fun setListeners(){

        binding.ui02BtnMODIFY.setOnClickListener {
            if(validateInformation()){
                Toast.makeText(this@ModifySellerDetailsActivity, "El proceso de modificacion de cuenta puede tomar un tiempo, sea paciente" , Toast.LENGTH_SHORT).show()
                modifySellerProccess()
            }
        }


        binding.ui12SpinFirstDay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                firstDay = binding.ui12SpinFirstDay.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.ui12SpinLastDay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                lastDay = binding.ui12SpinLastDay.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.ui12SpinFirstHour.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                firstHour = binding.ui12SpinFirstHour.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.ui12SpinLastHour.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                lastHour = binding.ui12SpinLastHour.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.ui12SpinSellerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                sellerType = binding.ui12SpinSellerType.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.ui12SpinBank.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                bankName = binding.ui12SpinBank.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun validateInformation() : Boolean{
        var isValid = false
        if(binding.ui12EtName.text.toString().isNullOrBlank() || binding.ui12EtLastName.text.toString().isNullOrBlank() || binding.ui12EtPhone.text.toString().isNullOrBlank() || binding.ui12EtLocalplace.text.toString().isNullOrBlank() || binding.ui12EtAccountNumber.text.toString().isNullOrBlank())
            Toast.makeText(this, applicationContext.getResources().getString(R.string.Code_Message_EmptyFields), Toast.LENGTH_SHORT).show()
        else isValid = true
        return isValid
    }

    private fun modifySellerProccess(){
        currentSeller!!.name = binding.ui12EtName.text.toString()
        currentSeller!!.lastName = binding.ui12EtLastName.text.toString()
        currentSeller!!.phoneNumber = binding.ui12EtPhone.text.toString()
        currentSeller!!.typeSeller = sellerType
        currentSeller!!.localDescription = binding.ui12EtLocalplace.text.toString()
        currentSeller!!.initialWorkDay = firstDay
        currentSeller!!.finalWorkDay = lastDay
        currentSeller!!.initialWorkHour = firstHour
        currentSeller!!.finalWorkHour = lastHour
        currentSeller!!.bankName = bankName
        currentSeller!!.accountBank = binding.ui12EtAccountNumber.text.toString()
        sellerViewModel.modifySeller(currentSeller!! /*, productImageUriSelected*/)
    }

    private fun initFlow(){
        lifecycleScope.launch(Dispatchers.Main) {

            whenCreated {
                sellerViewModel.sellerViewModelState.collect{

                    when(it){

                        is SellerViewModelState.UpdateSuccessFully -> {
                            hideProgress()
                            Log.d("???" , "${it.seller}")
                            Toast.makeText(this@ModifySellerDetailsActivity, "Vendedor actualizado Exitosamente" , Toast.LENGTH_SHORT).show()
                            finish()
                        }

                        is SellerViewModelState.Loading -> {
                            showProgress()
                        }

                        is SellerViewModelState.Empty -> {
                            hideProgress()
                            Log.d("???" , "VACIO")
                            Toast.makeText(this@ModifySellerDetailsActivity, "No se encontro vendedor" , Toast.LENGTH_SHORT).show()
                        }

                        is SellerViewModelState.Error -> {
                            Log.d("???" , "ERROR : ${it.message}")
                            Toast.makeText(this@ModifySellerDetailsActivity, "ERROR : ${it.message}" , Toast.LENGTH_SHORT).show()
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
}