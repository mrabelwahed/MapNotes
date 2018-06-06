package com.alex.mapnotes.login.signup

import com.alex.mapnotes.base.MvpView

interface SignUpView : MvpView {
    fun navigateToMapScreen()

    fun displayError(text: String)
}