package ge.gogichaishvili.lotto.app.network

import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale

class RequestInterceptor() :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = ""//UserSession.getAccessToken()
        val requestBuilder = chain.request().newBuilder()

        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        requestBuilder.addHeader("Accept-Language", Locale.getDefault().language)

        var request = requestBuilder.build()
        if (request.method == "GET") {
            requestBuilder.header("Content-Type", "application/json")
        } else {
            requestBuilder.header("Content-Type", "application/x-www-from-urlencoded")
        }

        request = requestBuilder.build()

        val response = chain.proceed(request)

        if (response.code == 401) {
            throw NotValidTokenException(errorMessage = "Invalid token")
        }

        return response
    }

}