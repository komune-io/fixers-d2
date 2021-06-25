package d2.dokka.storybook.renderer

import d2.dokka.storybook.model.WrapperTag
import org.jetbrains.dokka.pages.ContentGroup
import org.jetbrains.dokka.pages.ContentPage
import org.jetbrains.dokka.pages.ContentTable
import org.jetbrains.dokka.plugability.DokkaContext

class ModelDescriptionRenderer(context: DokkaContext): MarkdownRenderer(context) {

    override fun StringBuilder.buildTableProperties(node: ContentTable, pageContext: ContentPage) {
        node.children.forEach { property ->
            wrapWith(WrapperTag.Article) {
                buildNewLine()
                buildNewLine()
                property.children.forEach { child ->
                    val trailingSpace = if (child is ContentGroup) "" else " "
                    append(buildString { child.build(this, pageContext) } + trailingSpace)
                }
            }
            buildNewLine()
        }
    }
}