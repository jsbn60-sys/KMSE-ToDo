package javalinvue

import io.javalin.Javalin
import io.javalin.core.security.Role
import io.javalin.core.security.SecurityUtil.roles
import io.javalin.core.util.Header
import io.javalin.http.Context
import io.javalin.plugin.rendering.vue.JavalinVue
import io.javalin.plugin.rendering.vue.VueComponent

enum class AppRole : Role { ANYONE, LOGGED_IN }

fun main() {

    val app = Javalin.create { config ->
        config.enableWebjars()
        config.accessManager { handler, ctx, permittedRoles ->
            when {
                AppRole.ANYONE in permittedRoles -> handler.handle(ctx)
                AppRole.LOGGED_IN in permittedRoles && AuthController.verify(ctx) != null -> handler.handle(ctx)
                else -> {
                    ctx.status(401)
                    ctx.json("Unauthorized")
                }
            }
        }
        JavalinVue.stateFunction = { ctx -> mapOf("currentUser" to currentUser(ctx)) }
    }.start(7000)

    app.get("/", VueComponent("<hello-world></hello-world>"), roles(AppRole.ANYONE))
    app.get("/users", VueComponent("<user-overview></user-overview>"), roles(AppRole.ANYONE))
    app.get("/users/:user-id", VueComponent("<user-profile></user-profile>"), roles(AppRole.LOGGED_IN))
    app.error(404, "html", VueComponent("<not-found></not-found>"))

    app.get("/api/users", UserController::getAll, roles(AppRole.ANYONE))
    app.get("/api/users/:user-id", UserController::getOne, roles(AppRole.LOGGED_IN))

    app.post("/api/auth/register", AuthController::register, roles(AppRole.ANYONE))
    app.post("/api/auth/login", AuthController::login, roles(AppRole.ANYONE))
    app.get("/api/auth/verify", {ctx ->
        ctx.json(AuthController.verify(ctx)!!)
    }, roles(AppRole.LOGGED_IN))

}

private fun currentUser(ctx: Context) =
    if (ctx.basicAuthCredentialsExist()) ctx.basicAuthCredentials().username else null
