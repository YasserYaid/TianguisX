package com.universidadveracruzana.tianguisx.ui.cu19

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
import com.swein.easyphotopicker.SystemPhotoPickManager
import com.universidadveracruzana.tianguisx.databinding.ActivitySendPaymentTicketBinding
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.entities.Order
import com.universidadveracruzana.tianguisx.entities.Review
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.ui.commonmessages.ReviewDialog
import com.universidadveracruzana.tianguisx.viewmodels.SellerViewModel
import com.universidadveracruzana.tianguisx.viewmodels.SellerViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class SendPaymentTicketActivity : AppCompatActivity() {

    private val provider = "com.universidadveracruzana.tianguisx.provider"
    private val systemPhotoPickManager = SystemPhotoPickManager(this, provider)
    private lateinit var binding : ActivitySendPaymentTicketBinding
    var currentBuyer : Buyer? = null
    var consultSeller : Seller? = null
    var consultOrder : Order? = null
    private var ticketImageUri : Uri? = null

    private val sellerViewModel : SellerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendPaymentTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFlow()
        SendPaymentTicketGUIConfig()
    }

    fun SendPaymentTicketGUIConfig(){
        ////////////////START ////////////////////////////
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            currentBuyer = intent.getSerializableExtra("currentBuyer", Buyer::class.java)
            consultSeller = intent.getSerializableExtra("sellerConsult", Seller::class.java)
            consultOrder = intent.getSerializableExtra("orderConsult", Order::class.java)
        }
        else {
            currentBuyer = intent.getSerializableExtra("currentBuyer") as Buyer
            consultSeller = intent.getSerializableExtra("sellerConsult") as Seller
            consultOrder = intent.getSerializableExtra("orderConsult") as Order
        }

        Picasso.get().load(consultSeller?.profileImageURL).into(binding.ui19IvImageProfile)
        binding.ui19EtName.text = " " + consultSeller?.name + " " + consultSeller?.lastName
        binding.ui19EtCorreo.text = " " + consultSeller?.email
        binding.ui19EtPhone.text = " " + consultSeller?.phoneNumber
        binding.ui19EtSalesType.text = " " + consultSeller?.typeSeller
        binding.ui19EtState.text = " " + consultSeller?.state
        binding.ui19EtCity.text = " " + consultSeller?.city
        binding.ui19EtMarket.text = " " + consultSeller?.market
        binding.ui19EtLocal.text = " " + consultSeller?.localDescription
        binding.ui19EtFirstDay.text = " " + consultSeller?.initialWorkDay + " "
        binding.ui19EtLastDay.text = " " + consultSeller?.finalWorkDay
        binding.ui19EtFirstHour.text = " " + consultSeller?.initialWorkHour + " "
        binding.ui19EtLastHour.text = " " + consultSeller?.finalWorkHour
        binding.ui19EtBank.text = " " + consultSeller?.bankName
        binding.ui19EtBankAccount.text = " " + consultSeller?.accountBank
        binding.ui19EtSubjectMessageCode.text = consultOrder?.orderCode
        ////////////////Listeners/////////////////////
        binding.ui19AttachmentTicketButton.setOnClickListener{
            selectImageFromGallery()
        }
        binding.ui19SendMessageButton.setOnClickListener{
            sendMessageProcess()
        }
        binding.ui19RegisterReviewButton.setOnClickListener {
            registerSellerReviewProcess()
        }
        binding.ui19CancelButton.setOnClickListener {
            finish()
        }
    }

    private fun selectImageFromGallery(){
        systemPhotoPickManager.requestPermission {
            it.selectPicture {uri ->
                ticketImageUri = uri
            }
        }
    }

    private fun initFlow(){
        lifecycleScope.launch(Dispatchers.Main) {

            whenCreated {
                sellerViewModel.sellerViewModelState.collect{

                    when(it){

                        is SellerViewModelState.RegisterReviewSuccesFully -> {
                            hideProgress()
                            Toast.makeText(this@SendPaymentTicketActivity, "Reseña registrada exitosamente" , Toast.LENGTH_SHORT).show()
                            finish()
                        }

                        is SellerViewModelState.Loading -> {
                            showProgress()
                        }

                        is SellerViewModelState.Empty -> {
                            hideProgress()
                            Log.d("???" , "VACIO")
                            Toast.makeText(this@SendPaymentTicketActivity, "No se encontro el producto" , Toast.LENGTH_SHORT).show()
                        }

                        is SellerViewModelState.Error -> {
                            Log.d("???" , "ERROR : ${it.message}")
                            Toast.makeText(this@SendPaymentTicketActivity, "ERROR : ${it.message}" , Toast.LENGTH_SHORT).show()
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

    private fun sendMessageProcess(){
        val email = Intent(Intent.ACTION_SEND)
        email.type = "application/octet-stream"
        email.putExtra(Intent.EXTRA_EMAIL, listOf(consultSeller?.email).toTypedArray())
        email.putExtra(Intent.EXTRA_SUBJECT, binding.ui19EtSubjectMessage.text.toString() + " " + consultOrder?.orderCode)
        email.putExtra(Intent.EXTRA_TEXT, binding.ui19BodyMessageEditText.text.toString())
        email.putExtra(Intent.EXTRA_STREAM, ticketImageUri)
        startActivity(Intent.createChooser(email, "Por favor seleccione la aplicacion de email"))
    }

    private fun registerSellerReviewProcess(){
        var submitReview : Review?
        ReviewDialog(
            onSubmitClickListener = {actualReview ->
                if(actualReview.reviewRating == null || actualReview.reviewDescription.isNullOrBlank()) {
                    Toast.makeText(this@SendPaymentTicketActivity, "Se debe ingresar comentarios y un rating", Toast.LENGTH_LONG).show()
                }else if(actualReview.reviewRating!! < 1 || actualReview.reviewDescription!!.length < 10){
                    Toast.makeText(this@SendPaymentTicketActivity, "Se debe ingresar el rating y la longitud de la descripcion debe ser mayor o igual a diez caracteres", Toast.LENGTH_LONG).show()
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

    private fun getRandomUUIDString() : String {
        return UUID.randomUUID().toString().replace("-", "")
    }
}