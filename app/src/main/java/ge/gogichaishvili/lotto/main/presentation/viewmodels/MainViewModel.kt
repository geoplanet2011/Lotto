package ge.gogichaishvili.lotto.main.presentation.viewmodels

import androidx.lifecycle.ViewModel


class MainViewModel() : ViewModel() {


    fun createBag() {

        //create bag
        val bag = (1..90).toMutableList()

        //shuffle bag
        bag.shuffle()

        //print bag all items
        bag.forEach {
            println(it)
        }

    }

}