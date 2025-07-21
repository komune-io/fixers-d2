package d2.dokka.storybook.translator.description

import d2.dokka.storybook.model.doc.DocumentableIndexes
import d2.dokka.storybook.model.doc.tag.D2Type
import d2.dokka.storybook.model.doc.utils.descriptions
import d2.dokka.storybook.model.doc.utils.groupedTags
import d2.dokka.storybook.model.doc.utils.isOfType
import d2.dokka.storybook.model.doc.utils.title
import d2.dokka.storybook.translator.D2StorybookPageContentBuilder
import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.base.translators.documentables.PageContentBuilder
import org.jetbrains.dokka.model.Documentable
import org.jetbrains.dokka.model.doc.Constructor
import org.jetbrains.dokka.model.doc.Description
import org.jetbrains.dokka.model.doc.NamedTagWrapper
import org.jetbrains.dokka.model.doc.Param
import org.jetbrains.dokka.model.doc.Property
import org.jetbrains.dokka.model.doc.See
import org.jetbrains.dokka.model.doc.TagWrapper
import org.jetbrains.dokka.pages.ContentNode
import org.jetbrains.dokka.pages.ContentStyle
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

abstract class DescriptionPageContentBuilder: D2StorybookPageContentBuilder {
    protected abstract val contentBuilder: PageContentBuilder
    protected abstract val documentableIndexes: DocumentableIndexes

    protected fun contentForDescription(d: Documentable): List<ContentNode> {
        val sourceSets = d.sourceSets
        return contentBuilder.contentFor(d) {
            descriptionSectionContent(d, sourceSets)
            unnamedTagSectionContent(d, sourceSets)
        }.children
    }

    protected fun PageContentBuilder.DocumentableContentBuilder.buildTitle(d: Documentable) {
        header(d.headerLevel(), d.title().substringAfterLast("/"))
    }

    protected fun Documentable.headerLevel(): Int {
        val parent = documentableIndexes.childToParentMap[dri]?.let(documentableIndexes.documentables::get)
            ?: return 1

        val headerInc = if (parent.isOfType(D2Type.FUNCTION, D2Type.SECTION, D2Type.PAGE)) 1 else 0

        return parent.headerLevel() + headerInc
    }

    /* ----- After this point, copied from org.jetbrains.dokka.base.translators.documentables.
       DescriptionSections.kt v1.9.20 ----- */

    private val unnamedTagsExceptions: Set<KClass<out TagWrapper>> =
        setOf(Property::class, Description::class, Constructor::class, Param::class, See::class)

    internal fun PageContentBuilder.DocumentableContentBuilder.descriptionSectionContent(
        documentable: Documentable,
        sourceSets: Set<DokkaConfiguration.DokkaSourceSet>,
    ) {
        val descriptions = documentable.descriptions
        if (descriptions.any { it.value.root.children.isNotEmpty() }) {
            sourceSets.forEach { sourceSet ->
                descriptions[sourceSet]?.also {
                    group(sourceSets = setOf(sourceSet), styles = emptySet()) {
                        comment(it.root)
                    }
                }
            }
        }
    }

    /**
     * Tags in KDoc are used in form of "@tag name value".
     * This function handles tags that have only value parameter without name.
     * List of such tags: `@return`, `@author`, `@since`, `@receiver`
     */
    @Suppress("NestedBlockDepth")
    private fun PageContentBuilder.DocumentableContentBuilder.unnamedTagSectionContent(
        documentable: Documentable,
        sourceSets: Set<DokkaConfiguration.DokkaSourceSet>
    ) {
        val unnamedTags = documentable.groupedTags
            .filterNot { (k, _) -> k.isSubclassOf(NamedTagWrapper::class) || k in unnamedTagsExceptions }
            .values.flatten().groupBy { it.first }
            .mapValues { it.value.map { it.second } }
            .takeIf { it.isNotEmpty() } ?: return

        sourceSets.forEach { sourceSet ->
            unnamedTags[sourceSet]?.let { tags ->
                if (tags.isNotEmpty()) {
                    tags.groupBy { it::class }.forEach { (_, sameCategoryTags) ->
                        group(sourceSets = setOf(sourceSet), styles = setOf(ContentStyle.KDocTag)) {
                            header(
                                level = 4,
                                text = sameCategoryTags.first().toHeaderString(),
                                styles = setOf()
                            )
                            sameCategoryTags.forEach { comment(it.root, styles = setOf()) }
                        }
                    }
                }
            }
        }
    }

    protected open fun TagWrapper.toHeaderString(): String = this.javaClass.toGenericString().split('.').last()
}
