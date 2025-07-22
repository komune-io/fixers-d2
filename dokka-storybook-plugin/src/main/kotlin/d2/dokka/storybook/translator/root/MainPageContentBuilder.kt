package d2.dokka.storybook.translator.root

import d2.dokka.storybook.model.doc.DocumentableIndexes
import d2.dokka.storybook.model.doc.tag.D2Type
import d2.dokka.storybook.model.doc.utils.d2Type
import d2.dokka.storybook.model.doc.utils.isF2CommandFunction
import d2.dokka.storybook.model.doc.utils.isOfType
import d2.dokka.storybook.model.doc.utils.title
import d2.dokka.storybook.model.doc.utils.weight
import d2.dokka.storybook.model.page.FileData
import d2.dokka.storybook.model.render.D2ContentKind
import d2.dokka.storybook.model.render.D2Marker
import d2.dokka.storybook.service.DocumentablePageSelector
import d2.dokka.storybook.translator.D2StorybookPageContentBuilder
import java.util.SortedSet
import org.jetbrains.dokka.base.translators.documentables.PageContentBuilder
import org.jetbrains.dokka.links.DRI
import org.jetbrains.dokka.links.sureClassNames
import org.jetbrains.dokka.model.DTypeAlias
import org.jetbrains.dokka.model.Documentable
import org.jetbrains.dokka.pages.ContentGroup
import org.jetbrains.dokka.pages.ContentNode

internal abstract class MainPageContentBuilder(
    private val contentBuilder: PageContentBuilder,
    private val documentableIndexes: DocumentableIndexes,
): D2StorybookPageContentBuilder {

    override fun contentFor(d: Documentable): ContentNode? {
        return contentFor(d) {
            group(setOf(d.dri), kind = D2ContentKind.Source) {
                DocumentablePageSelector.filesFor(d)
                    .filter { file -> file !in listOf(FileData.MAIN, FileData.ROOT) }
                    .forEach { file(it, d) }
            }
        }
    }

    private fun contentFor(
        d: Documentable, 
        block: PageContentBuilder.DocumentableContentBuilder.() -> Unit = {}
    ): ContentGroup {
        return contentBuilder.contentFor(d, kind = D2ContentKind.Container)  {
            block()
            if (d.isOfType(D2Type.MODEL, D2Type.AUTOMATE, D2Type.API, D2Type.SERVICE)) {
                text("", kind = D2Marker.Spacer)
            }
            +contentForChildrenOf(d)
            if (d.isOfType(D2Type.FUNCTION)) {
                text("", kind = D2Marker.Spacer)
            }
        }
    }

    private fun contentForChildrenOf(d: Documentable): ContentNode {
        return contentBuilder.contentFor(
            dri = documentableIndexes.parentToChildMap[d.dri]
                .orEmpty()
                .mapNotNull(documentableIndexes.documentables::get)
                .driSortedByD2Type(),
            sourceSets = d.sourceSets,
            kind = D2ContentKind.Children
        ) {}
    }

    private fun PageContentBuilder.DocumentableContentBuilder.file(fileData: FileData, d: Documentable) {
        group(kind = fileData.kind) {
            text(fileData.id, kind = D2ContentKind.File)
            fileData.title(d)?.let { text(it, kind = D2ContentKind.Description) }
        }
    }

    private fun FileData.title(d: Documentable) = when (this) {
        FileData.VISUAL_JSON,
        FileData.VISUAL_KOTLIN,
        FileData.VISUAL_YAML -> d.title()
        else -> null
    }

    private fun List<Documentable>.driSortedByD2Type(): SortedSet<DRI> {
        val typeMap = this.associate { d -> d.dri to d.sortableD2Type() }
        val weightMap = this.associate { d -> d.dri to (d.weight() ?: Int.MAX_VALUE) }
        return this.map(Documentable::dri)
            .toSortedSet { dri1, dri2 ->
                compareWeights(typeMap[dri1]?.order, typeMap[dri2]?.order).takeIf { it != 0 }
                    ?: compareWeights(weightMap[dri1], weightMap[dri2]).takeIf { it != 0 }
                    ?: dri1.sureClassNames.compareTo(dri2.sureClassNames, true)
            }
    }

    private fun Documentable.sortableD2Type(): D2Type? {
        val type = d2Type()
        if (type != D2Type.FUNCTION || this !is DTypeAlias) {
            return type
        }

        return if (isF2CommandFunction(documentableIndexes.documentables)) D2Type.COMMAND else D2Type.QUERY
    }

    private fun compareWeights(w1: Int?, w2: Int?) = (w1 ?: Int.MAX_VALUE).compareTo(w2 ?: Int.MAX_VALUE)
}
