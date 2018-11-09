package todo.app

data class Todo(
        val id: String?,
        val url: String?,
        val title: String?,
        val order: Int?,
        val completed: Boolean? = false
) {
    fun merge(newTodo: Todo): Todo {
        return Todo(
                id,
                newTodo.url ?: url,
                newTodo.title ?: title,
                newTodo.order ?: order,
                newTodo.completed ?: completed
        )
    }
}