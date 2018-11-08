package todo.app

import java.util.*

class TodoRepository {
    private var todos = mutableMapOf<String, Todo>()

    fun save(todo: Todo): Todo {
        val randomUUID = UUID.randomUUID()
        todo.copy(id = "$randomUUID", url = "$randomUUID")
        todos.put(todo.id!!, todo)
        print("todo --- $todo")
        return todo
    }

    fun getAll(): Set<Todo> = todos.values.toSet()

    fun get(id: String): Todo? = todos.get(id)

    fun deleteAll() = todos.clear()
}


