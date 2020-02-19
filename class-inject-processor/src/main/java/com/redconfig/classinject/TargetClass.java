package com.redconfig.classinject;

import com.squareup.javapoet.ClassName;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class TargetClass {
  public final boolean isPublic;
  public final String qualifiedPackageName;
  public final String qualifiedProviderMethodName;
  public final String simpleProviderMethodName;
  public final ClassName className;
  public final TypeElement originatingTypeElement;

  public TargetClass(
    boolean isPublic,
    @NotNull String qualifiedPackageName,
    @NotNull String qualifiedProviderMethodName,
    @NotNull String simpleProviderMethodName,
    @NotNull ClassName className,
    @NotNull TypeElement originatingTypeElement) {
    this.isPublic = isPublic;
    this.qualifiedPackageName = qualifiedPackageName;
    this.qualifiedProviderMethodName = qualifiedProviderMethodName;
    this.simpleProviderMethodName = simpleProviderMethodName;
    this.className = className;
    this.originatingTypeElement = originatingTypeElement;
  }

  @NotNull
  public static TargetClass from(@NotNull TypeElement typeElement, @NotNull ProcessingEnvironment processingEnv) {

    boolean isPublic = typeElement.getModifiers().contains(Modifier.PUBLIC);

    ClassName className = ClassName.get(typeElement);
    String qualifiedPackageName = className.packageName();

    String qualifiedClassName = className.canonicalName();
    String semiQualifiedClassName = String.join(".", className.simpleNames());

    String qualifiedProviderMethodName = Config.PROVIDER_METHOD_PREFIX + qualifiedClassName.replace(".", "_");
    String simpleProviderMethodName = Config.PROVIDER_METHOD_PREFIX + semiQualifiedClassName.replace(".", "_");  //replace any leftover . (i.e. inner classes);

    return new TargetClass(
      isPublic,
      qualifiedPackageName,
      qualifiedProviderMethodName,
      simpleProviderMethodName,
      className,
      typeElement);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TargetClass that = (TargetClass) o;
    return isPublic == that.isPublic &&
      qualifiedPackageName.equals(that.qualifiedPackageName) &&
      qualifiedProviderMethodName.equals(that.qualifiedProviderMethodName) &&
      simpleProviderMethodName.equals(that.simpleProviderMethodName) &&
      className.equals(that.className) &&
      originatingTypeElement.equals(that.originatingTypeElement);
  }

  @Override
  public int hashCode() {
    return Objects.hash(isPublic, qualifiedPackageName, qualifiedProviderMethodName, simpleProviderMethodName, className, originatingTypeElement);
  }
}
