package d2.dokka.storybook.translator.description

import d2.dokka.storybook.model.Constants
import d2.dokka.storybook.model.doc.DocumentableIndexes
import d2.dokka.storybook.model.doc.utils.directAnnotation
import d2.dokka.storybook.model.doc.utils.directAnnotations
import d2.dokka.storybook.model.doc.utils.f2FunctionType
import d2.dokka.storybook.model.doc.utils.hasDirectAnnotation
import d2.dokka.storybook.model.doc.utils.isF2
import d2.dokka.storybook.model.doc.utils.isF2Supplier
import d2.dokka.storybook.model.render.D2TextStyle
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
) : FunctionListDescriptionPageContentBuilder() {

    override fun PageContentBuilder.DocumentableContentBuilder.functionsBlock(
        functions: Collection<DFunction>,
    ) {
        block(kind = ContentKind.Functions, elements = functions) { function ->
            val signature = FunctionSignature.of(function, documentableIndexes)
            group(setOf(function.dri), function.sourceSets.toSet(), ContentKind.Main) {
                header(level = 4, "${function.httpMethod()} /${signature.name}")

                function.documentation.forEach { (_, docNode) ->
                    docNode.children.firstOrNull()?.root?.let {
                        group(kind = ContentKind.Comment) {
                            comment(it)
                        }
                    }
                }

                functionContentType(function)
                functionAccess(function)
            }
            group(setOf(function.dri), function.sourceSets.toSet(), ContentKind.Parameters) {
                if (signature.params.isNotEmpty()) {
                    breakLine()
                    text("Body: ")
                }
                val isMultipart = function.isMultipartFormData()
                signature.params.forEachIndexed { i, param ->
                    when {
                        isMultipart -> {
                            breakLine()
                            text(param.name, styles = setOf(D2TextStyle.Code))
                            text(": ")
                            type(param.type)
                        }
                        else -> {
                            if (i > 0) {
                                text(", ")
                            }
                            type(param.type)
                        }
                    }
                }
                breakLine()
                if (signature.returnType != null) {
                    text("Result: ")
                    type(signature.returnType)
                }
            }
        }
    }

    private fun DFunction.httpMethod(): String {
        val restAnnotations = listOf(
            Constants.Annotation.RestMapping.GET,
            Constants.Annotation.RestMapping.POST,
            Constants.Annotation.RestMapping.PUT,
            Constants.Annotation.RestMapping.PATCH,
            Constants.Annotation.RestMapping.DELETE
        )
        val functionRestAnnotation = directAnnotations().firstOrNull { it.dri in restAnnotations }

        return when (functionRestAnnotation?.dri) {
            Constants.Annotation.RestMapping.GET -> "GET"
            Constants.Annotation.RestMapping.POST -> "POST"
            Constants.Annotation.RestMapping.PUT -> "PUT"
            Constants.Annotation.RestMapping.PATCH -> "PATCH"
            Constants.Annotation.RestMapping.DELETE -> "DELETE"
            else -> if (type.isF2() && f2FunctionType().isF2Supplier()) {
                "GET"
            } else {
                "POST"
            }
        }
    }

    private fun PageContentBuilder.DocumentableContentBuilder.functionContentType(function: DFunction) {
        val contentType = when {
            function.isMultipartFormData() -> "multipart/form-data"
            else -> "application/json"
        }
        text("Content-Type: $contentType", styles = setOf(TextStyle.Italic))
    }

    private fun DFunction.isMultipartFormData() = parameters.any { 
        it.hasDirectAnnotation(Constants.Annotation.REQUEST_PART) 
    }

    private fun PageContentBuilder.DocumentableContentBuilder.functionAccess(function: DFunction) {
        function.directAnnotation(Constants.Annotation.PERMIT_ALL)
            ?.let {
                breakLine()
                text("Access: public", styles = setOf(TextStyle.Italic))
            }

        function.directAnnotation(Constants.Annotation.ROLES_ALLOWED)
            ?.let { annotation ->
                val roles = (annotation.params["value"] as? ArrayValue)
                    ?.value
                    .orEmpty()
                    .map { it as LiteralValue }
                    .map(LiteralValue::text)
                breakLine()
                text("Access: ${roles.joinToString(", ")}", styles = setOf(TextStyle.Italic))
            }
    }
}
