# D2

D2 is a module for generating automatic documentation of the Komune architecture.

## dokka-storybook-plugin

Plugin [Dokka](https://github.com/Kotlin/dokka) compatible with Storybook and [G2-UI](https://github.com/komune-io/fixers-g2)

### Installation with gradle

Dokka can be started [from inside a gradle task](https://kotlin.github.io/dokka/1.4.32/user_guide/gradle/usage/) attached to any root or sub module. 

#### Single module

The first thing is to import it at the root of your project:

*build.gradle.kts at root level*
```kotlin
plugins {
    id("org.jetbrains.dokka") version "1.4.32"
}
```

Then, to use the dokka-storybook-plugin, create a new Dokka task and add it as a dependency:

*build.gradle.kts of a module*
```kotlin
tasks {
    create<org.jetbrains.dokka.gradle.DokkaTask>("dokkaStorybook") {
        dependencies {
            plugins("io.komune.d2:dokka-storybook-plugin")
        }
    }
}
```
This task will then generate documentation for the module it has been created for.

#### Multi-module

For multi-module projects, it's also possible to generate documentation from all modules at once.

*build.gradle.kts at root level*
```kotlin
plugins {
    id("org.jetbrains.dokka") version "1.4.32"
}

val dokkaStorybook = "dokkaStorybook"
val dokkaStorybookPartial = "${dokkaStorybook}Partial"

subprojects {
    tasks {
        register<org.jetbrains.dokka.gradle.DokkaTask>(dokkaStorybookPartial) {
            dependencies {
                plugins("io.komune.d2:dokka-storybook-plugin")
            }
        }
    }
}

tasks {
    register<org.jetbrains.dokka.gradle.DokkaCollectorTask>(dokkaStorybook) {
        dependencies {
            plugins("io.komune.d2:dokka-storybook-plugin")
        }
        addChildTask(dokkaStorybookPartial)
        addSubprojectChildTasks(dokkaStorybookPartial)
    }
}
```

This will create two tasks for each module of the project. 

The dokkaStorybookPartial is a single-module task and will generate documentation for the module it is attached to. It can be executed as a standalone task.

The dokkaStorybook will collect the results of every Partial task within the subprojects of its module and assemble it all as one documentation. 

### Writing documentation

This plugin follows the same syntax as the official [KDoc](https://kotlinlang.org/docs/kotlin-doc.html) with a few extra tags.

> NB: All tags specified bellow are case-insensitive

#### @d2 type

Marks an entity as documentable and specifies its type. Only documentable objects will be included in the generated documentation.\
Available types (case-insensitive): 
 - Model: Classlike object containing data
 - Function: Typically takes a command as input and returns an event as output 
 - Command: Classlike used as input of a function
 - Event: Classlike used as output of a command function
 - Result: Classlike used as output of a query function
 - Service: Classlike interface containing functions
 - Api: Classlike interface containing f2 functions
 - Page:
 - Section:
 - Hidden: The entity will not be displayed on any page, but can be used to generate examples
 - Inherit: The Classlike will not be displayed on any page, but any link to it will be redirected to its superclass or interface 

#### @parent identifier

Identifies another D2 documentable as a parent. The current object will then be integrated inside the documentation page of its parent. \
A documentable without parent means that it will have its own dedicated page.

#### @example jsonValue or identifier

Specifies an example json value for a property inside a classlike.\
If an identifier is given instead, the example of the targeted documentable will be used.\
If a property with a d2 documentable type misses an @example tag, it will attempt to use the generated sample of the type if it exists.

#### @ref identifier

Used only on properties, and the identifier must link to another property.\
A property tagged with @ref will use the description and example of the property the identifier refers to.\
NB: It is possible to overwrite the example with an @example tag

#### @title

Defines a custom title for the documentation part of the documentable. If no title is specified, it will take the name of the object by default.

#### @page (deprecated)

Specifies an introduction for the page of the documentable, applied if and only if it has its own dedicated page (i.e. no parent). 

The block after this tag is parsed as a standalone KDoc block, except that the tags must start with two '@' instead of one (see example below).

The page can optionally have a title, description and example, which are defined the same way as for documentables. 

#### Example

See `sample` and `sample-fragment` modules for more in-depth examples.

```kotlin
package sample
        
/**
 * This thing is so dull that no one knows how it could have ever been created
 *
 * @D2 model
 * @author unknown but they should be loathed for their creation
 * @title Boring Object
 * @page
 * Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,
 * quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
 * Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
 * @@example
 * {
 *   "name": "Stuff",
 *   "description": "Phrase describing the stuff",
 *   "things": [["Weird thingy", "Eww wat is dis"]]
 * }
 * @@title d2/Boring Documentation
 */
interface BoringInterface {
    /**
     * The name of smth not interesting at all
     * @example "Stuff"
     */
    val name: String

    /**
     * The most boring description you could ever think of
     * @example "Well calling it stuff is a bit... It's more like a pile of things being... existent"
     */
    val description: String

    /**
     * A vain attempt to make it look like there are a lot to see here
     * @example [["Weird thingy", "Eww wat is dis"]]
     */
    val things: List<String?>
}
```
```kotlin
/**
 * The child
 * @D2 model
 * @parent [sample.BoringInterface]
 * @order 10
 */
interface ChildInterface {

    /**
     * Its toy
     * @example "a cube"
     */
    val toy: String

    /**
     * The same things as you'd find in a BoringInterface
     * @example [[sample.BoringInterface.things]]
     */
    val things: List<String?>
}
```
```kotlin
/**
 * Retrieve a boring thing
 * @D2 function
 * @parent [sample.BoringInterface]
 */
typealias BoringGetQueryFunction = F2Function<BoringGetQuery, BoringGetQueryResult>

/**
 * @D2 query
 * @parent [BoringGetQueryFunction]
 */
interface BoringGetQuery {
    /**
     * name
     * @example "blblbl"
     */
    val name: String

    /**
     * desc
     * @example null
     */
    val description: String?
}

/**
 * @D2 event
 * @parent [BoringGetQueryFunction]
 */
interface BoringGetQueryResult {

    /**
     * retrieved stuff
     * @example {
     *   "name": "blblbl",
     *   "description": "blblblblbl bl lbbllb lblblblbl",
     *   "things": [[]]
     * }
     */
    val boringStuff: BoringInterface?
}
```
