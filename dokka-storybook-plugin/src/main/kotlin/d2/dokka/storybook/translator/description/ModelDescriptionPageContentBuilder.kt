package d2.dokka.storybook.translator.description

import d2.dokka.storybook.model.doc.DocumentableIndexes
import d2.dokka.storybook.model.doc.PageDocumentable
import d2.dokka.storybook.model.doc.SectionDocumentable
import d2.dokka.storybook.model.doc.tag.D2Type
import d2.dokka.storybook.model.doc.tag.Default
import d2.dokka.storybook.model.doc.tag.Ref
import d2.dokka.storybook.model.doc.utils.d2DocTagExtra
import d2.dokka.storybook.model.doc.utils.documentableIn
import d2.dokka.storybook.model.doc.utils.isOfType
import d2.dokka.storybook.model.doc.utils.title
import d2.dokka.storybook.model.doc.utils.toTypeString
import d2.dokka.storybook.model.render.D2TextStyle
import d2.dokka.storybook.translator.block
import org.jetbrains.dokka.base.translators.documentables.PageContentBuilder
import org.jetbrains.dokka.model.DClasslike
import org.jetbrains.dokka.model.DEnum
import org.jetbrains.dokka.model.DProperty
import org.jetbrains.dokka.model.DTypeAlias
import org.jetbrains.dokka.model.Documentable
import org.jetbrains.dokka.model.TypeAliased
import org.jetbrains.dokka.model.doc.DocTag
import org.jetbrains.dokka.pages.ContentGroup
import org.jetbrains.dokka.pages.ContentKind
import org.jetbrains.dokka.pages.ContentNode
import org.jetbrains.dokka.pages.DCI
import org.jetbrains.dokka.pages.TextStyle

internal abstract class ModelDescriptionPageContentBuilder(
    override val contentBuilder: PageContentBuilder,
    override val documentableIndexes: DocumentableIndexes
): DescriptionPageContentBuilder() {

    override fun contentFor(d: Documentable): ContentNode? {
        return when (d) {
            is PageDocumentable -> contentFor(d)
            is SectionDocumentable -> contentFor(d)
            is DEnum -> contentFor(d)
            is DClasslike -> contentFor(d)
            is DTypeAlias -> contentFor(d)
            else -> null
        }
    }

    private fun contentFor(p: PageDocumentable): ContentNode {
        return contentBuilder.contentFor(p)  {
            group(kind = ContentKind.Cover) {
                buildTitle(p)
                +contentForDescription(p)
            }
        }
    }

    private fun contentFor(s: SectionDocumentable): ContentNode {
        return contentBuilder.contentFor(s)  {
            group(kind = ContentKind.Cover) {
                buildTitle(s)
                +contentForDescription(s)
            }
        }
    }

    private fun contentFor(e: DEnum): ContentNode {
        return contentBuilder.contentFor(e)  {
            group(kind = ContentKind.Cover) {
                buildTitle(e)
                +contentForDescription(e)
            }

            unorderedList {
                e.entries.forEach { entry ->
                    item {
                        text(entry.name, styles = setOf(TextStyle.Bold))
                        text(": ")
                        entry.documentation.forEach { (_, docNode) ->
                            docNode.children.firstOrNull()?.root?.let {
                               commentInLine(it)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun contentFor(c: DClasslike): ContentNode {
        return contentBuilder.contentFor(c)  {
            group(kind = ContentKind.Cover) {
                buildTitle(c)
                buildType(c)
                +contentForDescription(c)
            }

            group {
                propertiesBlock(c.properties)
            }
        }
    }

    private fun contentFor(t: DTypeAlias): ContentNode {
        return contentBuilder.contentFor(t)  {
            group(kind = ContentKind.Cover) {
                buildTitle(t)
                buildType(t)
                +contentForDescription(t)
            }
        }
    }

    private fun PageContentBuilder.DocumentableContentBuilder.propertiesBlock(
        properties: Collection<DProperty>,
    ) {
        block(kind = ContentKind.Properties, elements = properties) { property ->
            propertySignature(property)

            val ref = property.d2DocTagExtra().firstTagOfTypeOrNull<Ref>()?.target
            if (ref != null) {
                if (ref.callable == null) {
                    throw IllegalArgumentException(
                        "Tag @ref of a property must link to a property (${property.dri} -> $ref"
                    )
                }
                val refProperty = (documentableIndexes.documentables[ref.copy(callable = null)] as DClasslike?)
                    ?.properties
                    ?.first { it.name == ref.callable!!.name }
                if(refProperty == null) {
                    throw IllegalArgumentException(
                        "Property ${ref.callable!!.name} not found in ${property.name}:${property.dri}"
                    )
                }
                propertyComment(refProperty)
            } else {
                propertyComment(property)
            }
        }
    }

    private fun PageContentBuilder.DocumentableContentBuilder.propertySignature(p: DProperty) {
        text("${p.name} ", styles = setOf(TextStyle.Italic, TextStyle.Bold))

        val propertyType = p.type
        val propertyTypeDocumentable = propertyType.documentableIn(documentableIndexes.documentables)
        if (propertyTypeDocumentable == null || propertyTypeDocumentable.isOfType(D2Type.HIDDEN)) {
            val typeString = if (propertyType is TypeAliased) {
                propertyType.inner.toTypeString(documentableIndexes.documentables)
            } else {
                propertyType.toTypeString(documentableIndexes.documentables)
            }
            text(typeString, styles = setOf(D2TextStyle.Code))
        } else {
            link(
                text = propertyType.toTypeString(documentableIndexes.documentables),
                address = propertyTypeDocumentable.dri,
                styles = setOf(D2TextStyle.Code)
            )
        }

        p.d2DocTagExtra().firstTagOfTypeOrNull<Default>()?.body?.let { defaultValue ->
            text(" (default: ", styles = emptySet())
            text(defaultValue, styles = setOf(TextStyle.Monospace))
            text(")", styles = emptySet())
        }
    }

    private fun PageContentBuilder.DocumentableContentBuilder.propertyComment(p: DProperty) {
        group(setOf(p.dri), p.sourceSets.toSet(), ContentKind.Main) {
            p.documentation.forEach { (_, docNode) ->
                docNode.children.firstOrNull()?.root?.let {
                    group(kind = ContentKind.Comment) {
                        comment(it)
                    }
                }
            }
        }
    }

    private fun PageContentBuilder.DocumentableContentBuilder.buildType(d: Documentable) {
        if (d.title() != d.name && !d.isOfType(D2Type.FUNCTION)) {
            text("Type: ")
            text(d.name!!, styles = setOf(D2TextStyle.Code))
        }
    }

    private fun PageContentBuilder.DocumentableContentBuilder.commentInLine(docTag: DocTag) {
        val descriptionNode = contentBuilder.commentsConverter.buildContent(
            docTag,
            DCI(mainDRI, ContentKind.Comment),
            mainSourcesetData
        ).firstOrNull()

        if (descriptionNode != null) {
            group(kind = ContentKind.Comment) {
                if (descriptionNode is ContentGroup) {
                    +descriptionNode.copy(style = descriptionNode.style - TextStyle.Paragraph)
                } else {
                    +descriptionNode
                }
            }
        }
    }
}
