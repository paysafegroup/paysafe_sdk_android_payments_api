package com.paysafe.android.hostedfields

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class FieldAndFieldsTest {

    @Test
    fun `Field properties assignment test`() {
        // Arrange
        val placeHolder = "placeholder"
        val accessibilityLabel = "accessibilityLabel"
        val accessibilityErrorMessage = "accessibilityErrorMessage"

        // Act
        val field = Field(placeHolder, accessibilityLabel, accessibilityErrorMessage)

        // Assert
        assertEquals(placeHolder, field.placeHolder)
        assertEquals(accessibilityLabel, field.accessibilityLabel)
        assertEquals(accessibilityErrorMessage, field.accessibilityErrorMessage)
    }

    @Test
    fun `Fields properties assignment test`() {
        // Arrange
        val cardNumber = Field("cardNumberPlaceholder", "cardNumberLabel", "cardNumberError")
        val cardHolderName = Field("cardHolderNamePlaceholder", "cardHolderNameLabel", "cardHolderNameError")
        val expiryDate = Field("expiryDatePlaceholder", "expiryDateLabel", "expiryDateError")
        val cvvField = Field("cvvFieldPlaceholder", "cvvFieldLabel", "cvvFieldError")

        // Act
        val fields = Fields(cardNumber, cardHolderName, expiryDate, cvvField)

        // Assert
        assertEquals(cardNumber, fields.cardNumber)
        assertEquals(cardHolderName, fields.cardHolderName)
        assertEquals(expiryDate, fields.expiryDate)
        assertEquals(cvvField, fields.cvvField)
    }

    @Test
    fun `serialize and deserialize Field`() {
        // Arrange
        val field = Field(
            placeHolder = "placeholder",
            accessibilityLabel = "accessibilityLabel",
            accessibilityErrorMessage = "accessibilityErrorMessage"
        )

        // Act
        val jsonString = Json.encodeToString(field)
        val deserializedField = Json.decodeFromString<Field>(jsonString)

        // Assert
        assertNotNull(jsonString)
        assertEquals(field, deserializedField)
    }

    @Test
    fun `serialize and deserialize Fields`() {
        // Arrange
        val fields = Fields(
            cardNumber = Field(
                placeHolder = "cardNumberPlaceholder",
                accessibilityLabel = "cardNumberLabel",
                accessibilityErrorMessage = "cardNumberError"
            ),
            cardHolderName = Field(
                placeHolder = "cardHolderNamePlaceholder",
                accessibilityLabel = "cardHolderNameLabel",
                accessibilityErrorMessage = "cardHolderNameError"
            ),
            expiryDate = Field(
                placeHolder = "expiryDatePlaceholder",
                accessibilityLabel = "expiryDateLabel",
                accessibilityErrorMessage = "expiryDateError"
            ),
            cvvField = Field(
                placeHolder = "cvvFieldPlaceholder",
                accessibilityLabel = "cvvFieldLabel",
                accessibilityErrorMessage = "cvvFieldError"
            )
        )

        // Act
        val jsonString = Json.encodeToString(fields)
        val deserializedFields = Json.decodeFromString<Fields>(jsonString)

        // Assert
        assertNotNull(jsonString)
        assertEquals(fields, deserializedFields)
    }

    @Test
    fun `serialize Fields with null values`() {
        // Arrange
        val fields = Fields(
            cardNumber = null,
            cardHolderName = Field(
                placeHolder = "cardHolderNamePlaceholder",
                accessibilityLabel = "cardHolderNameLabel",
                accessibilityErrorMessage = "cardHolderNameError"
            ),
            expiryDate = null,
            cvvField = Field(
                placeHolder = "cvvFieldPlaceholder",
                accessibilityLabel = "cvvFieldLabel",
                accessibilityErrorMessage = "cvvFieldError"
            )
        )

        // Act
        val jsonString = Json.encodeToString(fields)
        val deserializedFields = Json.decodeFromString<Fields>(jsonString)

        // Assert
        assertNotNull(jsonString)
        assertEquals(fields, deserializedFields)
    }

    @Test
    fun `Fields with empty strings`() {
        // Arrange
        val fields = Fields(
            cardNumber = Field("", "", ""),
            cardHolderName = Field("", "", ""),
            expiryDate = Field("", "", ""),
            cvvField = Field("", "", "")
        )

        // Act
        val jsonString = Json.encodeToString(fields)
        val deserializedFields = Json.decodeFromString<Fields>(jsonString)

        // Assert
        assertNotNull(jsonString)
        assertEquals(fields, deserializedFields)
    }
}
