/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.logging.domain.model

/**
 * Structure that encapsulates the information needed to send an event log.
 */
sealed interface LogEvent {

    /** Log type. */
    val type: LogType

    /** Log integration type. */
    val integrationType: LogIntegrationType

    /** True if it's a 3DS event, false otherwise. */
    val is3DSEvent: Boolean

    /**
     * Structure that encapsulates a simple string message into the event log.
     */
    data class InfoMessage(
        override val type: LogType,
        override val integrationType: LogIntegrationType,

        /** The message that will be logged. */
        val message: String,

        override val is3DSEvent: Boolean = false
    ) : LogEvent

    /**
     * Structure that encapsulates a [LogErrorMessage] into the event log.
     */
    data class ErrorMessage(
        override val type: LogType,
        override val integrationType: LogIntegrationType,

        /** The [LogErrorMessage] that will be logged. */
        val errorMessage: LogErrorMessage,

        override val is3DSEvent: Boolean = false
    ) : LogEvent

}
