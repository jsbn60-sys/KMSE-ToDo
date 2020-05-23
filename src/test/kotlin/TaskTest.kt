import io.javalin.http.Context
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import javalinvue.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.Test


class TaskTest {

    @Before
    fun setupTests(){
        Database.connect("jdbc:h2:./db", driver = "org.h2.Driver");
        transaction {
            SchemaUtils.create(Users, Tasks, Categories)
        }
    }
    private val ctx = mockk<Context>(relaxed = true)

    @Test
    fun createTask(){

        val user = registerAndLogin()

        every { ctx.attribute<User>("user") } returns user
        every { ctx.formParam("title") } returns "test"
        every { ctx.formParam("priority") } returns "low"
        every { ctx.formParam("planed") } returns "15-05-2020"
        every { ctx.formParam("category") } returns "1"
        UserController.addToMyTasks(ctx) // the handler we're testing
        verify { ctx.status(200) }

        deleteAcc(user)
    }


    // POST to create tasks without title throws for invalid task parameter
    @Test
    fun createTaskWithoutValidTitle(){
        val user = registerAndLogin()

        every { ctx.attribute<User>("user") } returns user
        every { ctx.formParam("title") } returns null // Title is required
        every { ctx.formParam("priority") } returns "low"
        every { ctx.formParam("planed") } returns "15.05.2020"
        every { ctx.formParam("category") } returns "BS"
        UserController.addToMyTasks(ctx) // returns 400 StatusCode
        verify{ctx.status(400)}

        deleteAcc(user)
    }

    // POST to create tasks with invalid priority throws for invalid task parameter
    @Test
    fun createTaskWithoutValidPriority(){
        val user = registerAndLogin()

        every { ctx.attribute<User>("user") } returns user
        every { ctx.formParam("title") } returns "test"
        every { ctx.formParam("priority") } returns "Quatsch" // Priority must be "low", "medium" or "high"
        every { ctx.formParam("planed") } returns "15.05.2020"
        every { ctx.formParam("category") } returns "BS"
        UserController.addToMyTasks(ctx) // returns 400 StatusCode
        verify{ctx.status(400)}

        deleteAcc(user)
    }

    fun registerAndLogin():User?{
        every { ctx.formParam("username") } returns "Mustermann"
        every { ctx.formParam("password") } returns "1234"
        every { ctx.formParam("email") } returns "max.mustermann@mni.thm.de"
        AuthController.register(ctx)

        every { ctx.formParam("username") } returns "Mustermann"
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

        every { ctx.queryParam("token")} returns token.captured
        return AuthController.verify(ctx)
    }

    fun deleteAcc(user: User?){
        every { ctx.attribute<User>("user")} returns user
        UserController.deleteAccount(ctx)
    }




}