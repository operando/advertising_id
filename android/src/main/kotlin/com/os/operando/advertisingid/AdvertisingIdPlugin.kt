package com.os.operando.advertisingid

import android.app.Activity
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import kotlin.concurrent.thread

class AdvertisingIdPlugin() : FlutterPlugin, ActivityAware, MethodCallHandler {
    private var activity: Activity? = null

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "advertising_id")
            val plugin = AdvertisingIdPlugin()
            plugin.activity = registrar.activity()
            channel.setMethodCallHandler(plugin)
        }
    }

    override fun onAttachedToEngine(binding: FlutterPluginBinding) {
        val channel = MethodChannel(binding.binaryMessenger, "advertising_id")
        channel.setMethodCallHandler(this)
    }

    override fun onDetachedFromEngine(binding: FlutterPluginBinding) {
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    }

    override fun onDetachedFromActivity() {
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        if (activity == null) {
            result.error("noActivity", "Activity is null", null)
            return
        }
        val a = activity!!
        when (call.method) {
            "getAdvertisingId" -> thread {
                try {
                    val id = AdvertisingIdClient.getAdvertisingIdInfo(a).id
                    a.runOnUiThread {
                        result.success(id)
                    }
                } catch (e: Exception) {
                    a.runOnUiThread {
                        result.error(e.javaClass.canonicalName, e.localizedMessage, null)
                    }
                }
            }
            "isLimitAdTrackingEnabled" -> thread {
                try {
                    val isLimitAdTrackingEnabled =
                        AdvertisingIdClient.getAdvertisingIdInfo(a).isLimitAdTrackingEnabled
                    a.runOnUiThread {
                        result.success(isLimitAdTrackingEnabled)
                    }
                } catch (e: Exception) {
                    a.runOnUiThread {
                        result.error(e.javaClass.canonicalName, e.localizedMessage, null)
                    }
                }
            }
            else -> result.notImplemented()
        }
    }
}
