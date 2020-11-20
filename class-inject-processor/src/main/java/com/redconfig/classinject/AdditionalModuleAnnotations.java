package com.redconfig.classinject;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class AdditionalModuleAnnotations {

  @NotNull
  public Map<String, AnnotationSpec> loadSupported() {
    Map<String, AnnotationSpec> out = new HashMap<>(10, 1f);
    out.put("hilt", hilt());
    return out;
  }

  @NotNull
  private AnnotationSpec hilt() {

    AnnotationSpec.Builder builder = AnnotationSpec.builder(ClassName.bestGuess("dagger.hilt.InstallIn"));

    HashSet<ClassName> classValues = new HashSet<>();
    classValues.add(ClassName.bestGuess("dagger.hilt.components.SingletonComponent"));

    Util.writeArrayedClassesForKey(builder, "value", classValues);

    return builder.build();
  }

}
