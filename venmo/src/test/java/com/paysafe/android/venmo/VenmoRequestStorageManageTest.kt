package com.paysafe.android.venmo

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class VenmoRequestStorageManageTest {

    private val storage = VenmoRequestStorageManage()
    private val context = RuntimeEnvironment.getApplication()

    @Test
    fun `IF store THEN fetch RETURNS the pending request`() {
        // Arrange
        val pendingRequest = "pending-request"

        // Act
        storage.store(context, pendingRequest)

        // Verify
        assertEquals(pendingRequest, storage.fetch(context))
    }

    @Test
    fun `IF clear THEN fetch RETURNS null`() {
        // Arrange
        storage.store(context, "pending-request")

        // Act
        storage.clear(context)

        // Verify
        assertNull(storage.fetch(context))
    }

    @Test
    fun `IF nothing is stored THEN fetch RETURNS null`() {
        // Verify
        assertNull(storage.fetch(context))
    }

    @Test
    fun `IF clear THEN leftover device data is removed with the pending request`() {
        // Arrange
        val preferences = context.getSharedPreferences(
            "venmo_pending_request_prefs",
            Context.MODE_PRIVATE
        )
        preferences.edit()
            .putString("venmo_pending_request", "pending-request")
            .putString("venmo_device_data", """{"correlation_id":"correlation-id"}""")
            .commit()

        // Act
        storage.clear(context)

        // Verify
        assertNull(preferences.getString("venmo_pending_request", null))
        assertNull(preferences.getString("venmo_device_data", null))
        assertNull(storage.fetch(context))
    }
}
