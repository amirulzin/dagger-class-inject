package com.redconfig.classinject;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;

public interface ClassProcessor {
  void writeOutput(@NotNull ProcessingEnvironment processingEnv,
                   @NotNull Set<TargetClass> targetClasses,
                   @NotNull Set<TargetClass> targetOriginClasses);
}
