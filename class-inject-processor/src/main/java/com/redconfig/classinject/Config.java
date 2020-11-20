package com.redconfig.classinject;

public class Config {
  public static final String PACKAGE = Config.class.getPackage().getName();
  public static final String MODULE_NAME = "GeneratedClassProviderModule";
  public static final String ROOT_MODULE_NAME = "ClassProvidersModule";
  public static final String KEY_OPTION_MODE = PACKAGE + ".mode";
  public static final String OPTION_MODE_MODULAR = "modular";
  public static final String OPTION_MODE_MONOLITH = "monolith";
  public static final String KEY_OPTION_MODULE_ANNOTATIONS = PACKAGE + ".module_annotations";
  public static final String PROVIDER_METHOD_PREFIX = "provides_";
}
