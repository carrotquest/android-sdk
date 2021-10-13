package io.carrotquest.sample.auth.view

interface IAuthView {
    fun close()
    fun showName(name: String)
    fun showEmail(email: String)
    fun showPhone(phone: String)
}