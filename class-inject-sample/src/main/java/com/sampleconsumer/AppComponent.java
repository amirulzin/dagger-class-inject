package com.sampleconsumer;

import com.redconfig.classinject.ClassInjectOrigin;

import dagger.Component;

@ClassInjectOrigin
@Component(modules = ClassProvidersModule.class)
public interface AppComponent {
  App providesApp();
}
