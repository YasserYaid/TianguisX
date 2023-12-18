package com.universidadveracruzana.tianguisx.ui.cu17

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
import com.universidadveracruzana.tianguisx.R
import com.universidadveracruzana.tianguisx.databinding.ActivityShowSelectedProductsListBinding
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.entities.Order
import com.universidadveracruzana.tianguisx.entities.Product
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.ui.adapters.OrderBuyerAdapter
import com.universidadveracruzana.tianguisx.ui.cu06.ShowProductDetailsActivity
import com.universidadveracruzana.tianguisx.ui.cu18.ShowProductInformationCardActivity
import com.universidadveracruzana.tianguisx.ui.cu19.SendPaymentTicketActivity
import com.universidadveracruzana.tianguisx.viewmodels.BuyerViewModel
import com.universidadveracruzana.tianguisx.viewmodels.BuyerViewModelState
import com.universidadveracruzana.tianguisx.viewmodels.SellerViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShowSelectedProductsListActivity : AppCompatActivity() {

    private lateinit var binding : ActivityShowSelectedProductsListBinding
    private lateinit var marketsPaths : List<String>
    private var orderBuyerAdapter : OrderBuyerAdapter? = null
    private val buyerViewModel : BuyerViewModel by viewModels()

    private var currentBuyer : Buyer? = null
    private var currentOrder : Order? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowSelectedProductsListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        marketsPaths = resources.getStringArray(R.array.Xalapa_Markets).toList()
        initFlow()
        showSelectedProductsListGUIConfig()
    }

    private fun showSelectedProductsListGUIConfig(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            currentBuyer = intent.getSerializableExtra("currentBuyer", Buyer::class.java)
        }
        else {
            currentBuyer = intent.getSerializableExtra("currentBuyer") as Buyer
        }

        recoverOrdersCurrentBuyer()
    }

    private fun recoverOrdersCurrentBuyer(){
        buyerViewModel.recoverBuyerOrders(currentBuyer!!)
    }

    private fun initFlow(){
        lifecycleScope.launch(Dispatchers.Main) {

            whenCreated {
                buyerViewModel.buyerViewModelState.collect{

                    when(it){

                        is BuyerViewModelState.OrderDeleteSuccessFully ->{
                            hideProgress()
                            finish()
                        }

                        is BuyerViewModelState.RecoverSellerSuccessFully ->{
                            hideProgress()
                            goToSendPaymentTicket(it.sellerFound, currentOrder!!)
                        }

                        is BuyerViewModelState.RecoverProductSuccessFully -> {
                            hideProgress()
                            goToShowProductInformation(it.productFound)
                        }

                        is BuyerViewModelState.RecoverOrdersSuccessFully -> {
                            hideProgress()
                            binding.ui17ListView.adapter = OrderBuyerAdapter(applicationContext, it.orderList, null, currentBuyer,
                                onOrderDetailsClickListener = {actualOrder ->
                                    currentOrder = actualOrder
                                    buyerViewModel.recoverProductFromOrder(actualOrder, marketsPaths)
                                },
                                onTicketClickListener = {actualOrder ->
                                    currentOrder = actualOrder
                                    buyerViewModel.recoverSellerFromOrder(actualOrder, marketsPaths)
                                },
                                onCancelClickListener = {actualOrder ->
                                    cancelOrderProcess(actualOrder)
                                }
                            )
                        }

                        is BuyerViewModelState.Loading -> {
                            showProgress()
                        }

                        is BuyerViewModelState.Empty -> {
                            hideProgress()
                            Log.d("???" , "VACIO")
                            Toast.makeText(this@ShowSelectedProductsListActivity, "No se encontraron productos apartados" , Toast.LENGTH_SHORT).show()
                        }

                        is BuyerViewModelState.Error -> {
                            Log.d("???" , "ERROR : ${it.message}")
                            Toast.makeText(this@ShowSelectedProductsListActivity, "ERROR : ${it.message}" , Toast.LENGTH_SHORT).show()
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

    override fun onRestart() {
        super.onRestart()
        recoverOrdersCurrentBuyer()
    }

    private fun goToShowProductInformation(product : Product){
        val intent = Intent(applicationContext, ShowProductInformationCardActivity::class.java)
        intent.putExtra("productConsult", product)
        intent.putExtra("currentBuyer", currentBuyer)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun goToSendPaymentTicket(seller : Seller, order : Order){
        val intent = Intent(applicationContext, SendPaymentTicketActivity::class.java)
        intent.putExtra("sellerConsult", seller)
        intent.putExtra("currentBuyer", currentBuyer)
        intent.putExtra("orderConsult", order)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun cancelOrderProcess(order : Order){
        val alertDialogBuilder = AlertDialog.Builder(this@ShowSelectedProductsListActivity)
        alertDialogBuilder.setMessage("Esta seguro de que desea cancelar la orden, si ya pago su dinero se perdera")
        alertDialogBuilder.setTitle("Cancelar Orden")
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setPositiveButton("yes"){ _,_ ->
            if(currentBuyer != null){
                buyerViewModel.deleteBuyerOrder(order, currentBuyer!!)
            }
        }
        alertDialogBuilder.setNegativeButton("no"){ _,_ ->
            Toast.makeText(this@ShowSelectedProductsListActivity, "La orden no se eliminara" , Toast.LENGTH_SHORT).show()
        }
        val alertDialogBox = alertDialogBuilder.create()
        alertDialogBox.show()
    }

}