package com.hnimrod.barcodeexaple

import android.graphics.Bitmap

data class BarcodeResult(
    val image: Bitmap?,
    val text: String
)
