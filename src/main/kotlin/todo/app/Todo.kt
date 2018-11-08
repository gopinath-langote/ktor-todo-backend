package todo.app

data class Todo(
        val id: String?,
        val url: String?,
        val title: String?,
        val order: Int? = 0,
        val completed: Boolean? = false
)