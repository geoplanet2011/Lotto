package ge.gogichaishvili.lotto.app.network

sealed class Resourece<T>(
    val data: T? = null,
    val message: String? = null,
    val errorCode: String? = null
) {
    class Success<T>(data: T) : Resourece<T>(data)
    class Loading<T>(data: T? = null) : Resourece<T>(data)
    class Error<T>(
        message: String,
        data: T? = null,
        errorCode: String? = null,
        val httpCode: Int? = null,
        ex: Throwable? = null
    ) :
        Resourece<T>(data, message, errorCode)

    val isSuccess: Boolean
        get() = this is Success && data != null

}
