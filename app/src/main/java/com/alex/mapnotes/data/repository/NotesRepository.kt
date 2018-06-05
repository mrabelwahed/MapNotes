package com.alex.mapnotes.data.repository

import com.alex.mapnotes.model.Note
import com.google.firebase.database.ValueEventListener

interface NotesRepository {

    fun addNote(note: Note)

    fun getNotes(listener: ValueEventListener)

    fun getNotesByNoteText(text: String, listener: ValueEventListener)

    fun getNotesByUser(userId: String, listener: ValueEventListener)
}