package com.sampleconsumer;

public class Launcher {
  public static void main(String[] args) {
    App app = DaggerAppComponent.create().providesApp();
    app.run();
  }
}
