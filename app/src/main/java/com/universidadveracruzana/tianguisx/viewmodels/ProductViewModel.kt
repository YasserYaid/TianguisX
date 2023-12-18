package com.universidadveracruzana.tianguisx.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.entities.Order
import com.universidadveracruzana.tianguisx.entities.Product
import com.universidadveracruzana.tianguisx.entities.Review
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.services.ProductService
import com.universidadveracruzana.tianguisx.utils.PathFBStorage
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.glxn.qrgen.android.QRCode
import org.checkerframework.checker.units.qual.Current

sealed class ProductViewModelState{

    data class RegisterSuccessFully(val product : Product) : ProductViewModelState()
    data class ProductFindSuccessFully(val product : Product) : ProductViewModelState()
    data class RegisterOrderSuccesFully(val order : Order) : ProductViewModelState()
    data class RegisterReviewSuccesFully(val review : Review) : ProductViewModelState()
    data class RecoverProductsSuccessFully(val productList : MutableList<Product>) : ProductViewModelState()
    data class RecoverReviewsSuccessFully(val reviewList : MutableList<Review>) : ProductViewModelState()
    data class UpdateSuccessFully(val product : Product) : ProductViewModelState()
    data class DeleteSuccessFully(val uuId : String) : ProductViewModelState()

    data class Error(val message : String?) : ProductViewModelState ()

    data object Empty : ProductViewModelState ()
    data object Loading : ProductViewModelState ()

    data object None : ProductViewModelState ()

    data object Clean : ProductViewModelState ()

}

class ProductViewModel : ViewModel() {

    private val _productViewModelState = MutableStateFlow<ProductViewModelState>(ProductViewModelState.None)
    val productViewModelState : StateFlow<ProductViewModelState> = _productViewModelState

    fun cleanViewModelState(){
        _productViewModelState.value = ProductViewModelState.Clean
    }

    fun registerOrderToBuy(buyer : Buyer, order : Order) = viewModelScope.launch {
        _productViewModelState.value = ProductViewModelState.Loading

        try {
            coroutineScope {
                val registerOrderResult = async {
                    ProductService.registerOrderObject(buyer, order)
                }.await()

                if(registerOrderResult){
                    _productViewModelState.value = ProductViewModelState.RegisterOrderSuccesFully(order)
                }
                else{
                    _productViewModelState.value = ProductViewModelState.Error("No se pudo registrar el producto")
                    return@coroutineScope
                }
            }
        }catch (e  : Exception){
            _productViewModelState.value = ProductViewModelState.Error(e.message)
        }
    }

    fun registerProductReview(buyer : Buyer, review : Review) = viewModelScope.launch {
        _productViewModelState.value = ProductViewModelState.Loading

        try {
            coroutineScope {
                val registerReviewResult = async {
                    ProductService.registerProductReviewObject(buyer, review)
                }.await()

                if(registerReviewResult){
                    _productViewModelState.value = ProductViewModelState.RegisterReviewSuccesFully(review)
                }
                else{
                    _productViewModelState.value = ProductViewModelState.Error("No se pudo registrar la reseña")
                    return@coroutineScope
                }
            }
        }catch (e  : Exception){
            _productViewModelState.value = ProductViewModelState.Error(e.message)
        }
    }

    fun registerProduct(seller : Seller, product: Product, productImageUri : Uri) = viewModelScope.launch {
        _productViewModelState.value = ProductViewModelState.Loading

        try{
            coroutineScope {
                var uploadQrImageUrl : String? = null
                var qrImagePath : String? = null
                var uploadProductImageUrl : String? = null
                var productImagePath : String? = null

                val qrImageBitmap = QRCode.from("Producto" + "_" + seller.state + "_" + seller.city + "_" + seller.market + "_" + product.uuIdProduct).withSize(300, 300).bitmap()

                uploadQrImageUrl = async {
                    ProductService.uploadQrProductImageFile(qrImageBitmap,"${product.uuIdProduct!!}.jpg")
                }.await()

                if(uploadQrImageUrl != null){
                    qrImagePath = "${PathFBStorage.PRODUCTS_QR_IMAGES_FOLDER_PATH}${product.uuIdProduct}.jpg"
                    product.qrImageURL = uploadQrImageUrl
                    product.qrImageFileCloudPath = qrImagePath
                }else{
                    _productViewModelState.value = ProductViewModelState.Error("La imagen qr del producto no se pudo subir")
                    return@coroutineScope
                }

                uploadProductImageUrl = async {
                    ProductService.uploadProductDescriptiveImageUri(productImageUri,"${product.uuIdProduct}.jpg")
                }.await()

                if(uploadProductImageUrl != null){
                    productImagePath = "${PathFBStorage.PRODUCTS_IMAGES_FOLDER_PATH}${product.uuIdProduct}.jpg"
                    product.productImageURL = uploadProductImageUrl
                    product.productImageFileCloudPath = productImagePath
                }else{
                    _productViewModelState.value = ProductViewModelState.Error("La imagen del producto no se pudo subir")
                    return@coroutineScope
                }

                val registerResult = async {
                    ProductService.registerProductObject(seller, product)
                }.await()

                if(registerResult){
                    _productViewModelState.value = ProductViewModelState.RegisterSuccessFully(product)
                }
                else{
                    _productViewModelState.value = ProductViewModelState.Error("No se pudo registrar el producto")
                    return@coroutineScope
                }

            }
        }catch (e  : Exception){
            _productViewModelState.value = ProductViewModelState.Error(e.message)
        }
    }

