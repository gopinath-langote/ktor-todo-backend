package todo.app

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.XForwardedHeadersSupport
import io.ktor.http.HttpHeaders.AccessControlAllowMethods
import io.ktor.http.HttpHeaders.AccessControlAllowOrigin
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.pipeline.PipelineContext
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*

fun Application.main() {
    val todos = TodoRepository()

    install(CORS) {
        anyHost()
        method(HttpMethod.Options)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(AccessControlAllowOrigin)
        header(AccessControlAllowMethods)
    }
    install(XForwardedHeadersSupport)
    install(DefaultHeaders)
    install(ContentNegotiation) {
        jackson {

        }
    }

    routing {
        get("/") {
            call.respond(todos.getAll())
        }

        get("/{id}") {
            respondOn { id: String -> todos.get(id) }
        }

        patch("/{id}") {
            val todo = call.receive<Todo>()
            respondOn { id: String -> todos.patch(id, todo) }
        }

        post("/") {
            call.respond(todos.save(call.receive()))
        }

        delete("/") {
            call.respond(todos.deleteAll())
        }

        delete("/{id}") {
            respondOn { id: String -> todos.delete(id) }
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.respondOn(operation: (String) -> Todo?) {
    call.parameters["id"]?.let {
        operation.invoke(it)?.let { todo -> call.respond(todo) }
    } ?: call.respond(HttpStatusCode.NotFound)
}
