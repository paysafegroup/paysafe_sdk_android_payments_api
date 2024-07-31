package com.paysafe.android.tokenization.domain.model.paymentHandle

enum class SimulatorType {
    EXTERNAL, INTERNAL;

    fun toCoreModuleSimulatorType(): com.paysafe.android.core.data.service.SimulatorType {
        return when (this) {
            EXTERNAL -> com.paysafe.android.core.data.service.SimulatorType.EXTERNAL
            INTERNAL -> com.paysafe.android.core.data.service.SimulatorType.INTERNAL
        }
    }
}