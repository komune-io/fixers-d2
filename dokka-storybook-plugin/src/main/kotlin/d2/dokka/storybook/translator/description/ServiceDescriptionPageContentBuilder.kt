package d2.dokka.storybook.translator.description

import d2.dokka.storybook.model.doc.DocumentableIndexes
import d2.dokka.storybook.translator.block
import org.jetbrains.dokka.base.translators.documentables.PageContentBuilder
import org.jetbrains.dokka.model.DFunction
import org.jetbrains.dokka.pages.ContentKind
import org.jetbrains.dokka.pages.TextStyle

internal abstract class ServiceDescriptionPageContentBuilder(
    override val contentBuilder: PageContentBuilder,
    override val documentableIndexes: DocumentableIndexes
): FunctionListDescriptionPageContentBuilder() {

    override fun PageContentBuilder.DocumentableContentBuilder.functionsBlock(
        functions: Collection<DFunction>,
    ) {
        block(kind = ContentKind.Properties, elements = functions) { function ->
            functionSignature(function)
            text("<br/>")

            group(setOf(function.dri), function.sourceSets.toSet(), ContentKind.Main) {
                function.documentation.forEach { (_, docNode) ->
                    docNode.children.firstOrNull()?.root?.let {
                        group(kind = ContentKind.Comment) {
                            comment(it)
                        }
                    }
                }
            }
        }
    }

    private fun PageContentBuilder.DocumentableContentBuilder.functionSignature(function: DFunction) {
        val signature = FunctionSignature.of(function, documentableIndexes)
        text(signature.name, styles = setOf(TextStyle.Bold))
        text("(")
        signature.params.forEachIndexed { i, (name, type) ->
            val separator = if (i > 0) ", " else ""
            text("$separator$name:")
            type(type)
        }
        text(")")
        signature.returnType?.let {
            text(":")
            type(it)
        }
    }
}
