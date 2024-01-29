package ge.gogichaishvili.lotto.main.models

data class User(
    val id: String?,
    val firstName: String?,
    val lastName: String?,
    val avatar: String?,
    val email: String?,
    val money: Int
) {
    fun getFullName() = "$firstName $lastName"
}