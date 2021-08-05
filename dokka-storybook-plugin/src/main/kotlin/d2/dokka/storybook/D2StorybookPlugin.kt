package d2.dokka.storybook

import d2.dokka.storybook.location.D2StorybookLocationProvider
import d2.dokka.storybook.renderer.D2StorybookRenderer
import d2.dokka.storybook.transformer.documentable.D2TagFilterTransformer
import d2.dokka.storybook.transformer.documentable.InheritedDocExtractorTransformer
import d2.dokka.storybook.translator.D2StorybookDocumentableToPageTranslator
import org.jetbrains.dokka.CoreExtensions
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.gfm.GfmPlugin
import org.jetbrains.dokka.plugability.DokkaPlugin
import org.jetbrains.dokka.transformers.pages.PageTransformer

class D2StorybookPlugin: DokkaPlugin() {

    private val dokkaBase by lazy { plugin<DokkaBase>() }
    private val gfmPlugin by lazy { plugin<GfmPlugin>() }

    val storybookPreprocessors by extensionPoint<PageTransformer>()

    val renderer by extending {
        CoreExtensions.renderer providing ::D2StorybookRenderer override gfmPlugin.renderer
    }

    val locationProvider by extending {
        dokkaBase.locationProviderFactory providing D2StorybookLocationProvider::Factory override gfmPlugin.locationProvider
    }

    val locationProviderFactory by lazy { dokkaBase.locationProviderFactory }
    val outputWriter by lazy { dokkaBase.outputWriter }

    val inheritedDocExtractor by extending {
        CoreExtensions.documentableTransformer with InheritedDocExtractorTransformer()
    }

    val d2AnnotationFilter by extending {
        CoreExtensions.documentableTransformer with D2TagFilterTransformer() order {
            after(inheritedDocExtractor)
        }
    }

    val documentableToPageTranslator by extending {
        CoreExtensions.documentableToPageTranslator providing ::D2StorybookDocumentableToPageTranslator override dokkaBase.documentableToPageTranslator
    }
}
