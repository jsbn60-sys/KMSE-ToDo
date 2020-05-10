package javalinvue

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import io.javalin.http.Context
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object AuthController {
    private val jwtAlgo = Algorithm.HMAC256("secret")
    private val jwtVerifyer = JWT.require(jwtAlgo).build()

    fun verify(ctx: Context): User? {
        val token = ctx.queryParam("token") ?: return null
        return try {
            val decoded = jwtVerifyer.verify(token)
            transaction {
                User.findById(decoded.subject.toInt())
            }
        } catch (exception: JWTVerificationException){
            null
        }
    }

    fun login(ctx: Context) {
        val username = ctx.formParam("username")
        val password = ctx.formParam("password")
        if (username == null || password == null) {
            ctx.status(400)
            ctx.json("Bad Request")
            return
        }
        transaction {
            val user = User.find { Users.username eq username }.firstOrNull()
            if (user == null) {
                ctx.status(404)
                ctx.json("Not Found")
                return@transaction
            }
            val ok = BCrypt.checkpw(password, user.password)
            if (!ok) {
                ctx.status(401)
                ctx.json("Unauthorized")
                return@transaction
            }
            val dateInOneDay = Date(Date().time + 24 * 60 * 60 * 1000)
            val token = JWT.create()
                    .withSubject(user.id.toString())
                    .withExpiresAt(dateInOneDay)
                    .sign(jwtAlgo)
            ctx.json(token)
        }
    }

    fun register(ctx: Context) {
        val username = ctx.formParam("username", null)
        val password = ctx.formParam("password", null)
        val email    = ctx.formParam("email", null)
        if (username == null || password == null || email == null) {
            ctx.status(400)
            ctx.json("Bad Request")
            return
        }
        transaction {
            val user = User.find { Users.username eq username }.firstOrNull()
            if (user != null) {
                ctx.status(409)
                ctx.json("Conflict")
                return@transaction
            }
            val hashed = BCrypt.hashpw(password, BCrypt.gensalt())
            User.new {
                this.username = username
                this.email = email
                this.password = hashed
            }
            ctx.status(200)
            ctx.json("Ok")
        }
    }
}
