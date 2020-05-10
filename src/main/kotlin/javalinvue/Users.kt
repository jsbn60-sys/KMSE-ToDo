package javalinvue

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Users: IntIdTable() {
    val username = varchar("username", 64)
    val email = varchar("email", 128)
    val password = char("password", 60)
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var username by Users.username
    var email    by Users.email
    var password by Users.password

    val tasks by Task referrersOn Tasks.owner
}
