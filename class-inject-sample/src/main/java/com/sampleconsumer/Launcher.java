package com.sampleconsumer;

import com.redconfig.classinject.InjectClass;
import com.redconfig.classinject.InjectClassOrigin;

public class Launcher {
  public static void main(String[] args) {
    App app = DaggerAppComponent.create().providesApp();
    app.run();
  }
}
