package com.os.operando.advertisingid

import com.google.android.gms.ads.identifier.AdvertisingIdClient
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.PluginRegistry.Registrar
import kotlin.concurrent.thread

class AdvertisingIdPlugin(private val registrar: Registrar) : MethodCallHandler {

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "advertising_id")
            channel.setMethodCallHandler(AdvertisingIdPlugin(registrar))
        }
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "getAdvertisingId" -> thread {
                try {
                    result.success(AdvertisingIdClient.getAdvertisingIdInfo(registrar.context()).id)
                } catch (e: Exception) {
                    result.success("")
                }
            }
            else -> result.notImplemented()
        }
    }
}
