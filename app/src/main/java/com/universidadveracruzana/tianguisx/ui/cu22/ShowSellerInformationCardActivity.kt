package com.universidadveracruzana.tianguisx.ui.cu22

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import com.squareup.picasso.Picasso
import com.universidadveracruzana.tianguisx.databinding.ActivityShowSellerInformationCardBinding
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.entities.Review
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.ui.commonmessages.ReviewDialog
import com.universidadveracruzana.tianguisx.ui.cu13.ShowSellerReviewsActivity
import com.universidadveracruzana.tianguisx.viewmodels.SellerViewModel
import com.universidadveracruzana.tianguisx.viewmodels.SellerViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class ShowSellerInformationCardActivity : AppCompatActivity() {

    private lateinit var binding : ActivityShowSellerInformationCardBinding
    var averageRating : Float? = null
    var currentBuyer : Buyer? = null
    var consultSeller : Seller? = null

    private val sellerViewModel : SellerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowSellerInformationCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFlow()
        showSellerInformationCardGUIConfig()
    }

    fun showSellerInformationCardGUIConfig(){

        ////////////////START ////////////////////////////
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            currentBuyer = intent.getSerializableExtra("currentBuyer", Buyer::class.java)
            consultSeller = intent.getSerializableExtra("sellerConsult", Seller::class.java)
        }
        else {
            currentBuyer = intent.getSerializableExtra("currentBuyer") as Buyer
            consultSeller = intent.getSerializableExtra("sellerConsult") as Seller
        }

        if(consultSeller != null){
            sellerViewModel.recoverSellerReviews(consultSeller!!)
            Picasso.get().load(consultSeller?.profileImageURL).into(binding.ui22IvImageProfile)
            binding.ui22EtName.text = " " + consultSeller?.name + " " + consultSeller?.lastName
            binding.ui22EtCorreo.text = " " + consultSeller?.email
            binding.ui22EtPhone.text = " " + consultSeller?.phoneNumber
            binding.ui22EtSalesType.text = " " + consultSeller?.typeSeller
            binding.ui22EtState.text = " " + consultSeller?.state
            binding.ui22EtCity.text = " " + consultSeller?.city
            binding.ui22EtMarket.text = " " + consultSeller?.market
            binding.ui22EtLocal.text = " " + consultSeller?.localDescription
            binding.ui22EtFirstDay.text = " " + consultSeller?.initialWorkDay + " "
            binding.ui22EtLastDay.text = " " + consultSeller?.finalWorkDay
            binding.ui22EtFirstHour.text = " " + consultSeller?.initialWorkHour + " "
            binding.ui22EtLastHour.text = " " + consultSeller?.finalWorkHour
            binding.ui22EtBank.text = " " + consultSeller?.bankName
            binding.ui22EtBankAccount.text = " " + consultSeller?.accountBank

            ////////////////Listeners/////////////////////
/*
            binding.ui22RegisterReviewButton.setOnClickListener{
                registerSellerReviewProcess()
            }
 */
            binding.ui22ShowEvaluationButton.setOnClickListener{
                goToShowSellerReviews()
            }
            binding.ui22ShareButton.setOnClickListener{
                shareQRProcess()
            }
        }
        else{
            Toast.makeText(this@ShowSellerInformationCardActivity, "No se pudo encontrar el vendedor" , Toast.LENGTH_SHORT).show()
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

                        is SellerViewModelState.RegisterReviewSuccesFully -> {
                            hideProgress()
                            Toast.makeText(this@ShowSellerInformationCardActivity, "Reseña registrada exitosamente" , Toast.LENGTH_SHORT).show()
                            finish()
                        }

                        is SellerViewModelState.Loading -> {
                            showProgress()
                        }

                        is SellerViewModelState.Empty -> {
                            hideProgress()
                            Log.d("???" , "VACIO")
                            Toast.makeText(this@ShowSellerInformationCardActivity, "No se encontro el producto" , Toast.LENGTH_SHORT).show()
                        }

                        is SellerViewModelState.Error -> {
                            Log.d("???" , "ERROR : ${it.message}")
                            Toast.makeText(this@ShowSellerInformationCardActivity, "ERROR : ${it.message}" , Toast.LENGTH_SHORT).show()
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

    private fun registerSellerReviewProcess(){
        var submitReview : Review?
        ReviewDialog(
            onSubmitClickListener = {actualReview ->
                if(actualReview.reviewRating == null || actualReview.reviewDescription.isNullOrBlank()) {
                    Toast.makeText(this@ShowSellerInformationCardActivity, "Se debe ingresar comentarios y un rating", Toast.LENGTH_LONG).show()
                }else if(actualReview.reviewRating!! < 1 || actualReview.reviewDescription!!.length < 10){
                    Toast.makeText(this@ShowSellerInformationCardActivity, "Se debe ingresar el rating y la longitud de la descripcion debe ser mayor o igual a diez caracteres", Toast.LENGTH_LONG).show()
                }else{
                    submitReview = actualReview
                    submitReview?.uuIdReview = getRandomUUIDString()
                    submitReview?.uuIdProduct = null
                    submitReview?.uuIdBuyer = currentBuyer?.uuId
                    submitReview?.uuIdSeller = consultSeller?.uuId
                    submitReview?.reviewDate = Date()
                    submitReview?.reviewAuthor = currentBuyer?.name + " " + currentBuyer?.lastName
                    sellerViewModel.registerSellerReview(consultSeller!!, submitReview!!)
                }
            }
        ).show(supportFragmentManager, "dialog")
    }

    private fun getAverageRatingSeller(sellerReviewList : MutableList<Review>){
        var totalRating = 0.0f
        for (review in sellerReviewList){
            totalRating += review.reviewRating!!
        }
        averageRating = totalRating / sellerReviewList.count()
        consultSeller?.rating = averageRating
        binding.ui22SellerEvaluationRatingBar.rating = consultSeller?.rating!!
        binding.ui22ShowProductDetailsEtProductAverage.text = (consultSeller?.rating!! * 2).toString()
    }

    private fun getRandomUUIDString() : String {
        return UUID.randomUUID().toString().replace("-", "")
    }

    private fun goToShowSellerReviews(){
        val intent = Intent(applicationContext, ShowSellerReviewsActivity::class.java)
        intent.putExtra("sellerConsult", consultSeller)
        intent.putExtra("currentBuyer", currentBuyer)
        intent.putExtra("currentSeller", Seller())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private  fun shareQRProcess(){
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, consultSeller?.qrImageURL)
            type = "text/plain"
        }
        var shareIntent = Intent.createChooser(intent, null)
        startActivity(shareIntent)
    }
}