package d2.dokka.storybook.model.doc.utils

import d2.dokka.storybook.model.doc.D2DocTagExtra
import d2.dokka.storybook.model.doc.PageDocumentable
import d2.dokka.storybook.model.doc.SectionDocumentable
import d2.dokka.storybook.model.doc.tag.D2
import d2.dokka.storybook.model.doc.tag.D2DocTagWrapper
import d2.dokka.storybook.model.doc.tag.D2Type
import d2.dokka.storybook.model.doc.tag.Order
import d2.dokka.storybook.model.doc.tag.Title
import d2.dokka.storybook.model.doc.tag.Visual
import d2.dokka.storybook.model.doc.tag.VisualType
import org.jetbrains.dokka.DokkaConfiguration.DokkaSourceSet
import org.jetbrains.dokka.links.DRI
import org.jetbrains.dokka.links.sureClassNames
import org.jetbrains.dokka.model.Annotations
import org.jetbrains.dokka.model.DClasslike
import org.jetbrains.dokka.model.DEnum
import org.jetbrains.dokka.model.Documentable
import org.jetbrains.dokka.model.SourceSetDependent
import org.jetbrains.dokka.model.doc.CustomTagWrapper
import org.jetbrains.dokka.model.doc.Description
import org.jetbrains.dokka.model.doc.NamedTagWrapper
import org.jetbrains.dokka.model.doc.TagWrapper
import org.jetbrains.dokka.model.orEmpty
import org.jetbrains.dokka.model.properties.PropertyContainer
import org.jetbrains.dokka.model.properties.WithExtraProperties
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
fun Documentable.toPageDocumentable() = PageDocumentable(
	name = name.orEmpty(),
	dri = dri.copy(classNames = dri.sureClassNames),
	documentation = documentation,
	sourceSets = sourceSets,
	expectPresentInSet = expectPresentInSet,
	children = listOf(this),
	extra = (this as? WithExtraProperties<Documentable>)?.extra ?: PropertyContainer.empty()
)

@Suppress("UNCHECKED_CAST")
fun Documentable.toSectionDocumentable() = SectionDocumentable(
	name = name.orEmpty(),
	dri = dri.copy(classNames = dri.sureClassNames),
	documentation = documentation,
	sourceSets = sourceSets,
	expectPresentInSet = expectPresentInSet,
	children = listOf(this),
	extra = (this as? WithExtraProperties<Documentable>)?.extra ?: PropertyContainer.empty()
)

fun Documentable.d2Type() = d2DocTagExtra().firstTagOfTypeOrNull<D2>()?.type
fun Documentable.weight() = d2DocTagExtra().firstTagOfTypeOrNull<Order>()?.weight
fun Documentable.title(): String = d2DocTagExtra().firstTagOfTypeOrNull<Title>()?.body
	?: generateTitle()

private fun Documentable.generateTitle() = when (d2Type()) {
	D2Type.COMMAND -> "Command"
	D2Type.QUERY -> "Query"
	D2Type.EVENT -> "Event"
	D2Type.RESULT -> "Result"
	D2Type.FUNCTION -> name!!.split(Regex("(?=[A-Z])"))
		.joinToString(" ")
		.substringBeforeLast("Function")
		.trim()
	else -> name!!
}

fun Documentable.asD2TypeDocumentable() = when (d2Type()) {
	D2Type.PAGE -> toPageDocumentable()
	D2Type.SECTION -> toSectionDocumentable()
	else -> this
}

inline fun <reified T : D2DocTagWrapper> Documentable.hasD2TagOfType(): Boolean {
	return d2DocTagExtra().firstTagOfTypeOrNull<T>() != null
}

fun Documentable.isOfType(vararg types: D2Type): Boolean {
	return d2DocTagExtra().firstTagOfTypeOrNull<D2>()?.type in types
}

@Suppress("UNCHECKED_CAST")
fun Documentable.d2DocTagExtra() = (this as? WithExtraProperties<Documentable>)
	?.extra?.get(D2DocTagExtra)
	?: D2DocTagExtra(emptyList())

fun Documentable.visualType() = when (d2Type()) {
	D2Type.API -> VisualType.NONE
	D2Type.SERVICE -> VisualType.NONE
	null -> VisualType.NONE
	else -> modelVisualType()
}

private fun Documentable.modelVisualType() = d2DocTagExtra()
	.firstTagOfTypeOrNull<Visual>()
	?.type
	?: defaultVisualType()

private fun Documentable.defaultVisualType() = when (this) {
	is DEnum -> VisualType.NONE
	is DClasslike -> VisualType.DEFAULT
	else -> VisualType.NONE
}

@Suppress("UNCHECKED_CAST")
fun Documentable.directAnnotations(): List<Annotations.Annotation> = (this as? WithExtraProperties<Documentable>)
	?.extra
	?.get(Annotations)
	?.directAnnotations
	.orEmpty()
	.flatMap { it.value }

fun Documentable.directAnnotation(dri: DRI): Annotations.Annotation? = directAnnotations()
	.firstOrNull { annotation -> annotation.dri == dri }

fun Documentable.hasDirectAnnotation(dri: DRI): Boolean = directAnnotation(dri) != null


/* ----- After this point, copied from org.jetbrains.dokka.base.translators.documentables.DefaultPageCreator.kt v1.9.20 ----- */

typealias GroupedTags = Map<KClass<out TagWrapper>, List<Pair<DokkaSourceSet?, TagWrapper>>>

val List<Documentable>.sourceSets: Set<DokkaSourceSet>
	get() = flatMap { it.sourceSets }.toSet()

val List<Documentable>.dri: Set<DRI>
	get() = map { it.dri }.toSet()

val Documentable.groupedTags: GroupedTags
	get() = documentation.flatMap { (pd, doc) ->
		doc.children.map { pd to it }.toList()
	}.groupBy { it.second::class }

val Documentable.descriptions: SourceSetDependent<Description>
	get() = groupedTags.withTypeUnnamed()

val Documentable.customTags: Map<String, SourceSetDependent<CustomTagWrapper>>
	get() = groupedTags.withTypeNamed()

@Suppress("UNCHECKED_CAST")
inline fun <reified T : TagWrapper> GroupedTags.withTypeUnnamed(): SourceSetDependent<T> =
	(this[T::class] as List<Pair<DokkaSourceSet, T>>?)?.toMap().orEmpty()

@Suppress("UNCHECKED_CAST")
inline fun <reified T : NamedTagWrapper> GroupedTags.withTypeNamed(): Map<String, SourceSetDependent<T>> =
	(this[T::class] as List<Pair<DokkaSourceSet, T>>?)
		?.groupByTo(linkedMapOf()) { it.second.name }
		?.mapValues { (_, v) -> v.toMap() }
		.orEmpty()
