package com.universidadveracruzana.tianguisx.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.universidadveracruzana.tianguisx.entities.MarketUser
import com.universidadveracruzana.tianguisx.entities.Product
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.services.ProductService
import com.universidadveracruzana.tianguisx.services.SellerService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class QRScanViewModelState{

    data class ProductFindSuccessFully(val product : Product) : QRScanViewModelState()
    data class SellerFindSuccessFully(val seller : Seller) : QRScanViewModelState()

    data class Error(val message : String?) : QRScanViewModelState ()

    data object Loading : QRScanViewModelState ()

    data object Empty : QRScanViewModelState ()

    data object None : QRScanViewModelState ()

    data object Clean : QRScanViewModelState ()

}

class QRScanViewModel : ViewModel() {
    private val _qrScanViewModelState = MutableStateFlow<QRScanViewModelState>(QRScanViewModelState.None)
    val qrScanViewModelState : StateFlow<QRScanViewModelState> = _qrScanViewModelState

    fun cleanViewModelState(){
        _qrScanViewModelState.value = QRScanViewModelState.Clean
    }

    fun searchProductByUUID(statePath : String, cityPath : String, marketPath : String, uuidProduct : String) = viewModelScope.launch{
        _qrScanViewModelState.value = QRScanViewModelState.Loading
        val productsFoundList : MutableList<Product> = mutableListOf<Product>()

        try{

            coroutineScope {

                val querySnapshot = async {
                    ProductService.searchProductByUuid(statePath, cityPath, marketPath, uuidProduct)
                }.await()

                when(querySnapshot){
                    null -> {
                        _qrScanViewModelState.value = QRScanViewModelState.Error("Ocurrio un error en el servidor")
                        return@coroutineScope
                    }
                    else -> {
                        if(querySnapshot.isEmpty){
                            _qrScanViewModelState.value = QRScanViewModelState.Empty
                            return@coroutineScope
                        }else{
                            productsFoundList.addAll(querySnapshot.toObjects(Product::class.java))
                            _qrScanViewModelState.value = QRScanViewModelState.ProductFindSuccessFully(productsFoundList.first())
                            return@coroutineScope
                        }
                    }
                }
            }
        }
        catch (e : Exception){
            _qrScanViewModelState.value = QRScanViewModelState.Error(e.message)
        }
    }

    fun searchSellerByUUID(statePath : String, cityPath : String, marketPath : String, uuidSeller : String) = viewModelScope.launch{
        _qrScanViewModelState.value = QRScanViewModelState.Loading
        try{

            coroutineScope {
                var documentSnapshot : DocumentSnapshot?
                var sellerFound : Seller? = null

                documentSnapshot = SellerService.searchSellerByUuid(uuidSeller, marketPath)
                if(documentSnapshot?.get(MarketUser.UUID_KEY) == uuidSeller){
                    sellerFound = documentSnapshot.toObject(Seller::class.java)
                }

                if(sellerFound == null){
                    _qrScanViewModelState.value = QRScanViewModelState.Empty
                    return@coroutineScope
                }else{
                    _qrScanViewModelState.value = QRScanViewModelState.SellerFindSuccessFully(sellerFound)
                    return@coroutineScope
                }
            }
        }
        catch (e : Exception){
            _qrScanViewModelState.value = QRScanViewModelState.Error(e.message)
        }
    }
}