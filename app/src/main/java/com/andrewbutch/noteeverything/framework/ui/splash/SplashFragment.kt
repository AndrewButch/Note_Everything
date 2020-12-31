package com.andrewbutch.noteeverything.framework.ui.splash

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.andrewbutch.noteeverything.R
import com.andrewbutch.noteeverything.framework.session.SessionManager
import com.andrewbutch.noteeverything.framework.ui.PreferenceKeys.Companion.SYNC
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.splash_fragment.*
import javax.inject.Inject


class SplashFragment : DaggerFragment() {
    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory

    private lateinit var viewModel: SplashViewModel

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var preferences: SharedPreferences

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel =
            ViewModelProvider(viewModelStore, providerFactory).get(SplashViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.splash_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get flag from SharedPreferences
        val shouldSync = getSyncPreference()

        // If should not sync - navigate to NotesFragment
        if (shouldSync) {
            viewModel.syncCacheWithNetwork(sessionManager.authUser.value!!)

            startSyncAnimation()

            viewModel.syncHasBeenExecuted().observe(viewLifecycleOwner) { syncCompleted ->
                if (syncCompleted) {
                    navToNotes()
                }
            }
        } else {
            navToNotes()
        }
    }

    private fun startSyncAnimation() {
        val animation: Animation = AnimationUtils.loadAnimation(
            context, R.anim.rotate
        )
        syncImage.startAnimation(animation)
    }

    private fun navToNotes() =
        NavHostFragment
            .findNavController(this)
            .navigate(R.id.action_splashFragment_to_notesFragment)


    private fun getSyncPreference(): Boolean = preferences.getBoolean(SYNC, true)
}