package todo.app

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import java.time.LocalDate

data class Model(val name: String, val items: List<Item>, val date: LocalDate = LocalDate.of(2018, 4, 13))
data class Item(val key: String, val value: String)

val model = Model("root", listOf(Item("A", "Apache"), Item("B", "Bing")))

fun Application.main() {
    val todos = TodoRepository()

    install(CORS){
        anyHost()
    }
    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
    install(ContentNegotiation) {
        jackson {
            configure(SerializationFeature.INDENT_OUTPUT, true)
            setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
                indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
                indentObjectsWith(DefaultIndenter("  ", "\n"))
            })
            registerModule(JavaTimeModule())  // support java.time.* types
        }
    }


    routing {
        get("/") {
            call.respond(todos.getAll())
        }

        get("/{id}") {
            val id = call.parameters.get("id")
            if (id == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                val message = todos.get(id.toInt())
                if (message == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(message)
                }
            }
        }

        patch("/{id}") {
            val id = call.parameters.get("id")
            if (id == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                val todo = call.receive<Todo>()
                todos.save(todo)
                call.respond(HttpStatusCode.OK)
            }
        }

        post("/") {
            val todo = call.receive<Todo>()
            todos.save(todo)
            call.respond(HttpStatusCode.OK)
        }

        delete("/") {
            todos.deleteAll()
            call.respond(HttpStatusCode.OK)
        }
    }
}
