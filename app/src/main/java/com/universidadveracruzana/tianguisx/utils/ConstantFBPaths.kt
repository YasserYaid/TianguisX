package com.universidadveracruzana.tianguisx.utils
////ESTAS DEBEN SER LAS MISMAS QUE ESTAN EN SPANISH_STRINGS PORUE SON TAMBIEN DE INTERFAZ DE USUSARIO Y PATHS DE FIREBASE SI NO SON LAS MISMAS NO JALA!!!!
class PathFBCountry {
    companion object {
        const val MEXICO_PATH = "Mexico"
        const val PERU_PATH = "Peru"
    }
}
class PathFBState {
    companion object {
        const val MEX_VERACRUZ_PATH = "Veracruz"
        const val MEX_JALISCO_PATH = "Jalisco"
        const val MEX_MICHOACAN_PATH = "Michoacan"
    }
}
class PathFBCity {
    companion object {
        const val MEX_VER_XALAPA_PATH = "Xalapa"
        const val MEX_VER_COATEPEC_PATH = "Coatepec"
        const val MEX_VER_VERACRUZ_PATH = "Veracruz"
        const val MEX_VER_BOCADELRIO_PATH = "Boca del rio"
    }
}
class PathFBMarketPlace{
    companion object{
        const val MEX_VER_XAL_PRIMERO_MAYO_PATH = "Primero de mayo"
        const val MEX_VER_XAL_NOPLACE_PATH  = "NO_PLACE"
        const val MEX_VER_XAL_DE_PULGAS_PATH = "De pulgas"
        const val MEX_VER_BOC_AUTOS_PATH = "Autos"
    }
}
class PathFBExtras{
    companion object{
        const val SELLERS_PATH = "Vendedor"
        const val BUYERS_PATH  = "Comprador"
        const val SELLER_REVIEWS_PATH = "Evaluaciones vendedor"
        const val PRODUCT_REVIEWS_PATH = "Evaluaciones producto"
        const val PRODUCT_LIST_PATH = "Productos"
        const val ORDER_PRODUCTS_PATH = "Pedidos"
    }
}

//////////////////
class PathFBStorage{
    companion object{
        const val SELLER_PROFILE_IMAGE_FOLDER_PATH = "SELLERS_PROFILES_IMAGES/"
        const val SELLER_QRS_IMAGES_FOLDER_PATH = "SELLERS_QR_CODES_IMAGES/"
        const val BUYER_PROFILES_IMAGE_FOLDER_PATH = "BUYERS_PROFILES_IMAGES/"
        const val PRODUCTS_IMAGES_FOLDER_PATH = "PRODUCTS_DESCRIPTIVE_IMAGES/"
        const val PRODUCTS_QR_IMAGES_FOLDER_PATH = "PRODUCTS_QR_CODES_IMAGE/"
    }
}