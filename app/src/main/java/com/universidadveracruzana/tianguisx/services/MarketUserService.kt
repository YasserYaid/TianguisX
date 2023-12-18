package com.universidadveracruzana.tianguisx.services

import android.net.Uri
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentSnapshot
import com.universidadveracruzana.tianguisx.entities.MarketUser
import com.universidadveracruzana.tianguisx.firebase.cloudFireStore.CloudFireStoreWrapper
import com.universidadveracruzana.tianguisx.firebase.storage.FirebaseStorageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object MarketUserService {

    suspend fun signUpWhitAuth (email : String, password: String) : AuthResult = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.signUpWithFirebaseAuth(email, password)
    }

    suspend fun signInWithAuth(email : String , password : String) : AuthResult = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.signInWithFirebaseAuth(email, password)
    }
/* USABLES

    suspend fun searchMarketUserByUuid(uuid : String) : DocumentSnapshot = withContext(Dispatchers.IO) {
        return@withContext CloudFireStoreWrapper.selectOneMarketUser(uuid)
    }

    suspend fun registerMarketUserObject(marketUser: MarketUser) : Boolean = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.registerMarketUser(marketUser)
    }
*/
/////////////////////////////////////////////////

    /* USABLES

    suspend fun modify(marketUser : MarketUser) : Void = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.update(MarketUser.CLOUD_FIRE_STORE_PATH, marketUser.uuId!!, marketUser.toDictionary())
    }

    suspend fun delete(uuId : String) : Void = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.delete(MarketUser.CLOUD_FIRE_STORE_PATH , uuId)
    }

    suspend fun uploadImageFile(uri : Uri, fileName : String) : String? = withContext(Dispatchers.IO){
        return@withContext FirebaseStorageManager.uploadImage(uri = uri, folderName = FirebaseStorageManager.SELLER_IMAGE_FOLDER, fileName = fileName)
    }

    suspend fun deleteImageFile(filePath : String) = withContext(Dispatchers.IO){
        return@withContext FirebaseStorageManager.deleteImage(filePath)
    }


     */

/* NO USABLES
    suspend fun register(marketUser : MarketUser) : Void = withContext(Dispatchers.IO){
        return@withContext CloudFireStoreWrapper.replace(
            MarketUser.CLOUD_FIRESTORE_PATH,
            marketUser.uuId!!,
            marketUser.toDictionary()
        )
    }
    suspend fun signIn(email : String, password : String) : QuerySnapshot = withContext(Dispatchers.IO){
        val map = mutableMapOf<String, Any>()
        map[MarketUser.EMAIL_KEY] = email
        map[MarketUser.PASSWORD_KEY] = password
        return@withContext CloudFireStoreWrapper.select(
            MarketUser.CLOUD_FIRESTORE_PATH,
            map
        )
    }
*/

}