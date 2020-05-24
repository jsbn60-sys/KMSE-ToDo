import io.javalin.http.Context
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import javalinvue.* // ktlint-disable no-wildcard-imports
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.Test

class UserLoginTest {
    private val ctx = mockk<Context>(relaxed = true)

    @Before
    fun setupTests() {
        Database.connect("jdbc:h2:./db", driver = "org.h2.Driver")
        transaction {
            SchemaUtils.create(Users, Tasks, Categories)
        }
    }

    @Test
    fun loginUserSuccess() {
        /* create user */
        every { ctx.formParam("username") } returns "omar"
        every { ctx.formParam("password") } returns "1234"
        every { ctx.formParam("email") } returns "omar@mni.thm.de"
        AuthController.register(ctx)

        /* login user */
        every { ctx.formParam("username") } returns "omar"
        every { ctx.formParam("password") } returns "1234"
        every { ctx.formParam("expires-in") } returns "1000"
        val token = slot<String>()
        every {
            ctx.json(
                capture(token)
            )
        } answers {
            this.callOriginal()
        }
        AuthController.login(ctx)
    }

    @Test
    fun loginUserWithoutUsername() {
        every { ctx.formParam("username") } returns "omar"
        every { ctx.formParam("password") } returns "1234"
        every { ctx.formParam("email") } returns "omar@mni.thm.de"
        AuthController.register(ctx)

        every { ctx.formParam("username") } returns null
        every { ctx.formParam("password") } returns "1234"
        every { ctx.formParam("expires-in") } returns "1000"
        val token = slot<String>()
        every {
            ctx.json(
                capture(token)
            )
        } answers {
            this.callOriginal()
        }
        AuthController.login(ctx)

        verify { (ctx.status(400)) }
    }

    @Test
    fun loginUserWithoutPassword() {
        every { ctx.formParam("username") } returns "omar"
        every { ctx.formParam("password") } returns "1234"
        every { ctx.formParam("email") } returns "omar@mni.thm.de"
        AuthController.register(ctx)

        every { ctx.formParam("username") } returns "omar"
        every { ctx.formParam("password") } returns null
        every { ctx.formParam("expires-in") } returns "1000"
        val token = slot<String>()
        every {
            ctx.json(
                capture(token)
            )
        } answers {
            this.callOriginal()
        }
        AuthController.login(ctx)

        verify { (ctx.status(400)) }
    }
}
