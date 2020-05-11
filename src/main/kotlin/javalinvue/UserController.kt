package javalinvue

import io.javalin.http.Context
import org.jetbrains.exposed.sql.transactions.transaction
import java.text.SimpleDateFormat
import java.util.*


object UserController {
    fun getUser(ctx: Context) {
        val user = ctx.attribute<User>("user")!!
        ctx.json(mapOf(
            "id" to user.id.toString(),
            "username" to user.username,
            "email" to user.email
        ))
    }
    fun myCategories(ctx: Context) {
        val user = ctx.attribute<User>("user")!!
        val categories = transaction {
            user.categories.map { category ->
                mapOf(
                    "id" to category.id.toString(),
                    "title" to category.title
                )
            }
        }
        ctx.json(categories)
    }
    private val dateFormat = SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH)
    fun myTasks(ctx: Context) {
        val user = ctx.attribute<User>("user")!!
        val tasks = transaction {
            user.tasks.map { task ->
                mapOf(
                    "id" to task.id.toString(),
                    "title" to task.title,
                    "done" to task.done,
                    "priority" to task.priority,
                    "planed" to dateFormat.format(Date(task.planed))
                )
            }
        }
        ctx.json(tasks)
    }
    fun addToMyTasks(ctx: Context) {
        val user = ctx.attribute<User>("user")!!
        val title = ctx.formParam("title")
        val priority = ctx.formParam("priority")
        val planed = ctx.formParam("planed")
        val category = ctx.formParam("category")
        if (title == null || priority == null || !arrayOf("low", "medium", "high").contains(priority) || planed == null || category == null) {
            ctx.status(400)
            ctx.json("Bad Request")
            return
        }
        transaction {
            Task.new {
                this.title = title
                this.priority = priority
                this.planed = dateFormat.parse(planed).time
                //this.category = category
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
