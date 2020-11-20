package com.redconfig.classinject;

import com.squareup.javapoet.AnnotationSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.Set;

public interface ClassProcessor {
  void writeOutput(@NotNull ProcessingEnvironment processingEnv,
                   @NotNull Set<TargetClass> targetClasses,
                   @NotNull Set<TargetClass> targetOriginClasses,
                   @NotNull Set<AnnotationSpec> additionalModuleAnnotations);
}
