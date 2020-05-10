package javalinvue

import io.javalin.Javalin
import io.javalin.core.security.Role
import io.javalin.core.security.SecurityUtil.roles
import io.javalin.http.Context
import io.javalin.plugin.rendering.vue.VueComponent
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

enum class AppRole : Role { ANYONE, LOGGED_IN }

fun main() {

    Database.connect("jdbc:h2:./db", driver = "org.h2.Driver");

    transaction {
        SchemaUtils.create(Users, Tasks)
    }

    val app = Javalin.create { config ->
        config.enableWebjars()
        config.accessManager { handler, ctx, permittedRoles ->
            val user = AuthController.verify(ctx)
            when {
                AppRole.ANYONE in permittedRoles -> handler.handle(ctx)
                AppRole.LOGGED_IN in permittedRoles && user != null -> {
                    ctx.attribute("user", user)
                    handler.handle(ctx)
                }
                else -> {
                    ctx.status(401)
                    ctx.json("Unauthorized")
                }
            }
        }
    }.start(7000)

    app.get("/", VueComponent("<hello-world></hello-world>"), roles(AppRole.ANYONE))
    app.get("/users", VueComponent("<user-overview></user-overview>"), roles(AppRole.ANYONE))
    app.get("/users/:user-id", VueComponent("<user-profile></user-profile>"), roles(AppRole.LOGGED_IN))
    app.error(404, "html", VueComponent("<not-found></not-found>"))

    app.post("/api/auth/register", AuthController::register, roles(AppRole.ANYONE))
    app.post("/api/auth/login", AuthController::login, roles(AppRole.ANYONE))
    app.get("/api/me", UserController::getUser, roles(AppRole.LOGGED_IN))
    app.get("/api/me/tasks", UserController::myTasks, roles(AppRole.LOGGED_IN))
    app.post("/api/me/tasks", UserController::addToMyTasks, roles(AppRole.LOGGED_IN))
    app.put("/api/me/tasks/:id", UserController::markMyTaskDone, roles(AppRole.LOGGED_IN))
}

private fun currentUser(ctx: Context) =
    if (ctx.basicAuthCredentialsExist()) ctx.basicAuthCredentials().username else null
