package com.ishwal.beverageinventory.ui.auth.signup

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
import org.joda.time.LocalDate
import org.joda.time.Years
import java.util.*
import java.text.SimpleDateFormat


class SignUpViewModel : ViewModel(),
    Observable {

    var isFirstLaunch = true

    init {
        isFirstLaunch = true
    }

    companion object {
        val TAG = SignUpViewModel::class.java.simpleName
        const val EMAIL_REGEX = "^[A-Za-z](.*)([@])(.+)(\\.)(.+)"
    }

    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    private var _signUpButtonClicked: MutableLiveData<Event<Unit>> = MutableLiveData()
    private var _dateOfBirthFieldClicked: MutableLiveData<Event<Unit>> = MutableLiveData()

    val signUpButtonClickedObserver: LiveData<Event<Unit>>
        get() = _signUpButtonClicked

    val dateOfBirthFieldClickedObserver: LiveData<Event<Unit>>
        get() = _dateOfBirthFieldClicked

    var emailAddress: String = ""
        @Bindable get
        set(value) {
            field = value
            callbacks.notifyCallbacks(this, BR.emailAddress, null)
        }

    var fullName: String = ""
        @Bindable get
        set(value) {
            field = value
            callbacks.notifyCallbacks(this, BR.fullName, null)
        }

    var defaultDate: Calendar = Calendar.getInstance()
        @Bindable get
        set(value) {
            field = value
            callbacks.notifyCallbacks(this, BR.defaultDate, null)
        }

    var dateOfBirth: String = ""
        @Bindable get
        set(value) {
            field = value
            callbacks.notifyCallbacks(this, BR.dateOfBirth, null)
        }

    var password: String = ""
        @Bindable get
        set(value) {
            field = value
            callbacks.notifyCallbacks(this, BR.password, null)
        }

    var confirmPassword: String = ""
        @Bindable get
        set(value) {
            field = value
            callbacks.notifyCallbacks(this, BR.confirmPassword, null)
        }


    val dateOfBirthError: String
        @Bindable("defaultDate") get() {
            val dobDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val period =
                Years.yearsBetween(LocalDate(defaultDate), LocalDate(Calendar.getInstance().time))
            if (isFirstLaunch) {
                isFirstLaunch = false
            } else {
                dateOfBirth = dobDateFormat.format(defaultDate.time)
                return if (period.years >= 21) {
                    ""
                } else {
                    "You must be 21 years old"
                }
            }
            return ""

        }


    val signUpEnable: Boolean
        @Bindable(
            "emailAddress",
            "fullName",
            "dateOfBirth",
            "password",
            "confirmPassword",
            "defaultDate"
        ) get() {

            val period =
                Years.yearsBetween(LocalDate(defaultDate), LocalDate(Calendar.getInstance().time))
            val isUsernameValid =
                if (emailAddress.isBlank()) {
                    false
                } else {
                    EMAIL_REGEX.toRegex().matches(emailAddress)
                }


            val isFullNameValid = !fullName.isBlank()
            val isDateOfBirthValid = !dateOfBirth.isBlank() && period.years >= 18
            val isPasswordValid = !password.isBlank() && password.length >= 6

            val isConfirmPasswordValid = password == confirmPassword

            return isUsernameValid && isFullNameValid && isDateOfBirthValid && isPasswordValid && isConfirmPasswordValid
        }


    fun onSignUpButtonClicked() {
        _signUpButtonClicked.value = Event(Unit)
    }

    fun onDateOfBirthFieldClicked() {
        _dateOfBirthFieldClicked.value = Event(Unit)
    }

    fun createUser(auth: FirebaseAuth): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(emailAddress, password)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }
}