package io.carrotquest.sample.input_data.view

interface IInputDataView {
    fun showConnectError()
    fun showFieldsIsEmptyError()
    fun openMainActivity()
    fun close()

    fun showProgress()
    fun hideProgress()
}