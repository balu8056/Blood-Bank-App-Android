package com.example.bloodapp

import android.app.Application
import com.example.bloodapp.data.repository.auth.AuthenticationRepository
import com.example.bloodapp.data.repository.homerep.HomeFragmentRepository
import com.example.bloodapp.ui.auth.AuthFactoryClass
import com.example.bloodapp.ui.home.HomeFactoryClass
import com.example.bloodapp.ui.home.fragments.accounts.AccountsFactory
import com.example.bloodapp.ui.home.fragments.find_donar_loc.FindDonarFactory
import com.example.bloodapp.ui.home.fragments.home.HomeFragmentFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

class KodeinBindingClass : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@KodeinBindingClass))

        bind() from singleton { AuthenticationRepository() }
        bind() from singleton { HomeFragmentRepository() }
        bind() from singleton { AuthFactoryClass(instance()) }
        bind() from singleton { HomeFactoryClass(instance()) }
        bind() from singleton { AccountsFactory(instance()) }
        bind() from singleton { HomeFragmentFactory(instance()) }
        bind() from singleton { FindDonarFactory(instance()) }

    }

}