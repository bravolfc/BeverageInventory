package com.ishwal.beverageinventory.ui.auth.login

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import androidx.databinding.library.baseAdapters.BR
import com.ishwal.beverageinventory.utils.Event

class LoginViewModel : ViewModel(),
    Observable {

    companion object {
        val TAG = LoginViewModel::class.java.simpleName
        const val EMAIL_REGEX = "^[A-Za-z](.*)([@])(.+)(\\.)(.+)"
    }

    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    private var _loginButtonClicked: MutableLiveData<Event<Unit>> = MutableLiveData()
    private var _forgotPasswordButtonClicked: MutableLiveData<Event<Unit>> = MutableLiveData()
    private var _signUpButtonClicked: MutableLiveData<Event<Unit>> = MutableLiveData()

    val loginButtonClickedObserver: LiveData<Event<Unit>>
        get() = _loginButtonClicked

    val forgotPasswordButtonClickedObserver: LiveData<Event<Unit>>
        get() = _forgotPasswordButtonClicked

    val signUpButtonClickedObserver: LiveData<Event<Unit>>
        get() = _signUpButtonClicked


    var emailAddress: String = ""
        @Bindable get
        set(value) {
            field = value
            callbacks.notifyCallbacks(this, BR.emailAddress, null)
        }

    var password: String = ""
        @Bindable get
        set(value) {
            field = value
            callbacks.notifyCallbacks(this, BR.password, null)
        }

    val loginEnable: Boolean
        @Bindable("emailAddress", "password") get() {

            val isUsernameValid =
                if (emailAddress.isBlank()) {
                    false
                } else {
                    EMAIL_REGEX.toRegex().matches(emailAddress)
                }


            val isPasswordValid = !password.isBlank()

            return isUsernameValid && isPasswordValid
        }


    fun onLoginClicked() {
        _loginButtonClicked.value = Event(Unit)
    }

    fun onForgotPasswordClicked() {
        _forgotPasswordButtonClicked.value = Event(Unit)
    }

    fun onSignUpClicked() {
        _signUpButtonClicked.value = Event(Unit)
    }

    fun signIn(auth: FirebaseAuth): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(emailAddress, password)
    }


    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

}