package com.plantplanet.android.utils

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class FirebaseHelper {

    fun loadCollection(collection: String, sortOption: String): Task<QuerySnapshot> {
        val db = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()
        db.firestoreSettings = settings
        return db.collection(collection).orderBy(sortOption, Query.Direction.ASCENDING).get()
    }

    fun searchDocument(collection: String, field: String, value: Any): Task<QuerySnapshot> {
        val db = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()
        db.firestoreSettings = settings
        return db.collection(collection).whereEqualTo(field, value).get()
    }

    fun setDocument(collection: String, id: String, data: Any): Task<Void> {
        val db = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()
        db.firestoreSettings = settings
        return db.collection(collection).document(id).set(data)
    }

    fun loadFile(context: Context, reference: String, child: String): FileDownloadTask {
        val storage = FirebaseStorage.getInstance().reference.child(reference).child(child)
        val file = File(context.filesDir, child)
        return storage.getFile(file)
    }
}