package com.redconfig.classinject;

import com.squareup.javapoet.*;
import dagger.Module;
import dagger.Provides;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Util {

  public static void writeArrayedClassesForKey(@NotNull AnnotationSpec.Builder builder, @NotNull String key, @NotNull Set<ClassName> classNames) {
    if (classNames.size() > 0) {
      CodeBlock.Builder arrayBlockBuilder = CodeBlock.builder().beginControlFlow("");
      Iterator<ClassName> iterator = classNames.iterator();
      while (iterator.hasNext()) {
        ClassName className = iterator.next();
        String itemFormat;
        if (iterator.hasNext()) {
          itemFormat = "$T.class,\n";
        } else {
          itemFormat = "$T.class";
        }
        arrayBlockBuilder.add(itemFormat, className);
      }
      builder.addMember(key, arrayBlockBuilder.endControlFlow().build());
    }
  }

  @NotNull
  public static AnnotationSpec createDaggerModuleAnnotationForIncludingModules(@NotNull Set<ClassName> includedModulesClassNames) {
    AnnotationSpec.Builder builder = AnnotationSpec.builder(Module.class);
    writeArrayedClassesForKey(builder, "includes", includedModulesClassNames);
    return builder.build();
  }

  @NotNull
  public static MethodSpec writeDaggerClassProviderMethod(@NotNull TargetClass targetClass, boolean qualified) {
    ClassName className = qualified ? ClassName.bestGuess(targetClass.qualifiedClassName)
      : ClassName.get(targetClass.qualifiedPackageName, targetClass.semiQualifiedClassName);

    String methodName = qualified ? targetClass.qualifiedProviderMethodName : targetClass.simpleProviderMethodName;
    return MethodSpec.methodBuilder(methodName)
      .addAnnotation(Provides.class)
      .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
      .returns(ParameterizedTypeName.get(ClassName.get(Class.class), className))
      .addCode(
        CodeBlock.builder()
          .addStatement(CodeBlock.builder()
            .add("return $T.class", className)
            .build())
          .build())
      .build();
  }

  public static void addClassToModule(@NotNull HashMap<String, TargetModule> modules, @NotNull TargetClass targetClass) {
    TargetModule targetModule = modules.computeIfAbsent(targetClass.qualifiedPackageName, TargetModule::new);
    targetModule.targetClasses.add(targetClass);
  }

  public static void writeClass(@NotNull String packageName, @NotNull TypeSpec typeSpec, @NotNull Filer filer) {
    JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
      .build();
    try {
      javaFile.writeTo(filer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
