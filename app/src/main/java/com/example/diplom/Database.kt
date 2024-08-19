package com.example.diplom

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class Database (private val context: Context) {
    private val db = FirebaseFirestore.getInstance()

    fun fetchDataFromFirestoreArray(string: String, callback: (List<ListItem>) -> Unit) {
        val listArray = ArrayList<ListItem>()
        var sort: String

        db.collection(string)
            .get()
            .addOnSuccessListener { results ->
                for (document in results) {
                    val image = document.get("Image").toString()
                    sort = if (string == "Sorts") {
                        document.get("Name").toString()
                    } else {
                        document.get("Name").toString()
                    }
                    val flora = document.get("Type").toString()
                    listArray.add(ListItem(image, sort, flora))
                }
                callback.invoke(listArray)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "EXCEPTION: $exception", Toast.LENGTH_LONG)
                    .show()
                callback.invoke(emptyList())
            }
    }

    suspend fun fetchDataFromFirestore(name: String, type: String, answer: String): String {
        return suspendCancellableCoroutine { continuation ->
            var info = ""

            db.collection(type)
                .get()
                .addOnSuccessListener { results ->
                    for (document in results) {
                        if (document.get("Name").toString() == name) {
                            info = document.get(answer).toString()
                            break
                        }
                    }
                    continuation.resume(info)
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "EXCEPTION: $exception", Toast.LENGTH_LONG)
                        .show()
                    continuation.resume(info)
                }
        }
    }

    fun fetchDataFromFirestore(TFLResult: String, callback: (String) -> Unit) {
        db.collection("Sorts").whereEqualTo("Name", TFLResult).get().addOnSuccessListener { results ->
            if (!results.isEmpty) {
                for (document in results) {
                    if (document.get("Name") == TFLResult) {
                        callback(document.get("Type").toString())
                    }
                }
            } else {
                db.collection("Diseases").whereEqualTo("Name", TFLResult).get().addOnSuccessListener { results ->
                    if (!results.isEmpty) {
                        for (document in results) {
                            if (document.get("Name") == TFLResult) {
                                callback(document.get("Type").toString())
                            }
                        }
                    } else {
                        Toast.makeText(context, "Сорт/болезнь не найден", Toast.LENGTH_SHORT).show()
                    }
                }
                    .addOnFailureListener { exception ->
                        Toast.makeText(context, "Ошибка получения данных: $exception", Toast.LENGTH_LONG).show()
                    }
            }
        }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Ошибка получения данных: $exception", Toast.LENGTH_LONG).show()
            }
    }
}