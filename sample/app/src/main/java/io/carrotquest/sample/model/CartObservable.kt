package io.carrotquest.sample.model

import java.util.*

class CartObservable: Observable() {
    override fun hasChanged(): Boolean {
        return true//        return super.hasChanged()
    }
}