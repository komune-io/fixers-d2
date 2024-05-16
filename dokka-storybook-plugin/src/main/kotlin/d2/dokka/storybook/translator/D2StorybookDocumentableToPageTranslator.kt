package d2.dokka.storybook.translator

import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.model.DModule
import org.jetbrains.dokka.pages.ModulePageNode
import org.jetbrains.dokka.plugability.DokkaContext
import org.jetbrains.dokka.plugability.plugin
import org.jetbrains.dokka.plugability.querySingle
import org.jetbrains.dokka.transformers.documentation.DocumentableToPageTranslator

class D2StorybookDocumentableToPageTranslator(
    context: DokkaContext
): DocumentableToPageTranslator {
    private val commentsToContentConverter = context.plugin<DokkaBase>().querySingle { commentsToContentConverter }
    private val signatureProvider = context.plugin<DokkaBase>().querySingle { signatureProvider }
    private val logger = context.logger

    override fun invoke(module: DModule): ModulePageNode =
        D2StorybookPageCreator(commentsToContentConverter, signatureProvider, logger).pageForModule(module)
}
