package com.alex.mapnotes.robots

import com.alex.mapnotes.MockTest
import com.alex.mapnotes.data.Result
import com.alex.mapnotes.model.AuthUser
import com.alex.mapnotes.model.Note
import io.mockk.coEvery
import io.mockk.every
import java.lang.Exception

fun prepare(scope: MockTest, func: PreparationRobot.() -> Unit) = PreparationRobot(scope).apply { func() }

class PreparationRobot(private val scope: MockTest) {

    fun mockLocationProvider() {
        val provider = scope.locationProvider
        every { provider.startLocationUpdates() } answers { nothing }
        every { provider.stopLocationUpdates() } answers { nothing }
        every { provider.addUpdatableLocationListener(any()) } answers { nothing }
        every { provider.isLocationAvailable() } returns false
    }

    fun mockSuccessfulSignIn(email: String, password: String) {
        val userRepository = scope.userRepository
        val authUser = AuthUser("111111")
        coEvery { userRepository.signIn(email, password) } returns Result.Success(authUser)
        coEvery { userRepository.getCurrentUser() } returns Result.Success(authUser)
    }

    fun mockUnsuccessfulSignInWithException() {
        val userRepository = scope.userRepository
        coEvery { userRepository.signIn(any(), any()) } returns Result.Error(Exception("SignIn error"))
    }

    fun mockSearchUserId(userId: String) {
        val userRepository = scope.userRepository
        coEvery { userRepository.getUserIdFromHumanReadableName(any()) } returns Result.Success(userId)
    }

    fun mockErrorDuringLoadingUserNames() {
        val userRepository = scope.userRepository
        coEvery { userRepository.getUserIdFromHumanReadableName(any()) } returns Result.Error(RuntimeException())
    }

    fun mockLoadingEmptyListOfNotes() {
        val notesRepository = scope.notesRepository
        coEvery { notesRepository.getNotes(any()) } returns Result.Success(listOf())
    }

    fun mockLoadingEmptyListOfNotesByNoteText() {
        val notesRepository = scope.notesRepository
        coEvery { notesRepository.getNotesByNoteText(any(), any()) } returns Result.Error(RuntimeException())
    }

    fun mockLoadingListOfNotes(notes: List<Note>) {
        val notesRepository = scope.notesRepository
        coEvery { notesRepository.getNotes(any()) } returns Result.Success(notes)
    }

    fun mockSearchNoteByAnyText(notes: List<Note>) {
        val notesRepository = scope.notesRepository
        coEvery { notesRepository.getNotesByNoteText(any(), any()) } returns Result.Success(notes)
    }

    fun mockSearchNoteByAnyUser(notes: List<Note>) {
        val notesRepository = scope.notesRepository
        coEvery { notesRepository.getNotesByUser(any(), any()) } returns Result.Success(notes)
    }
}