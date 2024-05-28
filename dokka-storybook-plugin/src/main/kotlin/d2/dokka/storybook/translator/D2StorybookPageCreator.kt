package d2.dokka.storybook.translator

import d2.dokka.storybook.model.doc.DocumentableIndexes
import d2.dokka.storybook.model.doc.tag.D2Type
import d2.dokka.storybook.model.doc.utils.asD2TypeDocumentable
import d2.dokka.storybook.model.doc.utils.d2Type
import d2.dokka.storybook.model.page.FileData
import d2.dokka.storybook.model.page.ModelPageNode
import d2.dokka.storybook.service.DocumentablePageSelector
import d2.dokka.storybook.translator.description.ApiDescriptionPageContentBuilder
import d2.dokka.storybook.translator.description.ModelDescriptionPageContentBuilder
import d2.dokka.storybook.translator.description.ServiceDescriptionPageContentBuilder
import d2.dokka.storybook.translator.root.MainPageContentBuilder
import d2.dokka.storybook.translator.root.RootPageContentBuilder
import d2.dokka.storybook.translator.visual.VisualPageContentBuilder
import org.jetbrains.dokka.base.signatures.SignatureProvider
import org.jetbrains.dokka.base.transformers.pages.comments.CommentsToContentConverter
import org.jetbrains.dokka.base.translators.documentables.PageContentBuilder
import org.jetbrains.dokka.links.DRI
import org.jetbrains.dokka.model.DModule
import org.jetbrains.dokka.model.Documentable
import org.jetbrains.dokka.pages.ModulePageNode
import org.jetbrains.dokka.utilities.DokkaLogger

class D2StorybookPageCreator(
    commentsToContentConverter: CommentsToContentConverter,
    signatureProvider: SignatureProvider,
    logger: DokkaLogger
) {
    private lateinit var documentableIndexes: DocumentableIndexes

    val contentBuilder: PageContentBuilder = PageContentBuilder(
        commentsToContentConverter, signatureProvider, logger
    )

    fun pageForModule(m: DModule): ModulePageNode {
        val documentables = m.packages.flatMap { pack -> pack.classlikes + pack.typealiases }
            .map(Documentable::asD2TypeDocumentable)

        documentableIndexes = DocumentableIndexes.from(documentables)

        val pages = documentableIndexes.parentToChildMap[DRI.topLevel]
            .orEmpty()
            .mapNotNull(documentableIndexes.documentables::get)
            .plus(documentables)
            .flatMap(::pagesFor)

        return ModulePageNode(
            name = m.name.ifEmpty { "<root>" },
            content = contentBuilder.contentFor(m),
            documentables = listOf(m),
            children = pages
        )
    }

    private fun pagesFor(d: Documentable): List<ModelPageNode> {
        return DocumentablePageSelector.filesFor(d).mapNotNull { d.toModelPageNode(it) }
    }

    private fun Documentable.toModelPageNode(fileData: FileData): ModelPageNode? {
        return fileData.contentBuilder(d2Type()).contentFor(this)
            ?.let { content ->
                ModelPageNode(
                    name = this.name.orEmpty(),
                    content = content,
                    dri = setOf(this.dri.copy(extra = fileData.id)),
                    documentables = listOf(this),
                    children = emptyList(),
                    fileData = fileData
                )
            }
    }

    private fun FileData.contentBuilder(d2Type: D2Type?) = when (this) {
        FileData.ROOT -> InnerRootPageContentBuilder()
        FileData.MAIN -> InnerMainPageContentBuilder()
        FileData.DESCRIPTION -> d2Type.descriptionContentBuilder()
        FileData.VISUAL_AUTOMATE,
        FileData.VISUAL_JSON,
        FileData.VISUAL_KOTLIN,
        FileData.VISUAL_YAML -> InnerVisualPageContentBuilder()
    }

    private fun D2Type?.descriptionContentBuilder() = when (this) {
        D2Type.API -> InnerApiDescriptionPageContentBuilder()
        D2Type.SERVICE -> InnerServiceDescriptionPageContentBuilder()
        else -> InnerModelDescriptionPageContentBuilder()
    }

    private inner class InnerApiDescriptionPageContentBuilder : ApiDescriptionPageContentBuilder(contentBuilder, documentableIndexes)
    private inner class InnerMainPageContentBuilder : MainPageContentBuilder(contentBuilder, documentableIndexes)
    private inner class InnerModelDescriptionPageContentBuilder : ModelDescriptionPageContentBuilder(contentBuilder, documentableIndexes)
    private inner class InnerServiceDescriptionPageContentBuilder
        : ServiceDescriptionPageContentBuilder(contentBuilder, documentableIndexes)
    private inner class InnerVisualPageContentBuilder : VisualPageContentBuilder(contentBuilder, documentableIndexes)
    private inner class InnerRootPageContentBuilder : RootPageContentBuilder(contentBuilder)
}
