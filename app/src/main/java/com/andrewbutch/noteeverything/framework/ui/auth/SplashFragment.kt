package com.andrewbutch.noteeverything.framework.ui.auth

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.andrewbutch.noteeverything.R
import com.andrewbutch.noteeverything.business.data.cache.CacheConstants.CACHE_DATA_NULL
import com.andrewbutch.noteeverything.framework.ui.BaseFragment
import com.andrewbutch.noteeverything.framework.ui.auth.state.AuthStateEvent
import javax.inject.Inject


class SplashFragment : BaseFragment(R.layout.fragment_splash) {

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory

    lateinit var viewModel: AuthViewModel

    override fun onBackPressed() {
        requireActivity().finish()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().run {
            viewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)
        }

        subscribeObservers()
        checkPreviousAuthUser()
    }

    private fun subscribeObservers() {
        viewModel.getStateMessage().observe(viewLifecycleOwner) { stateMessage ->
            if (stateMessage != null) {
                stateMessage.message?.let { message ->
                    if (message.contains(CACHE_DATA_NULL)) {
                        navToLogin()
                    }
                }
                viewModel.removeStateMessage()
            }
        }
    }

    private fun checkPreviousAuthUser() {
        viewModel.setStateEvent(AuthStateEvent.CheckPreviousAuth)
    }

    private fun navToLogin() {
        findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
    }


}