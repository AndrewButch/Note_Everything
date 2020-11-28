package com.andrewbutch.noteeverything.framework.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.andrewbutch.noteeverything.R
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.splash_fragment.*
import javax.inject.Inject


class SplashFragment : DaggerFragment() {
    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory

    private lateinit var viewModel: SplashViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.splash_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navToNotes()


//        startSyncAnimation()
//        viewModel =
//            ViewModelProvider(viewModelStore, providerFactory).get(SplashViewModel::class.java)
//        viewModel.syncHasBeenExecuted().observe(viewLifecycleOwner) { syncCompleted ->
//            if (syncCompleted) {
//                navToNotes()
//            }
//        }
    }

    private fun startSyncAnimation() {
        val animation: Animation = AnimationUtils.loadAnimation(
            context, R.anim.rotate
        )
        syncImage.startAnimation(animation)
    }

    private fun navToNotes() {
        NavHostFragment
            .findNavController(this)
            .navigate(R.id.action_splashFragment_to_notesFragment)
    }

}