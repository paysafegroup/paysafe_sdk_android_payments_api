/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.logging.domain.model

/**
 * Extra info related to current event for better understanding.
 */
internal sealed class LogPayload {

    /**
     * Simple info message that encapsulates just the message.
     */
    internal data class InfoMessage(

        /** The actual message containing the extra info. */
        val message: String

    ) : LogPayload()

    /**
     * Error message structure that encapsulates the data for an error message.
     */

    internal data class ErrorMessage(
        val code: String,
        val detailedMessage: String,
        val displayMessage: String,
        val name: String,
        val message: String
    ) : LogPayload()

}
