package com.universidadveracruzana.tianguisx.ui.cu16

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.universidadveracruzana.tianguisx.databinding.ActivityScanQrcodeBinding
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.entities.Product
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.ui.cu18.ShowProductInformationCardActivity
import com.universidadveracruzana.tianguisx.ui.cu22.ShowSellerInformationCardActivity
import com.universidadveracruzana.tianguisx.viewmodels.QRScanViewModel
import com.universidadveracruzana.tianguisx.viewmodels.QRScanViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScanQRCodeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityScanQrcodeBinding
    private val qrScanViewModel : QRScanViewModel by viewModels()
    private var currentBuyer : Buyer? = null

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        isGranted : Boolean ->
        if(isGranted){
            showCamera()
        }else{

        }
    }

    private val scanLauncher = registerForActivityResult(ScanContract()){//<<<<<<<<<<<<<<<---------------
        result : ScanIntentResult ->
        run {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                searchQRProcess(result.contents)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanQrcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFlow()
        scanQRCodeGUIConfig()
    }

    private fun scanQRCodeGUIConfig(){
        ////////////////START ////////////////////////////
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            currentBuyer = intent.getSerializableExtra("currentBuyer", Buyer::class.java)
        }
        else {
            currentBuyer = intent.getSerializableExtra("currentBuyer") as Buyer
        }

        //////////////////LISTENERS////////////////

        binding.ui16ScanQRCodeSearchButton.setOnClickListener{
            checkPermissionCamera(this)
        }
    }

    private fun checkPermissionCamera(context : Context){
        if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            showCamera()
        }else if(shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)){
            Toast.makeText(context, "Camera permission required" , Toast.LENGTH_LONG).show()
        }else{
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun showCamera(){
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan QR Code")
        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        options.setOrientationLocked(false)
        scanLauncher.launch(options)
    }

    private fun searchQRProcess(stringQRCode : String){
        binding.textResult.text = stringQRCode
        val delimitator = "_"
        val arrayWordsFromCode = stringQRCode.split(delimitator).toTypedArray()
        if (arrayWordsFromCode.size == 5 && arrayWordsFromCode[0].equals("Vendedor")){
            qrScanViewModel.searchSellerByUUID(arrayWordsFromCode[1], arrayWordsFromCode[2], arrayWordsFromCode[3], arrayWordsFromCode[4])
        }else if(arrayWordsFromCode.size == 5 && arrayWordsFromCode[0].equals("Producto")){
            qrScanViewModel.searchProductByUUID(arrayWordsFromCode[1], arrayWordsFromCode[2], arrayWordsFromCode[3], arrayWordsFromCode[4])
        }
        else{
            Toast.makeText(this, "El codigo qr no es compatible con la aplicacion", Toast.LENGTH_LONG).show()
        }
    }

    private fun initFlow(){
        lifecycleScope.launch(Dispatchers.Main) {

            whenCreated {
                qrScanViewModel.qrScanViewModelState.collect{

                    when(it){

                        is QRScanViewModelState.ProductFindSuccessFully -> {
                            hideProgress()
                            goToShowProductInformation(it.product)
                        }

                        is QRScanViewModelState.SellerFindSuccessFully -> {
                            hideProgress()
                            goToShowSellerInformation(it.seller)
                        }

                        is QRScanViewModelState.Loading -> {
                            showProgress()
                        }

                        is QRScanViewModelState.Empty -> {
                            hideProgress()
                            Log.d("???" , "VACIO")
                            Toast.makeText(this@ScanQRCodeActivity, "No se encontro informacion disponible con el codigo QR" , Toast.LENGTH_SHORT).show()
                        }

                        is QRScanViewModelState.Error -> {
                            Log.d("???" , "ERROR : ${it.message}")
                            Toast.makeText(this@ScanQRCodeActivity, "ERROR : ${it.message}" , Toast.LENGTH_SHORT).show()
                            hideProgress()
                        }

                        is QRScanViewModelState.None -> {
                            Log.d("???" , "NADA")
                        }

                        is QRScanViewModelState.Clean-> {
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
        qrScanViewModel.cleanViewModelState()
    }

    private fun goToShowSellerInformation(seller : Seller){
        val intent = Intent(applicationContext, ShowSellerInformationCardActivity::class.java)
        intent.putExtra("sellerConsult", seller)
        intent.putExtra("currentBuyer", currentBuyer)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun goToShowProductInformation(product : Product){
        val intent = Intent(applicationContext, ShowProductInformationCardActivity::class.java)
        intent.putExtra("productConsult", product)
        intent.putExtra("currentBuyer", currentBuyer)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}