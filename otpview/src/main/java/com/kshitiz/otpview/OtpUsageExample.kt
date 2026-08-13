package com.kshitiz.otpview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Minimal example showing how to drop OtpView into a screen.
 * Just call the composable — all styling is optional and defaults to
 * otpLength = 4, cornerRadius = 12.dp, boxColor = white, textColor = pink.
 */
@Composable
fun OtpScreenExample() {
    var verifiedOtp by remember { mutableStateOf<String?>(null) }

    Scaffold { padding ->
        Surface(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Enter the 4-digit code sent to your phone",
                    style = MaterialTheme.typography.bodyLarge
                )

                Column(modifier = Modifier.padding(top = 24.dp)) {
                    // Simplest possible call — just defaults.
                    OtpView(
                        otpLength = 4,
                        cornerRadius = 12.dp,
                        boxColor = Color.White,
                        textColor = Color(0xFFE91E63),
                        onOtpComplete = { otp ->
                            verifiedOtp = otp
                            // TODO: call your verify-OTP API here
                        }
                    )
                }

                verifiedOtp?.let {
                    Text(
                        text = "Entered code: $it",
                        modifier = Modifier.padding(top = 16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Example of a 6-digit variant with custom colors, elsewhere in your app:
                // OtpView(
                //     otpLength = 6,
                //     boxSize = 44.dp,
                //     cornerRadius = 8.dp,
                //     boxColor = Color(0xFFF5F5F5),
                //     textColor = Color(0xFF3F51B5),
                //     borderColor = Color(0xFFDDDDDD),
                //     onOtpComplete = { otp -> }
                // )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OtpScreenExamplePreview() {
    MaterialTheme {
        OtpScreenExample()
    }
}