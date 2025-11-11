package com.vital_self.base

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.vital_self.R
import com.vital_self.databinding.ToolbarLayoutBinding
import com.vital_self.dialog.CustomProgressDialog
import java.net.NetworkInterface
import java.util.Collections

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
abstract class BaseActivity : AppCompatActivity() {

    protected var toolbarLayout: ToolbarLayoutBinding? = null
    private var titleText: TextView? = null
    private var toolbarIcon: ImageView? = null
    private var toolbarShadowView: View? = null
    private var isNightMode : Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isNightMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }



    fun setUpToolbar(toolbarLayout: ToolbarLayoutBinding) {
        this.toolbarLayout = toolbarLayout
        if (isNightMode==true){
            toolbarLayout.imgLogo.setImageDrawable(resources.getDrawable(R.drawable.ic_logo_vital_self_night))
        }else{
            toolbarLayout.imgLogo.setImageDrawable(resources.getDrawable(R.drawable.ic_logo_vital_self))
        }

        toolbarShadowView = toolbarLayout.shadowView
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(
                ContextCompat.getDrawable(this@BaseActivity,
                    R.drawable.back_arrow
                )
            )
        }
    }


    protected fun showToolbarIcon() {
            titleText?.typeface = ResourcesCompat.getFont(this,
                R.font.manrope_regular
            )
            titleText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
            toolbarIcon?.visibility = View.GONE
            titleText?.visibility = View.VISIBLE
    }

    fun showHideProgress(show: Boolean, cancelable: Boolean = false) {
        if (show) {
            CustomProgressDialog.showProgressDialog(this, cancelable)
        } else {
            CustomProgressDialog.dismissProgressDialog()
        }
    }

    protected val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {}
    }

    fun isValidDevice(context: Context):String{
//        val predefinedDeviceId = "YOUR_PREDEFINED_DEVICE_ID"
        return  Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
//        return predefinedDeviceId == deviceId
    }

    @RequiresPermission("android.permission.READ_PRIVILEGED_PHONE_STATE")
    fun getIMEI(context: Context): String? {
        // Check if the app has permission to read phone state
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            return null
        }

        val telephonyManager = context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.imei  // For dual SIM phones, use getImei(0) or getImei(1)
    }


    fun getIPAddress(useIPv4: Boolean = true,context: Context): Boolean? {
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (networkInterface in interfaces) {
                val addresses = Collections.list(networkInterface.inetAddresses)
                for (address in addresses) {
                    if (!address.isLoopbackAddress) {
                        val sAddr = address.hostAddress ?: return null
                        val isIPv4 = sAddr.indexOf(':') < 0
                        if (useIPv4) {
                            if (isIPv4) return return (sAddr.equals("10.10.10.29"))
                        } else {
                            if (!isIPv4) {
                                val addr = sAddr.split('%')[0]
                                return (addr.equals("10.10.10.29"))
                            }
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
        }
        return null
    }

}
