package com.redconfig.classinject.processors;

import com.redconfig.classinject.*;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

public class MonolithicClassProcessor implements ClassProcessor {
  @Override
  public void writeOutput(@NotNull ProcessingEnvironment processingEnv, @NotNull Set<TargetClass> targetClasses, @NotNull Set<TargetClass> targetOriginClasses, @NotNull Set<AnnotationSpec> additionalModuleAnnotations) {
    writeProviderModules(processingEnv, targetOriginClasses, targetClasses, additionalModuleAnnotations);
  }

  private void writeProviderModules(@NotNull ProcessingEnvironment processingEnv, @NotNull Set<TargetClass> targetOriginClasses, @NotNull Set<TargetClass> targetClasses, @NotNull Set<AnnotationSpec> additionalModuleAnnotations) {
    Filer filer = processingEnv.getFiler();
    Messager messager = processingEnv.getMessager();
    //Create originModules first
    HashMap<String, TargetModule> originModules = new LinkedHashMap<>(targetOriginClasses.size(), 1f);
    for (TargetClass originClass : targetOriginClasses) {
      Util.addClassToModule(originModules, originClass);
    }

    //Add classes to originModules
    Collection<TargetModule> targetModules = originModules.values();
    for (TargetModule module : targetModules) {
      for (TargetClass targetClass : targetClasses) {
        if (targetClass.isPublic) {
          module.targetClasses.add(targetClass);
        } else {
          String errorMsg = String.format("%s class visibility must be public to be generated under @%s %s mode.", targetClass.className.canonicalName(), ClassInject.class.getSimpleName(), Config.OPTION_MODE_MONOLITH);
          messager.printMessage(Diagnostic.Kind.ERROR, errorMsg, targetClass.originatingTypeElement);
        }
      }
    }

    //Generate the classes
    for (TargetModule targetModule : targetModules) {
      TypeSpec typeSpec = targetModule.toClassProvidersTypeSpec(Config.ROOT_MODULE_NAME, additionalModuleAnnotations);
      Util.writeClass(targetModule.packageName, typeSpec, filer);
    }
  }
}
