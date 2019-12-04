package com.ishwal.beverageinventory.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.ishwal.beverageinventory.BaseActivity
import com.ishwal.beverageinventory.R
import com.ishwal.beverageinventory.databinding.ActivityLoginBinding
import com.ishwal.beverageinventory.ui.auth.signup.SignUpActivity
import com.ishwal.beverageinventory.ui.dashboard.DashboardActivity
import com.ishwal.beverageinventory.ui.recovery.ForgotPasswordActivity
import com.ishwal.beverageinventory.utils.EventObserver
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class LoginActivity : BaseActivity() {

    private val loginViewModel: LoginViewModel by viewModel()

    private val auth: FirebaseAuth by inject()

    private lateinit var activityLoginBinding: ActivityLoginBinding

    companion object {
        val TAG = LoginActivity::class.java.simpleName
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        activityLoginBinding.loginViewModel = this.loginViewModel

        loginViewModel.loginButtonClickedObserver.observe(this, EventObserver {
            showProgressDialog("Signing In")
            signIn()
        })

        loginViewModel.forgotPasswordButtonClickedObserver.observe(this, EventObserver {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        })

        loginViewModel.signUpButtonClickedObserver.observe(this, EventObserver {
            startActivity(Intent(this, SignUpActivity::class.java))
        })
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                Toast.makeText(
                    this,
                    "Email Verified and already Login ${currentUser.email}",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Email not Verified  and already Login ${currentUser.email}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }

    private fun signIn() {
        loginViewModel.signIn(auth)
            .addOnCompleteListener {
                hideProgressDialog()
                if (it.isSuccessful) {
                    val currentUser = auth.currentUser
                    updateUI(currentUser)
                } else {
                    // If sign in fails, display a message to the user.
                    Timber.w( "signInWithEmail:failure ${it.exception}")
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }
}
