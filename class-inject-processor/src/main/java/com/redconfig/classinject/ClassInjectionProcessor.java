package com.redconfig.classinject;

import com.google.auto.service.AutoService;
import com.redconfig.classinject.processors.ModularClassProcessor;
import com.redconfig.classinject.processors.MonolithicClassProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.LinkedHashSet;
import java.util.Set;

/*
 * Rule
 *
 * Maybe we should add the ClassInject at the class declaration?
 * Name choices:
 * AutoProvider
 * AutoClass
 * AutoClassifier
 * AutoClassFactory
 * AutoClassFactoryOrigin
 * AutoProxy
 * ProvidesClass
 * ClassFactory - good? seems misleading at times since its not a factory for other class. Imagine putting this in SomeObjectFactory...
 * AutoClassProvision
 * AutoProvisioned
 * ClassProvisioned
 * InjectClass
 *
 * Plans:
 * Modular mode = module per package. reverse graphed into a module tree (Done)
 * Monolith mode = class must be public. bound onto a root module. significantly lesser generated classes.
 *
 * Modular mode
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
    //We can use multi binding via Dagger but.. there're just 2 mode anyway and barely any constructor dependencies

    if (Config.OPTION_MODE_MONOLITH.equals(modeValue)) {
      return new MonolithicClassProcessor();
    } else {
      return new ModularClassProcessor();
    }
  }

}