package todo.app

import java.util.*

class TodoRepository {
    private var todos = mutableMapOf<String, Todo>()
    private val baseUrl = "https://ktor-todo-backend.herokuapp.com/"
    fun save(todo: Todo): Todo {
        val randomUUID = UUID.randomUUID()
        val copy = todo.copy(id = "$randomUUID", url = "$baseUrl$randomUUID", order = todo.order ?: 0)
        todos[copy.id!!] = copy
        return copy
    }

    fun getAll(): Set<Todo> = todos.values.toSet()

    fun get(id: String): Todo? = todos.get(id)

    fun deleteAll() = todos.clear()

    fun delete(id: String) = todos.remove(id)

    fun patch(id: String, todo: Todo): Todo {
        val savedTodo = todos[id]
        if (savedTodo != null) {
            val newTodo = savedTodo.merge(todo)
            todos[id] = newTodo
            return newTodo
        }
        return todo
    }
}


