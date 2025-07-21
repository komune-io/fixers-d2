package d2.dokka.storybook.translator.visual

import d2.dokka.storybook.model.doc.DocumentableIndexes
import d2.dokka.storybook.model.doc.PageDocumentable
import d2.dokka.storybook.model.doc.SectionDocumentable
import d2.dokka.storybook.model.doc.tag.Example
import d2.dokka.storybook.model.doc.tag.ExampleLink
import d2.dokka.storybook.model.doc.tag.ExampleText
import d2.dokka.storybook.model.doc.tag.Ref
import d2.dokka.storybook.model.doc.tag.Visual
import d2.dokka.storybook.model.doc.tag.VisualLink
import d2.dokka.storybook.model.doc.tag.VisualSimple
import d2.dokka.storybook.model.doc.tag.VisualText
import d2.dokka.storybook.model.doc.tag.VisualType
import d2.dokka.storybook.model.doc.tag.WithTarget
import d2.dokka.storybook.model.doc.utils.d2DocTagExtra
import d2.dokka.storybook.model.doc.utils.documentableIn
import d2.dokka.storybook.model.doc.utils.isCollection
import d2.dokka.storybook.model.doc.utils.isMap
import d2.dokka.storybook.model.doc.utils.visualType
import d2.dokka.storybook.translator.D2StorybookPageContentBuilder
import d2.dokka.storybook.translator.codeBlock
import org.jetbrains.dokka.base.translators.documentables.PageContentBuilder
import org.jetbrains.dokka.model.DClasslike
import org.jetbrains.dokka.model.DEnum
import org.jetbrains.dokka.model.DProperty
import org.jetbrains.dokka.model.DTypeAlias
import org.jetbrains.dokka.model.Documentable
import org.jetbrains.dokka.pages.ContentGroup
import org.jetbrains.dokka.pages.ContentKind
import org.jetbrains.dokka.pages.ContentNode
import org.jetbrains.dokka.pages.ContentStyle
import org.jetbrains.dokka.pages.Style

abstract class VisualPageContentBuilder(
	protected val contentBuilder: PageContentBuilder,
	protected val documentableIndexes: DocumentableIndexes
) : D2StorybookPageContentBuilder {

	override fun contentFor(d: Documentable): ContentNode? {
		try {
			return contentFor(d, null)
		} catch (e: StackOverflowError) {
			println("StackOverflowError when generating the visual of Documentable [${d.dri}]")
			throw e
		}
	}

	private fun contentFor(d: Documentable, visualType: VisualType?): ContentNode? {
		return when (d) {
			is PageDocumentable -> rawContentFor(d)
			is SectionDocumentable -> rawContentFor(d)
			is DEnum -> contentFor(d, visualType)
			is DClasslike -> contentFor(d, visualType)
			is DTypeAlias -> rawContentFor(d)
			is DProperty -> contentFor(d, visualType)
			else -> null
		}
	}

	private fun contentFor(c: DClasslike, visualType: VisualType?): ContentNode {
		val visualTag = c.d2DocTagExtra().firstTagOfTypeOrNull<Visual>()
		val actualVisualType = visualType ?: c.visualType()

		return if (visualTag == null || visualTag is VisualSimple || visualTag.type != actualVisualType) {
			contentBuilder.contentFor(c, kind = ContentKind.Properties) {
				header(0, c.name!!, kind = ContentKind.Symbol)
				+c.properties.mapNotNull { contentFor(it, actualVisualType) }
			}
		} else {
			rawContentForVisualTag(c, visualTag)
		}
	}

	private fun contentFor(e: DEnum, visualType: VisualType?): ContentNode {
		val visualTag = e.d2DocTagExtra().firstTagOfTypeOrNull<Visual>()
		val actualVisualType = visualType ?: e.visualType()

		return if (visualTag == null || visualTag is VisualSimple || visualTag.type != actualVisualType) {
			contentBuilder.contentFor(e, kind = ContentKind.Sample) {
				codeBlock("\"${e.entries.firstOrNull()?.name ?: ""}\"", "")
			}
		} else {
			rawContentForVisualTag(e, visualTag)
		}
	}

	private fun rawContentFor(d: Documentable): ContentNode? {
		return d.d2DocTagExtra().firstTagOfTypeOrNull<Visual>()?.let { rawContentForVisualTag(d, it) }
	}

	private fun rawContentForVisualTag(d: Documentable, visualTag: Visual): ContentNode {
		return contentBuilder.contentFor(d, kind = ContentKind.Sample) {
			when (visualTag) {
				is VisualSimple -> Unit
				is VisualText -> codeBlock(visualTag.body ?: "", "")
				is VisualLink -> contentForLinkedSample(d, visualTag, visualTag.type)?.let { +it }
			}
		}
	}

	private fun contentFor(property: DProperty, visualType: VisualType?): ContentNode? {
		return property.d2DocTagExtra().firstTagOfTypeOrNull<Example>()
			?.let { exampleTag -> contentForTaggedProperty(property, exampleTag, visualType) }
			?: contentForUntaggedProperty(property, visualType)
	}

	private fun contentForTaggedProperty(property: DProperty, exampleTag: Example, visualType: VisualType?): ContentNode? {
		return when (exampleTag) {
			is ExampleLink -> contentForLinkedSample(property, exampleTag, visualType)
			is ExampleText -> exampleTag.body?.let { body ->
				contentFor(property) { text(body) }
			}
		}
	}

	@Suppress("ReturnCount")
	private fun contentForLinkedSample(d: Documentable, targetTag: WithTarget, visualType: VisualType?): ContentNode? {
		val targetDri = targetTag.target ?: return null

		if (targetDri.callable == null) {
			return documentableIndexes.documentables[targetDri]?.let { contentFor(it, visualType) }
		}

		val targetDocumentable = documentableIndexes.documentables[targetDri.copy(callable = null)]
		if (targetDocumentable !is DClasslike) {
			return null
		}
		val targetProperty = targetDocumentable.properties.find { it.name == targetDri.callable!!.name } ?: return null
		return contentFor(targetProperty.copy(name = d.name!!), visualType)
	}

	@Suppress("ReturnCount")
	private fun contentForUntaggedProperty(property: DProperty, visualType: VisualType?): ContentNode? {
		val refTag = property.d2DocTagExtra().firstTagOfTypeOrNull<Ref>()
		if (refTag != null) {
			if (refTag.target?.callable == null) {
				throw IllegalArgumentException(
					"Tag @ref of a property must link to a property (${property.dri} -> ${refTag.target}"
				)
			}
			return contentForLinkedSample(property, refTag, visualType)
		}

		val styles = setOfNotNull<Style>(
			ContentStyle.TabbedContent.takeIf { property.type.isCollection() && !property.type.isMap() }
		)

		val propertyType = property.type.documentableIn(documentableIndexes.documentables)
			?: return null

		val contentForPropertyType = contentFor(propertyType, visualType)
			?.takeIf { it.children.isNotEmpty() }
			?: return null

		return contentFor(property, styles = styles) { +contentForPropertyType }
	}

	private fun contentFor(
		property: DProperty, kind: ContentKind = ContentKind.Main, styles: Set<Style> = emptySet(),
		propertyValue: PageContentBuilder.DocumentableContentBuilder.() -> Unit
	): ContentGroup {
		return contentBuilder.contentFor(property, sourceSets = property.sourceSets, kind = kind, styles = styles) {
			text(property.name)
			propertyValue()
		}
	}
}
