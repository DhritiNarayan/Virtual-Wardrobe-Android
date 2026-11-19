package com.example.virtualwardrobe

data class ClothingItem(
    val id: String,
    val imagePath: String,
    val category: String = "Uncategorized"
)