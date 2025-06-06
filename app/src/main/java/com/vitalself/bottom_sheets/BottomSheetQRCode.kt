package com.vitalself.bottom_sheets

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.vitalself.R
import com.vitalself.databinding.BottomSheetQrCodeBinding
import com.vitalself.utils.DateFormatter
import com.google.android.material.bottomsheet.BottomSheetDialog

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import java.io.File
import java.io.FileOutputStream

/**
 * Bottom Sheet Dialog Fragment to display vitals information
 */
class BottomSheetQRCode : BottomSheetDialogFragment() {

    private var _binding: BottomSheetQrCodeBinding? = null
    private val binding get() = _binding!!

    private lateinit var vitalsData: Bitmap
    private var name:String? = null
    private var date:String? = null
    private var time:String? = null

    companion object {
        private const val QR_IMAGE_DATA = "qr_image_data"
        private const val DATE = "date"
        private const val TIME = "time"
        private const val NAME = "name"

        fun newInstance(vitalsData: Bitmap,name:String,date: String,time: String): BottomSheetQRCode {
            val fragment = BottomSheetQRCode()
            val args = Bundle()

            // Handle API level difference safely
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                args.putParcelable(QR_IMAGE_DATA, vitalsData as Parcelable)
                args.putString(DATE, date)
                args.putString(TIME, time )
                args.putString(NAME, name )
            } else {
                @Suppress("DEPRECATION")
                args.putParcelable(QR_IMAGE_DATA, vitalsData)
                args.putString(DATE, date)
                args.putString(TIME, time )
                args.putString(NAME, name )
            }

            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetQrCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (dialog as? BottomSheetDialog)?.behavior?.isDraggable = true

        view.background = ContextCompat.getDrawable(requireContext(), R.drawable.top_rounded_bottom_sheet)


        // Get vitals data from arguments based on API level
        binding.lifecycleOwner = viewLifecycleOwner

        val qrBitmap: Bitmap? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(QR_IMAGE_DATA, Bitmap::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(QR_IMAGE_DATA)
        }

        date = arguments?.getString(DATE)
        time = arguments?.getString(TIME)
        name = arguments?.getString(NAME)
        val userName = if (name == "null"){
            ""
        }else{
            name
        }
        binding.tvVitalName.text = "Hello, $userName share your health report via QR code"
        val stamp = " $userName | ${DateFormatter.convertDateFormat(date.toString())} | ${DateFormatter.convertTimeFormat(time.toString())}"
        Log.d("TAG", "onViewCreated: $stamp")
        qrBitmap?.let {
            binding.imgEmoji.setImageBitmap(it)
            binding.tvDateTime.text = stamp
        }

        binding.btnShare.setOnClickListener {
            val bitmap = captureLayoutAsBitmap(binding.layoutQr) // Or capture only a specific layout like binding.layoutToShare
            shareBitmap(requireContext(), bitmap)
        }

        binding.btnBack.setOnClickListener {
            dismiss()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun captureLayoutAsBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun shareBitmap(context: Context, bitmap: Bitmap) {
        // Save the bitmap temporarily in cache
        val file = File(context.cacheDir, "shared_layout.png")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.close()

        // Get URI using FileProvider
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "com.briahai.fileprovider",  // Don't forget to declare this in AndroidManifest.xml
            file
        )

        // Share via intent
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Share QR Layout via"))
    }

}
