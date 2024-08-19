package com.example.diplom
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.IOException

object JsonHandler {
    private const val JSON_FILE_NAME = "data.json"

    fun saveData (context: Context, dataList: List<ListItem>) {
        val gson = Gson()
        val json = gson.toJson(dataList)
        try {
            val file = File(context.filesDir, JSON_FILE_NAME)
            file.writeText(json)
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("FileERROR", "Failed to save data: ${e.message}")
        }
    }

    fun loadData(context: Context): List<ListItem> {
        val gson = Gson()
        val file = File(context.filesDir, JSON_FILE_NAME)
        if (!file.exists()) {
            Log.w("FileError", "File ${file.absolutePath} does not exist. Returning empty list.")
            return emptyList()
        }
        val json = file.readText()
        Log.d("FileError", "JSON loaded: $json")
        val listType = object : TypeToken<List<ListItem>>() {}.type
        return try {
            gson.fromJson(json, listType)
        } catch (e: Exception) {
            Log.e("FileError", "Failed to parse JSON: ${e.message}")
            emptyList()
        }
    }

    fun clearData(context: Context) {
        val file = File(context.filesDir, JSON_FILE_NAME)
        if (file.exists()) {
            file.delete()
            Log.d("FileError", "Data file deleted: ${file.absolutePath}")
        } else {
            Log.w("FileError", "File ${file.absolutePath} does not exist. Nothing to delete.")
        }
    }
}