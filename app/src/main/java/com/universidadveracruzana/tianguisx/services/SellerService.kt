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

object SellerService {

    suspend fun uploadSellerProfileImageUri(imageUri : Uri, fileName: String) : String? = withContext(Dispatchers.IO){
        return@withContext FirebaseStorageManager.uploadImageFromUri(uri = imageUri, folderName = PathFBStorage.SELLER_PROFILE_IMAGE_FOLDER_PATH, fileName = fileName)
    }

    suspend fun uploadQrSellerImageFile(imageBitmap : Bitmap, fileName : String) : String? = withContext(Dispatchers.IO){
        return@withContext FirebaseStorageManager.uploadImageFromBitmap(imageBitmap = imageBitmap, folderName = PathFBStorage.SELLER_QRS_IMAGES_FOLDER_PATH, fileName = fileName)
    }

    suspend fun registerSellerObject(seller : Seller) : Boolean = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.registerSeller(seller)
    }

    suspend fun searchSellerByUuid(uuid : String, marketPath : String) : DocumentSnapshot? = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.searchSellerByUuid(uuid, marketPath)
    }

    suspend fun deleteSellerImages(seller: Seller) : Boolean = withContext(Dispatchers.IO){
        return@withContext FirebaseStorageManager.deleteBothSellerImages(seller)
    }

    suspend fun deleteSellerInformation(seller : Seller) : Boolean = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.deleteSeller(seller)
    }

    suspend fun modifySellerObject(seller : Seller) : Boolean = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.modifySeller(seller)
    }

    suspend fun recoverReviewsFromSellerConsult(seller : Seller) : QuerySnapshot? = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.recoverSellerReviews(seller)
    }

    suspend fun registerSellerReviewObject(seller: Seller , review: Review) : Boolean = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.registerSellerReview(seller, review)
    }

    suspend fun recoverOrdersFromSeller(seller : Seller) : QuerySnapshot? = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.recoverSellerOrders(seller)
    }

    suspend fun recoverSellerFromOrder(order: Order, marketPath : String) : DocumentSnapshot? = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.recoverSellerFromOrder(order, marketPath)
    }

    suspend fun deleteOrderFromSeller(order : Order, seller : Seller) : Boolean = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.deleteOrderFromSeller(order, seller)
    }

}