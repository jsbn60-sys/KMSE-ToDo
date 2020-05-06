package javalinvue

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import io.javalin.http.Context
import org.mindrot.jbcrypt.BCrypt
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object AuthController {
    //Quick text file based Database (do NOT use in production)
    private class UserDB(file: String) {
        private val inMemory = ConcurrentHashMap<String, String>()
        private val filePath = Paths.get(file)

        init {
            val input = File(file)
            if (input.exists()) {
                input.readText().split("\n").forEach { line ->
                    val split = line.split(":")
                    if (split.size == 2) {
                        val user = split[0]
                        val hash = split[1]
                        inMemory[user] = hash
                    }
                }
            }
        }

        operator fun set(username: String, hash: String) {
            Files.writeString(filePath, "${username}:${hash}\n", StandardOpenOption.APPEND, StandardOpenOption.CREATE)
            inMemory[username] = hash
        }

        operator fun get(username: String): String? {
            return inMemory[username]
        }
    }

    private val userDB = UserDB("users.txt")
    private val jwtAlgo = Algorithm.HMAC256("secret")
    private val jwtVerifyer = JWT.require(jwtAlgo).build()

    fun verify(ctx: Context): String? {
        val token = ctx.queryParam("token") ?: return null
        return try {
            val decoded = jwtVerifyer.verify(token)
            decoded.subject
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
        val hash = userDB[username]
        if (hash == null) {
            ctx.status(404)
            ctx.json("Not Found")
            return
        }
        val ok = BCrypt.checkpw(password, hash)
        if (!ok) {
            ctx.status(401)
            ctx.json("Unauthorized")
            return
        }
        val dateInOneDay = Date(Date().time + 24*60*60*1000)
        val token = JWT.create()
            .withSubject(username)
            .withExpiresAt(dateInOneDay)
            .sign(jwtAlgo)
        ctx.json(token)
    }

    fun register(ctx: Context) {
        val username = ctx.formParam("username", null)
        val password = ctx.formParam("password", null)
        if (username == null || password == null) {
            ctx.status(400)
            ctx.json("Bad Request")
            return
        }
        if (userDB[username] != null) {
            ctx.status(409)
            ctx.json("Conflict")
            return
        }
        val hashed = BCrypt.hashpw(password, BCrypt.gensalt())
        userDB[username] = hashed
        ctx.status(200)
        ctx.json("Ok")
    }
}
