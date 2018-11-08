package todo.app

class TodoRepository {
    private var todos = mutableMapOf<Int, Todo>()

    fun save(todo: Todo) = todos.put(todo.id, todo)

    fun getAll(): Set<Todo> = todos.values.toSet()

    fun get(id: Int): Todo? = todos.get(id)

    fun deleteAll() = todos.clear()
}


