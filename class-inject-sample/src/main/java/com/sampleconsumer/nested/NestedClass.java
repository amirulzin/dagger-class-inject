package com.sampleconsumer.nested;


import com.redconfig.classinject.InjectClass;

@InjectClass
public class NestedClass {
  @InjectClass
  public interface SampleTarget {

  }

}
