package com.andrewbutch.noteeverything.di.auth

import com.andrewbutch.noteeverything.framework.ui.auth.LoginFragment
import com.andrewbutch.noteeverything.framework.ui.auth.RegistrationFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthFragmentsBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributeRegistrationFragment(): RegistrationFragment
}