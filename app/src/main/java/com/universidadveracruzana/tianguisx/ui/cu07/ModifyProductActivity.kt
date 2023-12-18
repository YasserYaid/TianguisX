package com.universidadveracruzana.tianguisx.ui.cu07

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
import com.squareup.picasso.Picasso
import com.swein.easyphotopicker.SystemPhotoPickManager
import com.universidadveracruzana.tianguisx.R
import com.universidadveracruzana.tianguisx.databinding.ActivityModifyProductBinding
import com.universidadveracruzana.tianguisx.entities.Product
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.viewmodels.ProductViewModel
import com.universidadveracruzana.tianguisx.viewmodels.ProductViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ModifyProductActivity : AppCompatActivity() {

    private val provider = "com.universidadveracruzana.tianguisx.provider"
    private lateinit var binding : ActivityModifyProductBinding
    private val systemPhotoPickManager = SystemPhotoPickManager(this, provider)
    var currentSeller : Seller? = null
    var currentProduct : Product? = null
    var categorySelected : String? = null
    var quantitySelected : String? = null
    var productStateSelected : String? = null
    var productImageUriSelected : Uri? = null

    private val productViewModel : ProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModifyProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFlow()
        modifyProductGUIConfig()
    }

    fun modifyProductGUIConfig(){
        /////////////Start////////////
        currentSeller = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            intent.getSerializableExtra("currentSeller", Seller::class.java)
        }
        else{
            intent.getSerializableExtra("currentSeller") as Seller
        }
        currentProduct = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            intent.getSerializableExtra("productConsult", Product::class.java)
        }
        else{
            intent.getSerializableExtra("productConsult") as Product
        }
        ///////////////Fill Spinners///////////
        val arrayCategory : Array<String> = resources.getStringArray(R.array.ProductCategory)
        val arrayProductStates : Array<String> = resources.getStringArray(R.array.ProductStates)
        val arrayAvailableQuantity: Array<Int?> = arrayOfNulls<Int>(101)
        for (iterator in 0..100 ) arrayAvailableQuantity[iterator] = iterator
//        binding.ui07ModifyProductSpinnerQuantity.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, arrayAvailableQuantity)
        binding.ui07ModifyProductSpinnerQuantity.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayAvailableQuantity)
        binding.ui07ModifyProductSpinnerCategory.adapter = ArrayAdapter(this , android.R.layout.simple_spinner_item, arrayCategory)
        binding.ui07ModifyProductSpinnerProductState.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item , arrayProductStates)

        //////Load information to components
        for(iterator in 0 .. arrayAvailableQuantity.size -1){
            if(currentProduct?.availableQuantity == iterator){
                binding.ui07ModifyProductSpinnerQuantity.setSelection(iterator)
            } else binding.ui07ModifyProductSpinnerQuantity.setSelection(0)
        }
        for(iterator in 0 .. arrayCategory.size -1){
            if(currentProduct?.category == arrayCategory[iterator]){
                binding.ui07ModifyProductSpinnerCategory.setSelection(iterator)
            } else binding.ui07ModifyProductSpinnerCategory.setSelection(0)
        }
        for(iterator in 0 .. arrayProductStates.size -1){
            if(currentProduct?.productState != null && currentProduct?.productState == arrayCategory[iterator]){
                binding.ui07ModifyProductSpinnerCategory.setSelection(iterator)
            } else binding.ui07ModifyProductSpinnerCategory.setSelection(0)
        }

        if(currentProduct?.productImageURL != null) Picasso.get().load(currentProduct?.productImageURL).into(binding.ui07ModifyProductIvImage)
        binding.ui07ModifyProductEtName.setText(currentProduct?.name)
        binding.ui07ModifyProductEtProductBrand.setText(currentProduct?.brand)
        binding.ui07ModifyProductPrice.setText(currentProduct?.price.toString())
        binding.ui07ModifyProductDescriptionProduct.setText(currentProduct?.description)

        ////////////////Listeners/////////////////////
        setListeners()
    }

    fun setListeners(){
        binding.ui07ModifyProductImageButton.setOnClickListener {
            selectImageFromGallery()
        }
        binding.ui07ModifyProductRegisterButton.setOnClickListener {
            if(validateInformation()){
                Toast.makeText(this@ModifyProductActivity, "El proceso de modificacion de producto puede tomar un tiempo, sea paciente" , Toast.LENGTH_SHORT).show()
                modifyProductProccess()
            }
        }
        binding.ui07ModifyProductSpinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                categorySelected = binding.ui07ModifyProductSpinnerCategory.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.ui07ModifyProductSpinnerQuantity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                quantitySelected = binding.ui07ModifyProductSpinnerQuantity.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

    }

    private fun validateInformation() : Boolean{
        var isValid = false
        if(binding.ui07ModifyProductEtName.text.toString().isNullOrBlank() || binding.ui07ModifyProductEtProductBrand.text.toString().isNullOrBlank() || categorySelected.isNullOrBlank() || binding.ui07ModifyProductPrice.text.toString().isNullOrBlank() || quantitySelected == null || productStateSelected.toString().isNullOrBlank() || binding.ui07ModifyProductDescriptionProduct.text.toString().isNullOrBlank())
            Toast.makeText(this, applicationContext.getResources().getString(R.string.Code_Message_EmptyFields), Toast.LENGTH_SHORT).show()
        else if(productImageUriSelected == null)
            Toast.makeText(this, applicationContext.getResources().getString(R.string.Code_Message_SelectImageFirst), Toast.LENGTH_SHORT).show()
        else isValid = true
        return isValid
    }

    private fun selectImageFromGallery(){
        systemPhotoPickManager.requestPermission {
            it.selectPicture {uri ->
                productImageUriSelected = uri
                binding.ui07ModifyProductIvImage.setImageURI(uri)
            }
        }
    }

    fun modifyProductProccess(){
        currentProduct!!.name = binding.ui07ModifyProductEtName.text.toString()
        currentProduct!!.brand = binding.ui07ModifyProductEtProductBrand.text.toString()
        currentProduct!!.category = categorySelected
        currentProduct!!.price = binding.ui07ModifyProductPrice.text.toString().toDouble()
        currentProduct!!.availableQuantity = quantitySelected?.toInt()
        currentProduct!!.productState = productStateSelected
        currentProduct!!.description = binding.ui07ModifyProductDescriptionProduct.text.toString()
        productViewModel.modifyProduct(currentSeller!!, currentProduct!!, productImageUriSelected)
    }

    private fun initFlow(){
        lifecycleScope.launch(Dispatchers.Main) {

            whenCreated {
                productViewModel.productViewModelState.collect{

                    when(it){

                        is ProductViewModelState.UpdateSuccessFully -> {
                            hideProgress()
                            Log.d("???" , "${it.product}")
                            Toast.makeText(this@ModifyProductActivity, "Producto registrado Exitodamente" , Toast.LENGTH_SHORT).show()
                            finish()
                        }

                        is ProductViewModelState.Loading -> {
                            showProgress()
                        }

                        is ProductViewModelState.Empty -> {
                            hideProgress()
                            Log.d("???" , "VACIO")
                            Toast.makeText(this@ModifyProductActivity, "No se encontro producto" , Toast.LENGTH_SHORT).show()
                        }

                        is ProductViewModelState.Error -> {
                            Log.d("???" , "ERROR : ${it.message}")
                            Toast.makeText(this@ModifyProductActivity, "ERROR : ${it.message}" , Toast.LENGTH_SHORT).show()
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

}