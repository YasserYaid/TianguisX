package com.universidadveracruzana.tianguisx.ui.cu11

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import com.squareup.picasso.Picasso
import com.universidadveracruzana.tianguisx.databinding.ActivityShowSellerProfileDetailsBinding
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.entities.Review
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.ui.adapters.SellerReviewsAdapter
import com.universidadveracruzana.tianguisx.ui.cu01.LoginActivity
import com.universidadveracruzana.tianguisx.ui.cu06.ShowProductDetailsActivity
import com.universidadveracruzana.tianguisx.ui.cu12.ModifySellerDetailsActivity
import com.universidadveracruzana.tianguisx.ui.cu13.ShowSellerReviewsActivity
import com.universidadveracruzana.tianguisx.viewmodels.SellerViewModel
import com.universidadveracruzana.tianguisx.viewmodels.SellerViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.Executors

class ShowSellerProfileDetailsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityShowSellerProfileDetailsBinding
    var currentSeller : Seller? = null
    var averageRating : Float? = null
    var imageUri: Uri? = null

    private val sellerViewModel : SellerViewModel by viewModels()

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowSellerProfileDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFlow()
        showSellerDetailsGUIConfig()
    }

    private fun showSellerDetailsGUIConfig(){
        ////////START///////////////
        currentSeller = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            intent.getSerializableExtra("currentSeller", Seller::class.java)
        }
        else{
            intent.getSerializableExtra("currentSeller") as Seller
        }
        sellerViewModel.recoverSellerReviews(currentSeller!!)

        Picasso.get().load(currentSeller?.profileImageURL).into(binding.ui11IvImageProfile)
        binding.ui11EtName.text = " " + currentSeller?.name
        binding.ui11EtCorreo.text = " " + currentSeller?.email
        binding.ui11EtPhone.text = " " + currentSeller?.phoneNumber
        binding.ui11etSalesType.text = " " + currentSeller?.typeSeller
        binding.ui11etState.text = " " + currentSeller?.state
        binding.ui11etCity.text = " " + currentSeller?.city
        binding.ui11etMarket.text = " " + currentSeller?.market
        binding.ui11etLocal.text = " " + currentSeller?.localDescription
        binding.ui11EtFirstDay.text = " " + currentSeller?.initialWorkDay + " "
        binding.ui11EtLastDay.text = " " + currentSeller?.finalWorkDay
        binding.ui11EtFirstHour.text = " " + currentSeller?.initialWorkHour + " "
        binding.ui11EtLastHour.text = " " + currentSeller?.finalWorkHour
        binding.ui11EtBank.text = " " + currentSeller?.bankName
        binding.ui11EtBankAccount.text = " " + currentSeller?.accountBank

        ////////////////Listeners
        binding.ui11ShowEvaluationButton.setOnClickListener {
            goToShowEvaluations()
        }
        binding.ui11ShareButton.setOnClickListener {
            shareQRProcess()
        }
        binding.ui11DeleteButton.setOnClickListener {
            deleteSellerProccess()
        }
        binding.ui11ModifyButton.setOnClickListener {
            goToModifySeller()
        }

    }

    private fun initFlow(){
        lifecycleScope.launch(Dispatchers.Main) {

            whenCreated {
                sellerViewModel.sellerViewModelState.collect{

                    when(it){

                        is SellerViewModelState.RecoverReviewsSuccessFully -> {
                            hideProgress()
                            getAverageRatingSeller(it.reviewList)
                        }

                        is SellerViewModelState.DeleteSuccessFully -> {
                            hideProgress()
                            Toast.makeText(this@ShowSellerProfileDetailsActivity, "El vendedor se elimino correctamente" , Toast.LENGTH_SHORT).show()
                            val intent = Intent(applicationContext, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
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
                            Toast.makeText(this@ShowSellerProfileDetailsActivity, "ERROR : ${it.message}" , Toast.LENGTH_SHORT).show()
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

    private fun goToShowEvaluations(){
        val intent = Intent(applicationContext, ShowSellerReviewsActivity::class.java)
        intent.putExtra("currentSeller", currentSeller)
        intent.putExtra("currentBuyer", Buyer())
        intent.putExtra("sellerConsult", currentSeller)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun goToModifySeller(){
        val intent = Intent(applicationContext, ModifySellerDetailsActivity::class.java)
        intent.putExtra("currentSeller", currentSeller)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private  fun shareQRProcess(){
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, currentSeller?.qrImageURL)
            type = "text/plain"
        }
        var shareIntent = Intent.createChooser(intent, null)
        startActivity(shareIntent)
    }


    private  fun deleteSellerProccess(){
        val alertDialogBuilder = AlertDialog.Builder(this@ShowSellerProfileDetailsActivity)
        alertDialogBuilder.setMessage("Esta seguro de que desea eliminar la cuenta esta, accion no se puede deshacer")
        alertDialogBuilder.setTitle("Eliminar Comprador")
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setPositiveButton("yes"){ _,_ ->
            if(currentSeller != null){
                sellerViewModel.deleteSeller(currentSeller!!)
            }
        }
        alertDialogBuilder.setNegativeButton("no"){ _,_ ->
            Toast.makeText(this@ShowSellerProfileDetailsActivity, "La cuenta no se eliminara" , Toast.LENGTH_SHORT).show()
        }
        val alertDialogBox = alertDialogBuilder.create()
        alertDialogBox.show()
    }

    private fun getAverageRatingSeller(sellerReviewList : MutableList<Review>){
        var totalRating = 0.0f
        for (review in sellerReviewList){
            totalRating += review.reviewRating!!
        }
        averageRating = totalRating / sellerReviewList.count()
        currentSeller?.rating = averageRating
        binding.ui11RatingbarProductEvaluation.rating = currentSeller?.rating!!
        binding.ui11ShowProductDetailsEtProductAverage.text = (currentSeller?.rating!! * 2).toString()
    }

    private fun goToLogin(){
        val intent = Intent(applicationContext, LoginActivity::class.java)
        intent.putExtra("currentSeller", currentSeller)
        startActivity(intent)
        finish()
    }

}