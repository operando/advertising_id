package com.os.operando.advertisingid

sealed class AndroidIdResult {
    data class Success(val androidId: String) : AndroidIdResult()
    data class Error(val errorCode: String, val errorMessage: String?) : AndroidIdResult()
}

sealed class AppSetIdResult {
    data class Success(val appSetId: String) : AppSetIdResult()
    data class Error(val errorCode: String, val errorMessage: String?) : AppSetIdResult()
}
