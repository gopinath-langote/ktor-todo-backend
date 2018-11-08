package todo.app

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*

fun Application.main() {
    val todos = TodoRepository()

    install(CORS) {
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
                val message = todos.get(id)
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
                val save = todos.patch(todo)
                call.respond(save)
            }
        }

        post("/") {
            val todo = call.receive<Todo>()
            val save = todos.save(todo)
            call.respond(save)
        }

        delete("/") {
            todos.deleteAll()
            call.respond(HttpStatusCode.OK)
        }
    }
}
