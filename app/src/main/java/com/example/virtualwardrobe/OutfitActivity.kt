
package com.example.virtualwardrobe

import android.os.Bundle
import android.graphics.BitmapFactory
import android.graphics.Color
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import android.app.AlertDialog
class OutfitActivity : ComponentActivity() {

    private lateinit var gridSelectableClothes: GridLayout
    private lateinit var gridOutfitPreview: GridLayout

    private val selectedItems = mutableListOf<ClothingItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_outfit)

        val btnBack = findViewById<Button>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        gridSelectableClothes =
            findViewById(R.id.gridSelectableClothes)

        gridOutfitPreview =
            findViewById(R.id.gridOutfitPreview)

        val btnClearOutfit =
            findViewById<Button>(R.id.btnClearOutfit)

        val btnSaveOutfit =
            findViewById<Button>(R.id.btnSaveOutfit)

        loadWardrobeItems()

        btnClearOutfit.setOnClickListener {

            selectedItems.clear()
            refreshPreview()
            loadWardrobeItems()

            Toast.makeText(
                this,
                "Outfit cleared 💗",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnSaveOutfit.setOnClickListener {
            showSaveOutfitDialog()
        }
    }

    private fun loadWardrobeItems() {

        gridSelectableClothes.removeAllViews()

        val clothes = WardrobeStorage.getAllClothes(this)

        if (clothes.isEmpty()) {

            Toast.makeText(
                this,
                "Add clothes to your wardrobe first! 🎀",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        for (item in clothes) {

            val imageView = ImageView(this)

            val imageFile = java.io.File(
                filesDir,
                item.imagePath
            )

            val bitmap = BitmapFactory.decodeFile(
                imageFile.absolutePath
            )

            if (bitmap != null) {

                imageView.setImageBitmap(bitmap)

                imageView.scaleType =
                    ImageView.ScaleType.CENTER_CROP

                imageView.setPadding(
                    5.dpToPx(),
                    5.dpToPx(),
                    5.dpToPx(),
                    5.dpToPx()
                )

                val params = GridLayout.LayoutParams().apply {

                    width = 0
                    height = 150.dpToPx()

                    columnSpec = GridLayout.spec(
                        GridLayout.UNDEFINED,
                        1f
                    )

                    setMargins(
                        5.dpToPx(),
                        5.dpToPx(),
                        5.dpToPx(),
                        5.dpToPx()
                    )
                }

                imageView.setOnClickListener {

                    if (selectedItems.contains(item)) {
                        selectedItems.remove(item)
                    } else {
                        selectedItems.add(item)
                    }

                    refreshPreview()
                    updateSelectionAppearance(imageView, item)
                }

                gridSelectableClothes.addView(
                    imageView,
                    params
                )

                updateSelectionAppearance(imageView, item)
            }
        }
    }

    private fun refreshPreview() {

        gridOutfitPreview.removeAllViews()

        for (item in selectedItems) {

            val imageView = ImageView(this)

            val imageFile = java.io.File(
                filesDir,
                item.imagePath
            )

            val bitmap = BitmapFactory.decodeFile(
                imageFile.absolutePath
            )

            if (bitmap != null) {

                imageView.setImageBitmap(bitmap)

                imageView.scaleType =
                    ImageView.ScaleType.CENTER_CROP

                val params = GridLayout.LayoutParams().apply {

                    width = 0
                    height = 180.dpToPx()

                    columnSpec = GridLayout.spec(
                        GridLayout.UNDEFINED,
                        1f
                    )

                    setMargins(
                        5.dpToPx(),
                        5.dpToPx(),
                        5.dpToPx(),
                        5.dpToPx()
                    )
                }

                gridOutfitPreview.addView(
                    imageView,
                    params
                )
            }
        }
    }

    private fun updateSelectionAppearance(
        imageView: ImageView,
        item: ClothingItem
    ) {

        if (selectedItems.contains(item)) {

            imageView.setBackgroundColor(
                Color.parseColor("#E89AAE")
            )

        } else {

            imageView.setBackgroundColor(
                Color.parseColor("#FFFFFAFC")
            )
        }
    }

    private fun showSaveOutfitDialog() {

        if (selectedItems.isEmpty()) {

            Toast.makeText(
                this,
                "Select some clothes first! 👗",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val editText = EditText(this)

        editText.hint = "Enter outfit name"
        editText.setPadding(
            40,
            30,
            40,
            30
        )

        val dialog = AlertDialog.Builder(this)
            .setTitle("Save Your Outfit 💗")
            .setMessage("Give your outfit a name")
            .setView(editText)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {

                    val outfitName =
                        editText.text.toString().trim()

                    if (outfitName.isEmpty()) {

                        editText.error =
                            "Please enter an outfit name"

                    } else {

                        val clothingIds =
                            selectedItems.map { it.id }

                        val success =
                            WardrobeStorage.saveOutfit(
                                this,
                                outfitName,
                                clothingIds
                            )

                        if (success) {

                            Toast.makeText(
                                this,
                                "$outfitName saved successfully! 💗",
                                Toast.LENGTH_SHORT
                            ).show()

                            dialog.dismiss()

                        } else {

                            Toast.makeText(
                                this,
                                "Could not save outfit",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
        }

        dialog.show()
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}
