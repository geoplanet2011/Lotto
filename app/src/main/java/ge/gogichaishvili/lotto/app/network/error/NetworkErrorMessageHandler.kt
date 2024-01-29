package ge.gogichaishvili.lotto.app.network.error

import retrofit2.HttpException

fun HttpException.getErrorMessage(): String {
    return try {
        val errorResponse = response()
        if (errorResponse != null) {
            if (errorResponse.code() == 500) {
                errorResponse.errorBody()?.string() ?: ""
            } else {
                "Something went wrong"
            }
        } else {
            "Something went wrong"
        }
    } catch (e: Exception) {
        e.printStackTrace()
        "Something went wrong"
    }
}