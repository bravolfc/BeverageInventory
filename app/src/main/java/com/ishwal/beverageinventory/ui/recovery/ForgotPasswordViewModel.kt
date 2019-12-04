package com.ishwal.beverageinventory.ui.recovery

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import androidx.databinding.library.baseAdapters.BR
import com.ishwal.beverageinventory.utils.Event

class ForgotPasswordViewModel : ViewModel(),
    Observable {

    companion object {
        val TAG = ForgotPasswordViewModel::class.java.simpleName
        const val EMAIL_REGEX = "^[A-Za-z](.*)([@])(.+)(\\.)(.+)"
    }

    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    private var _submitButtonClicked: MutableLiveData<Event<Unit>> = MutableLiveData()

    val submitButtonClickedObserver: LiveData<Event<Unit>>
        get() = _submitButtonClicked

    var emailAddress: String = ""
        @Bindable get
        set(value) {
            field = value
            callbacks.notifyCallbacks(this, BR.emailAddress, null)
        }

    val submitEnable: Boolean
        @Bindable("emailAddress") get() {
            return if (emailAddress.isBlank()) {
                false
            } else {
                EMAIL_REGEX.toRegex().matches(emailAddress)
            }
        }

    fun onSubmitButtonClicked(){
        _submitButtonClicked.value = Event(Unit)
    }

    fun setPasswordResetEmail(auth: FirebaseAuth): Task<Void> {
        return auth.sendPasswordResetEmail(emailAddress)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }
}