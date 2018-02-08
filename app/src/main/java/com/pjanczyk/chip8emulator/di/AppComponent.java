package com.pjanczyk.chip8emulator.di;

import android.app.Application;

import com.pjanczyk.chip8emulator.App;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        AppModule.class
})
public interface AppComponent {
    void inject(App app);

    @Component.Builder
    interface Builder {
        @BindsInstance Builder application(Application application);

        AppComponent build();
    }
}
