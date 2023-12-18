package com.universidadveracruzana.tianguisx.viewmodels

import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.entities.MarketUser
import com.universidadveracruzana.tianguisx.entities.Order
import com.universidadveracruzana.tianguisx.entities.Product
import com.universidadveracruzana.tianguisx.entities.Review
import com.universidadveracruzana.tianguisx.entities.Seller
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
import kotlinx.coroutines.runBlocking
import net.glxn.qrgen.android.QRCode

sealed class SellerViewModelState{

    data class RegisterSuccessFully(val seller : Seller) : SellerViewModelState()
    data class RegisterReviewSuccesFully(val review : Review) : SellerViewModelState()
    data class RecoverOrdersSuccessFully(val orderList : MutableList<Order>) : SellerViewModelState()
    data class RecoverReviewsSuccessFully(val reviewList : MutableList<Review>) : SellerViewModelState()
    data class BuyerFindSuccessFully(val buyer : Buyer) : SellerViewModelState()
    data class SignInSuccessFully(val seller : Seller) : SellerViewModelState()
    data class UpdateSuccessFully(val seller : Seller) : SellerViewModelState()
    data class OrderDeleteSuccessFully(val uuId : String) : SellerViewModelState()
    data class DeleteSuccessFully(val uuId : String) : SellerViewModelState()

    data class Error(val message : String?) : SellerViewModelState ()

    data object Empty : SellerViewModelState ()
    data object Loading : SellerViewModelState ()

    data object None : SellerViewModelState ()

    data object Clean : SellerViewModelState ()

}

class SellerViewModel : ViewModel() {

    private val _sellerViewModelState = MutableStateFlow<SellerViewModelState>(SellerViewModelState.None)
    val sellerViewModelState : StateFlow<SellerViewModelState> = _sellerViewModelState

    fun signUp(seller : Seller, sellerImageUri : Uri) = viewModelScope.launch {
        _sellerViewModelState.value = SellerViewModelState.Loading

        try{
            coroutineScope {
                var uploadQrImageUrl : String? = null
                var imageQrPath : String? = null
                var uploadSellerImageUrl : String? = null
                var sellerImagePath : String? = null

                val signUpResult = async {
                    MarketUserService.signUpWhitAuth(seller.email!!, seller.password!!)
                }.await()

                if(signUpResult.user?.uid != null){
                    seller.uuId = signUpResult.user?.uid
                }
                else{
                    _sellerViewModelState.value = SellerViewModelState.Error("No se pudo resigstrar, probablemente el correo ya exista intente con otro correo")
                    return@coroutineScope
                }

                val imageQrBitmap = QRCode.from("Vendedor" + "_" + seller.state + "_" + seller.city + "_" + seller.market + "_" + seller.uuId).withSize(300 , 300).bitmap()

                uploadQrImageUrl = async {
                    SellerService.uploadQrSellerImageFile(imageQrBitmap, "${seller.uuId!!}.jpg")
                }.await()

                if(uploadQrImageUrl != null){
                    imageQrPath = "${PathFBStorage.SELLER_QRS_IMAGES_FOLDER_PATH}${seller.uuId}.jpg"
                    seller.qrImageURL = uploadQrImageUrl
                    seller.qrImageFileCloudPath = imageQrPath
                }
                else{
                    _sellerViewModelState.value = SellerViewModelState.Error("La imagen no se pudo subir")
                    return@coroutineScope
                }

                uploadSellerImageUrl = async {
                    SellerService.uploadSellerProfileImageUri(sellerImageUri,"${seller.uuId}.jpg")
                }.await()

                if(uploadSellerImageUrl != null){
                    sellerImagePath = "${PathFBStorage.SELLER_PROFILE_IMAGE_FOLDER_PATH}${seller.uuId}.jpg"
                    seller.profileImageURL = uploadSellerImageUrl
                    seller.profileImageFileCloudPath = sellerImagePath
                }else{
                    _sellerViewModelState.value = SellerViewModelState.Error("La imagen del producto no se pudo subir")
                    return@coroutineScope
                }

                val registerResult = async {
                    SellerService.registerSellerObject(seller)
                }.await()

                if(registerResult){
                    _sellerViewModelState.value = SellerViewModelState.RegisterSuccessFully(seller)
                }
                else{
                    _sellerViewModelState.value = SellerViewModelState.Error("No se pudo registrar")
                    return@coroutineScope
                }
            }
        }catch ( e : Exception ){
            _sellerViewModelState.value = SellerViewModelState.Error(e.message)
        }

    }

    fun modifySeller( seller : Seller) = viewModelScope.launch {
        _sellerViewModelState.value = SellerViewModelState.Loading

        try {
            coroutineScope {
                var modifyResult = false

                modifyResult = async {
                    SellerService.modifySellerObject(seller)
                }.await()

                if(modifyResult){
                    _sellerViewModelState.value = SellerViewModelState.UpdateSuccessFully(seller)
                }
                else{
                    _sellerViewModelState.value = SellerViewModelState.Error("No se pudo modificar el vendedor")
                    return@coroutineScope
                }
            }

        }catch (e : Exception){
            _sellerViewModelState.value = SellerViewModelState.Error(e.message)
        }
    }

