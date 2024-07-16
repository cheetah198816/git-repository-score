package com.gitrepositoryscore;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import java.util.HashMap;
import java.util.Map;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;

@AnalyzeClasses(packages = ArchitectureTest.ROOT_PACKAGE, importOptions = {
  ImportOption.DoNotIncludeTests.class
})
class ArchitectureTest {

  static final String ROOT_PACKAGE = "com.gitrepositoryscore";

  @ArchTest
  static final ArchRule onionArchitectureIsRespected = onionArchitecture()
    .domainModels(ROOT_PACKAGE + ".domain..")
          .applicationServices("application", ROOT_PACKAGE + ".application..")
          .domainServices(ROOT_PACKAGE + ".domain..")// models and domain services (repositories) are not separated by concrete package names
          .adapter("infrastructure", ROOT_PACKAGE + ".infrastructure..");

  @ArchTest
  static final ArchRule layerDependenciesAreRespected = layeredArchitecture()
    .consideringAllDependencies()
    .layer("domain").definedBy(ROOT_PACKAGE + ".domain..")
    .layer("infrastructure").definedBy(ROOT_PACKAGE + ".infrastructure..")
    .layer("application").definedBy(ROOT_PACKAGE + ".application..")
    .whereLayer("domain").mayOnlyBeAccessedByLayers("infrastructure", "application")
    .whereLayer("infrastructure").mayNotBeAccessedByAnyLayer()
    .whereLayer("application").mayNotBeAccessedByAnyLayer();

  /**
   * Will only cover direct byte code dependencies,
   * but not for example data return type in domain interfaces,
   * like e.g. org.springframework.data.domain.Page.
   */
  @ArchTest
  static final ArchRule domainLayerIsIndependentFromExternalLibraries = noClasses()
    .that().haveNameMatching(ROOT_PACKAGE + ".domain.*")
    .should().accessClassesThat().haveNameMatching("org.springframework.((?![data.domain|context.annotation|core.type|http.HttpStatus]).*)")
    .orShould().accessClassesThat().haveNameMatching("com.fasterxml.*")
    .because("Domain layer should not leak infrastructure libraries.");

  static ArchCondition<JavaClass> haveAnUniqueName =
    new ArchCondition<>("have an unique class name") {
      private final Map<String, String> simpleNameToFullClassName = new HashMap<>();

      @Override
      public void check(JavaClass item, ConditionEvents events) {
        String simpleClassName = item.getSimpleName();
        String packageName = item.getPackageName();
        if (simpleNameToFullClassName.containsKey(simpleClassName) && !packageName.contains(ROOT_PACKAGE + ".generated")) {
          String message = String
            .format("Duplicate class name %s already exists here: %s and here: %s", simpleClassName, item.getName(),
              simpleNameToFullClassName.get(simpleClassName));
          events.add(SimpleConditionEvent.violated(item, message));
        }
        simpleNameToFullClassName.put(simpleClassName, item.getName());
      }
    };

  @ArchTest
  static final ArchRule classNamesShouldBeUniqueAcrossLayers = classes()
    .that().areNotInnerClasses()
    .and().areNotMemberClasses()
    .should(haveAnUniqueName);
}
