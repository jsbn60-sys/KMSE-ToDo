import io.javalin.http.Context
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import javalinvue.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.Test


class TaskTest {

    @Before
    fun setupTests(){
        Database.connect("jdbc:h2:./db", driver = "org.h2.Driver");
    }
    private val user = mockk<User>(relaxed = true)
    private val ctx = mockk<Context>(relaxed = true)

    /*
    // POST to create tasks gives 201 for success request
    @Test
    fun createTask(){

        every { ctx.formParam("username") } returns "test"
        every { ctx.formParam("password") } returns "1234"
        every { ctx.formParam("email") } returns "max.mustermann@mni.thm.de"
        every { ctx.formParam("expires-in") } returns "15-05-2021"
        AuthController.register(ctx)
        AuthController.login(ctx)

        // user muss vom Typ User sein. sonst wird eine ClassCastException geworfen
        // user ist so nicht definiert und wird Null zurückgegeben. Liefert 400.
        //every { ctx.attribute<User>("user")!! } returns user
        every { ctx.formParam("title") } returns "test"
        every { ctx.formParam("priority") } returns "low"
        every { ctx.formParam("planed") } returns "15-05-2020"
        every { ctx.formParam("category") } returns "1"
        UserController.addToMyTasks(ctx) // the handler we're testing
        verify { ctx.status(201) }
    }
    */

    // POST to create tasks without title throws for invalid task parameter
    @Test
    fun createTaskWithoutValidTitle(){
        every { ctx.attribute<User>("user")!! } returns user
        every { ctx.formParam("title") } returns null // Title is required
        every { ctx.formParam("priority") } returns "low"
        every { ctx.formParam("planed") } returns "15.05.2020"
        every { ctx.formParam("category") } returns "BS"
        UserController.addToMyTasks(ctx) // returns 400 StatusCode
        verify{ctx.status(400)}
    }

    // POST to create tasks with invalid priority throws for invalid task parameter
    @Test
    fun createTaskWithoutValidPriority(){
        every { ctx.attribute<User>("user")!! } returns user
        every { ctx.formParam("title") } returns "test"
        every { ctx.formParam("priority") } returns "Quatsch" // Priority must be "low", "medium" or "high"
        every { ctx.formParam("planed") } returns "15.05.2020"
        every { ctx.formParam("category") } returns "BS"
        UserController.addToMyTasks(ctx) // returns 400 StatusCode
        verify{ctx.status(400)}

    }




}