package com.redconfig.classinject.processors;

import com.redconfig.classinject.ClassInject;
import com.redconfig.classinject.ClassProcessor;
import com.redconfig.classinject.Config;
import com.redconfig.classinject.TargetClass;
import com.redconfig.classinject.TargetModule;
import com.redconfig.classinject.Util;
import com.squareup.javapoet.TypeSpec;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;

public class MonolithicClassProcessor implements ClassProcessor {
  @Override
  public void writeOutput(@NotNull ProcessingEnvironment processingEnv, @NotNull Set<TargetClass> targetClasses, @NotNull Set<TargetClass> targetOriginClasses) {
    writeProviderModules(processingEnv, targetOriginClasses, targetClasses);
  }

  private void writeProviderModules(@NotNull ProcessingEnvironment processingEnv, @NotNull Set<TargetClass> targetOriginClasses, @NotNull Set<TargetClass> targetClasses) {
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
      TypeSpec typeSpec = targetModule.toClassProvidersTypeSpec(Config.ROOT_MODULE_NAME);
      Util.writeClass(targetModule.packageName, typeSpec, filer);
    }
  }
}
