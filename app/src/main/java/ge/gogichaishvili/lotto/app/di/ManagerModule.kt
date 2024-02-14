package ge.gogichaishvili.lotto.app.di

import ge.gogichaishvili.lotto.main.helpers.LottoCardManager
import ge.gogichaishvili.lotto.main.helpers.LottoStonesManager
import ge.gogichaishvili.lotto.main.helpers.OpponentCardManager
import ge.gogichaishvili.lotto.main.helpers.OpponentManager
import org.koin.dsl.module

object ManagerModule {
    val module = module {

        single { LottoStonesManager }

        single { LottoCardManager }

        single { OpponentManager() }

        single { OpponentCardManager }

    }
}
