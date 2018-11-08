package todo.app

class TodoRepository {
    private var todos = mutableSetOf<Todo>()

    fun save(todo: Todo) {
        todos.add(todo)
    }

    fun getAll(): Set<Todo> = todos
}


