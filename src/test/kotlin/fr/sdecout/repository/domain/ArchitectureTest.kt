package fr.sdecout.repository.domain

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption.Predefined.DO_NOT_INCLUDE_TESTS
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.junit.jupiter.api.Test

class ArchitectureTest {
    val domainPackage = "fr.sdecout.repository.domain"
    val languagePackages = arrayOf("java..", "kotlin..", "org.jetbrains.annotations..")
    val testLibPackages = arrayOf("io.kotest..", "org.junit.jupiter..", "io.mockk..", "com.tngtech.archunit..")
    val classes: JavaClasses = ClassFileImporter().withImportOption(DO_NOT_INCLUDE_TESTS).importPackages(domainPackage)

    @Test
    fun `domain should be isolated`() {
        classes()
            .should().onlyDependOnClassesThat().resideInAnyPackage(
                *languagePackages,
                *testLibPackages,
                "$domainPackage..",
            )
            .`as`("The domain of the hexagon should not depend on infrastructure and technology")
            .because("business rules and technology have distinct lifecycles. Modifications to one should have minimal impact on the other.")
            .check(classes)
    }

    @Test
    fun `domain should have a functional core`() {
        layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("functional core").definedBy("..core..")
            .layer("imperative shell").definedBy("..shell..")
            .layer("driving ports").definedBy("..api..")
            .layer("driven ports").definedBy("..spi..")
            .whereLayer("functional core").mayNotAccessAnyLayer()
            .whereLayer("imperative shell").mayOnlyBeAccessedByLayers("driving ports")
            .whereLayer("driving ports").mayOnlyAccessLayers("functional core")
            .whereLayer("driven ports").mayOnlyBeAccessedByLayers("imperative shell")
            .`as`("Domain of the hexagon have a functional core")
            .because("pure functions are easy to test.")
            .check(classes)
    }
}
