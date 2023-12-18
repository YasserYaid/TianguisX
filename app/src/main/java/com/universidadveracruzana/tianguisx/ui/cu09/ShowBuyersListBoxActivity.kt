package com.universidadveracruzana.tianguisx.ui.cu09

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
import com.universidadveracruzana.tianguisx.databinding.ActivityShowBuyersListBoxBinding
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.entities.Order
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.ui.adapters.OrderSellerAdapter
import com.universidadveracruzana.tianguisx.ui.cu10.ShowBuyerInformationCardActivity
import com.universidadveracruzana.tianguisx.viewmodels.SellerViewModel
import com.universidadveracruzana.tianguisx.viewmodels.SellerViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShowBuyersListBoxActivity : AppCompatActivity() {

    private lateinit var binding : ActivityShowBuyersListBoxBinding
    private var orderSellerAdapter : OrderSellerAdapter? = null
    private val sellerViewModel : SellerViewModel by viewModels()

    private var currentSeller : Seller? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowBuyersListBoxBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFlow()
        showBuyerListBoxGUIConfig()
    }

    private fun showBuyerListBoxGUIConfig(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            currentSeller = intent.getSerializableExtra("currentSeller", Seller::class.java)
        }
        else {
            currentSeller = intent.getSerializableExtra("currentSeller") as Seller
        }

        recoverOrdersCurrentSeller()
    }

    private fun recoverOrdersCurrentSeller(){
        sellerViewModel.recoverSellerOrders(currentSeller!!)
    }

    private fun initFlow(){
        lifecycleScope.launch(Dispatchers.Main) {

            whenCreated {
                sellerViewModel.sellerViewModelState.collect{

                    when(it){

                        is SellerViewModelState.OrderDeleteSuccessFully ->{
                            hideProgress()
                            finish()
                        }

                        is SellerViewModelState.BuyerFindSuccessFully ->{
                            hideProgress()
                            goToShowBuyerInformationCard(it.buyer)
                        }

                        is SellerViewModelState.RecoverOrdersSuccessFully -> {
                            hideProgress()
                            binding.ui09ListView.adapter = OrderSellerAdapter(applicationContext, it.orderList, currentSeller, null,
                                onBuyerDetailsClickListener = { actualOrder  ->
                                    sellerViewModel.recoverBuyerFromOrder(actualOrder)
                                },
                                onConfirmClickListener = { actualOrder  ->
                                    confirmOrderProcess(actualOrder)
                                }
                            )
                        }

                        is SellerViewModelState.Loading -> {
                            showProgress()
                        }

                        is SellerViewModelState.Empty -> {
                            hideProgress()
                            Log.d("???" , "VACIO")
                            Toast.makeText(this@ShowBuyersListBoxActivity, "No se encontraron productos apartados" , Toast.LENGTH_SHORT).show()
                        }

                        is SellerViewModelState.Error -> {
                            Log.d("???" , "ERROR : ${it.message}")
                            Toast.makeText(this@ShowBuyersListBoxActivity, "ERROR : ${it.message}" , Toast.LENGTH_SHORT).show()
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

    override fun onRestart() {
        super.onRestart()
        recoverOrdersCurrentSeller()
    }

    fun goToShowBuyerInformationCard(buyer: Buyer?){
        val intent = Intent(applicationContext, ShowBuyerInformationCardActivity::class.java)
        intent.putExtra("consultBuyer", buyer)
        intent.putExtra("currentSeller", currentSeller)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        applicationContext.startActivity(intent)
    }

    private fun confirmOrderProcess(order : Order){
        val alertDialogBuilder = AlertDialog.Builder(this@ShowBuyersListBoxActivity)
        alertDialogBuilder.setMessage("Esta seguro de que desea confirmar el pago y eliminar la orden de apartado, si el pago no se realizo perdera su dinero")
        alertDialogBuilder.setTitle("Confirmar Pago")
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setPositiveButton("yes"){ _,_ ->
            if(currentSeller != null){
                sellerViewModel.deleteSellerOrder(order, currentSeller!!)
            }
        }
        alertDialogBuilder.setNegativeButton("no"){ _,_ ->
            Toast.makeText(this@ShowBuyersListBoxActivity, "La orden no se eliminara" , Toast.LENGTH_SHORT).show()
        }
        val alertDialogBox = alertDialogBuilder.create()
        alertDialogBox.show()
    }

}