package d2.dokka.storybook.translator.description

import d2.dokka.storybook.model.Constants
import d2.dokka.storybook.model.doc.DocumentableIndexes
import d2.dokka.storybook.model.doc.utils.directAnnotation
import d2.dokka.storybook.translator.block
import org.jetbrains.dokka.base.translators.documentables.PageContentBuilder
import org.jetbrains.dokka.model.ArrayValue
import org.jetbrains.dokka.model.DFunction
import org.jetbrains.dokka.model.LiteralValue
import org.jetbrains.dokka.pages.ContentKind
import org.jetbrains.dokka.pages.TextStyle

internal abstract class ApiDescriptionPageContentBuilder(
    override val contentBuilder: PageContentBuilder,
    override val documentableIndexes: DocumentableIndexes
): FunctionListDescriptionPageContentBuilder() {

    override fun PageContentBuilder.DocumentableContentBuilder.functionsBlock(
        functions: Collection<DFunction>,
    ) {
        block(kind = ContentKind.Functions, elements = functions) { function ->
            val signature = FunctionSignature.of(function, documentableIndexes)
            group(setOf(function.dri), function.sourceSets.toSet(), ContentKind.Main) {
                header(4, "POST /${signature.name}")
                functionAccess(function)

                function.documentation.forEach { (_, docNode) ->
                    docNode.children.firstOrNull()?.root?.let {
                        group(kind = ContentKind.Comment) {
                            comment(it)
                        }
                    }
                }
            }
            group(setOf(function.dri), function.sourceSets.toSet(), ContentKind.Parameters) {
                if (signature.params.isNotEmpty()) {
                    breakLine()
                    text("Body: ")
                }
                signature.params.forEachIndexed { i, (_, type) ->
                    val separator = if (i > 0) ", " else ""
                    text(separator)
                    type(type)
                }
                breakLine()
                if (signature.returnType != null) {
                    text("Result: ")
                    type(signature.returnType)
                }
            }
        }
    }

    private fun PageContentBuilder.DocumentableContentBuilder.functionAccess(function: DFunction) {
        function.directAnnotation(Constants.Annotation.PERMIT_ALL)
            ?.let { text("Access: public", styles = setOf(TextStyle.Italic)) }

        function.directAnnotation(Constants.Annotation.ROLES_ALLOWED)
            ?.let { annotation ->
                val roles = (annotation.params["value"] as? ArrayValue)
                    ?.value
                    .orEmpty()
                    .map { it as LiteralValue }
                    .map(LiteralValue::text)
                text("Access: ${roles.joinToString(", ")}", styles = setOf(TextStyle.Italic))
            }
    }
}
