package com.example.usb

import android.hardware.usb.UsbDevice

enum class FlashStatus {
    DISCONNECTED,
    NO_MEDIA,          // Controller responds but reports Medium Not Present (Sense 02/3A/00)
    CORRUPTED_MBR,     // MBR invalid or raw unpartitioned 0 bytes
    READY_HEALTHY,     // Repaired, 128GB readable and formatted
    REPAIRING          // Operation in progress
}

enum class LogLevel {
    INFO,
    WARN,
    ERROR,
    SUCCESS
}

data class DiagnosticLog(
    val timestamp: String,
    val message: String,
    val level: LogLevel = LogLevel.INFO
)

data class SanDiskDeviceInfo(
    val vendorId: Int = 0x0781,             // SanDisk Vendor ID
    val productId: Int = 0x5591,            // SanDisk Ultra Dual Drive USB-C
    val manufacturer: String = "SanDisk",
    val productName: String = "Ultra Dual Drive USB Type-C / USB-A",
    val serialNumber: String = "4C530001280819114132",
    val capacityBytes: Long = 128L * 1024 * 1024 * 1024, // 128 GB
    val reportedLbaCount: Long = 244140625L, // 128GB / 512 bytes
    val sectorSize: Int = 512,
    val firmwareVersion: String = "1.00",
    val controllerChip: String = "SanDisk 82-00508 / SMI SM3267",
    val flashType: String = "SanDisk 3D TLC NAND Flash",
    val status: FlashStatus = FlashStatus.NO_MEDIA,
    val isSimulated: Boolean = false,
    val usbDevice: UsbDevice? = null
) {
    val formattedCapacity: String = "128.0 جيجابايت"
    val vidHex: String = "0x0781"
    val pidHex: String = "0x5591"
}
