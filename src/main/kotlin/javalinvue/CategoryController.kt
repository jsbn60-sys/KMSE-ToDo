package javalinvue

import io.javalin.http.Context
import io.javalin.http.NotFoundResponse

data class Category(val id: String, val name: String, val user: User)

val categories = setOf<Category>(
    Category(id = "1", name = "Einkaufen", user = USEROBJECT)
)

object CategoryController {

    fun getAll(ctx: Context) {
        ctx.json(users.map { it.copy(userDetails = null) }) // remove sensitive information
    }

    fun getOne(ctx: Context) {
        val user = users.find { it.id == ctx.pathParam("user-id") } ?: throw NotFoundResponse()
        ctx.json(user)
    }

}
