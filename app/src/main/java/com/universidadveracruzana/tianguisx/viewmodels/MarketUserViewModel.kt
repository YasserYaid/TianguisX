package com.universidadveracruzana.tianguisx.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.entities.MarketUser
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.firebase.storage.FirebaseStorageManager
import com.universidadveracruzana.tianguisx.services.BuyerService
import com.universidadveracruzana.tianguisx.services.MarketUserService
import com.universidadveracruzana.tianguisx.services.SellerService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MarketUserViewModelState{

    data class RegisterSuccessFully(val marketUser : MarketUser) : MarketUserViewModelState()
    data class SignInBuyerSuccessFully(val buyer : Buyer) : MarketUserViewModelState()
    data class SignInSellerSuccessFully(val seller : Seller) : MarketUserViewModelState()
    data class UpdateSuccessFully(val marketUser : MarketUser) : MarketUserViewModelState()
    data class DeleteSuccessFully(val uuId : String) : MarketUserViewModelState()

    data class Error(val message : String?) : MarketUserViewModelState ()

    data object Empty : MarketUserViewModelState ()
    data object Loading : MarketUserViewModelState ()

    data object None : MarketUserViewModelState ()

    data object Clean : MarketUserViewModelState ()

}

class MarketUserViewModel : ViewModel () {

    private val _marketUserViewModelState = MutableStateFlow<MarketUserViewModelState>(MarketUserViewModelState.None)
    val marketUserViewModelState : StateFlow<MarketUserViewModelState> = _marketUserViewModelState

    fun cleanViewModelState(){
        _marketUserViewModelState.value = MarketUserViewModelState.Clean
    }

    fun sigInMarketUser(email : String, password: String, typeUser : String, markets : List<String>) = viewModelScope.launch {
        _marketUserViewModelState.value = MarketUserViewModelState.Loading

        try{
            coroutineScope {

                var documentSnapshot : DocumentSnapshot?
                var buyerFound : Buyer? = null
                var sellerFound : Seller? = null

                val firebaseUser = async {
                    MarketUserService.signInWithAuth(email, password)
                }.await().user

                if(firebaseUser == null){
                    _marketUserViewModelState.value = MarketUserViewModelState.Empty
                    return@coroutineScope
                }
                else {
                    if (typeUser == "Comprador"){
                        documentSnapshot = BuyerService.searchBuyerByUuid(firebaseUser.uid)
                        if(documentSnapshot?.get(MarketUser.UUID_KEY) == firebaseUser.uid){
                            buyerFound = documentSnapshot.toObject(Buyer::class.java)!!
                            _marketUserViewModelState.value = MarketUserViewModelState.SignInBuyerSuccessFully(buyerFound!!)
                            return@coroutineScope
                        }
                    }
                    if (typeUser == "Vendedor"){
                        for (marketPath in markets){
                            documentSnapshot = SellerService.searchSellerByUuid(firebaseUser.uid, marketPath)
                            if(documentSnapshot?.get(MarketUser.UUID_KEY) == firebaseUser.uid){
                                sellerFound = documentSnapshot.toObject(Seller::class.java)
                                break
                            }
                        }
                        _marketUserViewModelState.value = MarketUserViewModelState.SignInSellerSuccessFully(sellerFound!!)
                        return@coroutineScope
                    }
                }
                if(buyerFound == null && sellerFound == null){
                    _marketUserViewModelState.value = MarketUserViewModelState.Empty
                    return@coroutineScope
                }
            }
        }
        catch (e : Exception){
            _marketUserViewModelState.value = MarketUserViewModelState.Error(e.message)
        }
    }

