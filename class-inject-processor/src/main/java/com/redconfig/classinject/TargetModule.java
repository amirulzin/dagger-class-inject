package com.redconfig.classinject;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import dagger.Module;
import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Modifier;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class TargetModule {
  public final String packageName;
  public final LinkedHashSet<TargetClass> targetClasses;

  public TargetModule(@NotNull String packageName) {
    this.packageName = packageName;
    this.targetClasses = new LinkedHashSet<>();
  }

  @NotNull
  public TypeSpec toClassProvidersTypeSpec(@NotNull String moduleClassName, @NotNull Set<AnnotationSpec> additionalModuleAnnotations) {
    ClassName className = ClassName.get(packageName, moduleClassName);
    TypeSpec.Builder moduleBuilder = TypeSpec.interfaceBuilder(className)
      .addAnnotation(Module.class)
      .addModifiers(Modifier.PUBLIC);

    for (AnnotationSpec annotationSpec : additionalModuleAnnotations) {
      moduleBuilder.addAnnotation(annotationSpec);
    }

    // A Map of method name to its MethodSpec.
    // We can determine if either qualified or simple method name is needed (default to simple).
    // In monolithic mode, this is is vital to avoid method name collisions.
    HashMap<String, MethodSpec> providerMethods = new HashMap<>(targetClasses.size());
    for (TargetClass targetClass : targetClasses) {
      MethodSpec methodSpec = Util.writeDaggerClassProviderMethod(targetClass, providerMethods.containsKey(targetClass.simpleProviderMethodName));
      providerMethods.put(targetClass.simpleProviderMethodName, methodSpec);
    }

    moduleBuilder.addMethods(providerMethods.values());

    return moduleBuilder.build();
  }

  @NotNull
  public TypeSpec toOriginClassProvidersTypeSpec(@NotNull String moduleClassName, @NotNull Set<ClassName> targetModulesClassNames, @NotNull Set<AnnotationSpec> additionalModuleAnnotations) {
    ClassName className = ClassName.get(packageName, moduleClassName);
    TypeSpec.Builder moduleBuilder = TypeSpec.interfaceBuilder(className)
      .addModifiers(Modifier.PUBLIC);

    AnnotationSpec daggerModuleAnnotationForIncludingModules = Util.createDaggerModuleAnnotationForIncludingModules(targetModulesClassNames);
    moduleBuilder.addAnnotation(daggerModuleAnnotationForIncludingModules);

    for (AnnotationSpec annotationSpec : additionalModuleAnnotations) {
      moduleBuilder.addAnnotation(annotationSpec);
    }

    return moduleBuilder.build();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TargetModule that = (TargetModule) o;
    return packageName.equals(that.packageName) &&
      targetClasses.equals(that.targetClasses);
  }

  @Override
  public int hashCode() {
    return Objects.hash(packageName, targetClasses);
  }
}
