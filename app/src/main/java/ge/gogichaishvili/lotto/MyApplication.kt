package ge.gogichaishvili.lotto

import android.app.Application
import ge.gogichaishvili.lotto.di.NetworkModule
import ge.gogichaishvili.lotto.di.RepositoryModule
import ge.gogichaishvili.lotto.di.UseCaseModule
import ge.gogichaishvili.lotto.di.ViewModelModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

//Koin
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@MyApplication)
            modules(
                listOf(
                    ViewModelModules.modules,
                    RepositoryModule.module,
                    UseCaseModule.module,
                    NetworkModule.modules
                )
            )
        }
    }
}