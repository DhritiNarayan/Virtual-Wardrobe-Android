
package com.example.virtualwardrobe

import android.content.Context
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object WardrobeStorage {

    private const val PREF_NAME = "WardrobeData"
    private const val KEY_CLOTHES = "clothes"
    private const val KEY_OUTFITS = "outfits"

    // Get storage preferences
    private fun getPreferences(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // Save an image permanently
    fun addClothing(
        context: Context,
        imageUri: Uri,
        category: String = "Uncategorized"
    ): ClothingItem? {

        return try {

            val id = UUID.randomUUID().toString()
            val fileName = "$id.jpg"

            val imageFile = File(context.filesDir, fileName)

            context.contentResolver.openInputStream(imageUri)?.use { input ->
                FileOutputStream(imageFile).use { output ->
                    input.copyTo(output)
                }
            } ?: return null

            val preferences = getPreferences(context)

            val savedItems = preferences
                .getStringSet(KEY_CLOTHES, emptySet())
                ?.toMutableSet() ?: mutableSetOf()

            savedItems.add("$fileName|$category")

            preferences.edit()
                .putStringSet(KEY_CLOTHES, savedItems)
                .apply()

            ClothingItem(
                id = id,
                imagePath = fileName,
                category = category
            )

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Retrieve all saved clothes
    fun getAllClothes(context: Context): List<ClothingItem> {

        val savedItems = getPreferences(context)
            .getStringSet(KEY_CLOTHES, emptySet())
            ?: emptySet()

        return savedItems.mapNotNull { entry ->

            val parts = entry.split("|", limit = 2)

            if (parts.size == 2) {

                ClothingItem(
                    id = parts[0].removeSuffix(".jpg"),
                    imagePath = parts[0],
                    category = parts[1]
                )

            } else {
                null
            }

        }.filter {
            File(context.filesDir, it.imagePath).exists()
        }
    }

    // Delete a clothing item
    fun deleteClothing(
        context: Context,
        item: ClothingItem
    ) {

        File(context.filesDir, item.imagePath).delete()

        val preferences = getPreferences(context)

        val savedItems = preferences
            .getStringSet(KEY_CLOTHES, emptySet())
            ?.toMutableSet() ?: mutableSetOf()

        savedItems.remove("${item.imagePath}|${item.category}")

        preferences.edit()
            .putStringSet(KEY_CLOTHES, savedItems)
            .apply()
    }

    // Save an outfit
    fun saveOutfit(
        context: Context,
        name: String,
        clothingIds: List<String>
    ): Boolean {

        return try {

            val outfitId = UUID.randomUUID().toString()

            val outfitJson = JSONObject().apply {
                put("id", outfitId)
                put("name", name)

                val idsArray = JSONArray()

                clothingIds.forEach { clothingId ->
                    idsArray.put(clothingId)
                }

                put("clothingIds", idsArray)
            }

            val preferences = getPreferences(context)

            val savedOutfits = preferences
                .getStringSet(KEY_OUTFITS, emptySet())
                ?.toMutableSet() ?: mutableSetOf()

            savedOutfits.add(outfitJson.toString())

            preferences.edit()
                .putStringSet(KEY_OUTFITS, savedOutfits)
                .apply()

            true

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Retrieve all saved outfits
    fun getSavedOutfits(context: Context): List<SavedOutfit> {

        val savedOutfits = getPreferences(context)
            .getStringSet(KEY_OUTFITS, emptySet())
            ?: emptySet()

        return savedOutfits.mapNotNull { outfitString ->

            try {

                val outfitJson = JSONObject(outfitString)

                val idsArray = outfitJson.getJSONArray("clothingIds")

                val clothingIds = mutableListOf<String>()

                for (i in 0 until idsArray.length()) {
                    clothingIds.add(idsArray.getString(i))
                }

                SavedOutfit(
                    id = outfitJson.getString("id"),
                    name = outfitJson.getString("name"),
                    clothingIds = clothingIds
                )

            } catch (e: Exception) {
                null
            }
        }
    }

    // Delete a saved outfit
    fun deleteSavedOutfit(
        context: Context,
        outfit: SavedOutfit
    ) {

        val preferences = getPreferences(context)

        val savedOutfits = preferences
            .getStringSet(KEY_OUTFITS, emptySet())
            ?.toMutableSet() ?: mutableSetOf()

        savedOutfits.removeAll { outfitString ->

            try {
                JSONObject(outfitString).getString("id") == outfit.id
            } catch (e: Exception) {
                false
            }
        }

        preferences.edit()
            .putStringSet(KEY_OUTFITS, savedOutfits)
            .apply()
    }
}
