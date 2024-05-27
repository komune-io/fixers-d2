package d2.dokka.storybook.model

import org.jetbrains.dokka.links.DRI
import org.jetbrains.dokka.links.PointingToDeclaration

object Constants {
    object Annotation {
        val ROLES_ALLOWED = DRI(
            packageName = "javax.annotation.security",
            classNames = "RolesAllowed",
            target = PointingToDeclaration
        )
        val PERMIT_ALL = DRI(
            packageName = "javax.annotation.security",
            classNames = "PermitAll",
            target = PointingToDeclaration
        )
        object RestMapping {
            private const val PACKAGE_NAME = "org.springframework.web.bind.annotation"
            val GET = DRI(
                packageName = PACKAGE_NAME,
                classNames = "GetMapping",
                target = PointingToDeclaration
            )
            val POST = DRI(
                packageName = PACKAGE_NAME,
                classNames = "PostMapping",
                target = PointingToDeclaration
            )
            val PUT = DRI(
                packageName = PACKAGE_NAME,
                classNames = "PutMapping",
                target = PointingToDeclaration
            )
            val PATCH = DRI(
                packageName = PACKAGE_NAME,
                classNames = "PatchMapping",
                target = PointingToDeclaration
            )
            val DELETE = DRI(
                packageName = PACKAGE_NAME,
                classNames = "DeleteMapping",
                target = PointingToDeclaration
            )
        }
    }
}
