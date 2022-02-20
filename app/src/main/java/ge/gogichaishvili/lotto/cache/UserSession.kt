package ge.gov.sa.sagency.cache

object UserSession {

    private var isLoggedIn = false
    private var accessToken: String? = null
    private var session: String? = null

    fun getSession() = session

    fun saveSession(session: String?) {
        this.session = session
    }

    fun isLoggedIn() = isLoggedIn

    fun setIsLoggedIn(isLoggedIn: Boolean) {
        this.isLoggedIn = isLoggedIn
    }

    fun getAccessToken() = accessToken

    fun setAccessToken(accessToken: String) {
        this.accessToken = accessToken
    }

    fun logOut() {
        isLoggedIn = false
        accessToken = null
    }
}