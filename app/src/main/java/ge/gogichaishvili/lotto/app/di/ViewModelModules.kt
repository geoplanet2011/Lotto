package ge.gogichaishvili.lotto.app.di

import ge.gogichaishvili.lotto.login.presentation.viewmodels.LoginViewModel
import ge.gogichaishvili.lotto.main.presentation.viewmodels.CreateRoomViewModel
import ge.gogichaishvili.lotto.main.presentation.viewmodels.DashboardViewModel
import ge.gogichaishvili.lotto.main.presentation.viewmodels.GameBoardViewModel
import ge.gogichaishvili.lotto.main.presentation.viewmodels.HighScoreViewModel
import ge.gogichaishvili.lotto.main.presentation.viewmodels.MainActivityViewModel
import ge.gogichaishvili.lotto.main.presentation.viewmodels.RoomListViewModel
import ge.gogichaishvili.lotto.settings.presentation.viewmodels.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ViewModelModules {
    val modules = module {

        viewModel {
            MainActivityViewModel(pref = get())
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

        viewModel {
            HighScoreViewModel( pref = get(), ratingManager = get() )
        }

        viewModel {
            RoomListViewModel()
        }

        viewModel {
            CreateRoomViewModel()
        }

        viewModel {
            LoginViewModel( pref = get() )
        }

        viewModel {
            DashboardViewModel(
                lottoManager = get(),
                lottoCardManager = get(),
                pref = get()
            )
        }

    }
}