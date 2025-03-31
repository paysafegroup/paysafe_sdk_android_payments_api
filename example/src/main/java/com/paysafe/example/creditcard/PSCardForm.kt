/*
 * Copyright (c) 2025 Paysafe Group
 */

import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.paysafe.android.hostedfields.cardnumber.PSCardNumberView
import com.paysafe.android.hostedfields.cvv.PSCvvView
import com.paysafe.android.hostedfields.expirydate.PSExpiryDatePickerView
import com.paysafe.android.hostedfields.holdername.PSCardholderNameView

@Composable
fun PSCardForm(
    isSubmitEnabled: Boolean,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    cardNumberView: PSCardNumberView,
    cardHolderNameView: PSCardholderNameView,
    expiryDateView: PSExpiryDatePickerView,
    cvvView: PSCvvView,
) {
    val isCardNumberValid = remember { mutableStateOf(false) }
    val isCardholderNameValid = remember { mutableStateOf(false) }
    val isExpiryDateValid = remember { mutableStateOf(false) }
    val isCvvValid = remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        cardNumberView.isValidLiveData.observeForever { valid ->
            isCardNumberValid.value = valid
        }
        cardHolderNameView.isValidLiveData.observeForever { valid ->
            isCardholderNameValid.value = valid
        }
        expiryDateView.isValidLiveData.observeForever { valid ->
            isExpiryDateValid.value = valid
        }
        cvvView.isValidLiveData.observeForever { valid ->
            isCvvValid.value = valid
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        AndroidView(
            factory = {
                cardNumberView.apply { id = View.generateViewId() }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        AndroidView(
            factory = {
                cardHolderNameView.apply { id = View.generateViewId() }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        AndroidView(
            factory = {
                expiryDateView.apply { id = View.generateViewId() }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        AndroidView(
            factory = {
                cvvView.apply { id = View.generateViewId() }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            SubmitButton(
                isEnabled = isSubmitEnabled &&
                        isCardNumberValid.value &&
                        isCardholderNameValid.value &&
                        isExpiryDateValid.value &&
                        isCvvValid.value &&
                        !isLoading,
                isLoading = isLoading,
                onClick = {
                    isLoading = true
                    onSubmit()
                },
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            CancelButton(
                onClick = {
                    onCancel()
                },
                enabled = !isLoading, // Disable cancel button while submitting
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun SubmitButton(
    isEnabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        modifier = modifier
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(text = "Submit")
        }
    }
}

@Composable
fun CancelButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
    ) {
        Text(text = "Cancel")
    }
}
