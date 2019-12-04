package com.ishwal.beverageinventory.ui.auth.signup

import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.ishwal.beverageinventory.BaseActivity
import com.ishwal.beverageinventory.R
import com.ishwal.beverageinventory.databinding.ActivitySignUpBinding
import com.ishwal.beverageinventory.model.InventoryUser
import com.ishwal.beverageinventory.utils.EventObserver
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named
import java.util.*

class SignUpActivity : BaseActivity() {

    private  val signUpViewModel: SignUpViewModel by viewModel()

    private lateinit var activitySignUpBinding: ActivitySignUpBinding

    private val auth: FirebaseAuth by inject()

    private val databaseReferenceForUser: DatabaseReference by inject(qualifier = named("USER"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySignUpBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        activitySignUpBinding.signUpViewModel = this.signUpViewModel


        signUpViewModel.signUpButtonClickedObserver.observe(this, EventObserver {
            showProgressDialog("Creating User")
            createUser()
        })

        signUpViewModel.dateOfBirthFieldClickedObserver.observe(this, EventObserver{
            showDatePicker()
        })
    }


    private fun showDatePicker(){
        MaterialDialog(this).show {
            title(text = "Select DOB")
            datePicker(requireFutureDate = false,currentDate = signUpViewModel.defaultDate , maxDate = Calendar.getInstance() ) { _, dateTime ->
                // Use dateTime (Calendar)
                signUpViewModel.defaultDate = dateTime

            }
        }
    }

    private fun createUser(){
        signUpViewModel.createUser(auth)
            .addOnCompleteListener {task ->
                hideProgressDialog()
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    if(user != null) {
                        writeNewUser(user.uid, signUpViewModel.fullName,signUpViewModel.emailAddress,signUpViewModel.dateOfBirth)
                        Toast.makeText(baseContext, "createUserWithEmail:success -> ${user.email}",
                            Toast.LENGTH_SHORT).show()
                        showProgressDialog("Sending Verification Email")
                        sendEmailVerification()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, task.exception?.message,
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun writeNewUser(userId: String, fullName: String, email: String, dateOfBirth : String) {
        val user = InventoryUser(userId, fullName, email, dateOfBirth)
        databaseReferenceForUser.child(userId).setValue(user)
    }

    private fun sendEmailVerification() {
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener(this) { task ->
                hideProgressDialog()
                if (task.isSuccessful) {
                    Toast.makeText(baseContext,
                        "Verification email sent to ${user.email} ",
                        Toast.LENGTH_SHORT).show()
                    signUpViewModel.fullName = ""
                    signUpViewModel.emailAddress = ""
                    signUpViewModel.dateOfBirth = ""
                    signUpViewModel.password = ""
                    signUpViewModel.confirmPassword = ""
                    signUpViewModel.isFirstLaunch = true
                    signUpViewModel.defaultDate = Calendar.getInstance()
                    onBackPressed()
                } else {
                    Toast.makeText(baseContext,
                        "Failed to send verification email.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}
