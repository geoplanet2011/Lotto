package ge.gogichaishvili.lotto.main.data

data class User(
    val id: String?,
    val firstName: String?,
    val lastName: String?,
    val phoneNumber: String?,
    val photo: String?,
    val email: String?,
    val avatar: String?,
    val money: Int
) {
    fun getFullName() = "$firstName $lastName"
}