    fun recoverProductReviews(product : Product) = viewModelScope.launch{
        _productViewModelState.value = ProductViewModelState.Loading
        val reviewsFoundList : MutableList<Review> = mutableListOf<Review>()

        try {

            coroutineScope {

                val querySnapshot = async {
                    ProductService.recoverReviewsFromProduct(product)
                }.await()

                when(querySnapshot){
                    null -> {
                        _productViewModelState.value = ProductViewModelState.Error("Ocurrio un error en el servidor")
                        return@coroutineScope
                    }
                    else -> {
                        if(querySnapshot.isEmpty){
                            _productViewModelState.value = ProductViewModelState.Empty
                            return@coroutineScope
                        }else{
                            reviewsFoundList.addAll(querySnapshot.toObjects(Review::class.java))
                            _productViewModelState.value = ProductViewModelState.RecoverReviewsSuccessFully(reviewsFoundList)
                            return@coroutineScope
                        }
                    }
                }

            }

        }catch (e : Exception){
            _productViewModelState.value = ProductViewModelState.Error(e.message)
        }

    }

    fun recoverSellerInventory(seller : Seller) = viewModelScope.launch {
        _productViewModelState.value = ProductViewModelState.Loading
        val productsFoundList : MutableList<Product> = mutableListOf<Product>()

        try{

            coroutineScope {

                val querySnapshot = async {
                    ProductService.recoverInventoryFromSeller(seller)
                }.await()

                when(querySnapshot){
                    null -> {
                        _productViewModelState.value = ProductViewModelState.Error("Ocurrio un error en el servidor")
                        return@coroutineScope
                    }
                    else -> {
                        if(querySnapshot.isEmpty){
                            _productViewModelState.value = ProductViewModelState.Empty
                            return@coroutineScope
                        }else{
                            productsFoundList.addAll(querySnapshot.toObjects(Product::class.java))
                            _productViewModelState.value = ProductViewModelState.RecoverProductsSuccessFully(productsFoundList)
                            return@coroutineScope
                        }
                    }
                }

            }
        }
        catch (e : Exception){
            _productViewModelState.value = ProductViewModelState.Error(e.message)
        }
    }

    fun exploreProducts(statePath : String, cityPath : String, marketPath : String, category : String) = viewModelScope.launch {
        _productViewModelState.value = ProductViewModelState.Loading
        val productsFoundList : MutableList<Product> = mutableListOf<Product>()

        try{

            coroutineScope {

                val querySnapshot = async {
                    ProductService.exploreProductsFromBuyer(statePath, cityPath, marketPath, category)
                }.await()

                when(querySnapshot){
                    null -> {
                        _productViewModelState.value = ProductViewModelState.Error("Ocurrio un error en el servidor")
                        return@coroutineScope
                    }
                    else -> {
                        if(querySnapshot.isEmpty){
                            _productViewModelState.value = ProductViewModelState.Empty
                            return@coroutineScope
                        }else{
                            productsFoundList.addAll(querySnapshot.toObjects(Product::class.java))
                            _productViewModelState.value = ProductViewModelState.RecoverProductsSuccessFully(productsFoundList)
                            return@coroutineScope
                        }
                    }
                }
            }
        }
        catch (e : Exception){
            _productViewModelState.value = ProductViewModelState.Error(e.message)
        }
    }

