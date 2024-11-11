package ru.quipy.api.context

import ru.quipy.api.Project
import java.util.UUID

class ProjectContext {
    private val projects = mutableMapOf<UUID, Project>()

    fun addProject(project: Project) {
        projects[project.id] = project
    }

    fun getProject(id: UUID): Project? = projects[id]

    fun getAllProjects(): List<Project> = projects.values.toList()
}
