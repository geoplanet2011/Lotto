package ge.gogichaishvili.lotto.app.di


import ge.gogichaishvili.lotto.main.helpers.LottoStonesManager
import org.koin.dsl.module

object ManagerModule {
    val module = module {

        single { LottoStonesManager }
    }
}
