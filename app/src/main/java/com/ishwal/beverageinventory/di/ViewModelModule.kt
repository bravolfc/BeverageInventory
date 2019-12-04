package com.ishwal.beverageinventory.di

import com.ishwal.beverageinventory.ui.addinventory.AddInventoryItemViewModel
import com.ishwal.beverageinventory.ui.auth.login.LoginViewModel
import com.ishwal.beverageinventory.ui.auth.signup.SignUpViewModel
import com.ishwal.beverageinventory.ui.changepassword.ChangePasswordViewModel
import com.ishwal.beverageinventory.ui.dashboard.DashboardViewModel
import com.ishwal.beverageinventory.ui.inventory.InventoryViewModel
import com.ishwal.beverageinventory.ui.recovery.ForgotPasswordViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {

    viewModel {
        LoginViewModel()
    }

    viewModel {
        SignUpViewModel()
    }

    viewModel {
        ForgotPasswordViewModel()
    }

    viewModel {
        DashboardViewModel()
    }

    viewModel {
        AddInventoryItemViewModel()
    }

    viewModel {
        InventoryViewModel()
    }

    viewModel {
        ChangePasswordViewModel()
    }
}