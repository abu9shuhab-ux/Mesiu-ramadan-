package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.usb.UsbMassStorageManager
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("مصلح فلاش SanDisk", appName)
  }

  @Test
  fun `verify 128GB MBR signature generation`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val manager = UsbMassStorageManager(context)
    val mbr = manager.generate128GbMbrBytes()
    assertEquals(512, mbr.size)
    assertEquals(0x55.toByte(), mbr[510])
    assertEquals(0xAA.toByte(), mbr[511])
  }
}
