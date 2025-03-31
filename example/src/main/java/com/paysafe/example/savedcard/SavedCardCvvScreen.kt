/*
 * Copyright (c) 2025 Paysafe Group
 */

import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.paysafe.android.hostedfields.cvv.PSCvvView
import com.paysafe.example.savedcard.FragmentSavedCardCvvArgs

@Composable
fun SavedCardCvvScreen(
    args: FragmentSavedCardCvvArgs,
    onBackClick: () -> Unit,
    onPlaceOrderClick: () -> Unit,
    onCancelClick: () -> Unit,
    isPlaceOrderEnabled: Boolean,
    cvvView: PSCvvView,
) {

    val cardType = args.savedCardChosen.cardBrandType
    val lastDigits = args.savedCardChosen.lastDigits
    val cardBrandRes = args.savedCardChosen.cardBrandRes
    val holderName = args.savedCardChosen.holderName
    val expiryDate = args.savedCardChosen.expiryDate
    val totalRawAmount = args.productForCheckout.totalRaw

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                    contentDescription = "Back"
                )
            }
            Image(painter = painterResource(id = cardBrandRes), contentDescription = "Card Brand")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Saved Card (${cardType.value})",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(text = "**** **** **** $lastDigits", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Cardholder: $holderName", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Expiry Date: $expiryDate", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(24.dp))

        AndroidView(
            factory = {
                cvvView.apply {
                    this.cardType = cardType
                    id = View.generateViewId()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 28.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onPlaceOrderClick()
            },
            enabled = isPlaceOrderEnabled,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(text = "Place Order ($$totalRawAmount)")
        }

        TextButton(
            onClick = onCancelClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        ) {
            Text(text = "Cancel")
        }
    }
}