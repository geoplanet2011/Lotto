package ge.gogichaishvili.lotto.app.network

object ApiEndpoints {

    //Base URL
    const val baseUrl = "https://dev-api-identity.sa.gov.ge/"

    const val accounts = "Accounts"
    private const val connect = "connect"
    private const val users = "Users"
    private const val api = "api"

    //Login
    const val authenticate = "$api/$accounts/Authenticate"
    const val token = "$connect/token"
    const val biometric_authenticate = "$api/$accounts/BiometricAuthentication"
    const val validate_biometric = "$api/$accounts/ValidateAuthCode"

}