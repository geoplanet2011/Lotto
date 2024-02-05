package ge.gogichaishvili.lotto.app.di

import ge.gogichaishvili.lotto.app.tools.SharedPreferenceManager
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

object RepositoryModule {

    val module = module {

        single { SharedPreferenceManager(context = androidApplication()) }

    }

}