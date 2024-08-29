package com.paysafe.android.tokenization.domain.model.paymentHandle

import org.junit.Assert.assertEquals
import org.junit.Test
class SimulatorTypeTest {
    @Test
    fun `test EXTERNAL toCoreModuleSimulatorType`() {
        val simulatorType = SimulatorType.EXTERNAL
        val coreModuleSimulatorType = simulatorType.toCoreModuleSimulatorType()
        assertEquals(com.paysafe.android.core.data.service.SimulatorType.EXTERNAL, coreModuleSimulatorType)
    }

    @Test
    fun `test INTERNAL toCoreModuleSimulatorType`() {
        val simulatorType = SimulatorType.INTERNAL
        val coreModuleSimulatorType = simulatorType.toCoreModuleSimulatorType()
        assertEquals(com.paysafe.android.core.data.service.SimulatorType.INTERNAL, coreModuleSimulatorType)
    }
}