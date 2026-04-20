package fr.sdecout.repository.infra

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices
import org.junit.jupiter.api.Test

class ArchitectureTest {
    val infrastructurePackage = "fr.sdecout.repository.infra"
    val classes: JavaClasses = ClassFileImporter().importPackages(infrastructurePackage)

    @Test
    fun `adapters should not depend on one another`() {
        slices()
            .matching("$infrastructurePackage.(*).(*)..").namingSlices("$1 adapter '$2")
            .should().notDependOnEachOther()
            .`as`("Adapters around the domain of the hexagon should not depend on one another")
            .because("every business process should be under the control of the domain.")
            .check(classes)
    }
}
