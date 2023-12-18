package com.universidadveracruzana.tianguisx.ui.cu04

import android.net.Uri
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
import com.swein.easyphotopicker.SystemPhotoPickManager
import com.universidadveracruzana.tianguisx.R
import com.universidadveracruzana.tianguisx.databinding.ActivityRegisterProductBinding
import com.universidadveracruzana.tianguisx.entities.Product
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.viewmodels.ProductViewModel
import com.universidadveracruzana.tianguisx.viewmodels.ProductViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.UUID

class RegisterProductActivity : AppCompatActivity() {

    private val provider = "com.universidadveracruzana.tianguisx.provider"
    private lateinit var binding : ActivityRegisterProductBinding
    private val systemPhotoPickManager = SystemPhotoPickManager(this, provider)
    private var currentSeller : Seller? = null
    private var categoryProduct : String? = null
    private var productState : String? = null
    private var availableQuantityArray : Array<Int?>? = null
    private var availableQuantity : Int? = null
    private var currentProduct : Product? = null
    private var currenProductImageUri : Uri? = null

    private val productViewModel : ProductViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFlow()
        registerProductGUIConfig()
    }

    private fun registerProductGUIConfig(){
        ///////////////////////START////////////////////////
        currentSeller = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            intent.getSerializableExtra("currentSeller", Seller::class.java)
        }else{
            intent.getSerializableExtra("currentSeller") as Seller
        }
        fillSpinner()

        /////////////////////LISTENERS///////////////////////

        spinnerListennersConfig()

        binding.ui04BtnRegisterProduct.setOnClickListener{
            if(validateInformation()){
                Toast.makeText(this@RegisterProductActivity, "El proceso de registro puede tomar un tiempo, sea paciente" , Toast.LENGTH_SHORT).show()
                registerProductProcess()
            }
        }
        binding.ui04BtnSelectImage.setOnClickListener(){
            selectImageFromGallery()
        }
    }

    private fun selectImageFromGallery(){
        systemPhotoPickManager.requestPermission {
            it.selectPicture {uri ->
                currenProductImageUri = uri
                binding.ui04IvImage.setImageURI(uri)
            }
        }
    }

    private fun registerProductProcess(){
        currentProduct = Product(
            uuIdProduct = getRandomUUIDString(),
            uuIdSeller = currentSeller!!.uuId!!,
            name = binding.ui04EtName.text.toString(),
            brand = binding.ui04EtProductBrand.text.toString(),
            category = categoryProduct!!,
            availableQuantity = availableQuantity!!,
            description = binding.ui04EtProductDescription.text.toString(),
            productImageURL = null,
            productImageFileCloudPath = null,
            price = binding.ui04EtPrice.text.toString().toDouble() ,
            productState = productState!!
        )
        productViewModel.registerProduct(currentSeller!!, currentProduct!!, currenProductImageUri!!)
    }

    private fun validateInformation() : Boolean{
        var isValid = false
        if(binding.ui04EtName.text.toString().isNullOrBlank() || binding.ui04EtProductBrand.text.toString().isNullOrBlank() || categoryProduct.isNullOrBlank() || binding.ui04EtPrice.text.toString().isNullOrBlank() || availableQuantity == null || productState.toString().isNullOrBlank() || binding.ui04EtProductDescription.text.toString().isNullOrBlank())
            Toast.makeText(this, applicationContext.getResources().getString(R.string.Code_Message_EmptyFields), Toast.LENGTH_SHORT).show()
        else if(currenProductImageUri == null)
            Toast.makeText(this, applicationContext.getResources().getString(R.string.Code_Message_SelectImageFirst), Toast.LENGTH_SHORT).show()
        else isValid = true
        return isValid
    }

    private fun fillSpinner(){
        binding.ui04SpinCategory.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.ProductCategory))
        binding.ui04SpinCategory.setSelection(0)
        categoryProduct = binding.ui04SpinCategory.selectedItem.toString()
        binding.ui04SpinProductState.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.ProductStates))
        binding.ui04SpinProductState.setSelection(0)
        productState = binding.ui04SpinProductState.selectedItem.toString()
        availableQuantityArray = arrayOfNulls<Int>(101)
        for (iterator in 0..100 ) availableQuantityArray!![iterator] = iterator
        binding.ui04SpinQuantity.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, availableQuantityArray!!)
        if(binding.ui04SpinQuantity.selectedItem != null) availableQuantity = binding.ui04SpinQuantity.selectedItem as Int
    }

    private fun spinnerListennersConfig(){
        binding.ui04SpinQuantity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                availableQuantity = binding.ui04SpinQuantity.selectedItem as Int
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.ui04SpinCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                categoryProduct = binding.ui04SpinCategory.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.ui04SpinProductState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                productState = binding.ui04SpinProductState.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

    }

    private fun getRandomUUIDString() : String {
        return UUID.randomUUID().toString().replace("-", "")
    }

    private fun initFlow(){
        lifecycleScope.launch(Dispatchers.Main) {

            whenCreated {
                productViewModel.productViewModelState.collect{

                    when(it){

                        is ProductViewModelState.RegisterSuccessFully -> {
                            hideProgress()
                            Log.d("???" , "${it.product}")
                            Toast.makeText(this@RegisterProductActivity, "Producto registrado Exitodamente" , Toast.LENGTH_SHORT).show()
                            clean()
                        }

                        is ProductViewModelState.Loading -> {
                            showProgress()
                        }

                        is ProductViewModelState.Empty -> {
                            hideProgress()
                            Log.d("???" , "VACIO")
                            Toast.makeText(this@RegisterProductActivity, "No se encontro producto" , Toast.LENGTH_SHORT).show()
                        }

                        is ProductViewModelState.Error -> {
                            Log.d("???" , "ERROR : ${it.message}")
                            Toast.makeText(this@RegisterProductActivity, "ERROR : ${it.message}" , Toast.LENGTH_SHORT).show()
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

    private fun clean(){
        binding.ui04IvImage.setImageBitmap(null)
        binding.ui04EtName.setText(null)
        binding.ui04EtProductBrand.setText(null)
        binding.ui04EtPrice.setText(null)
        binding.ui04EtProductDescription.setText(null)
        binding.ui04SpinCategory.setSelection(0)
        binding.ui04SpinQuantity.setSelection(0)
        binding.ui04SpinProductState.setSelection(0)
        changeViewModelStateToClean()
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

}