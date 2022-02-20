package ge.gogichaishvili.lotto.app.di

import ge.gogichaishvili.lotto.main.presentation.viewmodels.MainViewModel
import ge.gogichaishvili.lotto.settings.presentation.viewmodels.SettingsViewModel

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ViewModelModules {
    val modules = module {


        viewModel {
            MainViewModel()
        }

        viewModel {
            SettingsViewModel(pref = get())
        }

    }
}