    /*  USABLES

    fun signUpMarketUser(imageUri : Uri, marketUser : MarketUser) = viewModelScope.launch {
        _marketUserViewModelState.value = MarketUserViewModelState.Loading

        try{
            coroutineScope {
                var uploadImageUrl : String? = null
                var imagePath : String? = null

                val signUpResult = async {
                    MarketUserService.signUpWhitAuth(marketUser.email!!, marketUser.password!!)
                }.await()

                if(signUpResult.user?.uid != null){
                    marketUser.uuId = signUpResult.user?.uid
                }
                else{
                    _marketUserViewModelState.value = MarketUserViewModelState.Error("No se pudo resigstrar, probablemente el correo ya exista intente con otro correo")
                    return@coroutineScope
                }

                uploadImageUrl = async {
                    MarketUserService.uploadImageFile(imageUri, "${marketUser.uuId!!}.jpg")
                }.await()

                if(uploadImageUrl != null){
                    imagePath = "${FirebaseStorageManager.SELLER_IMAGE_FOLDER}${marketUser.uuId}.jpg"
                    marketUser.profileImageURL = uploadImageUrl
                    marketUser.profileImageFileCloudPath = imagePath
                }
                else{
                    _marketUserViewModelState.value = MarketUserViewModelState.Error("La imagen no se pudo subir")
                    return@coroutineScope
                }

                val registerResult = async {
                    MarketUserService.registerMarketUserObject(marketUser)
                }.await()

                if(registerResult){
                    _marketUserViewModelState.value = MarketUserViewModelState.RegisterSuccessFully(marketUser)
                }
                else{
                    _marketUserViewModelState.value = MarketUserViewModelState.Error("No se pudo registrar")
                    return@coroutineScope
                }
            }
        }catch ( e : Exception ){
            _marketUserViewModelState.value = MarketUserViewModelState.Error(e.message)
        }

    }

        fun modify(imageUri: Uri?, marketUser : MarketUser) = viewModelScope.launch {

        _marketUserViewModelState.value = MarketUserViewModelState.Loading

        try{

            coroutineScope {

                //Modify member info with or whithout image file
                imageUri?.let {
                    val uploadImage = async {
                        MarketUserService.uploadImageFile(imageUri , "${marketUser.uuId}.jpg")
                    }
                    val imageUrl = uploadImage.await()
                    marketUser.profileImageURL= imageUrl
                }

                val modify = async {
                    MarketUserService.modify(marketUser)
                }
                modify.await()
                _marketUserViewModelState.value = MarketUserViewModelState.UpdateSuccessFully(marketUser)
            }

        }catch (e : Exception){
            _marketUserViewModelState.value = MarketUserViewModelState.Error(e.message)
        }
    }

    fun delete(marketUser : MarketUser) = viewModelScope.launch {

        _marketUserViewModelState.value = MarketUserViewModelState.Loading

        try {
            coroutineScope {

                val deleteMarketUserImage = async {
                    MarketUserService.deleteImageFile(marketUser.profileImageFileCloudPath!!)
                }

                deleteMarketUserImage.await()

                val deleteMarketUserInformation = async {
                    MarketUserService.delete(marketUser.uuId!!)
                }

                deleteMarketUserInformation.await()
                _marketUserViewModelState.value = MarketUserViewModelState.DeleteSuccessFully(marketUser.uuId!!)
            }
        }catch (e : Exception){
            _marketUserViewModelState.value = MarketUserViewModelState.Error(e.message)
        }

    }

     */







    /* NO USABLES
    
    fun register(imageUri : Uri, imageFileName : String, marketUser : MarketUser) = viewModelScope.launch {
        _marketUserViewModelState.value = MarketUserViewModelState.Loading

        try{
            coroutineScope {

                val uploadImage = async {
                    MarketUserService.uploadImageFile(imageUri, imageFileName)
                }

                val imageUrl = uploadImage.await()
                val imagePath = "${FirebaseStorageManager.SELLER_IMAGE_FOLDER}$imageFileName"

                marketUser.profileImageURL = imageUrl
                marketUser.profileImageFileCloudPath = imagePath


                val register = async {
                    MarketUserService.register(marketUser)
                }
                register.await()
                _marketUserViewModelState.value = MarketUserViewModelState.RegisterSuccessFully(marketUser)
            }
        }catch ( e : Exception ){
            _marketUserViewModelState.value = MarketUserViewModelState.Error(e.message)
        }

    }
    
    fun signIn(email : String, password : String) = viewModelScope.launch {
        _marketUserViewModelState.value = MarketUserViewModelState.Loading
        try{
            val marketUserslist : MutableList<MarketUser?> = mutableListOf<MarketUser?>()

            coroutineScope {

                val querySnapshot = async {
                    MarketUserService.signIn(email, password)
                }.await()

                if(querySnapshot.isEmpty){
                    _marketUserViewModelState.value = MarketUserViewModelState.Empty
                    return@coroutineScope
                }

                else{
                    /*
                    for (document in querySnapshot.documents){
                        marketUserslist.add(document.toObject(MarketUser::class.java))
                    }
                    */
                    marketUserslist.addAll(querySnapshot.toObjects())
                    _marketUserViewModelState.value = MarketUserViewModelState.SignInSuccessFully(marketUserslist.first()!!)
                }
            }
        }
        catch (e : Exception){
            _marketUserViewModelState.value = MarketUserViewModelState.Error("MARKET_USER_VIEWMODEL SIGNIN " + e.message)
        }
    }

    fun sigInMarketUser(email : String, password: String) = viewModelScope.launch {
        _marketUserViewModelState.value = MarketUserViewModelState.Loading

        try{
            coroutineScope {

                val firebaseUser = async {
                    MarketUserService.signInWithAuth(email, password)
                }.await().user

                if(firebaseUser == null){
                    _marketUserViewModelState.value = MarketUserViewModelState.Empty
                    return@coroutineScope
                }
                else {
                    var documentSnapshot = MarketUserService.searchMarketUserByUuid(firebaseUser.uid)
                    _marketUserViewModelState.value = MarketUserViewModelState.SignInSuccessFully(documentSnapshot.toObject(MarketUser::class.java)!!)
                    /*
                    if(documentSnapshot[MarketUser.TYPE_USER_KEY]!! == "BUYER"){
                        var buyerFound : Buyer? = documentSnapshot.toObject(Buyer::class.java)
                        _marketUserViewModelState.value = MarketUserViewModelState.SignInSellerSuccessFully(buyerFound)
                    }else if(documentSnapshot[MarketUser.TYPE_USER_KEY]!! == "SELLER"){
                        var sellerFound : Seller? = documentSnapshot.toObject(Seller::class.java)
                        _marketUserViewModelState.value = MarketUserViewModelState.SignInSellerSuccessFully(sellerFound)
                    }
                    */
                }
            }
        }
        catch (e : Exception){
            _marketUserViewModelState.value = MarketUserViewModelState.Error(e.message)
        }
    }

    */
}
