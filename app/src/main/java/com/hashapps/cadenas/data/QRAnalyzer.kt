package com.hashapps.cadenas.data

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class QRAnalyzer(
    private val onCodeScanned: (List<Barcode>) -> Unit,
) : ImageAnalysis.Analyzer {
    @OptIn(ExperimentalGetImage::class) override fun analyze(imageProxy: ImageProxy) {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        val scanner = BarcodeScanning.getClient(options)

        val mediaImage = imageProxy.image
        mediaImage?.let {
            val image = InputImage.fromMediaImage(it, imageProxy.imageInfo.rotationDegrees)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        onCodeScanned(barcodes)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("QRAnalyzer", "QRAnalyzer: Something went wrong ($exception)")
                }
                .addOnCompleteListener {
                    mediaImage.close()
                    imageProxy.close()
                }
        }
    }
}