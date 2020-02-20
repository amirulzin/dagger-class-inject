package com.sampleconsumer;

import com.sampleconsumer.nested.NestedClass;

import javax.inject.Inject;

public class App {
  private final SomeTool tool;

  @Inject
  public App(SomeTool tool) {
    this.tool = tool;
  }

  public void run() {
    tool.doThings();
  }

  public static class SomeTool {

    private Class<NestedClass.SampleTarget> targetClazz;

    @Inject
    public SomeTool(Class<NestedClass.SampleTarget> targetClazz) {
      this.targetClazz = targetClazz;
    }

    public void doThings() {
      System.out.println("Hello from SomeTool with injected " + targetClazz.getCanonicalName());
    }
  }
}
