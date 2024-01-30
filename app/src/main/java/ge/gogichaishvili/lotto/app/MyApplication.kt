package ge.gogichaishvili.lotto.app

import android.app.Application
import ge.gogichaishvili.lotto.app.di.ManagerModule
import ge.gogichaishvili.lotto.app.di.NetworkModule
import ge.gogichaishvili.lotto.app.di.RepositoryModule
import ge.gogichaishvili.lotto.app.di.ViewModelModules
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
                    ManagerModule.module,
                    NetworkModule.modules
                )
            )
        }
    }
}