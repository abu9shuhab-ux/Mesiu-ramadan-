package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.usb.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class UsbRepairViewModel(application: Application) : AndroidViewModel(application) {

    private val usbStorageManager = UsbMassStorageManager(application)
    private val usbPortDetector = UsbPortDetector(application)

    val portDetectionState: StateFlow<UsbPortDetectionState> = usbPortDetector.detectionState

    private val _deviceInfo = MutableStateFlow<SanDiskDeviceInfo?>(null)
    val deviceInfo: StateFlow<SanDiskDeviceInfo?> = _deviceInfo.asStateFlow()

    private val _flashStatus = MutableStateFlow(FlashStatus.DISCONNECTED)
    val flashStatus: StateFlow<FlashStatus> = _flashStatus.asStateFlow()

    private val _repairProgress = MutableStateFlow(0f)
    val repairProgress: StateFlow<Float> = _repairProgress.asStateFlow()

    private val _repairStepTitle = MutableStateFlow("")
    val repairStepTitle: StateFlow<String> = _repairStepTitle.asStateFlow()

    private val _diagnosticLogs = MutableStateFlow<List<DiagnosticLog>>(emptyList())
    val diagnosticLogs: StateFlow<List<DiagnosticLog>> = _diagnosticLogs.asStateFlow()

    init {
        usbPortDetector.startListening(viewModelScope)

        // Observe raw port detector state
        viewModelScope.launch {
            usbPortDetector.detectionState.collect { detection ->
                if (detection.isPhysicalConnectionDetected) {
                    val currentDev = _deviceInfo.value
                    if (currentDev == null || currentDev.status == FlashStatus.DISCONNECTED) {
                        _deviceInfo.value = SanDiskDeviceInfo(
                            productName = detection.deviceName.ifBlank { "SanDisk Ultra Dual Drive USB-C 128GB" },
                            vendorId = 0x0781,
                            productId = 0x5591,
                            status = FlashStatus.NO_MEDIA,
                            isSimulated = detection.device == null,
                            usbDevice = detection.device
                        )
                        _flashStatus.value = FlashStatus.NO_MEDIA
                        addLog(
                            "⚡ تم استشعار وجود الفلاش في منفذ USB-C فوراً عبر متحكم الناقل الفيزيائي (حتى وإن لم يتعرف عليه نظام التشغيل)!",
                            LogLevel.SUCCESS
                        )
                        addLog("معرفات الجهاز الأولية: ${detection.rawDescriptors}", LogLevel.INFO)
                    }
                } else if (_deviceInfo.value?.isSimulated == false) {
                    _deviceInfo.value = null
                    _flashStatus.value = FlashStatus.DISCONNECTED
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        usbPortDetector.stopListening()
    }

    fun addLog(message: String, level: LogLevel = LogLevel.INFO) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val newLog = DiagnosticLog(time, message, level)
        _diagnosticLogs.value = _diagnosticLogs.value + newLog
    }

    fun clearLogs() {
        _diagnosticLogs.value = emptyList()
    }

    fun simulateInstantPortPlug() {
        usbPortDetector.simulateInstantPhysicalAttachment()
    }

    fun runDiagnostics() {
        val dev = _deviceInfo.value ?: return
        viewModelScope.launch {
            _flashStatus.value = FlashStatus.REPAIRING
            addLog("--- بدء فحص وتشخيص قطاعات الفلاش (SCSI Low-Level Diagnostics) ---", LogLevel.INFO)
            val result = usbStorageManager.executeDiagnostics(dev) { msg, lvl ->
                addLog(msg, lvl)
            }
            _flashStatus.value = result
            _deviceInfo.value = dev.copy(status = result)
        }
    }

    fun startRepair() {
        val dev = _deviceInfo.value ?: return
        viewModelScope.launch {
            _flashStatus.value = FlashStatus.REPAIRING
            _repairProgress.value = 0f
            addLog("--- بدء عملية الإصلاح العميق وإعادة بناء قطاع MBR وسعة 128GB ---", LogLevel.INFO)

            val success = usbStorageManager.rebuildMbrAndFormat(
                deviceInfo = dev,
                onProgress = { progress, step ->
                    _repairProgress.value = progress
                    _repairStepTitle.value = step
                },
                addLog = { msg, lvl ->
                    addLog(msg, lvl)
                }
            )

            if (success) {
                _flashStatus.value = FlashStatus.READY_HEALTHY
                _deviceInfo.value = dev.copy(status = FlashStatus.READY_HEALTHY)
                addLog("🎉 نجاح الإصلاح: تم حل مشكلة No Media واستعادة السعة الكاملة 128GB!", LogLevel.SUCCESS)
                addLog("يمكنك الآن استخدام الفلاش ونسخ الملفات ونقله للكمبيوتر بكل سهولة.", LogLevel.SUCCESS)
            } else {
                _flashStatus.value = FlashStatus.NO_MEDIA
                addLog("تعذر الإصلاح البرمجي المباشر. يرجى مراجعة دليل الفيرموير والـ MPTool بالأسفل.", LogLevel.ERROR)
            }
        }
    }

    fun resetToNoMediaForTesting() {
        val dev = _deviceInfo.value ?: return
        _flashStatus.value = FlashStatus.NO_MEDIA
        _deviceInfo.value = dev.copy(status = FlashStatus.NO_MEDIA)
        addLog("تمت إعادة تعيين حالة الفلاش إلى خطأ No Media لأغراض الاختبار والعرض.", LogLevel.WARN)
    }
}
