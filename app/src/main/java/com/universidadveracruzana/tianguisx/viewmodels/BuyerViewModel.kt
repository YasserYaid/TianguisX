package com.universidadveracruzana.tianguisx.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.entities.MarketUser
import com.universidadveracruzana.tianguisx.entities.Order
import com.universidadveracruzana.tianguisx.entities.Product
import com.universidadveracruzana.tianguisx.entities.Review
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.firebase.storage.FirebaseStorageManager
import com.universidadveracruzana.tianguisx.services.BuyerService
import com.universidadveracruzana.tianguisx.services.MarketUserService
import com.universidadveracruzana.tianguisx.services.ProductService
import com.universidadveracruzana.tianguisx.services.SellerService
import com.universidadveracruzana.tianguisx.utils.PathFBStorage
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.glxn.qrgen.android.QRCode

sealed class BuyerViewModelState{

    data class RegisterSuccessFully(val buyer : Buyer) : BuyerViewModelState()
    data class SignInSuccessFully(val buyer : Buyer) : BuyerViewModelState()
    data class RecoverSellerSuccessFully(val sellerFound : Seller) : BuyerViewModelState()
    data class RecoverProductSuccessFully(val productFound : Product) : BuyerViewModelState()
    data class RecoverOrdersSuccessFully(val orderList : MutableList<Order>) : BuyerViewModelState()
    data class UpdateSuccessFully(val buyer : Buyer) : BuyerViewModelState()
    data class OrderDeleteSuccessFully(val uuId : String) : BuyerViewModelState()
    data class DeleteSuccessFully(val uuId : String) : BuyerViewModelState()

    data class Error(val message : String?) : BuyerViewModelState()

    data object Empty : BuyerViewModelState()
    data object Loading : BuyerViewModelState()

    data object None : BuyerViewModelState()

    data object Clean : BuyerViewModelState()

}

class BuyerViewModel : ViewModel() {

    private val _buyerViewModelState = MutableStateFlow<BuyerViewModelState>(BuyerViewModelState.None)
    val buyerViewModelState : StateFlow<BuyerViewModelState> = _buyerViewModelState

    fun signUp(buyer : Buyer, buyerImageUri : Uri) = viewModelScope.launch {
        _buyerViewModelState.value = BuyerViewModelState.Loading

        try{
            coroutineScope {

                var uploadBuyerImageUrl : String? = null
                var buyerImagePath : String? = null

                val signUpResult = async {
                    MarketUserService.signUpWhitAuth(buyer.email!!, buyer.password!!)
                }.await()

                if(signUpResult.user?.uid != null){
                    buyer.uuId = signUpResult.user?.uid
                }
                else{
                    _buyerViewModelState.value = BuyerViewModelState.Error("No se pudo resigstrar, probablemente el correo ya exista intente con otro correo")
                    return@coroutineScope
                }

                uploadBuyerImageUrl = async {
                    BuyerService.uploadBuyerProfileImageUri(buyerImageUri,"${buyer.uuId}.jpg")
                }.await()

                if(uploadBuyerImageUrl != null){
                    buyerImagePath = "${PathFBStorage.BUYER_PROFILES_IMAGE_FOLDER_PATH}${buyer.uuId}.jpg"
                    buyer.profileImageURL = uploadBuyerImageUrl
                    buyer.profileImageFileCloudPath = buyerImagePath
                }else{
                    _buyerViewModelState.value = BuyerViewModelState.Error("La imagen del comprador no se pudo subir")
                    return@coroutineScope
                }

                val registerResult = async {
                    BuyerService.registerBuyerObject(buyer)
                }.await()

                if(registerResult){
                    _buyerViewModelState.value = BuyerViewModelState.RegisterSuccessFully(buyer)
                }
                else{
                    _buyerViewModelState.value = BuyerViewModelState.Error("No se pudo registrar")
                    return@coroutineScope
                }
            }
        }catch ( e : Exception ){
            _buyerViewModelState.value = BuyerViewModelState.Error(e.message)
        }

    }

    fun deleteBuyer(buyer : Buyer) = viewModelScope.launch{
        _buyerViewModelState.value = BuyerViewModelState.Loading

        try {
            coroutineScope {

                var isBuyerImageDelete : Boolean = false
                var isBuyerInformationDelete : Boolean = false

                if(buyer.profileImageFileCloudPath != null){
                    isBuyerImageDelete = async {
                        BuyerService.deleteBuyerImage(buyer)
                    }.await()
                    if(!isBuyerImageDelete){
                        _buyerViewModelState.value = BuyerViewModelState.Error("No se pudo eliminar la imagen, el comprador no se eliminara")
                        return@coroutineScope
                    }
                }

                isBuyerInformationDelete = async {
                    BuyerService.deleteBuyerInformation(buyer)
                }.await()

                if(isBuyerInformationDelete){
                    _buyerViewModelState.value = BuyerViewModelState.DeleteSuccessFully(buyer.uuId!!)
                    return@coroutineScope
                }
                else{
                    _buyerViewModelState.value = BuyerViewModelState.Error("No se pudo eliminar la informacion del comprador")
                    return@coroutineScope
                }
            }

        }catch (e : Exception){
            _buyerViewModelState.value = BuyerViewModelState.Error(e.message)
        }
    }

