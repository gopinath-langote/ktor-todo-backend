package todo.app

import java.util.*

class TodoRepository {
    private var todos = mutableMapOf<String, Todo>()
    private val baseUrl = "https://ktor-todo-backend.herokuapp.com/"
    fun save(todo: Todo): Todo {
        val randomUUID = UUID.randomUUID()
        val newId = "$randomUUID"
        val updatedTodo = todo.copy(id = newId, url = "$baseUrl$randomUUID", order = todo.order ?: 0)
        todos[newId] = updatedTodo
        return updatedTodo
    }

    fun getAll(): Set<Todo> = todos.values.toSet()

    fun get(id: String): Todo? = todos.get(id)

    fun deleteAll() = todos.clear()

    fun delete(id: String): Todo? = todos.remove(id)

    fun patch(id: String, todo: Todo): Todo? {
        return todos[id]?.let {
            val newTodo = todos[id]?.merge(todo)
            newTodo?.let { a -> todos[id] = a }
            newTodo
        }
    }
}


