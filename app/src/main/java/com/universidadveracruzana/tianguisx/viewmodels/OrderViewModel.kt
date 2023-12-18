package com.universidadveracruzana.tianguisx.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.entities.MarketUser
import com.universidadveracruzana.tianguisx.entities.Order
import com.universidadveracruzana.tianguisx.services.BuyerService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class OrderViewModelState{

    data class BuyerFindSuccessFully(val buyer : Buyer) : OrderViewModelState()

    data class Error(val message : String?) : OrderViewModelState()

    data object Loading : OrderViewModelState()

    data object Empty : OrderViewModelState()

    data object None : OrderViewModelState()

    data object Clean : OrderViewModelState()

}

class OrderViewModel : ViewModel(){

    private val _orderViewModelState = MutableStateFlow<OrderViewModelState>(OrderViewModelState.None)
    val orderViewModelState : StateFlow<OrderViewModelState> = _orderViewModelState

    fun cleanViewModelState(){
        _orderViewModelState.value = OrderViewModelState.Clean
    }
}