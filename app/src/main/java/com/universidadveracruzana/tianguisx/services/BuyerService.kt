package com.universidadveracruzana.tianguisx.services

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.entities.Order
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.firebase.cloudFireStore.CloudFireStoreWrapper
import com.universidadveracruzana.tianguisx.firebase.storage.FirebaseStorageManager
import com.universidadveracruzana.tianguisx.utils.PathFBStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object BuyerService {

    suspend fun uploadBuyerProfileImageUri(imageUri : Uri, fileName: String) : String? = withContext(Dispatchers.IO){
        return@withContext FirebaseStorageManager.uploadImageFromUri(uri = imageUri, folderName = PathFBStorage.BUYER_PROFILES_IMAGE_FOLDER_PATH, fileName = fileName)
    }

    suspend fun registerBuyerObject(buyer : Buyer) : Boolean = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.registerBuyer(buyer)
    }

    suspend fun searchBuyerByUuid(uuid : String) : DocumentSnapshot? = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.searchBuyerByUuid(uuid)
    }

    suspend fun deleteBuyerImage(buyer : Buyer) : Boolean = withContext(Dispatchers.IO){
        return@withContext FirebaseStorageManager.deleteImage(buyer.profileImageFileCloudPath!!)
    }

    suspend fun deleteBuyerInformation(buyer : Buyer) : Boolean = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.deleteBuyer(buyer)
    }

    suspend fun modifyBuyerObject(buyer : Buyer) : Boolean = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.modifyBuyer(buyer)
    }

    suspend fun recoverOrdersFromBuyer(buyer : Buyer) : QuerySnapshot? = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.recoverBuyerOrders(buyer)
    }

    suspend fun searchBuyerFromOrder(order : Order) : DocumentSnapshot? = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.recoverBuyerFromOrder(order)
    }

    suspend fun deleteOrderFromBuyer(order : Order, buyer : Buyer) : Boolean = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.deleteOrderFromBuyer(order, buyer)
    }

}