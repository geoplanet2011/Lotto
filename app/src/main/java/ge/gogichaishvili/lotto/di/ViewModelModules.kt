package ge.gogichaishvili.lotto.di

import ge.gogichaishvili.lotto.main.presentation.viewmodels.MainViewModel

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ViewModelModules {
    val modules = module {


        viewModel {
            MainViewModel()
        }


    }
}