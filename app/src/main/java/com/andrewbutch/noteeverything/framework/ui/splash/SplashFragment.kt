package com.andrewbutch.noteeverything.framework.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.andrewbutch.noteeverything.R
import dagger.android.support.DaggerFragment
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
        viewModel =
            ViewModelProvider(viewModelStore, providerFactory).get(SplashViewModel::class.java)
        viewModel.syncHasBeenExecuted().observe(viewLifecycleOwner) { syncCompleted ->
            if (syncCompleted) {
                NavHostFragment
                    .findNavController(this)
                    .navigate(R.id.action_splashFragment_to_notesFragment)
            }
        }
    }

}