    fun deleteSeller(seller : Seller) = viewModelScope.launch{
        _sellerViewModelState.value = SellerViewModelState.Loading

        try {
            coroutineScope {

                var isSellerImageDelete : Boolean = false
                var isSellerInformationDelete : Boolean = false

                isSellerImageDelete = async {
                    SellerService.deleteSellerImages(seller)
                }.await()

                if(isSellerImageDelete){

                    isSellerInformationDelete = async {
                        SellerService.deleteSellerInformation(seller)
                    }.await()

                    if(isSellerInformationDelete){
                        _sellerViewModelState.value = SellerViewModelState.DeleteSuccessFully(seller.uuId!!)
                        return@coroutineScope
                    }
                    else{
                        _sellerViewModelState.value = SellerViewModelState.Error("No se pudo eliminar la informacion del vendedor")
                        return@coroutineScope
                    }

                }
                else{
                    _sellerViewModelState.value = SellerViewModelState.Error("No se pudieron eliminar ambas imagenes, el vendedor no se eliminara")
                    return@coroutineScope
                }
            }

        }catch (e : Exception){
            _sellerViewModelState.value = SellerViewModelState.Error(e.message)
        }
    }

    fun registerSellerReview(seller : Seller , review : Review) = viewModelScope.launch {
        _sellerViewModelState.value = SellerViewModelState.Loading

        try {
            coroutineScope {
                val registerReviewResult = async {
                    SellerService.registerSellerReviewObject(seller, review)
                }.await()

                if(registerReviewResult){
                    _sellerViewModelState.value = SellerViewModelState.RegisterReviewSuccesFully(review)
                }
                else{
                    _sellerViewModelState.value = SellerViewModelState.Error("No se pudo registrar la reseña")
                    return@coroutineScope
                }
            }
        }catch (e  : Exception){
            _sellerViewModelState.value = SellerViewModelState.Error(e.message)
        }
    }

    fun recoverSellerReviews(seller : Seller) = viewModelScope.launch{
        _sellerViewModelState.value = SellerViewModelState.Loading
        val reviewsFoundList : MutableList<Review> = mutableListOf<Review>()

        try {

            coroutineScope {

                val querySnapshot = async {
                    SellerService.recoverReviewsFromSellerConsult(seller)
                }.await()

                when(querySnapshot){
                    null -> {
                        _sellerViewModelState.value = SellerViewModelState.Error("Ocurrio un error en el servidor")
                        return@coroutineScope
                    }
                    else -> {
                        if(querySnapshot.isEmpty){
                            _sellerViewModelState.value = SellerViewModelState.Empty
                            return@coroutineScope
                        }else{
                            reviewsFoundList.addAll(querySnapshot.toObjects(Review::class.java))
                            _sellerViewModelState.value = SellerViewModelState.RecoverReviewsSuccessFully(reviewsFoundList)
                            return@coroutineScope
                        }
                    }
                }

            }

        }catch (e : Exception){
            _sellerViewModelState.value = SellerViewModelState.Error(e.message)
        }

    }

    fun recoverSellerOrders(seller : Seller) = viewModelScope.launch {

        _sellerViewModelState.value = SellerViewModelState.Loading

        val ordersFoundList : MutableList<Order> = mutableListOf<Order>()
        val ordersFoundListFiltred: MutableList<Order> = mutableListOf<Order>()

        try {
            coroutineScope {

                val querySnapshot = async {
                    SellerService.recoverOrdersFromSeller(seller)
                }.await()

                when(querySnapshot){
                    null -> {
                        _sellerViewModelState.value = SellerViewModelState.Error("Ocurrio un error en el servidor")
                        return@coroutineScope
                    }
                    else -> {
                        if(querySnapshot.isEmpty){
                            _sellerViewModelState.value = SellerViewModelState.Empty
                            return@coroutineScope
                        }else{
                            ordersFoundList.addAll(querySnapshot.toObjects(Order::class.java))
                            for(order in ordersFoundList){
                                if(order.orderStatus.equals("Apartado"))
                                    ordersFoundListFiltred.add(order)
                            }
                            _sellerViewModelState.value = SellerViewModelState.RecoverOrdersSuccessFully(ordersFoundListFiltred)
                            return@coroutineScope
                        }
                    }
                }

            }
        }catch (e : Exception){
            _sellerViewModelState.value = SellerViewModelState.Error(e.message)
        }

    }

    fun recoverBuyerFromOrder(order : Order) = viewModelScope.launch{
        _sellerViewModelState.value = SellerViewModelState.Loading

        try{
            coroutineScope {
                var buyerFound : Buyer? = null
                val documentSnapshot : DocumentSnapshot? = BuyerService.searchBuyerFromOrder(order)
                if(documentSnapshot?.get(MarketUser.UUID_KEY) == order.uuIdBuyer){
                    buyerFound = documentSnapshot?.toObject(Buyer::class.java)!!
                    _sellerViewModelState.value = SellerViewModelState.BuyerFindSuccessFully(buyerFound!!)
                    return@coroutineScope
                }else{
                    _sellerViewModelState.value = SellerViewModelState.Empty
                    return@coroutineScope
                }
            }
        }
        catch (e : Exception){
            _sellerViewModelState.value = SellerViewModelState.Error(e.message)
        }
    }

    fun deleteSellerOrder(order : Order, seller : Seller) = viewModelScope.launch{
        _sellerViewModelState.value = SellerViewModelState.Loading

        try {
            coroutineScope {

                var isOrderDelete : Boolean = false

                isOrderDelete = async {
                    SellerService.deleteOrderFromSeller(order, seller)
                }.await()

                if(isOrderDelete){
                    _sellerViewModelState.value = SellerViewModelState.OrderDeleteSuccessFully(order.uuIdOrder!!)
                    return@coroutineScope
                }
                else{
                    _sellerViewModelState.value = SellerViewModelState.Error("No se pudo eliminar la informacion de la orden")
                    return@coroutineScope
                }
            }

        }catch (e : Exception){
            _sellerViewModelState.value = SellerViewModelState.Error(e.message)
        }
    }

    fun cleanViewModelState(){
        _sellerViewModelState.value = SellerViewModelState.Clean
    }
}