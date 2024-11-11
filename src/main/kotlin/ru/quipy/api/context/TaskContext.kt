package ru.quipy.api.context

import ru.quipy.api.Task
import java.util.UUID

class TaskContext {
    private val tasks = mutableMapOf<UUID, Task>()

    fun addTask(task: Task) {
        tasks[task.id] = task
    }

    fun getTask(id: UUID): Task? = tasks[id]

    fun getAllTasks(): List<Task> = tasks.values.toList()
}