    fun deleteProduct(seller : Seller, product: Product) = viewModelScope.launch{
        _productViewModelState.value = ProductViewModelState.Loading

        try {
            coroutineScope {

                var isProductImageDelete : Boolean = false
                var isProductInformationDelete : Boolean = false

                isProductImageDelete = async {
                    ProductService.deleteProductImages(product)
                }.await()

                if(isProductImageDelete){

                    isProductInformationDelete = async {
                        ProductService.deleteProductInformation(seller, product)
                    }.await()

                    if(isProductInformationDelete){
                        _productViewModelState.value = ProductViewModelState.DeleteSuccessFully(product.uuIdProduct!!)
                        return@coroutineScope
                    }
                    else{
                        _productViewModelState.value = ProductViewModelState.Error("No se pudo eliminar la informacion del producto")
                        return@coroutineScope
                    }

                }
                else{
                    _productViewModelState.value = ProductViewModelState.Error("No se pudieron eliminar ambas imagenes, el producto no se eliminara")
                    return@coroutineScope
                }
            }

        }catch (e : Exception){
            _productViewModelState.value = ProductViewModelState.Error(e.message)
        }
    }

    fun modifyProduct(seller : Seller, product : Product, newUriImage : Uri?) = viewModelScope.launch{
        _productViewModelState.value = ProductViewModelState.Loading
        try{
            coroutineScope {
                var uploadProductImageUrl : String? = null
                var productImagePath : String? = null
                var modifyResult = false

                if(newUriImage != null){
                    if(product.productImageURL != null ){
                        ProductService.deleteDescriptiveImageProduct(product.productImageFileCloudPath!!)
                        product.productImageURL = null
                        product.productImageFileCloudPath = null
                    }
                    uploadProductImageUrl = async {
                        ProductService.uploadProductDescriptiveImageUri(newUriImage,"${product.uuIdProduct}.jpg")
                    }.await()
                    if(uploadProductImageUrl != null){
                        productImagePath = "${PathFBStorage.PRODUCTS_IMAGES_FOLDER_PATH}${product.uuIdProduct}.jpg"
                        product.productImageURL = uploadProductImageUrl
                        product.productImageFileCloudPath = productImagePath
                    }else{
                        _productViewModelState.value = ProductViewModelState.Error("La imagen del producto no se pudo subir")
                        return@coroutineScope
                    }
                }

                modifyResult = async {
                    ProductService.modifyProductObject(seller, product)
                }.await()

                if(modifyResult){
                    _productViewModelState.value = ProductViewModelState.UpdateSuccessFully(product)
                }
                else{
                    _productViewModelState.value = ProductViewModelState.Error("No se pudo modificar el producto")
                    return@coroutineScope
                }
            }

        }catch (e : Exception){
            _productViewModelState.value = ProductViewModelState.Error(e.message)
        }
    }

    /*
    fun deleteProduct(seller : Seller, product: Product) = viewModelScope.launch{
        _productViewModelState.value = ProductViewModelState.Loading

        try {
            coroutineScope {

                var isProductImageDelete : Boolean? = null
                var isProductInformationDelete : Boolean? = null

                isProductImageDelete = async {
                    ProductService.deleteProductImages(product)
                }.await()

                when(isProductImageDelete){
                    true -> {
                        isProductInformationDelete = async {
                            ProductService.deleteProductInformation(seller, product)
                        }.await()
                        isProductInformationDelete?.let {
                            if(isProductInformationDelete){
                                _productViewModelState.value = ProductViewModelState.DeleteSuccessFully(product.uuIdProduct!!)
                                return@coroutineScope
                            }else{
                                _productViewModelState.value = ProductViewModelState.Error("No se pudo eliminar la informacion del producto")
                                return@coroutineScope
                            }
                        }
                        _productViewModelState.value = ProductViewModelState.Error("No se pudo eliminar la informacion del producto")
                        return@coroutineScope
                    }
                    false -> {
                        _productViewModelState.value = ProductViewModelState.Error("No se pudieron eliminar ambas imagenes, el producto no se eliminara")
                        return@coroutineScope
                    }
                    null -> {
                        _productViewModelState.value = ProductViewModelState.Error("No se pudieron eliminar ambas imagenes, el producto no se eliminara")
                        return@coroutineScope
                    }
                }
            }

        }catch (e : Exception){
            _productViewModelState.value = ProductViewModelState.Error(e.message)
        }
    }
     */

}