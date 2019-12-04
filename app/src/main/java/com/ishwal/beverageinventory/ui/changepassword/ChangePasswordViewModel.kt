package com.ishwal.beverageinventory.ui.changepassword

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ishwal.beverageinventory.utils.Event

class ChangePasswordViewModel: ViewModel(),
    Observable {

    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    private var _changePasswordButtonClicked: MutableLiveData<Event<Unit>> = MutableLiveData()

    val changePasswordButtonClickedObserver: LiveData<Event<Unit>>
        get() = _changePasswordButtonClicked


    var oldPassword: String = ""
        @Bindable get
        set(value) {
            field = value
            callbacks.notifyCallbacks(this, BR.oldPassword, null)
        }

    var newPassword: String = ""
        @Bindable get
        set(value) {
            field = value
            callbacks.notifyCallbacks(this, BR.newPassword, null)
        }

    var confirmChangePassword: String = ""
        @Bindable get
        set(value) {
            field = value
            callbacks.notifyCallbacks(this, BR.confirmChangePassword, null)
        }

    val changePassowrdEnable: Boolean
        @Bindable(
            "oldPassword",
            "newPassword",
            "confirmChangePassword"
        ) get() {

            val isOldPasswordValid = !oldPassword.isBlank() && oldPassword.length >= 6
            val isNewPasswordValid = !newPassword.isBlank() && newPassword.length >= 6

            val  isConfirmChangePasswordValid = newPassword == confirmChangePassword

            return isOldPasswordValid && isNewPasswordValid && isConfirmChangePasswordValid
        }


    fun changePassword(){
        _changePasswordButtonClicked.value = Event(Unit)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

}