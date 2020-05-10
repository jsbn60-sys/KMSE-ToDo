package javalinvue

import io.javalin.http.Context
import io.javalin.http.NotFoundResponse

data class Task(val id: Int, val title: String, val user: User, val category: Category?, val checked: Boolean, val priority: Int)

val tasks = setOf<Task>(
    Task(id = 1, title = "Task", user = USEROBJECT, category = CATEGORYOBJECT, checked = false, priority = 10)
    )

object taskController {

    fun getAll(ctx: Context) {
        ctx.json(users.map { it.copy(userDetails = null) }) // remove sensitive information
    }

    fun getOne(ctx: Context) {
        val user = users.find { it.id == ctx.pathParam("user-id") } ?: throw NotFoundResponse()
        ctx.json(user)
    }

}
