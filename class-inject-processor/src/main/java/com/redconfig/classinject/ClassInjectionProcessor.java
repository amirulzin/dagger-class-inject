package com.redconfig.classinject;

import com.google.auto.service.AutoService;
import com.redconfig.classinject.processors.ModularClassProcessor;
import com.redconfig.classinject.processors.MonolithicClassProcessor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/*
 * Modular mode = 1 Module per package. Reverse graphed into a module tree
 * Monolith mode = Public classes only. Bound onto a root module. This significantly generate less classes.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class ClassInjectionProcessor extends AbstractProcessor {

  @Override
  public Set<String> getSupportedOptions() {
    Set<String> set = new LinkedHashSet<>();
    set.add(Config.OPTION_MODE);
    return set;
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> set = new LinkedHashSet<>();
    set.add(InjectClass.class.getCanonicalName());
    set.add(InjectClassOrigin.class.getCanonicalName());
    return set;
  }

  @Override
  public boolean process(@NotNull Set<? extends TypeElement> annotations, @NotNull RoundEnvironment roundEnv) {
    Messager messager = processingEnv.getMessager();

    Set<TargetClass> targetClasses = new LinkedHashSet<>(1024, 1f);
    Set<TargetClass> targetOriginClasses = new LinkedHashSet<>(16, 1f);

    for (TypeElement annotation : annotations) {
      Name annotationQualifiedName = annotation.getQualifiedName();

      if (annotationQualifiedName.contentEquals(InjectClass.class.getCanonicalName())) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
        for (Element element : elements) {
          if (element instanceof TypeElement) {
            TypeElement typeElement = (TypeElement) element;
            if (typeElement.getModifiers().contains(Modifier.PRIVATE)) {
              messager.printMessage(Diagnostic.Kind.WARNING, String.format("@%s can't be used for private classes. Please elevate %s class visibility to public or package local.", annotation.getSimpleName(), typeElement.getSimpleName()), element);
              continue;
            }
            targetClasses.add(TargetClass.from(typeElement, processingEnv));
          }
        }
      } else if (annotationQualifiedName.contentEquals(InjectClassOrigin.class.getCanonicalName())) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
        for (Element element : elements) {
          if (element instanceof TypeElement) {
            TypeElement typeElement = (TypeElement) element;
            targetOriginClasses.add(TargetClass.from(typeElement, processingEnv));
          }
        }
      }
    }

    ClassProcessor processor = getProcessorForMode(processingEnv.getOptions().get(Config.OPTION_MODE));
    processor.writeOutput(processingEnv, targetClasses, targetOriginClasses);
    return true;
  }

  @NotNull
  private ClassProcessor getProcessorForMode(@Nullable String modeValue) {
    if (modeValue != null && Config.OPTION_MODE_MONOLITH.contentEquals(modeValue)) {
      return new MonolithicClassProcessor();
    } else {
      return new ModularClassProcessor();
    }
  }

}