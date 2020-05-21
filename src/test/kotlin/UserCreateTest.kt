import io.javalin.http.Context
import io.mockk.*
import javalinvue.AuthController
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

    /*
    @Test
    fun createUserSuccess(){
        every { ctx1.queryParam("username") } returns "Mustermann"
        every { ctx1.queryParam("password") } returns "1234"
        every { ctx1.queryParam("email") } returns "max.mustermann@mni.thm.de"
        AuthController.register(ctx1)
        verify{ctx1.status(200)}
        /* find way to remove user again */
    }
     */

    @Test
    fun createUserWithExistingUsername(){
        every { ctx1.formParam("username") } returns "Mustermann"
        every { ctx1.formParam("password") } returns "1234"
        every { ctx1.formParam("email") } returns "max.mustermann@mni.thm.de"
        val token = slot<String>()
        every {
            ctx1.json(
                    capture(token)
            )
        } answers {
            println("TOKEN: " + token.captured)
            this.callOriginal()
        }
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