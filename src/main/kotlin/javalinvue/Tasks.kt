package javalinvue

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Tasks: IntIdTable() {
    val title = varchar("title", 64)
    val done = bool("done")
    val owner = reference("owner", Users)
    val priority = varchar("priority", 6)
    val planed = long("planedDate")
}

class Task(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Task>(Tasks)

    var title by Tasks.title
    var done  by Tasks.done
    var owner by User referencedOn Tasks.owner
    var priority by Tasks.priority
    var planed by Tasks.planed
}
