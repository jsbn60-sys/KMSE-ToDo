package javalinvue

import io.javalin.http.Context
import org.jetbrains.exposed.sql.transactions.transaction
import java.text.ParseException
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
                    "name" to category.name
                )
            }
        }
        ctx.json(categories)
    }
    fun newCategory(ctx: Context) {
        val user = ctx.attribute<User>("user")!!
        val name = ctx.formParam("name")
        if (name == null) {
            ctx.status(400)
            ctx.json("Bad Request")
            return
        }
        transaction {
            Category.new {
                this.name = name
                this.owner = user
            }
        }
        ctx.status(200)
        ctx.json("Ok")
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
                    "planed" to dateFormat.format(Date(task.planed)),
                    "category" to mapOf(
                            "id" to task.category.id.toString(),
                            "name" to task.category.name
                    )
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
        val planedParsed = try {dateFormat.parse(planed)} catch (_: ParseException) {null}
        val categoryID = ctx.formParam("category")
        if (title == null || priority == null || !arrayOf("low", "medium", "high").contains(priority)
                || planed == null || planedParsed == null || categoryID == null) {
            ctx.status(400)
            ctx.json("Bad Request")
            return
        }
        transaction {
            val category = Category.findById(categoryID.toInt())
            if (category == null || category.owner.id != user.id) {
                ctx.status(401)
                ctx.json("Bad Request")
                return@transaction
            }
            Task.new {
                this.title = title
                this.priority = priority
                this.planed = planedParsed.time
                this.category = category
                this.done = false
                this.owner = user
            }
            ctx.json("Ok")
        }
    }
    fun markMyTaskDone(ctx: Context) {
        val user = ctx.attribute<User>("user")!!
        val id = ctx.pathParam("id").toInt()
        val ok = transaction {
            val task = Task.findById(id)
            if (task == null || task.owner.id != user.id) {
                return@transaction false
            }
            task.done = !task.done
            return@transaction true
        }
        if (!ok) {
            ctx.status(404)
            ctx.json("Not Found")
            return
        }
        ctx.json("Ok")
    }
    fun deleteAccount(ctx: Context) {
        val user = ctx.attribute<User>("user")!!
        transaction {
            user.tasks.forEach(Task::delete)
            user.categories.forEach(Category::delete)
            user.delete()
        }
        ctx.json("Ok")
    }
}
