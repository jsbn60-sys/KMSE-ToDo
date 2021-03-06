package javalinvue

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import io.javalin.http.Context
import java.util.Date
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

object AuthController {
    private val jwtSecret = System.getenv("SECRET") ?: {
        println("WARNING: Using default jwt secret. Use the SECRET environment variable to set custom secret in production.")
        "secret"
    }()
    private val jwtAlgo = Algorithm.HMAC256(jwtSecret)
    private val jwtVerifyer = JWT.require(jwtAlgo).build()

    fun verify(ctx: Context): User? {
        val token = ctx.queryParam("token") ?: return null
        return try {
            val decoded = jwtVerifyer.verify(token)
            transaction {
                User.findById(decoded.subject.toInt())
            }
        } catch (exception: JWTVerificationException) {
            null
        }
    }

    fun login(ctx: Context) {
        val username = ctx.formParam("username")
        val password = ctx.formParam("password")
        val expiresIn = ctx.formParam("expires-in")
        if (username == null || password == null || (expiresIn != null && expiresIn.toIntOrNull() == null)) {
            ctx.status(400)
            ctx.json("Bad Request")
            return
        }
        if (username.equals("test")) {
            ctx.status(200)
            ctx.json("Test login")
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
            val token = if (expiresIn == null) {
                createToken(user.id.value)
            } else {
                val expires = Date(Date().time + expiresIn.toInt() * 1000)
                createToken(user.id.value, expires)
            }
            ctx.json(token)
        }
    }

    fun register(ctx: Context) {
        val username = ctx.formParam("username", null)
        val password = ctx.formParam("password", null)
        val email = ctx.formParam("email", null)
        if (username == null || password == null || email == null) {
            ctx.status(400)
            ctx.json("Bad Request")
            return
        }
        transaction {
            val existingUser = User.find { Users.username eq username }.firstOrNull()
            if (existingUser != null) {
                ctx.status(409)
                ctx.json("Conflict")
                return@transaction
            }
            val hashed = BCrypt.hashpw(password, BCrypt.gensalt())
            val user = User.new {
                this.username = username
                this.email = email
                this.password = hashed
            }
            addDefaults(user)
            val token = createToken(user.id.value)
            ctx.json(token)
            ctx.status(200)
        }
    }

    private val dateInOneDay = Date(Date().time + 24 * 60 * 60 * 1000)
    private fun createToken(userID: Int, expiresAt: Date = dateInOneDay): String {
        return JWT.create()
                .withSubject(userID.toString())
                .withExpiresAt(expiresAt)
                .sign(jwtAlgo)
    }

    private fun addDefaults(user: User) {
        val defaultCategory = Category.new {
            this.name = "Default"
            this.owner = user
        }
        Task.new {
            this.title = "Add more tasks"
            this.category = defaultCategory
            this.priority = "medium"
            this.planed = Date().time
            this.done = false
            this.owner = user
        }
    }
}
