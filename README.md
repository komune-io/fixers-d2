# D2

D2 is a module for generating automatic documentation of the Komune architecture.

<!-- TOC -->
* [D2](#d2)
* [dokka-storybook-plugin](#dokka-storybook-plugin)
  * [Installation with gradle](#installation-with-gradle)
    * [Single module](#single-module)
    * [Multi-module](#multi-module)
  * [Writing documentation](#writing-documentation)
    * [Structure](#structure)
    * [Documentables](#documentables)
      * [Page](#page)
      * [Section](#section)
      * [Model](#model)
      * [Command, Event, Query, Result](#command-event-query-result)
      * [Function](#function)
      * [Service, API](#service-api)
      * [Hidden](#hidden)
      * [Inherit](#inherit)
    * [Tags](#tags)
      * [@d2 [type]](#d2-type)
      * [@title](#title)
      * [@parent [identifier]](#parent-identifierglossary)
      * [@child [identifier]](#child-identifierglossary)
      * [@order [number]](#order-number)
      * [@visual [type] [value?]](#visual-type-value)
      * [@example [value / identifier]](#example-value--identifierglossary)
      * [@ref [identifier]](#ref-identifierglossary)
      * [@default [value]](#default-value)
  * [Glossary](#glossary)
<!-- TOC -->

# dokka-storybook-plugin

Plugin [Dokka](https://github.com/Kotlin/dokka) compatible with Storybook and [G2-UI](https://github.com/komune-io/fixers-g2)

## Installation with gradle

Dokka can be started [from inside a gradle task](https://kotlin.github.io/dokka/1.4.32/user_guide/gradle/usage/) attached to any root or sub module. 

### Single module

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

### Multi-module

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

## Writing documentation

This plugin follows the same syntax as the official [KDoc](https://kotlinlang.org/docs/kotlin-doc.html) with a few extra tags.

### Structure

The generated documentation is structured into pages, usually one page per module or domain part. A page can contain sections, defined either manually or automatically depending on the objects it contains.

A page or section can contain any number of [documentables](#documentables), which are ordered depending on their specified type:
1. [API](#service-api)
2. [Service](#service-api)
3. [Model](#model)
4. [Function](#function)
5. [Query](#command-event-query-result)
6. [Result](#command-event-query-result)
7. [Command](#command-event-query-result)
8. [Event](#command-event-query-result)
9. [Manually defined Sections](#section)

A [documentable](#glossary) can be assigned to a section via a parent / child relationship, and a [documentable](#glossary) can also be seen as a section itself that may contain children.  
The ancestor tree can be as deep as needed, but the root ancestor must be a page.

All sections (including pages) have the same documentation structure. They contain a [title](#title), a text summary, a technical description, an optional [visual](#visual-type-value), and then their children sections.  
The text summary is picked up from any written content present before the tags in the javadoc.  
The content of the technical description is automatically generated and depends on the type of [documentable](#glossary).

### Documentables

Below is the list of all documentable types that can be used in the documentation, and a description of their specificities.

#### Page

A page is the root of the documentation tree. It can contain any number of sections and documentables, and will define the content of the actual documentation page that will be rendered in the end.  
The title of the page will also be displayed in the sidebar of the documentation.  
A page does not have a technical description nor any visual by default.

Example:

```kotlin
/**
 * NEIPA (New England India Pale Ale) is a style of beer that originated in the New England region of the United States.
 * It is characterized by its hazy appearance and juicy, fruit-forward hop flavor. Unlike traditional IPAs, NEIPAs are
 * less bitter and focus more on hop aroma and flavor.
 * @d2 page
 * @title Neipa
 */
interface D2Neipa
```

#### Section

A section is a structural container for other documentables. It is not usually needed to manually define a section unless you want to organize the documentation in a specific way.  
A section does not have a technical description nor any visual by default.

Example:

```kotlin
/**
 * The brewing process for a NEIPA involves several steps, each contributing to its unique characteristics. Here's a general outline:
 * 1. **Milling the Grains**: The first step in brewing is to crush the malted grains. This exposes the starchy center of the barley grain which will be converted into sugars during the mashing process.
 * 2. **Mashing**: The milled grains are then soaked in hot water in a process called mashing. This activates enzymes in the barley that convert the grain's starches into fermentable sugars.
 * 3. **Lautering and Sparging**: After mashing, the liquid (now called wort) is separated from the grain husks. The grains are then rinsed in a process called sparging to extract any remaining sugars.
 * 4. **Boiling and Hopping**: The wort is boiled and hops are added. For a NEIPA, a significant amount of hops are added late in the boil and during fermentation (a process known as dry hopping). This maximizes the hop aroma and flavor while minimizing bitterness.
 * 5. **Cooling and Fermenting**: The wort is cooled and transferred to a fermentation vessel. Yeast is added and the fermentation process begins, where the yeast consumes the sugars and produces alcohol and carbon dioxide.
 * 6. **Conditioning**: After fermentation is complete, the beer is conditioned (or aged) for several weeks. This allows any remaining yeast and sediment to settle out and the flavors of the beer to fully develop.
 * 7. **Packaging**: The beer is then carbonated (if it wasn't naturally carbonated during fermentation) and packaged into kegs, bottles, or cans.
 * 
 * Please note that this is a simplified overview of the brewing process. The actual process can be much more complex and varies from brewery to brewery.
 * @d2 section
 * @title Brewing Process
 * @parent [D2Neipa]
 */
interface D2NeipaBrewing
```

#### Model

A model is used to describe the data structures used in the application.

The technical description is a breakdown of the properties it contains. Each property will have a name, a type, a description, and optionally a default value.  
For enum classes, each value is considered a property.

By default, the visual representation of a model is an example json object with the properties displayed as key-value pairs.  
Enums are an exception and do not have a visual representation by default.

Example:

```kotlin
/**
 * @d2 model
 * @parent [D2Neipa]
 */
interface Neipa {
    /** 
     * The name of the variety. 
     * @example "Loco"
     */
    val name: String
    
    /** 
     * The alcohol by volume percentage.
     * @example 6.5
     */
    val abv: Double
    
    /** 
     * The International Bitterness Units.
     * @example 40
     */
    val ibu: Int

    /**
     * The brewery that produces the beer.
     * @example "La Barbote"
     * @default "Unknown"
     */
    val brewery: String
}
```

#### Command, Event, Query, Result

These documentables, juste like [models](#model), are used to describe data structures. Contrary to models however, they are designed to be used in the context (i.e. as child) of a [function](#function), where Command / Query are the input and Event / Result are the output.  
As so, their default titles are always "Command", "Event", "Query", and "Result" respectively.

The technical description is a breakdown of the properties they contain. Each property will have a name, a type, a description, and optionally a default value.

By default, their visual representation is an example json object with the properties displayed as key-value pairs.

Example: see [function](#function)

#### Function

A function is used to describe the operations that can be performed in the application. It usually has a pair of command / event or query / result as children.  
A function does not have a technical description nor any visual by default.

A function has a default title generated from the name of the kotlin object. A space is inserted before any uppercase letter and the keyword "Function" at the end is removed (e.g. "NeipaCreateFunction" becomes "Neipa Create").

Example:

```kotlin
/**
 * Create a new NEIPA variety.
 * @d2 function
 * @parent [D2Neipa]
 */
interface NeipaCreateFunction

/**
 * @d2 command
 * @parent [NeipaCreateFunction]
 */
interface NeipaCreateCommand {
    /**
     * The name of the variety.
     * @example "Loco"
     */
    val name: String

    /**
     * The alcohol by volume percentage.
     * @example 6.5
     */
    val abv: Double

    /**
     * The International Bitterness Units.
     * @example 40
     */
    val ibu: Int

    /**
     * The brewery that produces the beer.
     * @example "La Barbote"
     */
    val brewery: String
}

/**
 * @d2 event
 * @parent [NeipaCreateFunction]
 */
interface NeipaCreatedEvent {
    /**
     * The id of the created variety.
     * @example "1234"
     */
    val id: String
}
```

#### Service, API

A service and an API generate the same documentation structure, but are rendered slightly differently.  
They are used to describe a list of functions that can be performed in the application.

The technical description is a list of the functions it contains. Each function will have a name, a description, a list of parameters and a return type.  
A service will render each function for a Kotlin usage (i.e. reproduce their Kotlin signature), while an API will render each function for an HTTP usage.

The functions are separated into two categories: commands and queries. This segregation is done automatically based on the following rules.  
A function is considered a command if:
- it is annotated with `@d2 command`
- its parameters contain at least one type that is tagged as a [command documentable](#command-event-query-result)
- it returns an `F2Function` with an input that:
  - is tagged as a [command documentable](#command-event-query-result) 
  - contains the keyword "Command" in its name

Any function that is not a command will be considered a query.

They have no visual representation by default.  
They have two titles "Queries" and "Commands" which are not customizable.

Example:

```kotlin
/**
 * @d2 api
 * @parent [D2Neipa]
 */
interface NeipaApi {
    /**
     * Create a new NEIPA variety.
     */
    fun create(command: NeipaCreateCommand): NeipaCreatedEvent

    /**
     * Delete a NEIPA variety
     * @d2 command
     */
    fun delete(id: String)
  
    /**
     * Get a list of all NEIPA varieties.
     */
    fun list(): List<Neipa>
}
```

#### Hidden

A hidden documentable will not be displayed on any page, but can have its uses:
- A property pointing to a hidden TypeAlias will display its underlying type in the documentation.
- Visuals of hidden documentables can still be referred to in other documentables.

A hidden documentable doesn't need a parent as it will not be rendered in the documentation.

Example:

```kotlin
/**
 * @d2 hidden
 * @visual json "1234"
 */
typealias NeipaId = String

/**
 * @d2 model
 * @parent [D2Neipa]
 */
interface Neipa {
    /**
     * This will use the example of NeipaId in the visual representation,
     * and the type of the property will be "String".
     */
    val id: NeipaId
}
```

#### Inherit

An inherit documentable will not be displayed on any page, but any link to it will be redirected to its superclass or interface.

An inherit documentable doesn't need a parent as it will not be rendered in the documentation.

Example:

```kotlin

/**
 * @d2 function
 * @parent [D2Neipa]
 */
typealias NeipaCreateFunction = F2Function<NeipaCreateCommand, NeipaCreatedEvent>

/**
 * @d2 command
 * @parent [NeipaCreateFunction]
 */
interface NeipaCreateCommandDTO

/**
 * @d2 inherit
 */
class NeipaCreateCommand : NeipaCreateCommandDTO

/**
 * @d2 event
 * @parent [NeipaCreateFunction]
 */
interface NeipaCreatedEventDTO

/**
 * @d2 inherit
 */
class NeipaCreatedEvent : NeipaCreatedEventDTO

/**
 * @d2 api
 * @parent [D2Neipa]
 */
interface NeipaApi {
  /**
   * The types of the parameters and return type will be replaced by their DTO counterparts.
   */
  fun create(): NeipaCreateFunction
}
```

### Tags

D2 comes with a few extra tags that can be used to customize the documentation.

> NB: All tags specified bellow are case-insensitive

#### @d2 [type]

*Can be used on [classlikes](#glossary), or on functions of [services or apis](#service-api).*

Marks an entity as [documentable](#glossary) and specifies its type. Only [documentable](#glossary) objects will be included in the generated documentation.  
Available types (case-insensitive): 
- [Page](#page)
- [Section](#section)
- [Model](#model)
- [Command](#command-event-query-result)
- [Event](#command-event-query-result)
- [Query](#command-event-query-result)
- [Result](#command-event-query-result)
- [Function](#function)
- [Service](#service-api)
- [Api](#service-api)
- [Hidden](#hidden)
- [Inherit](#inherit)

Can also be used on functions of [services or apis](#service-api) to specify if they are a command or a query.

#### @title

*Can be used on [classlikes](#glossary).*

Defines a custom title for the documentation section of the [documentable](#glossary). If no title is specified, it will take the name of the object by default, except for [commands, events, queries, and results](#command-event-query-result) which will have a default title based on their type.  
If a title other than the kotlin type name is defined on a [documentable](#glossary) of type Model, Command, Event, Query or Result, the original class name will be displayed below in the documentation.  
[Services](#service-api) and [APIs](#service-api) do not have a customizable title.

Example:

```kotlin
/**
 * @d2 model
 * @parent [D2Neipa]
 * @title NEIPA Variety
 */
interface Neipa
```

#### @parent [[identifier](#glossary)]

*Can be used on [classlikes](#glossary).*

Identifies another [documentable](#glossary) as a parent. The current [documentable](#glossary) will then be integrated inside the documentation section of its parent (see [Structure](#Structure)).  
A [documentable](#glossary) that is not a [page](#page) must have a parent (or be referenced as a [child](#child-identifierglossary)).

Example:

```kotlin
/**
 * @d2 page
 */
interface D2Neipa

/**
 * @d2 model
 * @parent [D2Neipa]
 */
interface Neipa
```

#### @child [[identifier](#glossary)]

*Can be used on [classlikes](#glossary).*

Identifies another [documentable](#glossary) as a child. The child [documentable](#glossary) will then be integrated inside the documentation section of the current [documentable](#glossary) (see [Structure](#Structure)).

Example:

```kotlin
/**
 * @d2 page
 * @child [Neipa]
 */
interface D2Neipa

/**
 * @d2 model
 */
interface Neipa
```

#### @order [number]

*Can be used on [classlikes](#glossary).*

Specifies the order of a [documentable](#glossary) inside its parent. The order is relative to the other children of the parent of the same type (see [type order](#structure)).

Example:

```kotlin
/**
 * Children order will be:
 * 1. Neipa
 * 2. NeipaBrewing
 * 3. NeipaCreateFunction
 *   a. NeipaCreateCommand
 *   b. NeipaCreatedEvent
 * Because Functions are always rendered after Models, and Commands are always rendered before Events.
 * @d2 page
 */
interface D2Neipa

/**
 * @d2 model
 * @parent [D2Neipa]
 * @order 1
 */
interface Neipa

/**
 * @d2 model
 * @parent [D2Neipa]
 * @order 2
 */
interface NeipaBrewing

/**
 * @d2 function
 * @parent [D2Neipa]
 * @order 0
 */
interface NeipaCreateFunction

/**
 * @d2 command
 * @parent [NeipaCreateFunction]
 */
interface NeipaCreateCommand

/**
 * @d2 event
 * @parent [NeipaCreateFunction]
 */
interface NeipaCreatedEvent
```

#### @visual [type] [value?]

*Can be used on [classlikes](#glossary) or typealiases.*

Specifies a visual representation for a [documentable](#glossary). The visual type can be one of the following:
- `none`: No visual representation will be displayed. No value needed.
- `json`: A json object will be displayed inside a code block. The value is optional.
- `kotlin` (experimental): A kotlin object will be displayed inside a code block. The value is optional.
- `automate`: An S2 automaton will be rendered as a graph. The value must be a path to a json file containing the automaton.

Some types of [documentables](#glossary) have a default visual representation. See their [specific documentation](#documentables) for more information.

If a value is specified, it will be used as-is for the visual representation. If not, it will be generated automatically based on the [@example](#example-value--identifierglossary) tags on the properties of the [documentable](#glossary).

Note: The values must be valid in relation to the visual type. For example, a json value must be a valid json object or literal.

Example:

```kotlin
/**
 * @d2 model
 * @parent [D2Neipa]
 * @visual json {
 *  "name": "Loco",
 *  "abv": 6.5,
 *  "ibu": 40,
 *  "brewery": "La Barbote"
 * }
 */
interface Neipa {
    val name: String
    val abv: Double
    val ibu: Int
    val brewery: String
}

/**
 * @d2 hidden
 * @visual json "1234"
 */
typealias NeipaId = String

/**
 * @d2 model
 * @parent [D2Neipa]
 * @visual automate sample/src/main/resources/neipa.json
 */
enum class NeipaState {
    START,
    MASHING,
    FERMENTING,
    CONDITIONING,
    DRINKABLE
}
```

#### @example [value / [identifier](#glossary)]

*Can be used on properties of a [classlike](#glossary).*

Specifies an example value for a property inside a classlike to use in the [visual representation](#visual-type-value). The value must match the visual type of the [documentable](#glossary).  
If an [identifier](#glossary) is given instead, the example of the targeted [documentable](#glossary) (or property) will be used.  
If a property with a [documentable](#glossary) type lacks an @example tag, it will attempt to use the generated visual of the type if it exists.

Example:

```kotlin
/**
 * @d2 hidden
 * @visual json "1234"
 */
typealias NeipaId = String

/**
 * @d2 model
 * @parent [D2Neipa]
 */
interface Neipa {
    /**
     * The id of the variety.
     * Will use "1234" from NeipaId as example.
     */
    val id: NeipaId
  
    /**
     * The name of the variety.
     * @example "Loco"
     */
    val name: String

    /**
     * The alcohol by volume percentage.
     * @example 6.5
     */
    val abv: Double

    /**
     * The International Bitterness Units.
     * @example 40
     */
    val ibu: Int

    /**
     * The brewery that produces the beer.
     * @example "La Barbote"
     */
    val brewery: String
}

/**
 * @d2 model
 * @parent [D2Neipa]
 */
interface Brewery {
    /**
     * The name of the brewery.
     * @example [Neipa.brewery]
     */
    val name: String

    /**
     * The beers produced by the brewery.
     * Will automatically generate a json array containing one example of Neipa from its visual representation.
     */
    val beers: List<Neipa>
}
```

#### @ref [[identifier](#glossary)]

*Can be used on properties of a [classlike](#glossary).*

The [identifier](#glossary) must link to another property.  
A property tagged with @ref will use the description and example of the linked property.  

The example can still be overridden by an [@example](#example-value--identifierglossary) tag if needed.

Example:

```kotlin
/**
 * @d2 model
 * @parent [D2Neipa]
 */
interface Neipa {
    /**
     * The name of the variety.
     * @example "Loco"
     */
    val name: String

    /**
     * The alcohol by volume percentage.
     * @example 6.5
     */
    val abv: Double

    /**
     * The International Bitterness Units.
     * @example 40
     */
    val ibu: Int

    /**
     * The brewery that produces the beer.
     * @example "La Barbote"
     */
    val brewery: String
}

/**
 * @d2 command
 * @parent [D2Neipa]
 */
interface NeipaCreateCommand {
    /**
     * @ref [Neipa.name]
     */
    val name: String

    /**
     * @ref [Neipa.abv]
     */
    val abv: Double

    /**
     * @ref [Neipa.ibu]
     */
    val ibu: Int

    /**
     * @ref [Neipa.brewery]
     */
    val brewery: String
}
```

#### @default [value]

*Can be used on properties of a [classlike](#glossary).*

Specifies a default value for a property. It will be displayed in the technical description of the [documentable](#glossary).

Example:

```kotlin
/**
 * @d2 model
 * @parent [D2Neipa]
 */
interface Neipa {
    /**
     * The name of the variety.
     * @example "Loco"
     */
    val name: String

    /**
     * The alcohol by volume percentage.
     * @example 6.5
     * @default 0.0
     */
    val abv: Double

    /**
     * The International Bitterness Units.
     * @example 40
     * @default 0
     */
    val ibu: Int

    /**
     * The brewery that produces the beer.
     * @example "La Barbote"
     * @default "Unknown"
     */
    val brewery: String
}
```

## Glossary

- **Classlike**: Kotlin class, interface, or enum.
- **Documentable**: Any object annotated with the [d2](#d2-type) tag.
- **Identifier**: A reference to a Kotlin object (e.g. `io.komune.d2.Neipa`)
