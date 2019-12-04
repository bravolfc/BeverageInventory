package com.ishwal.beverageinventory

import android.app.ProgressDialog
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager


@Suppress("DEPRECATION")
abstract class BaseActivity : AppCompatActivity() {

    //9851030400

    @VisibleForTesting
    val progressDialog by lazy {
        ProgressDialog(this)
    }


    @Suppress("UNUSED")
    fun addFragment(@IdRes layoutResource: Int, fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(layoutResource, fragment)
        transaction.commit()
    }

    /**
     * Replace the current fragment with new one
     *
     * @param layoutResource id of the container
     * @param fragment       to be replaced
     * @param addToBackStack add transaction to backstack
     */
    @Suppress("UNUSED")
    fun replaceFragment(@IdRes layoutResource: Int, fragment: Fragment, addToBackStack: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(layoutResource, fragment)
        if (addToBackStack)
            transaction.addToBackStack(null)
        else
            supportFragmentManager.popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        transaction.commitAllowingStateLoss()
    }


    @Suppress("UNUSED")
    fun addFragment(@IdRes layoutResource: Int, fragment: Fragment, addToBackStack: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(layoutResource, fragment)
        if (addToBackStack)
            transaction.addToBackStack(null)
        else
            supportFragmentManager.popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        transaction.commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    fun showProgressDialog(message : String = getString(R.string.loading)) {
        progressDialog.setMessage(message)
        progressDialog.isIndeterminate = true
        progressDialog.show()
    }

    fun hideProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    public override fun onStop() {
        super.onStop()
        hideProgressDialog()
    }
}