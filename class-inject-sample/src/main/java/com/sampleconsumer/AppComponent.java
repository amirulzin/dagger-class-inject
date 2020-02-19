package com.sampleconsumer;

import com.redconfig.classinject.InjectClassOrigin;

import dagger.Component;

@InjectClassOrigin
@Component(modules = ClassProvidersModule.class)
public interface AppComponent {
  App providesApp();
}
