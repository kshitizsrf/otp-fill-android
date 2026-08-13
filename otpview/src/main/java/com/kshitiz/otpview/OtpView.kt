
package com.kshitiz.otpview

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A reusable, auto-filling OTP input for Jetpack Compose.
 *
 * - Each digit renders in its own box with a "small to big" pop-in animation as it's typed
 *   or auto-filled from an incoming SMS.
 * - SMS auto-fill works out of the box via the Compose Autofill APIs (AutofillType.SmsOtpCode).
 *   No extra library, permission, or BroadcastReceiver is required — it relies on the device's
 *   autofill service (e.g. "Autofill with Google") to detect the code from an incoming SMS.
 *
 * Usage:
 * ```
 * OtpView(
 *     otpLength = 4,
 *     cornerRadius = 12.dp,
 *     boxColor = Color.White,
 *     textColor = Color(0xFFE91E63),
 *     onOtpComplete = { otp -> /* verify otp */ }
 * )
 * ```
 *
 * @param otpLength number of digits in the OTP (default 4)
 * @param boxSize size (width & height) of each digit box
 * @param boxSpacing horizontal gap between boxes
 * @param cornerRadius corner radius of each box (default 12.dp)
 * @param boxColor background color of each box (default white)
 * @param textColor color of the digit text (default pink)
 * @param borderColor border color of an unfocused/empty box
 * @param focusedBorderColor border color of the box currently awaiting input (defaults to textColor)
 * @param fontSize size of the digit text
 * @param autoFocus whether to request focus (and open the keyboard) as soon as this composable appears
 * @param onOtpComplete called once, with the full code, as soon as the last digit is filled
 */
@Composable
fun OtpView(
    modifier: Modifier = Modifier,
    otpLength: Int = 4,
    boxSize: Dp = 48.dp,
    boxSpacing: Dp = 10.dp,
    cornerRadius: Dp = 12.dp,
    boxColor: Color = Color.White,
    textColor: Color = Color(0xFFE91E63), // pink
    borderColor: Color = Color(0xFFE0E0E0),
    focusedBorderColor: Color = textColor,
    fontSize: TextUnit = 22.sp,
    autoFocus: Boolean = true,
    onOtpComplete: (String) -> Unit = {}
) {
    var otpText by rememberSaveable { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }
    val autofill = LocalAutofill.current
    val autofillNode = remember {
        AutofillNode(
            autofillTypes = listOf(AutofillType.SmsOtpCode),
            onFill = { value ->
                otpText = value.filter { it.isDigit() }.take(otpLength)
            }
        )
    }
    LocalAutofillTree.current += autofillNode

    LaunchedEffect(otpText) {
        if (otpText.length == otpLength) {
            onOtpComplete(otpText)
        }
    }

    LaunchedEffect(Unit) {
        if (autoFocus) focusRequester.requestFocus()
    }

    Box(modifier = modifier) {
        // Visible row of animated digit boxes.
        Row(
            horizontalArrangement = Arrangement.spacedBy(boxSpacing)
        ) {
            for (index in 0 until otpLength) {
                val digit = otpText.getOrNull(index)?.toString() ?: ""
                val isActiveBox = index == otpText.length

                val scale by animateFloatAsState(
                    targetValue = if (digit.isNotEmpty()) 1f else 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "otpDigitScale$index"
                )

                Box(
                    modifier = Modifier
                        .size(boxSize)
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(boxColor)
                        .border(
                            width = 1.5.dp,
                            color = if (isActiveBox) focusedBorderColor else borderColor,
                            shape = RoundedCornerShape(cornerRadius)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = digit,
                        color = textColor,
                        fontSize = fontSize,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                    )
                }
            }
        }

        // Invisible text field overlaid on top of the boxes.
        // Handles real keyboard input, focus, and SMS autofill; renders no visible text/cursor.
        BasicTextField(
            value = otpText,
            onValueChange = { newValue ->
                val filtered = newValue.filter { it.isDigit() }
                if (filtered.length <= otpLength) {
                    otpText = filtered
                }
            },
            modifier = Modifier
                .matchParentSize()
                .focusRequester(focusRequester)
                .onGloballyPositioned { coordinates ->
                    autofillNode.boundingBox = coordinates.boundsInWindow()
                }
                .onFocusChanged { focusState ->
                    autofill?.run {
                        if (focusState.isFocused) {
                            requestAutofillForNode(autofillNode)
                        } else {
                            cancelAutofillForNode(autofillNode)
                        }
                    }
                },
            textStyle = TextStyle(color = Color.Transparent, fontSize = 1.sp),
            cursorBrush = SolidColor(Color.Transparent),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Done
            )
        )
    }
}