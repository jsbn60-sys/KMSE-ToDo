import io.javalin.http.Context
import io.mockk.mockk
import org.jetbrains.exposed.sql.Database
import org.junit.Before

class UserLoginTest {
    private val ctx = mockk<Context>(relaxed = true)

    @Before
    fun setupTests() {
        Database.connect("jdbc:h2:./db", driver = "org.h2.Driver")
    }

    /*
    @Test
    fun `POST to login User gives 200 for success`(){
        every {ctx.formParam("username")} returns "omar"
        every {ctx.formParam("password")} returns "123456789"
        AuthController.login(ctx)
        verify{ctx.status(200)}
    }


    @Test(expected = BadRequestResponse::class)
    fun loginUserWithoutUsername(){
        every {ctx.formParam("username")} returns null
        every {ctx.formParam("password")} returns "123456789"
        AuthController.login(ctx)
    }

    @Test(expected = BadRequestResponse::class)
    fun loginUserWithoutPassword(){
        every {ctx.formParam("username")} returns "omar"
        every {ctx.formParam("password")} returns null
        AuthController.login(ctx)
    }
     */
}
