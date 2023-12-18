package com.universidadveracruzana.tianguisx.firebase.cloudFireStore

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firestore.bundle.BundleElement
import com.universidadveracruzana.tianguisx.entities.Buyer
import com.universidadveracruzana.tianguisx.entities.MarketUser
import com.universidadveracruzana.tianguisx.entities.Order
import com.universidadveracruzana.tianguisx.entities.Product
import com.universidadveracruzana.tianguisx.entities.Review
import com.universidadveracruzana.tianguisx.entities.Seller
import com.universidadveracruzana.tianguisx.utils.PathFBCity
import com.universidadveracruzana.tianguisx.utils.PathFBCountry
import com.universidadveracruzana.tianguisx.utils.PathFBExtras
import com.universidadveracruzana.tianguisx.utils.PathFBMarketPlace
import com.universidadveracruzana.tianguisx.utils.PathFBState
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object CloudFireStoreWrapper {

    suspend fun deleteOrderFromSeller(order : Order, seller : Seller): Boolean {
        return suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PathFBCountry.MEXICO_PATH).document(seller.state!!)
                .collection(seller.city!!).document(PathFBMarketPlace.MEX_VER_XAL_NOPLACE_PATH).collection(PathFBExtras.ORDER_PRODUCTS_PATH).document(order.uuIdOrder!!).delete()
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun deleteOrderFromBuyer(order : Order, buyer : Buyer): Boolean {
        return suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PathFBCountry.MEXICO_PATH).document(buyer.state!!)
                .collection(buyer.city!!).document(PathFBMarketPlace.MEX_VER_XAL_NOPLACE_PATH).collection(PathFBExtras.ORDER_PRODUCTS_PATH).document(order.uuIdOrder!!).delete()
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun registerOrder(buyer : Buyer, order : Order): Boolean {
        return suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PathFBCountry.MEXICO_PATH).document(buyer.state!!)
                .collection(buyer.city!!).document(PathFBMarketPlace.MEX_VER_XAL_NOPLACE_PATH).collection(PathFBExtras.ORDER_PRODUCTS_PATH).document(order.uuIdOrder!!).set(order)
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun recoverBuyerFromOrder(order : Order) : DocumentSnapshot?{
        return suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PathFBCountry.MEXICO_PATH).document(order.orderState!!)
                .collection(order.orderCity!!).document(PathFBMarketPlace.MEX_VER_XAL_NOPLACE_PATH)
                .collection(PathFBExtras.BUYERS_PATH).document(order.uuIdBuyer!!).get()
                .addOnSuccessListener {
                    continuation.resume(it)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun recoverSellerOrders(seller : Seller) : QuerySnapshot? {
        return suspendCoroutine { continuation ->
            val collectionReference = Firebase.firestore.collection(PathFBCountry.MEXICO_PATH).document(seller.state!!)
                .collection(seller.city!!).document(PathFBMarketPlace.MEX_VER_XAL_NOPLACE_PATH).collection(PathFBExtras.ORDER_PRODUCTS_PATH)
            var query = collectionReference.whereEqualTo(Review.UUID_SELLER_KEY, seller.uuId)
            query.get()
                .addOnSuccessListener {
                    continuation.resume(it)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun recoverBuyerOrders(buyer : Buyer) : QuerySnapshot? {
        return suspendCoroutine { continuation ->
            val collectionReference = Firebase.firestore.collection(PathFBCountry.MEXICO_PATH).document(buyer.state!!)
                .collection(buyer.city!!).document(PathFBMarketPlace.MEX_VER_XAL_NOPLACE_PATH).collection(PathFBExtras.ORDER_PRODUCTS_PATH)
            var query = collectionReference.whereEqualTo(Review.UUID_BUYER_KEY, buyer.uuId)
            query.get()
                .addOnSuccessListener {
                    continuation.resume(it)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun recoverSellerReviews(seller: Seller) : QuerySnapshot? {
        return suspendCoroutine { continuation ->
            val collectionReference = Firebase.firestore.collection(PathFBCountry.MEXICO_PATH).document(seller.state!!)
                .collection(seller.city!!).document(PathFBMarketPlace.MEX_VER_XAL_NOPLACE_PATH).collection(PathFBExtras.SELLER_REVIEWS_PATH)
            var query = collectionReference.whereEqualTo(Review.UUID_SELLER_KEY, seller.uuId)
            query.get()
                .addOnSuccessListener {
                    continuation.resume(it)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun recoverProductReviews(product: Product) : QuerySnapshot? {
        return suspendCoroutine { continuation ->
            val collectionReference = Firebase.firestore.collection(PathFBCountry.MEXICO_PATH).document(PathFBState.MEX_VERACRUZ_PATH)
                .collection(PathFBCity.MEX_VER_XALAPA_PATH).document(PathFBMarketPlace.MEX_VER_XAL_NOPLACE_PATH).collection(PathFBExtras.PRODUCT_REVIEWS_PATH)
            var query = collectionReference.whereEqualTo(Review.UUID_PRODUCT_KEY, product.uuIdProduct)
            query.get()
                .addOnSuccessListener {
                    continuation.resume(it)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun registerSellerReview(seller : Seller, review: Review): Boolean {
        return suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PathFBCountry.MEXICO_PATH).document(seller.state!!)
                .collection(seller.city!!).document(PathFBMarketPlace.MEX_VER_XAL_NOPLACE_PATH).collection(PathFBExtras.SELLER_REVIEWS_PATH).document(review.uuIdReview!!).set(review)
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun registerProductReview(buyer : Buyer, review: Review): Boolean {
        return suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PathFBCountry.MEXICO_PATH).document(buyer.state!!)
                .collection(buyer.city!!).document(PathFBMarketPlace.MEX_VER_XAL_NOPLACE_PATH).collection(PathFBExtras.PRODUCT_REVIEWS_PATH).document(review.uuIdReview!!).set(review)
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun modifyBuyer(buyer : Buyer): Boolean {
        return suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PathFBCountry.MEXICO_PATH).document(buyer.state!!)
                .collection(buyer.city!!).document(PathFBMarketPlace.MEX_VER_XAL_NOPLACE_PATH).collection(PathFBExtras.BUYERS_PATH).document(buyer.uuId!!).update(buyer.toDictionary())
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun modifySeller(seller : Seller): Boolean {
        return suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PathFBCountry.MEXICO_PATH).document(seller.state!!)
                .collection(seller.city!!).document(seller.market!!).collection(PathFBExtras.SELLERS_PATH).document(seller.uuId!!).update(seller.toDictionary())
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun modifyProduct(seller : Seller, product : Product): Boolean {
        return suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PathFBCountry.MEXICO_PATH).document(seller.state!!)
                .collection(seller.city!!).document(seller.market!!).collection(PathFBExtras.PRODUCT_LIST_PATH).document(product.uuIdProduct!!).update(product.toDictionary())
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun deleteProduct(seller : Seller, product : Product): Boolean {
        return suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PathFBCountry.MEXICO_PATH).document(seller.state!!)
                .collection(seller.city!!).document(seller.market!!).collection(PathFBExtras.PRODUCT_LIST_PATH).document(product.uuIdProduct!!).delete()
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun registerProduct(seller : Seller, product : Product): Boolean {
        return suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PathFBCountry.MEXICO_PATH).document(seller.state!!)
                .collection(seller.city!!).document(seller.market!!).collection(PathFBExtras.PRODUCT_LIST_PATH).document(product.uuIdProduct!!).set(product)
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun deleteSeller(seller : Seller): Boolean {
        return suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PathFBCountry.MEXICO_PATH).document(seller.state!!)
                .collection(seller.city!!).document(seller.market!!).collection(PathFBExtras.SELLERS_PATH).document(seller.uuId!!).delete()
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun deleteBuyer(buyer : Buyer): Boolean {
        return suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PathFBCountry.MEXICO_PATH).document(buyer.state!!)
                .collection(buyer.city!!).document(PathFBMarketPlace.MEX_VER_XAL_NOPLACE_PATH).collection(PathFBExtras.BUYERS_PATH).document(buyer.uuId!!).delete()
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun registerSeller(seller : Seller): Boolean {
        return suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PathFBCountry.MEXICO_PATH).document(seller.state!!)
                .collection(seller.city!!).document(seller.market!!).collection(PathFBExtras.SELLERS_PATH).document(seller.uuId!!).set(seller)
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun registerBuyer(buyer : Buyer): Boolean {
        return suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PathFBCountry.MEXICO_PATH).document(buyer.state!!)
                .collection(buyer.city!!).document(PathFBMarketPlace.MEX_VER_XAL_NOPLACE_PATH).collection(PathFBExtras.BUYERS_PATH).document(buyer.uuId!!).set(buyer)
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun signUpWithFirebaseAuth(email : String, password : String): AuthResult {
        return suspendCoroutine { continuation ->
            val authResult = FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            authResult
                .addOnSuccessListener {
                    continuation.resume(it)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun searchBuyerByUuid(uuid : String) : DocumentSnapshot?{
        return suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PathFBCountry.MEXICO_PATH).document(PathFBState.MEX_VERACRUZ_PATH)
                .collection(PathFBCity.MEX_VER_XALAPA_PATH).document(PathFBMarketPlace.MEX_VER_XAL_NOPLACE_PATH)
                .collection(PathFBExtras.BUYERS_PATH).document(uuid).get()
                .addOnSuccessListener {
                    continuation.resume(it)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun searchSellerByUuid(uuid : String, marketPath : String) : DocumentSnapshot?{
        return suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PathFBCountry.MEXICO_PATH).document(PathFBState.MEX_VERACRUZ_PATH)
                .collection(PathFBCity.MEX_VER_XALAPA_PATH).document(marketPath)
                .collection(PathFBExtras.SELLERS_PATH).document(uuid).get()
                .addOnSuccessListener {
                    continuation.resume(it)
                }.addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun signInWithFirebaseAuth(email: String, password : String): AuthResult {
        return suspendCoroutine { continuation ->
            val authResult = FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            authResult
                .addOnSuccessListener {
                    continuation.resume(it)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun recoverInventorySeller(seller : Seller) : QuerySnapshot? {
        return suspendCoroutine { continuation ->
            val collectionReference = Firebase.firestore.collection(PathFBCountry.MEXICO_PATH).document(PathFBState.MEX_VERACRUZ_PATH)
                .collection(PathFBCity.MEX_VER_XALAPA_PATH).document(seller.market!!).collection(PathFBExtras.PRODUCT_LIST_PATH)
            var query = collectionReference.whereEqualTo(Product.UUID_SELLER_KEY, seller.uuId)
            query.get()
                .addOnSuccessListener {
                    continuation.resume(it)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun searchProducts(statePath : String, cityPath : String, marketPath : String, category : String) : QuerySnapshot? {
        return suspendCoroutine { continuation ->
            val collectionReference = Firebase.firestore.collection(PathFBCountry.MEXICO_PATH).document(statePath)
                .collection(cityPath).document(marketPath).collection(PathFBExtras.PRODUCT_LIST_PATH)
            var query = collectionReference.whereEqualTo(Product.CATEGORY_KEY, category)
            query.get()
                .addOnSuccessListener {
                    continuation.resume(it)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun searchProductByUuid(statePath : String, cityPath : String, marketPath: String, uuidProduct : String) : QuerySnapshot?{
        return suspendCoroutine { continuation ->
            val collectionReference = Firebase.firestore.collection(PathFBCountry.MEXICO_PATH).document(statePath)
                .collection(cityPath).document(marketPath).collection(PathFBExtras.PRODUCT_LIST_PATH)
            var query = collectionReference.whereEqualTo(Product.UUID_PRODUCT_KEY, uuidProduct)
            query.get()
                .addOnSuccessListener {
                    continuation.resume(it)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun recoverProductFromOrder(order: Order, marketPath : String) : DocumentSnapshot?{
        return suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PathFBCountry.MEXICO_PATH).document(order.orderState!!)
                .collection(order.orderCity!!).document(marketPath)
                .collection(PathFBExtras.PRODUCT_LIST_PATH).document(order.uuIdProduct!!).get()
                .addOnSuccessListener {
                    continuation.resume(it)
                }.addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun recoverSellerFromOrder(order: Order, marketPath : String) : DocumentSnapshot?{
        return suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(PathFBCountry.MEXICO_PATH).document(order.orderState!!)
                .collection(order.orderCity!!).document(marketPath)
                .collection(PathFBExtras.SELLERS_PATH).document(order.uuIdSeller!!).get()
                .addOnSuccessListener {
                    continuation.resume(it)
                }.addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }


/*



    suspend fun selectOneMarketUser(uuid : String) : DocumentSnapshot{
        return suspendCoroutine { continuation ->
            var documentFound  = FirebaseFirestore.getInstance().collection(CLOUD_FIRE_STORE_PATH).document(uuid).get()
            documentFound
                .addOnSuccessListener {
                    continuation.resume(it)
                }
                .addOnFailureListener {
                    documentFound = FirebaseFirestore.getInstance().collection(CLOUD_FIRE_STORE_PATH).document(uuid).get()
                        .addOnSuccessListener {
                            continuation.resume(it)
                        }
                        .addOnFailureListener{
                            continuation.resumeWithException(it)
                        }
                }
        }
    }

    suspend fun delete(collectionPath : String,  documentPath : String) : Void{
        return suspendCoroutine { continuation ->
            val currentFirebaseUser = FirebaseAuth.getInstance().currentUser
            currentFirebaseUser?.delete()?.addOnSuccessListener {
                Firebase.firestore.collection(collectionPath).document(documentPath).delete()
                    .addOnSuccessListener {
                        continuation.resume(it)
                    }
                    .addOnFailureListener {
                        continuation.resumeWithException(it)
                    }
            }?.addOnFailureListener {
                continuation.resumeWithException(it)
            }
        }
    }

    suspend fun update(collectionPath : String,  documentPath : String, map: MutableMap<String, Any>) : Void{
        return suspendCoroutine { continuation ->
            val currentFirebaseUser = FirebaseAuth.getInstance().currentUser
            if(currentFirebaseUser != null){
                val userEmail = map[EMAIL_KEY] as String
                val userPassword = map[PASSWORD_KEY] as String
                Log.d("WRAPPER", "Email: $userEmail Password: $userPassword")
                val credential : AuthCredential = EmailAuthProvider.getCredential(userEmail, userPassword)
                currentFirebaseUser.verifyBeforeUpdateEmail(userEmail)
                    .addOnSuccessListener {
                        currentFirebaseUser.reauthenticate(credential)
                            .addOnSuccessListener{
                                Firebase.firestore.collection(collectionPath).document(documentPath).update(map)
                                    .addOnSuccessListener {
                                        continuation.resume(it)
                                    }
                                    .addOnFailureListener {
                                        continuation.resumeWithException(it)
                                    }
                            }
                            .addOnFailureListener {
                                continuation.resumeWithException(it)
                            }
                    }
                    .addOnFailureListener {
                        continuation.resumeWithException(it)
                    }
            }
        }
    }

    suspend fun registerMarketUser(marketUser : MarketUser): Boolean {
        return suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance().collection(CLOUD_FIRE_STORE_PATH).document(marketUser.uuId!!).set(marketUser)
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }


 */
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*
    suspend fun replace(collectionPath : String , documentPath : String , map: MutableMap<String , Any>) : Void {
        return suspendCoroutine { continuation ->
            Firebase.firestore.collection(collectionPath).document(documentPath).set(map)
                .addOnSuccessListener {
                    continuation.resume(it)
                }
                .addOnFailureListener{
                    continuation.resumeWithException(it)
                }
        }
    }

    suspend fun select(collectionPath : String, contentionMap : MutableMap<String , Any>? = null, limit : Long = 1) : QuerySnapshot {

        return suspendCoroutine { continuation ->

            val collectionReference = Firebase.firestore.collection(collectionPath)
            //Select One Default
            var query = collectionReference.limit(limit)
            contentionMap?.let {
                it.forEach{map ->
                    query = collectionReference.whereEqualTo(map.key, map.value)
                }
            }

            query.get()
                .addOnSuccessListener {
                    continuation.resume(it)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }

    }

    suspend fun signUpWithFirebaseAuth(marketUser : MarketUser): AuthResult {
        return suspendCoroutine { continuation ->
            val authResult = FirebaseAuth.getInstance().createUserWithEmailAndPassword(marketUser.email!!, marketUser.password!!)
            authResult.result.user?.uid?.let { uid ->
                marketUser.uuId = uid
                FirebaseFirestore.getInstance().collection(CLOUD_FIRESTORE_PATH).document(uid).set(marketUser)
            }?.addOnSuccessListener {
                continuation.resume(authResult.result)
            }?.addOnFailureListener {
                continuation.resumeWithException(it)
            }
        }
    }

    suspend fun selectOneMarketUser(uuid : String) : DocumentSnapshot{
        return suspendCoroutine { continuation ->
            var documentFound  = FirebaseFirestore.getInstance().collection(CLOUD_FIRESTORE_PATH).document(uuid).get()
            documentFound
                .addOnSuccessListener {
                    if(documentFound.result.exists()){
                        continuation.resume(it)
                    }else{
                        documentFound = FirebaseFirestore.getInstance().collection(CLOUD_FIRESTORE_PATH).document(uuid).get()
                            .addOnSuccessListener {
                                continuation.resume(it)
                            }
                            .addOnFailureListener{
                                continuation.resumeWithException(it)
                            }
                    }
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }
*/
}