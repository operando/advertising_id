package com.os.operando.advertisingid

import android.app.Activity
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.PluginRegistry.Registrar
import kotlin.concurrent.thread

class AdvertisingIdPlugin() : FlutterPlugin, ActivityAware, MethodCallHandler {
    private lateinit var activity: Activity

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
        when (call.method) {
            "getAdvertisingId" -> thread {
                try {
                    val id = AdvertisingIdClient.getAdvertisingIdInfo(activity).id
                    activity.runOnUiThread {
                        result.success(id)
                    }
                } catch (e: Exception) {
                    activity.runOnUiThread {
                        result.error(e.javaClass.canonicalName, e.localizedMessage, null)
                    }
                }
            }
            "isLimitAdTrackingEnabled" -> thread {
                try {
                    val isLimitAdTrackingEnabled = AdvertisingIdClient.getAdvertisingIdInfo(activity).isLimitAdTrackingEnabled
                    activity.runOnUiThread {
                        result.success(isLimitAdTrackingEnabled)
                    }
                } catch (e: Exception) {
                    activity.runOnUiThread {
                        result.error(e.javaClass.canonicalName, e.localizedMessage, null)
                    }
                }
            }
            else -> result.notImplemented()
        }
    }
}
