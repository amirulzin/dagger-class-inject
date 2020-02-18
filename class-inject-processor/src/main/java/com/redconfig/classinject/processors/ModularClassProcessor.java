package com.redconfig.classinject.processors;

import com.redconfig.classinject.*;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class ModularClassProcessor implements ClassProcessor {
  @Override
  public void writeOutput(@NotNull ProcessingEnvironment processingEnv, @NotNull Set<TargetClass> targetClasses, @NotNull Set<TargetClass> targetOriginClasses) {
    Filer filer = processingEnv.getFiler();
    Set<ClassName> providerModules = writeProviderModules(targetClasses, filer);
    writeOriginModules(targetOriginClasses, providerModules, filer);
  }

  @NotNull
  private Set<ClassName> writeProviderModules(@NotNull Set<TargetClass> targetClasses, @NotNull Filer filer) {
    HashMap<String, TargetModule> modules = new LinkedHashMap<>(targetClasses.size(), 1f);

    for (TargetClass targetClass : targetClasses) {
      Util.addClassToModule(modules, targetClass);
    }

    Set<ClassName> out = new LinkedHashSet<>();
    for (TargetModule targetModule : modules.values()) {
      String moduleClassName = Config.MODULE_NAME;
      TypeSpec generatedClassProviderModule = targetModule.toClassProvidersTypeSpec(moduleClassName);
      out.add(ClassName.get(targetModule.packageName, moduleClassName));
      Util.writeClass(targetModule.packageName, generatedClassProviderModule, filer);
    }

    return out;
  }


  private void writeOriginModules(@NotNull Set<TargetClass> targetOriginClasses, @NotNull Set<ClassName> providerModules, @NotNull Filer filer) {
    HashMap<String, TargetModule> originModules = new HashMap<>(targetOriginClasses.size(), 1f);
    for (TargetClass originClass : targetOriginClasses) {
      Util.addClassToModule(originModules, originClass);
    }

    for (TargetModule targetModule : originModules.values()) {
      TypeSpec typeSpec = targetModule.toOriginClassProvidersTypeSpec(Config.ROOT_MODULE_NAME, providerModules);
      Util.writeClass(targetModule.packageName, typeSpec, filer);
    }
  }

}
