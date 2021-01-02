package com.andrewbutch.noteeverything.framework.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.andrewbutch.noteeverything.R
import com.andrewbutch.noteeverything.business.data.cache.CacheConstants.CACHE_DATA_NULL
import com.andrewbutch.noteeverything.framework.session.SessionManager
import com.andrewbutch.noteeverything.framework.ui.auth.state.AuthStateEvent
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_splash.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class SplashFragment : DaggerFragment() {

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory

    lateinit var viewModel: AuthViewModel

    @Inject
    lateinit var sessionManager: SessionManager

    private var animationComplete: Boolean = false
    private var checkSessionComplete: Boolean = false
    private var slideFromTopAnimation: Animation? = null
    private var slideFromBottomAnimation: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        slideFromTopAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_from_top)
        slideFromBottomAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_from_buttom)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().run {
            viewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)
        }

        subscribeObservers()
        checkPreviousAuthUser()
        startAnimation()
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            viewState.user?.let {
                checkSessionComplete = true
                if (animationComplete) {
                    sessionManager.login(user = it)
                }
            }
        }
        viewModel.getStateMessage().observe(viewLifecycleOwner) { stateMessage ->
            if (stateMessage != null) {
                stateMessage.message?.let { message ->
                    if (message.contains(CACHE_DATA_NULL)) {
                        checkSessionComplete = true
                        if (animationComplete) {
                            navToLogin()
                        }
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

    private fun startAnimation() {
        splashLogo.animation = slideFromTopAnimation
        splashGreeting.animation = slideFromBottomAnimation

        slideFromTopAnimation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                animationComplete = true
                if (checkSessionComplete) {
                    val user = viewModel.getUser()
                    if (user == null) {
                        if (checkSessionComplete) {
                            navToLogin()
                        }
                    } else {
                        sessionManager.login(user = user)

                    }

                }
            }
        })
        splashLogo.startAnimation(slideFromTopAnimation)
        splashGreeting.startAnimation(slideFromBottomAnimation)
    }


}