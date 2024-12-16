package com.hnimrod.barcodeexaple

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.hnimrod.barcodeexaple.ui.theme.BarcodeExapleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BarcodeExapleTheme {
                BarcodeExampleScreen()
            }
        }
    }
}

@Composable
fun BarcodeExampleScreen() {
    var text by remember { mutableStateOf("123456789012") }
    var barcodeResult by remember { mutableStateOf<BarcodeResult?>(null) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(text) {
        barcodeResult = generateEAN13Barcode(text)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { focusManager.clearFocus() }
            }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.weight(1f))

        val result = barcodeResult

        if (result != null) {
            if (result.image != null) {
                Image(
                    bitmap = result.image.asImageBitmap(),
                    contentDescription = "Barcode Image",
                    modifier = Modifier
                        .size(width = 300.dp, height = 150.dp)
                        .padding(bottom = 8.dp)
                )
            }
            Text(result.text)
        } else {
            Text("Generating Barcode...")
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedTextField(
            value = text,
            onValueChange = { newValue ->
                text = newValue
            },
            label = { Text("Barcode") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.weight(0.5f))
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BarcodeExapleTheme {
        BarcodeExampleScreen()
    }
}

private fun generateEAN13Barcode(text: String): BarcodeResult? {
    return try {
        val writer = MultiFormatWriter()
        val bitMatrix: BitMatrix = writer.encode(text, BarcodeFormat.EAN_13, 300, 150)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (x in 0 until width) {
            for (y in 0 until height) {
                val color = if (bitMatrix[x, y]) {
                    android.graphics.Color.BLACK
                } else {
                    android.graphics.Color.WHITE
                }
                bitmap.setPixel(
                    x,
                    y,
                    color
                )
            }
        }
        BarcodeResult(bitmap, text)
    } catch (e: Exception) {
        null
    }
}
