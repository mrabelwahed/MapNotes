package com.alex.mapnotes.home

import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.alex.mapnotes.AppExecutors
import com.alex.mapnotes.R
import com.alex.mapnotes.base.ScopedPresenter
import com.alex.mapnotes.data.Result
import com.alex.mapnotes.data.repository.UserRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomePresenter(
    private val appExecutors: AppExecutors,
    private val userRepository: UserRepository
) : ScopedPresenter<HomeView>(), HomeMvpPresenter {

    private var view: HomeView? = null

    override fun onAttach(view: HomeView?) {
        super.onAttach(view)
        this.view = view
    }

    override fun handleNavigationItemClick(itemId: Int): Boolean {
        view?.let { view ->
            when (itemId) {
                R.id.navigation_add_note -> {
                    view.updateMapInteractionMode(true)
                    view.displayAddNote()
                    view.updateNavigationState(BottomSheetBehavior.STATE_COLLAPSED)
                    return true
                }
                R.id.navigation_map -> {
                    view.updateNavigationState(BottomSheetBehavior.STATE_HIDDEN)
                    return true
                }
                R.id.navigation_search_notes -> {
                    view.updateMapInteractionMode(true)
                    view.displaySearchNotes()
                    view.updateNavigationState(BottomSheetBehavior.STATE_EXPANDED)
                    return true
                }
                else -> throw IllegalArgumentException("Unknown itemId")
            }
        }
        return false
    }

    override fun showLocationPermissionRationale() {
        view?.let {
            it.showPermissionExplanationSnackBar()
            it.hideContentWhichRequirePermissions()
        }
    }

    override fun checkUser() {
        view?.let { view ->
            launch(appExecutors.uiContext) {
                val currentUser = userRepository.getCurrentUser()
                when (currentUser) {
                    is Result.Error -> {
                        view.navigateToLoginScreen()
                    }
                }
            }
        }
    }

    override fun signOut() {
        view?.let { view ->
            launch(appExecutors.uiContext) {
                withContext(appExecutors.ioContext) {
                    userRepository.signOut()
                }
                view.navigateToLoginScreen()
            }
        }
    }
    override fun onDetach() {
        super.onDetach()
        this.view = null
    }
}
