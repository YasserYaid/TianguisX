package com.universidadveracruzana.tianguisx.ui.cu14

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import com.squareup.picasso.Picasso
import com.universidadveracruzana.tianguisx.databinding.ActivityShowBuyerProfileDetailsBinding
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.ui.cu01.LoginActivity
import com.universidadveracruzana.tianguisx.ui.cu12.ModifySellerDetailsActivity
import com.universidadveracruzana.tianguisx.ui.cu15.ModifyBuyerDetailsActivity
import com.universidadveracruzana.tianguisx.viewmodels.BuyerViewModel
import com.universidadveracruzana.tianguisx.viewmodels.BuyerViewModelState
import com.universidadveracruzana.tianguisx.viewmodels.SellerViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShowBuyerProfileDetailsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityShowBuyerProfileDetailsBinding
    var currentBuyer : Buyer? = null

    private val buyerViewModel : BuyerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowBuyerProfileDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFlow()
        showBuyerDetailsGUIConfig()
    }

    private fun showBuyerDetailsGUIConfig(){
        ///////////START/////////////////
        currentBuyer = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            intent.getSerializableExtra("currentBuyer", Buyer::class.java)
        }
        else{
            intent.getSerializableExtra("currentBuyer") as Buyer
        }

        if(currentBuyer?.profileImageURL != null) Picasso.get().load(currentBuyer?.profileImageURL).into(binding.ui14ShowBuyerProfileDetailsIvImageProfile)
        binding.ui14ShowBuyerProfileDetailsEtName.text = " " + currentBuyer?.name
        binding.ui14ShowBuyerProfileDetailsEtCorreo.text = " " + currentBuyer?.email
        binding.ui14ShowBuyerProfileDetailsEtPhone.text = " " + currentBuyer?.phoneNumber
        binding.ui14ShowBuyerProfileDetailsEtReceptionDirection.text = " " + currentBuyer?.streetAddress
        binding.ui14ShowBuyerProfileDetailsEtNumberHouse.text = " " + currentBuyer?.numberAddress.toString()
        binding.ui14ShowBuyerProfileDetailsEtFirstDay.text = " " + currentBuyer?.initialReceptionDay + " "
        binding.ui14ShowBuyerProfileDetailsEtLastDay.text = " " + currentBuyer?.finalReceptionDay
        binding.ui14ShowBuyerProfileDetailsEtFirstHour.text = " " + currentBuyer?.initialReceptionHour + " "
        binding.ui14ShowBuyerProfileDetailsEtLastHour.text = " " + currentBuyer?.finalReceptionHour
        binding.ui14ShowBuyerProfileDetailsEtAditionalInformation.text = currentBuyer?.descriptionAddress

        ////////////////Listeners
        binding.ui14ShowBuyerProfileDetailsModifyButton.setOnClickListener {
            goToModifyBuyer()
        }
        binding.ui14ShowBuyerProfileDetailsDeleteButton.setOnClickListener {
            deleteBuyerProccess()
        }
    }

    private fun initFlow(){
        lifecycleScope.launch(Dispatchers.Main) {

            whenCreated {
                buyerViewModel.buyerViewModelState.collect{

                    when(it){

                        is BuyerViewModelState.DeleteSuccessFully -> {
                            hideProgress()
                            Toast.makeText(this@ShowBuyerProfileDetailsActivity, "El comprador se elimino correctamente" , Toast.LENGTH_SHORT).show()
                            val intent = Intent(applicationContext, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
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
                            Toast.makeText(this@ShowBuyerProfileDetailsActivity, "ERROR : ${it.message}" , Toast.LENGTH_SHORT).show()
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

    private fun goToModifyBuyer(){
        val intent = Intent(applicationContext, ModifyBuyerDetailsActivity::class.java)
        intent.putExtra("currentBuyer", currentBuyer)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private  fun deleteBuyerProccess(){
        val alertDialogBuilder = AlertDialog.Builder(this@ShowBuyerProfileDetailsActivity)
        alertDialogBuilder.setMessage("Esta seguro de que desea eliminar la cuenta esta, accion no se puede deshacer")
        alertDialogBuilder.setTitle("Eliminar Comprador")
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setPositiveButton("yes"){ _,_ ->
            if(currentBuyer != null){
                buyerViewModel.deleteBuyer(currentBuyer!!)
            }
        }
        alertDialogBuilder.setNegativeButton("no"){ _,_ ->
            Toast.makeText(this@ShowBuyerProfileDetailsActivity, "La cuenta no se eliminara" , Toast.LENGTH_SHORT).show()
        }
        val alertDialogBox = alertDialogBuilder.create()
        alertDialogBox.show()
    }

    private fun goToLogin(){
        val intent = Intent(applicationContext, LoginActivity::class.java)
        intent.putExtra("currentBuyer", currentBuyer)
        startActivity(intent)
        finish()
    }

}