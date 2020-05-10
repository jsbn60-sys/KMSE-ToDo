package javalinvue

import io.javalin.http.Context
import org.jetbrains.exposed.sql.transactions.transaction

object UserController {
    fun getUser(ctx: Context) {
        val user = ctx.attribute<User>("user")!!
        ctx.json(mapOf(
                "id" to user.id.toString(),
                "username" to user.username,
                "email" to user.email
        ))
    }
    fun myTasks(ctx: Context) {
        val user = ctx.attribute<User>("user")!!
        val tasks = transaction {
            user.tasks.map { task ->
                mapOf(
                        "id" to task.id.toString(),
                        "title" to task.title,
                        "done" to task.done
                )
            }
        }
        ctx.json(tasks)
    }
    fun addToMyTasks(ctx: Context) {
        val user = ctx.attribute<User>("user")!!
        val title = ctx.formParam("title", null)
        if (title == null) {
            ctx.status(400)
            ctx.json("Bad Request")
            return
        }
        transaction {
            Task.new {
                this.title = title
                this.done = false
                this.owner = user
            }
        }
        ctx.json("Ok")
    }
    fun markMyTaskDone(ctx: Context) {
        val user = ctx.attribute<User>("user")!!
        val id = ctx.pathParam("id").toInt()
        val ok = transaction {
            val task = Task.findById(id)
            if (task == null || task.owner.id != user.id) {
                return@transaction false
            }
            task.done = true
            return@transaction true
        }
        if (!ok) {
            ctx.status(404)
            ctx.json("Not Found")
            return
        }
        ctx.json("Ok")
    }
}
