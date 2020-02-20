package com.sampleconsumer.nested;


import com.redconfig.classinject.ClassInject;

@ClassInject
public class NestedClass {
  @ClassInject
  public interface SampleTarget {

  }
}
