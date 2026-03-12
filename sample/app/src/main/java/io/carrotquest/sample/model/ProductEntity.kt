package io.carrotquest.sample.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductEntity(
    val name: String,
    val description: String,
    val price: Float,
    val imageUri: String
) : Parcelable
