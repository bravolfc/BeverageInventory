package com.ishwal.beverageinventory.ui.changepassword

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.ishwal.beverageinventory.BaseActivity
import com.ishwal.beverageinventory.R
import com.ishwal.beverageinventory.databinding.ActivityChangePasswordBinding
import com.ishwal.beverageinventory.ui.auth.login.LoginActivity
import com.ishwal.beverageinventory.utils.EventObserver
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChangePasswordActivity : BaseActivity() {

    private val changePasswordViewModel: ChangePasswordViewModel by viewModel()

    private val auth: FirebaseAuth by inject()

    private lateinit var activityChangePasswordBinding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityChangePasswordBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_change_password)
        activityChangePasswordBinding.changePasswordViewModel = changePasswordViewModel


        changePasswordViewModel.changePasswordButtonClickedObserver.observe(this, EventObserver {
            val currentUser = auth.currentUser

            if (currentUser != null) {
                showProgressDialog("Changing Password")
                val credentials = EmailAuthProvider
                    .getCredential(currentUser.email!!, changePasswordViewModel.oldPassword)

                currentUser.reauthenticate(credentials)
                    .addOnCompleteListener { reAuthenticateResponse ->
                        if (reAuthenticateResponse.isSuccessful) {
                            currentUser.updatePassword(changePasswordViewModel.newPassword)
                                .addOnCompleteListener { changePasswordResponse ->
                                    if (changePasswordResponse.isSuccessful) {
                                        Toast.makeText(
                                            this,
                                            "Successfully changed password . Please re login",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        hideProgressDialog()
                                        auth.signOut()
                                        val intent = Intent(this, LoginActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(
                                            intent
                                        )
                                        finish()
                                    } else {
                                        hideProgressDialog()
                                        Toast.makeText(
                                            this,
                                            changePasswordResponse.exception?.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        } else {
                            hideProgressDialog()
                            Toast.makeText(
                                this,
                                reAuthenticateResponse.exception?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

            }


        })

    }
}
