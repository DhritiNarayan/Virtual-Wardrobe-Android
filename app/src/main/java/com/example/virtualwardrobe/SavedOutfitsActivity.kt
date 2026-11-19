
package com.example.virtualwardrobe

import android.os.Bundle
import android.graphics.BitmapFactory
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.app.AlertDialog
import androidx.activity.ComponentActivity
import java.io.File

class SavedOutfitsActivity : ComponentActivity() {

    private lateinit var outfitContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_saved_outfits)

        outfitContainer =
            findViewById(R.id.outfitContainer)

        val btnBack =
            findViewById<Button>(R.id.btnBackSavedOutfits)

        btnBack.setOnClickListener {
            finish()
        }

        loadSavedOutfits()
    }

    private fun loadSavedOutfits() {

        outfitContainer.removeAllViews()

        val outfits = WardrobeStorage.getSavedOutfits(this)
        val clothes = WardrobeStorage.getAllClothes(this)

        if (outfits.isEmpty()) {

            val emptyText = TextView(this)

            emptyText.text = "No outfits saved yet! 💗"
            emptyText.textSize = 18f
            emptyText.setTextColor(
                android.graphics.Color.parseColor("#92566B")
            )
            emptyText.gravity = Gravity.CENTER
            emptyText.setPadding(20, 60, 20, 60)

            outfitContainer.addView(emptyText)

            return
        }

        for (outfit in outfits) {

            val card = LinearLayout(this)

            card.orientation = LinearLayout.VERTICAL
            card.setPadding(20, 20, 20, 20)

            card.setBackgroundColor(
                android.graphics.Color.parseColor("#FFFFFAFC")
            )

            val cardParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            cardParams.setMargins(0, 0, 0, 24)

            card.layoutParams = cardParams

            // Outfit name
            val nameText = TextView(this)

            nameText.text = outfit.name
            nameText.textSize = 20f
            nameText.setTextColor(
                android.graphics.Color.parseColor("#92566B")
            )
            nameText.setPadding(0, 0, 0, 12)

            card.addView(nameText)

            // Images row
            val imageRow = LinearLayout(this)

            imageRow.orientation = LinearLayout.HORIZONTAL
            imageRow.gravity = Gravity.CENTER_VERTICAL

            for (clothingId in outfit.clothingIds) {

                val item = clothes.find {
                    it.id == clothingId
                }

                if (item != null) {

                    val imageView = ImageView(this)

                    val imageFile = File(
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

                        val imageParams =
                            LinearLayout.LayoutParams(
                                110.dpToPx(),
                                140.dpToPx()
                            )

                        imageParams.setMargins(
                            0,
                            0,
                            8.dpToPx(),
                            0
                        )

                        imageView.layoutParams = imageParams

                        imageRow.addView(imageView)
                    }
                }
            }

            card.addView(imageRow)

            // Delete button
            val deleteButton = Button(this)

            deleteButton.text = "Delete Outfit"
            deleteButton.setTextColor(
                android.graphics.Color.WHITE
            )
            deleteButton.backgroundTintList =
                android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#E89AAE")
                )

            val buttonParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                48.dpToPx()
            )

            buttonParams.setMargins(
                0,
                16.dpToPx(),
                0,
                0
            )

            deleteButton.layoutParams = buttonParams

            deleteButton.setOnClickListener {

                AlertDialog.Builder(this)
                    .setTitle("Delete Outfit?")
                    .setMessage(
                        "Do you want to delete '${outfit.name}'?"
                    )
                    .setPositiveButton("Delete") { _, _ ->

                        WardrobeStorage.deleteSavedOutfit(
                            this,
                            outfit
                        )

                        Toast.makeText(
                            this,
                            "Outfit deleted",
                            Toast.LENGTH_SHORT
                        ).show()

                        loadSavedOutfits()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            card.setOnClickListener {

                val intent = android.content.Intent(
                    this,
                    OutfitDetailActivity::class.java
                )

                intent.putExtra("outfitName", outfit.name)

                intent.putStringArrayListExtra(
                    "clothingIds",
                    ArrayList(outfit.clothingIds)
                )

                startActivity(intent)
            }

            card.addView(deleteButton)

            outfitContainer.addView(card)
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}