    fun modifyBuyer(buyer : Buyer) = viewModelScope.launch {
        _buyerViewModelState.value = BuyerViewModelState.Loading

        try {
            coroutineScope {
                var modifyResult = false

                modifyResult = async {
                    BuyerService.modifyBuyerObject(buyer)
                }.await()

                if(modifyResult){
                    _buyerViewModelState.value = BuyerViewModelState.UpdateSuccessFully(buyer)
                }
                else{
                    _buyerViewModelState.value = BuyerViewModelState.Error("No se pudo modificar el comprador")
                    return@coroutineScope
                }
            }

        }catch (e : Exception){
            _buyerViewModelState.value = BuyerViewModelState.Error(e.message)
        }
    }

    fun recoverBuyerOrders(buyer : Buyer) = viewModelScope.launch {

        _buyerViewModelState.value = BuyerViewModelState.Loading

        val ordersFoundList : MutableList<Order> = mutableListOf<Order>()
        val ordersFoundListFiltred: MutableList<Order> = mutableListOf<Order>()

        try {
            coroutineScope {

                val querySnapshot = async {
                    BuyerService.recoverOrdersFromBuyer(buyer)
                }.await()

                when(querySnapshot){
                    null -> {
                        _buyerViewModelState.value = BuyerViewModelState.Error("Ocurrio un error en el servidor")
                        return@coroutineScope
                    }
                    else -> {
                        if(querySnapshot.isEmpty){
                            _buyerViewModelState.value = BuyerViewModelState.Empty
                            return@coroutineScope
                        }else{
                            ordersFoundList.addAll(querySnapshot.toObjects(Order::class.java))
                            for(order in ordersFoundList){
                                if(order.orderStatus.equals("Apartado"))
                                    ordersFoundListFiltred.add(order)
                            }
                            _buyerViewModelState.value = BuyerViewModelState.RecoverOrdersSuccessFully(ordersFoundListFiltred)
                            return@coroutineScope
                        }
                    }
                }

            }
        }catch (e : Exception){
            _buyerViewModelState.value = BuyerViewModelState.Error(e.message)
        }

    }

    fun recoverProductFromOrder(order : Order, marketList : List<String>) = viewModelScope.launch{
        _buyerViewModelState.value = BuyerViewModelState.Loading
        var productFound : Product? = null

        try{
            coroutineScope {
                var documentSnapshot : DocumentSnapshot? = null

                for (marketPath in marketList){
                    documentSnapshot = ProductService.recoverProductFromOrder(order, marketPath)
                    if(documentSnapshot?.get(Product.UUID_PRODUCT_KEY) == order.uuIdProduct){
                        productFound = documentSnapshot?.toObject(Product::class.java)
                        break
                    }
                }

                if(documentSnapshot == null){
                    _buyerViewModelState.value = BuyerViewModelState.Error("Ocurrio un error en el servidor")
                    return@coroutineScope
                }

                if(productFound == null){
                    _buyerViewModelState.value = BuyerViewModelState.Empty
                    return@coroutineScope
                }else{
                    _buyerViewModelState.value = BuyerViewModelState.RecoverProductSuccessFully(productFound!!)
                    return@coroutineScope
                }

            }
        }
        catch (e : Exception){
            _buyerViewModelState.value = BuyerViewModelState.Error(e.message)
        }
    }

    fun recoverSellerFromOrder(order : Order, marketList : List<String>) = viewModelScope.launch{
        _buyerViewModelState.value = BuyerViewModelState.Loading
        var sellerFound : Seller? = null

        try{
            coroutineScope {
                var documentSnapshot : DocumentSnapshot? = null

                for (marketPath in marketList){
                    documentSnapshot = SellerService.recoverSellerFromOrder(order, marketPath)
                    if(documentSnapshot?.get(MarketUser.UUID_KEY) == order.uuIdSeller){
                        sellerFound = documentSnapshot?.toObject(Seller::class.java)
                        break
                    }
                }

                if(documentSnapshot == null){
                    _buyerViewModelState.value = BuyerViewModelState.Error("Ocurrio un error en el servidor")
                    return@coroutineScope
                }

                if(sellerFound == null){
                    _buyerViewModelState.value = BuyerViewModelState.Empty
                    return@coroutineScope
                }else{
                    _buyerViewModelState.value = BuyerViewModelState.RecoverSellerSuccessFully(sellerFound!!)
                    return@coroutineScope
                }

            }
        }
        catch (e : Exception){
            _buyerViewModelState.value = BuyerViewModelState.Error(e.message)
        }
    }

    fun deleteBuyerOrder(order : Order, buyer : Buyer) = viewModelScope.launch{
        _buyerViewModelState.value = BuyerViewModelState.Loading

        try {
            coroutineScope {

                var isOrderDelete : Boolean = false

                isOrderDelete = async {
                    BuyerService.deleteOrderFromBuyer(order, buyer)
                }.await()

                if(isOrderDelete){
                    _buyerViewModelState.value = BuyerViewModelState.OrderDeleteSuccessFully(order.uuIdOrder!!)
                    return@coroutineScope
                }
                else{
                    _buyerViewModelState.value = BuyerViewModelState.Error("No se pudo eliminar la informacion de la orden")
                    return@coroutineScope
                }
            }

        }catch (e : Exception){
            _buyerViewModelState.value = BuyerViewModelState.Error(e.message)
        }
    }

    fun cleanViewModelState(){
        _buyerViewModelState.value = BuyerViewModelState.Clean
    }

}