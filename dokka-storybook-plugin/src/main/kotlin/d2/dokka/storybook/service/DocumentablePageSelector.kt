package d2.dokka.storybook.service

import d2.dokka.storybook.model.doc.PageDocumentable
import d2.dokka.storybook.model.doc.SectionDocumentable
import d2.dokka.storybook.model.doc.tag.D2Type
import d2.dokka.storybook.model.doc.utils.isOfType
import d2.dokka.storybook.model.doc.utils.visualType
import d2.dokka.storybook.model.page.FileData
import org.jetbrains.dokka.model.DClasslike
import org.jetbrains.dokka.model.DTypeAlias
import org.jetbrains.dokka.model.Documentable

object DocumentablePageSelector {
    fun filesFor(d: Documentable): List<FileData> {
        if (d.isOfType(D2Type.INHERIT, D2Type.HIDDEN)) {
            return emptyList()
        }

        return when (d) {
            is PageDocumentable -> filesFor(d)
            is SectionDocumentable -> filesFor(d)
            is DClasslike -> filesFor(d)
            is DTypeAlias -> filesFor(d)
            else -> emptyList()
        }
    }

    fun filesFor(d: PageDocumentable) = listOfNotNull(
        FileData.ROOT,
        FileData.MAIN,
        FileData.DESCRIPTION.takeIf { d.hasDescription },
        d.visualType().fileData
    )

    fun filesFor(d: SectionDocumentable) = listOfNotNull(
        FileData.MAIN,
        FileData.DESCRIPTION.takeIf { d.hasDescription },
        d.visualType().fileData
    )

    fun filesFor(d: DClasslike) =  listOfNotNull(
        FileData.MAIN,
        FileData.DESCRIPTION,
        d.visualType().fileData
    )

    fun filesFor(d: DTypeAlias) = listOfNotNull(
        FileData.MAIN,
        FileData.DESCRIPTION,
        d.visualType().fileData
    )
}
