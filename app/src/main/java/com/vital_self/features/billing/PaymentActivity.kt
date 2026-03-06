package com.vital_self.features.billing

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vital_self.R
import com.vital_self.core.utils.helpers.AnimationsHandler

class PaymentActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefresh: SwipeRefreshLayout

    companion object {
        const val EXTRA_AUTHORIZATION_URL = "authorization_url"
        const val EXTRA_REFERENCE = "reference"

        fun startActivity(activity: Activity, authorizationUrl: String, reference: String, requestCode: Int) {
            Intent(activity, PaymentActivity::class.java).apply {
                putExtra(EXTRA_AUTHORIZATION_URL, authorizationUrl)
                putExtra(EXTRA_REFERENCE, reference)
            }.run {
                activity.startActivityForResult(this, requestCode)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)
        swipeRefresh = findViewById(R.id.swipeRefresh)

        swipeRefresh.setOnRefreshListener {
            webView.reload()
        }

        val authorizationUrl = intent.getStringExtra(EXTRA_AUTHORIZATION_URL)
        val reference = intent.getStringExtra(EXTRA_REFERENCE)

        if (authorizationUrl.isNullOrEmpty() || reference.isNullOrEmpty()) {
            setResult(RESULT_CANCELED)
            finish()
            return
        }

        setupWebView(reference)
        webView.loadUrl(authorizationUrl)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    setResult(RESULT_CANCELED)
                    finish()
                    AnimationsHandler.playActivityAnimation(
                        this@PaymentActivity, AnimationsHandler.Animations.LeftToRight
                    )
                }
            }
        })

        findViewById<View>(R.id.btnClose).setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
            AnimationsHandler.playActivityAnimation(this, AnimationsHandler.Animations.LeftToRight)
        }

        findViewById<View>(R.id.btnRefresh).setOnClickListener {
            webView.reload()
        }
    }

    @android.annotation.SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(reference: String) {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return false
                if (url.contains("payment-processing")) {
                    val resultIntent = Intent().apply {
                        putExtra(EXTRA_REFERENCE, reference)
                    }
                    Log.d("TAG", "shouldOverrideUrlLoading: url $url")
                    setResult(RESULT_OK, resultIntent)
                    finish()
                    AnimationsHandler.playActivityAnimation(
                        this@PaymentActivity, AnimationsHandler.Animations.LeftToRight
                    )
                    return true
                }
                return false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
                swipeRefresh.isRefreshing = false
            }
        }

        webView.webChromeClient = WebChromeClient()
    }
}
