package com.example.virtualwardrobe

import android.os.Bundle
import android.graphics.BitmapFactory
import android.view.Gravity
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import java.io.File

class OutfitDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_outfit_detail)

        val btnBack = findViewById<Button>(R.id.btnBackDetail)
        val outfitNameText = findViewById<TextView>(R.id.tvOutfitDetailName)
        val grid = findViewById<GridLayout>(R.id.gridOutfitDetail)

        // Get the outfit name passed from the previous screen
        val outfitName = intent.getStringExtra("outfitName") ?: "My Outfit"

        // Get the clothing IDs passed from the previous screen
        val clothingIds = intent.getStringArrayListExtra("clothingIds")
            ?: arrayListOf()

        btnBack.setOnClickListener {
            finish()
        }

        outfitNameText.text = outfitName

        // Get all clothing items stored in the wardrobe
        val allClothes = WardrobeStorage.getAllClothes(this)

        // Find the clothes belonging to this outfit
        val outfitItems = allClothes.filter { item ->
            clothingIds.contains(item.id)
        }

        if (outfitItems.isEmpty()) {

            val emptyText = TextView(this)

            emptyText.text = "No clothing items found 💗"
            emptyText.textSize = 18f
            emptyText.gravity = Gravity.CENTER

            grid.addView(emptyText)

            return
        }

        // Display each clothing item
        for (item in outfitItems) {

            val imageView = ImageView(this)

            val imageFile = File(filesDir, item.imagePath)

            val bitmap = BitmapFactory.decodeFile(
                imageFile.absolutePath
            )

            if (bitmap != null) {

                imageView.setImageBitmap(bitmap)

                imageView.scaleType =
                    ImageView.ScaleType.CENTER_CROP

                val params = GridLayout.LayoutParams()

                params.width = 160.dpToPx()
                params.height = 220.dpToPx()

                params.setMargins(
                    8.dpToPx(),
                    8.dpToPx(),
                    8.dpToPx(),
                    8.dpToPx()
                )

                imageView.layoutParams = params

                grid.addView(imageView)
            }
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}