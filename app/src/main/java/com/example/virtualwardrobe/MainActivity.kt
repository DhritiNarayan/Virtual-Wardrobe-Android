package com.example.virtualwardrobe

import android.content.Intent
import android.os.Bundle
import android.graphics.BitmapFactory
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {

    private lateinit var gridWardrobe: GridLayout
    private lateinit var txtEmpty: TextView
    private lateinit var btnShowAllClothes: Button

    private var selectedCategory: String = "All"

    // Controls whether clothing is currently visible
    private var showWardrobe: Boolean = false

    // Image picker
    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->

        if (uri != null) {

            val categories = arrayOf(
                "Tops",
                "Bottoms",
                "Dresses",
                "Shoes",
                "Accessories",
                "Jackets"
            )

            android.app.AlertDialog.Builder(this)
                .setTitle("Choose clothing category 🎀")
                .setItems(categories) { _, which ->

                    val category = categories[which]

                    val item = WardrobeStorage.addClothing(
                        this,
                        uri,
                        category
                    )

                    if (item != null) {

                        Toast.makeText(
                            this,
                            "$category added successfully! 💗",
                            Toast.LENGTH_SHORT
                        ).show()

                        refreshWardrobe()

                    } else {

                        Toast.makeText(
                            this,
                            "Unable to save image",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Saved Outfits button
        val btnMySavedOutfits =
            findViewById<Button>(R.id.btnMySavedOutfits)

        btnMySavedOutfits.setOnClickListener {
            val intent = Intent(
                this,
                SavedOutfitsActivity::class.java
            )
            startActivity(intent)
        }

        // Add Clothes button
        val btnAddClothes =
            findViewById<Button>(R.id.btnAddClothes)

        gridWardrobe =
            findViewById(R.id.gridWardrobe)

        txtEmpty =
            findViewById(R.id.txtEmpty)

        btnShowAllClothes =
            findViewById(R.id.btnShowAllClothes)

        // Add clothes
        btnAddClothes.setOnClickListener {
            imagePicker.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }

        // Create Outfit button
        val btnCreateOutfit =
            findViewById<Button>(R.id.btnCreateOutfit)

        btnCreateOutfit.setOnClickListener {
            val intent = Intent(
                this,
                OutfitActivity::class.java
            )
            startActivity(intent)
        }

        // Show / Hide all clothes
        btnShowAllClothes.setOnClickListener {

            showWardrobe = !showWardrobe

            // When Show All is clicked, reset category to All
            if (showWardrobe) {
                selectedCategory = "All"
            }

            refreshWardrobe()
        }

        // Category filters
        findViewById<TextView>(R.id.categoryTops)
            .setOnClickListener {
                selectedCategory = "Tops"
                showWardrobe = true
                refreshWardrobe()
            }

        findViewById<TextView>(R.id.categoryBottoms)
            .setOnClickListener {
                selectedCategory = "Bottoms"
                showWardrobe = true
                refreshWardrobe()
            }

        findViewById<TextView>(R.id.categoryDresses)
            .setOnClickListener {
                selectedCategory = "Dresses"
                showWardrobe = true
                refreshWardrobe()
            }

        findViewById<TextView>(R.id.categoryShoes)
            .setOnClickListener {
                selectedCategory = "Shoes"
                showWardrobe = true
                refreshWardrobe()
            }

        findViewById<TextView>(R.id.categoryAccessories)
            .setOnClickListener {
                selectedCategory = "Accessories"
                showWardrobe = true
                refreshWardrobe()
            }

        findViewById<TextView>(R.id.categoryJackets)
            .setOnClickListener {
                selectedCategory = "Jackets"
                showWardrobe = true
                refreshWardrobe()
            }

        // Initially hide all clothes
        refreshWardrobe()
    }

    private fun refreshWardrobe() {

        gridWardrobe.removeAllViews()

        // Update button text
        btnShowAllClothes.text = if (showWardrobe) {
            "Hide All Clothes"
        } else {
            "Show All Clothes"
        }

        // If wardrobe is hidden, don't display clothes or empty message
        if (!showWardrobe) {
            gridWardrobe.visibility = View.GONE
            txtEmpty.visibility = View.GONE
            return
        }

        val allClothes = WardrobeStorage.getAllClothes(this)

        // Filter clothes according to selected category
        val clothes = if (selectedCategory == "All") {
            allClothes
        } else {
            allClothes.filter {
                it.category.equals(
                    selectedCategory,
                    ignoreCase = true
                )
            }
        }

        // Handle empty wardrobe/category
        if (clothes.isEmpty()) {

            gridWardrobe.visibility = View.GONE
            txtEmpty.visibility = View.VISIBLE

            txtEmpty.text = if (selectedCategory == "All") {
                "Your wardrobe is empty! Add some clothes 🎀"
            } else {
                "No $selectedCategory found! 💗"
            }

        } else {

            gridWardrobe.visibility = View.VISIBLE
            txtEmpty.visibility = View.GONE

            for (item in clothes) {

                val imageView = layoutInflater.inflate(
                    R.layout.item_clothing,
                    gridWardrobe,
                    false
                ) as ImageView

                val imageFile = java.io.File(
                    filesDir,
                    item.imagePath
                )

                val bitmap = BitmapFactory.decodeFile(
                    imageFile.absolutePath
                )

                if (bitmap != null) {

                    imageView.setImageBitmap(bitmap)

                    val params = GridLayout.LayoutParams().apply {

                        width = 0
                        height = 180.dpToPx()

                        columnSpec = GridLayout.spec(
                            GridLayout.UNDEFINED,
                            1f
                        )

                        setMargins(
                            6.dpToPx(),
                            6.dpToPx(),
                            6.dpToPx(),
                            6.dpToPx()
                        )
                    }

                    // Long press to delete clothing
                    imageView.setOnLongClickListener {

                        android.app.AlertDialog.Builder(this)
                            .setTitle("Remove clothing? 💗")
                            .setMessage(
                                "Do you want to remove this item from your wardrobe?"
                            )
                            .setPositiveButton("Delete") { _, _ ->

                                WardrobeStorage.deleteClothing(
                                    this,
                                    item
                                )

                                Toast.makeText(
                                    this,
                                    "Clothing removed",
                                    Toast.LENGTH_SHORT
                                ).show()

                                refreshWardrobe()
                            }
                            .setNegativeButton("Cancel", null)
                            .show()

                        true
                    }

                    gridWardrobe.addView(
                        imageView,
                        params
                    )
                }
            }
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}