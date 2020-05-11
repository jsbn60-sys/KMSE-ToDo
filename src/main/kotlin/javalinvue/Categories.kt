package javalinvue

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Categories: IntIdTable() {
    val title = varchar("title", 64)
    val owner = reference("owner", Users)
}

class Category(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Category>(Categories)

    var title by Categories.title
    var owner by User referencedOn Categories.owner

    val tasks by Task referrersOn Tasks.category
}
