package com.example.virtualwardrobe

data class SavedOutfit(
    val id: String,
    val name: String,
    val clothingIds: List<String>
)