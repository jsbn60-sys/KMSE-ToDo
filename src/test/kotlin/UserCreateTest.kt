import io.javalin.http.Context
import io.mockk.*
import javalinvue.AuthController
import javalinvue.User
import javalinvue.UserController
import org.jetbrains.exposed.sql.Database
import org.junit.Before
import org.junit.Test

class UserCreateTest {

    private val ctx1 = mockk<Context>(relaxed = true)
    private val ctx2 = mockk<Context>(relaxed = true)

    @Before
    fun setupTests(){
        Database.connect("jdbc:h2:./db", driver = "org.h2.Driver");
    }


    @Test
    fun createUserSuccess() {
        every { ctx1.formParam("username") } returns "Mustermann3"
        every { ctx1.formParam("password") } returns "1234"
        every { ctx1.formParam("email") } returns "max.mustermann@mni.thm.de"
        AuthController.register(ctx1)

        verify { ctx1.status(200)}

        // Log in and delete account again
        every { ctx1.formParam("username") } returns "Mustermann3"
        every { ctx1.formParam("password") } returns "1234"
        every { ctx1.formParam("expires-in") } returns "1000"
        val token = slot<String>()
        every {
            ctx1.json(
                capture(token)
            )
        } answers {
            this.callOriginal()
        }
        AuthController.login(ctx1)

        every { ctx1.queryParam("token")} returns token.captured
        val user = AuthController.verify(ctx1)

        every { ctx1.attribute<User>("user")} returns user

        UserController.deleteAccount(ctx1)
    }


    @Test
    fun createUserWithExistingUsername(){
        every { ctx1.formParam("username") } returns "Mustermann"
        every { ctx1.formParam("password") } returns "1234"
        every { ctx1.formParam("email") } returns "max.mustermann@mni.thm.de"
        AuthController.register(ctx1)
        every { ctx2.formParam("username") } returns "Mustermann"
        every { ctx2.formParam("password") } returns "5678"
        every { ctx2.formParam("email") } returns "peter.mustermann@mni.thm.de"
        AuthController.register(ctx2)
        verify{ctx2.status(409)}
    }

    @Test
    fun failToCreateUser(){
        every { ctx1.formParam("username") } returns null
        every { ctx1.formParam("password") } returns "1234"
        every { ctx1.formParam("email") } returns "max.mustermann@mni.thm.de"
        AuthController.register(ctx1);
        verify{ctx1.status(400)}
    }
}