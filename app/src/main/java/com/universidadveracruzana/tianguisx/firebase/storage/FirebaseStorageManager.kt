package com.universidadveracruzana.tianguisx.firebase.storage

import android.graphics.Bitmap
import android.net.Uri
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.universidadveracruzana.tianguisx.entities.Product
import com.universidadveracruzana.tianguisx.entities.Seller
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object FirebaseStorageManager {

    private const val FIREBASE_STORAGE_DOMAIN = "gs://tianguisx-b1f28.appspot.com"

    suspend fun uploadImageFromUri(uri : Uri, folderName : String, fileName : String) : String? {

        return suspendCoroutine {continuation ->
            val storageRef = Firebase.storage(FIREBASE_STORAGE_DOMAIN).reference
            val storageReference : StorageReference = storageRef.child("$folderName/$fileName")
            val uploadTask : UploadTask = storageReference.putFile(uri)

            uploadTask.addOnFailureListener(){
                continuation.resumeWithException(it)
            }.addOnSuccessListener {
                it.task.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>>{ task ->

                    if(!task.isSuccessful){
                        task.exception?.let {exception ->
                            continuation.resumeWithException(exception)
                        }
                    }

                    return@Continuation storageReference.downloadUrl
                }).addOnCompleteListener{ uriTask ->
                    if(uriTask.isSuccessful){
                        val downloadUri = uriTask.result
                        continuation.resume(downloadUri.toString())
                    }
                }
            }
        }
    }

    suspend fun uploadImageFromBitmap(imageBitmap: Bitmap, folderName : String, fileName : String) : String? {
        return suspendCoroutine {continuation ->
            val storageReference = Firebase.storage(FIREBASE_STORAGE_DOMAIN).reference.child("$folderName/$fileName")
            val byteArrayOutputStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val downloadUrl: UploadTask = storageReference.putBytes(byteArrayOutputStream.toByteArray())
            downloadUrl.addOnFailureListener(){
                continuation.resumeWithException(it)
            }.addOnSuccessListener {
                it.task.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>>{ task ->

                    if(!task.isSuccessful){
                        task.exception?.let {exception ->
                            continuation.resumeWithException(exception)
                        }
                    }

                    return@Continuation storageReference.downloadUrl
                }).addOnCompleteListener{ uriTask ->
                    if(uriTask.isSuccessful){
                        val downloadUri = uriTask.result
                        continuation.resume(downloadUri.toString())
                    }
                }
            }
        }
    }

    suspend fun deleteBothProductImages(product : Product) : Boolean {
        return suspendCoroutine { continuation ->
            var storageRef : StorageReference = FirebaseStorage.getInstance(FIREBASE_STORAGE_DOMAIN).reference
            var storageReference = storageRef.child(product.productImageFileCloudPath!!)
            storageReference.delete().addOnSuccessListener {
                storageReference = storageRef.child(product.qrImageFileCloudPath!!)
                storageReference.delete().addOnSuccessListener {
                    continuation.resume(true)
                }.addOnFailureListener {
                    continuation.resumeWithException(it)
                }
            }.addOnFailureListener{
                continuation.resumeWithException(it)
            }
        }
    }

    suspend fun deleteBothSellerImages(seller : Seller) : Boolean {
        return suspendCoroutine { continuation ->
            var storageRef : StorageReference = FirebaseStorage.getInstance(FIREBASE_STORAGE_DOMAIN).reference
            var storageReference = storageRef.child(seller.profileImageFileCloudPath!!)
            storageReference.delete().addOnSuccessListener {
                storageReference = storageRef.child(seller.qrImageFileCloudPath!!)
                storageReference.delete().addOnSuccessListener {
                    continuation.resume(true)
                }.addOnFailureListener {
                    continuation.resumeWithException(it)
                }
            }.addOnFailureListener{
                continuation.resumeWithException(it)
            }
        }
    }

    suspend fun deleteImage(filePath : String) : Boolean{
        return suspendCoroutine { continuation ->
            val storageRef : StorageReference = FirebaseStorage.getInstance(FIREBASE_STORAGE_DOMAIN).reference
            val storageReference = storageRef.child(filePath)
            storageReference.delete().addOnSuccessListener {
                continuation.resume(true)
            }.addOnFailureListener{
                continuation.resumeWithException(it)
            }
        }
    }
/*
    suspend fun deleteImage(filePath : String) : Void{
        return suspendCoroutine { continuation ->
            val storageRef : StorageReference = FirebaseStorage.getInstance(FIREBASE_STORAGE_DOMAIN).reference
            val storageReference = storageRef.child(filePath)
            storageReference.delete().addOnSuccessListener {
                continuation.resume(it)
            }.addOnFailureListener{
                continuation.resumeWithException(it)
            }
        }
    }
*/
}