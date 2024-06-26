package com.hashapps.cadenas

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.hashapps.cadenas.ui.CadenasApp
import com.hashapps.cadenas.ui.theme.CadenasAppTheme
import java.util.concurrent.Executor

/**
 * The Cadenas [activity][ComponentActivity].
 *
 * Defines the activity-creation for Cadenas. In particular, it sets the
 * content of the activity to the [CadenasApp] composable element, which hosts
 * the main navigation graph of the application.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                //App can authenticate using biometrics.
                getAuthenticationAndStartApp()
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                //No biometric features available on this device.
                startApp()
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                //Biometric features are currently unavailable.
                startApp()
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                // Could prompt the user to create credentials, but for now just start the app.
                startApp()
        }
    }

    fun getAuthenticationAndStartApp() {
        setContentView(androidx.biometric.R.layout.fingerprint_dialog_layout)
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(
            this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext,
                        getString(R.string.biometrics_error_msg)+"$errString", Toast.LENGTH_SHORT)
                        .show()
                    finish();
                    System.exit(0);
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    startApp()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, getString(R.string.biometrics_failure_msg),
                        Toast.LENGTH_SHORT)
                        .show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometrics_login_title))
            .setSubtitle(getString(R.string.biometrics_login_prompt))
            .setNegativeButtonText(getString(R.string.biometrics_login_pswd_prompt))
            .build()

        //Authenticate now
        biometricPrompt.authenticate(promptInfo)

    }

    fun startApp() {
        setContent {
            CadenasAppTheme {
                CadenasApp()
            }
        }
    }
}
