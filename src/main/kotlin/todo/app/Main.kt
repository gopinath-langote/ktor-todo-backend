package todo.app

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpHeaders.AccessControlAllowCredentials
import io.ktor.http.HttpHeaders.AccessControlAllowHeaders
import io.ktor.http.HttpHeaders.AccessControlAllowMethods
import io.ktor.http.HttpHeaders.AccessControlAllowOrigin
import io.ktor.http.HttpHeaders.AccessControlExposeHeaders
import io.ktor.http.HttpHeaders.AccessControlMaxAge
import io.ktor.http.HttpHeaders.AccessControlRequestHeaders
import io.ktor.http.HttpHeaders.AccessControlRequestMethod
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*

fun Application.main() {
    val todos = TodoRepository()

    install(CORS) {
        anyHost()
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Delete)
        method(HttpMethod.Post)
        method(HttpMethod.Patch)
        method(HttpMethod.Put)
        header(HttpHeaders.AccessControlAllowCredentials)
        header(AccessControlAllowOrigin)
        header(AccessControlAllowMethods)
        header(AccessControlAllowCredentials)
        header(AccessControlAllowHeaders)
        header(AccessControlRequestMethod)
        header(AccessControlRequestHeaders)
        header(AccessControlExposeHeaders)
        header(AccessControlMaxAge)

        header("Access-Control-Request-Headers: Content-Type")
        header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS, HEAD")
    }
    install(XForwardedHeadersSupport)
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
                val save = todos.patch(id, todo)
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

        delete("/{id}") {
            val id = call.parameters.get("id")
            if (id == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                todos.delete(id)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
