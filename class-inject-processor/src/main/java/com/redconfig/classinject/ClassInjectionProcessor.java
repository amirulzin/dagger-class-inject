package com.redconfig.classinject;

import com.google.auto.service.AutoService;
import com.redconfig.classinject.processors.ModularClassProcessor;
import com.redconfig.classinject.processors.MonolithicClassProcessor;
import com.squareup.javapoet.AnnotationSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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
    set.add(Config.KEY_OPTION_MODE);
    return set;
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> set = new LinkedHashSet<>();
    set.add(ClassInject.class.getCanonicalName());
    set.add(ClassInjectOrigin.class.getCanonicalName());
    return set;
  }

  @Override
  public boolean process(@NotNull Set<? extends TypeElement> annotations, @NotNull RoundEnvironment roundEnv) {
    Messager messager = processingEnv.getMessager();

    Set<TargetClass> targetClasses = new LinkedHashSet<>(1024, 1f);
    Set<TargetClass> targetOriginClasses = new LinkedHashSet<>(16, 1f);

    for (TypeElement annotation : annotations) {
      Name annotationQualifiedName = annotation.getQualifiedName();

      if (annotationQualifiedName.contentEquals(ClassInject.class.getCanonicalName())) {
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
      } else if (annotationQualifiedName.contentEquals(ClassInjectOrigin.class.getCanonicalName())) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
        for (Element element : elements) {
          if (element instanceof TypeElement) {
            TypeElement typeElement = (TypeElement) element;
            targetOriginClasses.add(TargetClass.from(typeElement, processingEnv));
          }
        }
      }
    }

    Map<String, String> envOptions = processingEnv.getOptions();

    ClassProcessor processor = getProcessorForMode(envOptions);

    Set<AnnotationSpec> additionalModuleAnnotations = loadAdditionalModuleAnnotations(envOptions);

    processor.writeOutput(processingEnv, targetClasses, targetOriginClasses, additionalModuleAnnotations);
    return true;
  }

  @NotNull
  private ClassProcessor getProcessorForMode(@NotNull Map<String, String> envOptions) {
    String modeValue = envOptions.get(Config.KEY_OPTION_MODE);
    if (modeValue != null && Config.OPTION_MODE_MONOLITH.contentEquals(modeValue)) {
      return new MonolithicClassProcessor();
    } else {
      return new ModularClassProcessor();
    }
  }

  @NotNull
  private Set<AnnotationSpec> loadAdditionalModuleAnnotations(@NotNull Map<String, String> envOptions) {
    String annotationPairString = envOptions.get(Config.KEY_OPTION_MODULE_ANNOTATIONS);

    Set<AnnotationSpec> out = new HashSet<>(10, 1f);
    if (annotationPairString != null) {

      String[] keys = annotationPairString.split(",");
      AdditionalModuleAnnotations annotations = new AdditionalModuleAnnotations();
      Map<String, AnnotationSpec> supportedSpecs = annotations.loadSupported();

      for (String key : keys) {
        AnnotationSpec spec = supportedSpecs.get(key);
        if (spec != null) {
          out.add(spec);
        }
      }

    }
    return out;
  }

}