package com.vital_self.core.utils.helpers

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import com.vital_self.core.domain.model.Model
import com.vital_self.core.utils.constants.AppConstants
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PDFGenerator {

    fun createVitalPdf(
        context: Context,
        healthVitals: List<Model.VitalsData>, // List of VitalsData
        additionalText: String, // Additional parameter for extra text
        bmi: String,
        height: String,
        weight: String,
        time : String,
        date : String,
        age: String,
        name: String,
        wellnessLevel: String,
        wellnessIndex: String,
        headertext: String
    ) {
        // Create a new PDF document
        Log.d("TAG", "createVitalPdf: bmi value $bmi bmi ${bmi.isNotBlank()} ")

        val pdfDocument = PdfDocument()
        val pageWidth = 595
        val pageHeight = 800 // Adjusted for easier viewing
        val margin = 80f
        val contentWidth = pageWidth - 2 * margin
        var currentY = 100f // Starting Y position

        // Paints for various text types
        val titlePaint = Paint().apply {
            textSize = 24f
            isFakeBoldText = true
            color = Color.parseColor("#000c54")
        }
        val subTitlePaint = Paint().apply {
            textSize = 18f
            isFakeBoldText = true
            color = Color.parseColor("#000c54")
        }
        val textPaint = TextPaint().apply {
            textSize = 14f
            color = Color.BLACK
        }
        val textPaintSub = TextPaint().apply {
            textSize = 12f
            color = Color.parseColor("#484848")
        }

        val textPaintSubItalic = TextPaint().apply {
            textSize = 10f
            color = Color.parseColor("#484848")
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        }
        val linePaint = Paint().apply {
            strokeWidth = 1f
            color = Color.LTGRAY
        }

        // Start the first page
        var pageNumber = 1
        var page = pdfDocument.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
        var canvas = page.canvas

        // Draw "Your Health Vital Report" header
        canvas.drawText("Your VitalSelf Health Report", margin, currentY, titlePaint)
        val stamp = "${DateFormatter.convertDateFormat(date.toString())} | ${DateFormatter.convertTimeFormat(time.toString())}"

        canvas.drawText(stamp, margin + 350f, currentY, textPaint)
        currentY += 20f
        val staticheader = StaticLayout.Builder.obtain(
            headertext,
            0,
            headertext.length,
            textPaintSub,
            contentWidth.toInt()
        ).setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(1.0f, 1.2f)
            .setIncludePad(false)
            .build()

        canvas.save()
        canvas.translate(margin, currentY)
        staticheader.draw(canvas)
        canvas.restore()
        currentY += 140f

        // Draw "Your Details" section
        if (name != "null"){

            canvas.drawText("Full Name", margin, currentY, subTitlePaint)
            canvas.drawText(name, margin + 100f, currentY, subTitlePaint)
            currentY += 30f
        }
        if (height != "null"){
            canvas.drawText("Height", margin, currentY, textPaint)
            canvas.drawText(height, margin, currentY + 30f, subTitlePaint)
        }

        if (weight != "null"){
            canvas.drawText("Weight", margin + 130f, currentY, textPaint)
            canvas.drawText(weight, margin + 130f, currentY + 30f, subTitlePaint)
        }

        if (height != "null"){
            canvas.drawText("BMI", margin + 260f, currentY, textPaint)
            canvas.drawText(bmi, margin + 260f, currentY + 30f, subTitlePaint)

        }
        currentY += 80f
        // Draw wellness score
        canvas.drawText("Wellness Score", margin, currentY, titlePaint)
        canvas.drawText("$wellnessIndex/10", margin + 380f, currentY, titlePaint)
        currentY += 30f
        canvas.drawText("Your wellness level is $wellnessLevel", margin, currentY, subTitlePaint)
        currentY += 30f
        val testText = "Wellness Score is meant to predict your cardiovascular risk for the next 5 to 10 years, based only on the measured vitals."
        val staticheaderTest = StaticLayout.Builder.obtain(
            testText,
            0,
            testText.length,
            textPaintSub,
            contentWidth.toInt()
        ).setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(1.0f, 1.2f)
            .setIncludePad(false)
            .build()

        canvas.save()
        canvas.translate(margin, currentY)
        staticheaderTest.draw(canvas)
        canvas.restore()
        currentY += staticheaderTest.height + 10f

        // Draw a line separator
        currentY += 150f

        // Draw each health vital item from the list
        for (vital in healthVitals) {
            when(vital.vitalName){
                "HRV-SDNN" -> {
                    if (currentY + 400 > pageHeight - margin) {
                        pdfDocument.finishPage(page)
                        pageNumber++
                        page = pdfDocument.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
                        canvas = page.canvas
                        currentY = margin // Reset Y for the new page
                    }
                }
                "RMSSD" ->{
                    if (currentY + 250 > pageHeight - margin) {
                        pdfDocument.finishPage(page)
                        pageNumber++
                        page = pdfDocument.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
                        canvas = page.canvas
                        currentY = margin // Reset Y for the new page
                    }
                }
                else -> {
                    if (currentY + 150 > pageHeight - margin) {
                        pdfDocument.finishPage(page)
                        pageNumber++
                        page = pdfDocument.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
                        canvas = page.canvas
                        currentY = margin // Reset Y for the new page
                    }
                }
            }
            // Check if we need a new page

            currentY += 40f
            // Draw vital name, value, and details
            canvas.drawText("${vital.vitalName}", margin, currentY, subTitlePaint)
            val status = when (vital.vitalStatus) {
                AppConstants.HIGH -> {
                    "Your ${vital.vitalName} is High"
                }
                AppConstants.NORMAL -> {
                    "Your ${vital.vitalName} is Normal"
                }
                AppConstants.LOW -> {
                    "Your ${vital.vitalName} is Low"
                }
                else -> {
                    ""
                }
            }

            canvas.drawText(status, margin + 300f, currentY, textPaintSub)
            currentY += 20f

            if (!vital.confidenceLevel.isNullOrEmpty()) {
                canvas.drawText("Confidence Level is ${vital.confidenceLevel}", margin + 300f, currentY, textPaintSub)
            }

            currentY += 10f
            canvas.drawText("${vital.vitalValue ?: "N/A"} ${vital.vitalUnit ?: ""}", margin, currentY, subTitlePaint)
            currentY += 20f
            canvas.drawLine(margin, currentY, pageWidth - margin, currentY, linePaint)
            currentY += 20f

            val staticLayout = StaticLayout.Builder.obtain(
                vital.vitalDetail ?: "No details provided",
                0,
                (vital.vitalDetail ?: "").length,
                textPaintSub,
                contentWidth.toInt()
            ).setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(1.0f, 1.2f)
                .setIncludePad(false)
                .build()

            canvas.save()
            canvas.translate(margin, currentY)
            staticLayout.draw(canvas)
            canvas.restore()
            currentY += staticLayout.height + 20f

            // Check if the current content would overflow
            if (currentY + 100 > pageHeight - margin) {
                pdfDocument.finishPage(page)
                pageNumber++
                page = pdfDocument.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
                canvas = page.canvas
                currentY = margin // Reset Y for the new page
            }
        }
        currentY += 40f
        val staticLayout = StaticLayout.Builder.obtain(
            additionalText,
            0,
            additionalText.length,
            textPaintSubItalic,
            contentWidth.toInt()
        ).setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(1.0f, 1.2f)
            .setIncludePad(false)
            .build()

        canvas.save()
        canvas.translate(margin, currentY)
        staticLayout.draw(canvas)
        canvas.restore()

        // Finish the last page
        pdfDocument.finishPage(page)

        // Save the document to a file
        val directory = File(context.getExternalFilesDir(null), "PDFs")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        Log.d("TAG", "sharePdfFile: file path $name VitalsReport.pdf")
        val filePath = File(directory, "$name VitalsReport.pdf")
        Log.d("TAG", "sharePdfFile: file name $name VitalsReport.pdf")
        Log.d("TAG", "sharePdfFile: file path $filePath")
        try {
            pdfDocument.writeTo(FileOutputStream(filePath))
            Log.d("TAG", "PDF generated at: ${filePath.absolutePath}")
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }
    }


    fun getCurrentDateTime(): String {
            val dateTimeFormat = SimpleDateFormat("hh:mm a dd/MM/yyyy", Locale.getDefault())
            val currentDate = Date()
            return dateTimeFormat.format(currentDate)
        }

}



