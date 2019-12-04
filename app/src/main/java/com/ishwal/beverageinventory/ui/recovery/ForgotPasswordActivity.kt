package com.ishwal.beverageinventory.ui.recovery

import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.ishwal.beverageinventory.BaseActivity
import com.ishwal.beverageinventory.R
import com.ishwal.beverageinventory.databinding.ActivityForgotPasswordBinding
import com.ishwal.beverageinventory.utils.EventObserver
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ForgotPasswordActivity : BaseActivity() {

    private val forgotPasswordViewModel: ForgotPasswordViewModel by viewModel()

    private lateinit var activityForgotPasswordBinding: ActivityForgotPasswordBinding

    private val auth: FirebaseAuth by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        activityForgotPasswordBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        activityForgotPasswordBinding.forgotPasswordViewModel = this.forgotPasswordViewModel

        forgotPasswordViewModel.submitButtonClickedObserver.observe(this, EventObserver{
            showProgressDialog("Sending confirmation Email")
            passwordReset()
        })

    }

    private fun passwordReset() {
        forgotPasswordViewModel.setPasswordResetEmail(auth)
            .addOnCompleteListener { task ->
                hideProgressDialog()
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Email has been sent to ${forgotPasswordViewModel.emailAddress}",
                        Toast.LENGTH_SHORT
                    ).show()
                    forgotPasswordViewModel.emailAddress = ""
                }
                else{
                    Toast.makeText(
                        this,
                        "${task.exception?.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
