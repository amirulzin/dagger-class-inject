package com.redconfig.classinject;

import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.util.Objects;

public class TargetClass {
  public final boolean isPublic;
  public final String qualifiedPackageName;
  public final String simplePackageName;
  public final String qualifiedClassName;
  public final String semiQualifiedClassName;
  public final String qualifiedProviderMethodName;
  public final String simpleProviderMethodName;

  public TargetClass(
    boolean isPublic,
    @NotNull String qualifiedPackageName,
    @NotNull String simplePackageName,
    @NotNull String qualifiedClassName,
    @NotNull String semiQualifiedClassName,
    @NotNull String qualifiedProviderMethodName,
    @NotNull String simpleProviderMethodName) {
    this.isPublic = isPublic;
    this.qualifiedPackageName = qualifiedPackageName;
    this.simplePackageName = simplePackageName;
    this.qualifiedClassName = qualifiedClassName;
    this.semiQualifiedClassName = semiQualifiedClassName;
    this.qualifiedProviderMethodName = qualifiedProviderMethodName;
    this.simpleProviderMethodName = simpleProviderMethodName;
  }

  @NotNull
  public static TargetClass from(@NotNull TypeElement typeElement, @NotNull ProcessingEnvironment processingEnv) {

    boolean isPublic = typeElement.getModifiers().contains(Modifier.PUBLIC);

    PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(typeElement);
    String simplePackageName = packageElement.getSimpleName().toString();
    String qualifiedPackageName = packageElement.getQualifiedName().toString();

    String qualifiedClassName = typeElement.getQualifiedName().toString();
    String semiQualifiedClassName = qualifiedClassName.replace(qualifiedPackageName + ".", ""); //remove package from qualified class name

    String methodPrefix = "provides_";
    String qualifiedProviderMethodName = methodPrefix + qualifiedClassName.replace(".", "_");  //replace any leftover . (i.e. inner classes);
    String simpleProviderMethodName = methodPrefix + semiQualifiedClassName.replace(".", "_");  //replace any leftover . (i.e. inner classes);
    return new TargetClass(
      isPublic,
      qualifiedPackageName,
      simplePackageName,
      qualifiedClassName,
      semiQualifiedClassName,
      qualifiedProviderMethodName,
      simpleProviderMethodName
    );
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TargetClass that = (TargetClass) o;
    return isPublic == that.isPublic &&
      qualifiedPackageName.equals(that.qualifiedPackageName) &&
      simplePackageName.equals(that.simplePackageName) &&
      qualifiedClassName.equals(that.qualifiedClassName) &&
      semiQualifiedClassName.equals(that.semiQualifiedClassName) &&
      qualifiedProviderMethodName.equals(that.qualifiedProviderMethodName) &&
      simpleProviderMethodName.equals(that.simpleProviderMethodName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(isPublic, qualifiedPackageName, simplePackageName, qualifiedClassName, semiQualifiedClassName, qualifiedProviderMethodName, simpleProviderMethodName);
  }
}
