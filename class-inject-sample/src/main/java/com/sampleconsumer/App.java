package com.sampleconsumer;

import com.redconfig.classinject.InjectClass;
import com.redconfig.classinject.InjectClassOrigin;
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

  @InjectClass
  public interface SampleTarget {

  }

  public static class SomeTool {

    private Class<NestedClass> targetClazz;

    @Inject
    public SomeTool(Class<NestedClass> targetClazz) {
      this.targetClazz = targetClazz;
    }

    public void doThings() {
      System.out.println("Hello from SomeTool with injected " + targetClazz.getCanonicalName());
    }
  }
}
