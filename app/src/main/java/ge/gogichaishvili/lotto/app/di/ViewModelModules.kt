package ge.gogichaishvili.lotto.app.di

import ge.gogichaishvili.lotto.main.presentation.viewmodels.GameBoardViewModel
import ge.gogichaishvili.lotto.main.presentation.viewmodels.MainActivityViewModel
import ge.gogichaishvili.lotto.settings.presentation.viewmodels.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ViewModelModules {
    val modules = module {

        viewModel {
            MainActivityViewModel()
        }

        viewModel {
            GameBoardViewModel(
                lottoManager = get(),
                lottoCardManager = get(),
                pref = get(),
                opponentManager = get(),
                opponentCardManager = get()
            )
        }

        viewModel {
            SettingsViewModel(pref = get())
        }

    }
}