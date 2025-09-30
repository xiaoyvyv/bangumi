import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the

val Project.libs: LibrariesForLibs
    get() = the<LibrariesForLibs>()

val Project.composeResourceId: String
    get() = project.name.lowercase().asUnderscoredIdentifier()

val Project.frameworkBaseName: String
    get() = project.name.lowercase().asUnderscoredIdentifier()

fun String.asUnderscoredIdentifier(): String =
    replace('-', '_')
        .let { if (it.isNotEmpty() && it.first().isDigit()) "_$it" else it }