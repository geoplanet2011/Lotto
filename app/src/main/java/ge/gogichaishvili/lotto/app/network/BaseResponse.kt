package ge.gogichaishvili.lotto.app.network

data class BaseResponse<T>(
    val status: Int,
    val message: String,
    val data: T?
) {
    val isSuccess: Boolean
        get() = status in 200..299
}