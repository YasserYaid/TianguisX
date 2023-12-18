package com.universidadveracruzana.tianguisx.services

import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.entities.Order
import com.universidadveracruzana.tianguisx.entities.Product
import com.universidadveracruzana.tianguisx.entities.Review
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.firebase.cloudFireStore.CloudFireStoreWrapper
import com.universidadveracruzana.tianguisx.firebase.storage.FirebaseStorageManager
import com.universidadveracruzana.tianguisx.utils.PathFBStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ProductService {

    suspend fun uploadQrProductImageFile(imageBitmap : Bitmap, fileName : String) : String? = withContext(Dispatchers.IO){
        return@withContext FirebaseStorageManager.uploadImageFromBitmap(imageBitmap = imageBitmap, folderName = PathFBStorage.PRODUCTS_QR_IMAGES_FOLDER_PATH, fileName = fileName)
    }

    suspend fun uploadProductDescriptiveImageUri(imageUri : Uri, fileName: String) : String? = withContext(Dispatchers.IO){
        return@withContext FirebaseStorageManager.uploadImageFromUri(uri = imageUri, folderName = PathFBStorage.PRODUCTS_IMAGES_FOLDER_PATH, fileName = fileName)
    }

    suspend fun registerProductObject(seller : Seller, product : Product) : Boolean = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.registerProduct(seller, product)
    }

    suspend fun recoverInventoryFromSeller(seller : Seller) : QuerySnapshot? = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.recoverInventorySeller(seller)
    }

    suspend fun deleteDescriptiveImageProduct(productImageCloudPath : String) : Boolean = withContext(Dispatchers.IO){
        return@withContext FirebaseStorageManager.deleteImage(productImageCloudPath)
    }

    suspend fun deleteProductImages(product : Product) : Boolean = withContext(Dispatchers.IO){
        return@withContext FirebaseStorageManager.deleteBothProductImages(product)
    }

    suspend fun deleteProductInformation(seller : Seller, product : Product) : Boolean = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.deleteProduct(seller, product)
    }

    suspend fun modifyProductObject(seller : Seller, product: Product) : Boolean = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.modifyProduct(seller, product)
    }

    suspend fun exploreProductsFromBuyer(statePath : String, cityPath : String, marketPath : String, category : String) : QuerySnapshot? = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.searchProducts(statePath, cityPath, marketPath, category)
    }

    suspend fun searchProductByUuid(statePath : String, cityPath : String, marketPath: String, uuidProduct : String) : QuerySnapshot? = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.searchProductByUuid(statePath, cityPath, marketPath, uuidProduct)
    }

    suspend fun registerOrderObject(buyer : Buyer, order: Order) : Boolean = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.registerOrder(buyer, order)
    }

    suspend fun registerProductReviewObject(buyer : Buyer, review: Review) : Boolean = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.registerProductReview(buyer, review)
    }

    suspend fun recoverReviewsFromProduct(product : Product) : QuerySnapshot? = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.recoverProductReviews(product)
    }

    suspend fun recoverProductFromOrder(order: Order, marketPath : String) : DocumentSnapshot? = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.recoverProductFromOrder(order, marketPath)
    }

}