package com.os.operando.advertisingid

import android.app.Activity
import android.os.Build
import android.provider.Settings.Secure
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.appset.AppSet
import com.google.android.gms.appset.AppSetIdClient
import com.google.android.gms.appset.AppSetIdInfo
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
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
        TODO("Not yet implemented")
    }

    private fun getAndroidId(a: Activity): AndroidIdResult {
        return try {
            val androidId = Secure.getString(a.contentResolver, Secure.ANDROID_ID);
            AndroidIdResult.Success(androidId)
        } catch (e: Exception) {
            val errorCode = e.javaClass.canonicalName ?: "UnknownError"
            AndroidIdResult.Error(errorCode, e.localizedMessage)
        }
    }

    private fun fetchAppSetId(a: Activity): AppSetIdResult {
        return try {
            val client: AppSetIdClient = AppSet.getClient(a)
            val appSetIdInfoTask: Task<AppSetIdInfo> = client.appSetIdInfo
            val appSetIdInfo = Tasks.await(appSetIdInfoTask)
            AppSetIdResult.Success(appSetIdInfo.id)
        } catch (e: Exception) {
            val errorCode = e.javaClass.canonicalName ?: "UnknownError"
            AppSetIdResult.Error(errorCode, e.localizedMessage)
        }
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        if (activity == null) {
            result.error("noActivity", "Activity is null", null)
            return
        }
        val a = activity!!
        when (call.method) {
            "getId" -> thread {
                try {
                    val isLimitAdTrackingEnabled =
                        AdvertisingIdClient.getAdvertisingIdInfo(a).isLimitAdTrackingEnabled;
                    if (!isLimitAdTrackingEnabled) {
                        val id = AdvertisingIdClient.getAdvertisingIdInfo(a).id
                        a.runOnUiThread {
                            result.success(id)
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            when (val appSetIdResult: AppSetIdResult = fetchAppSetId(a)) {
                                is AppSetIdResult.Success -> {
                                    a.runOnUiThread {
                                        result.success(appSetIdResult.appSetId);
                                    }
                                }

                                is AppSetIdResult.Error -> {
                                    result.error(
                                        appSetIdResult.errorCode,
                                        appSetIdResult.errorMessage,
                                        null,
                                    );
                                }
                            }
                        } else {
                            when (val androidIdResult: AndroidIdResult = getAndroidId(a)) {
                                is AndroidIdResult.Success -> {
                                    a.runOnUiThread {
                                        result.success(androidIdResult.androidId);
                                    }
                                }

                                is AndroidIdResult.Error -> {
                                    a.runOnUiThread {
                                        result.error(
                                            androidIdResult.errorCode,
                                            androidIdResult.errorMessage,
                                            null,
                                        );
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    a.runOnUiThread {
                        e.javaClass.canonicalName?.let {
                            result.error(
                                it, e.localizedMessage, null
                            )
                        }
                    }
                }
            }

            "getAdvertisingId" -> thread {
                try {
                    val id = AdvertisingIdClient.getAdvertisingIdInfo(a).id
                    a.runOnUiThread {
                        result.success(id)
                    }
                } catch (e: Exception) {
                    a.runOnUiThread {
                        e.javaClass.canonicalName?.let {
                            result.error(
                                it, e.localizedMessage, null
                            )
                        }
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
                        e.javaClass.canonicalName?.let {
                            result.error(
                                it, e.localizedMessage, null
                            )
                        }
                    }
                }
            }

            "getAndroidId" -> thread {
                when (val androidIdResult: AndroidIdResult = getAndroidId(a)) {
                    is AndroidIdResult.Success -> {
                        a.runOnUiThread {
                            result.success(androidIdResult.androidId);
                        }
                    }

                    is AndroidIdResult.Error -> {
                        a.runOnUiThread {
                            result.error(
                                androidIdResult.errorCode,
                                androidIdResult.errorMessage,
                                null,
                            );
                        }
                    }
                }
            }

            "getAppSetId" -> thread {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val client: AppSetIdClient = AppSet.getClient(a);
                    val appSetIdInfoTask: Task<AppSetIdInfo> = client.appSetIdInfo;
                    appSetIdInfoTask.addOnSuccessListener {
                        val id = it.id;
                        a.runOnUiThread {
                            result.success(id);
                        }
                    }.addOnFailureListener { e: Exception ->
                        a.runOnUiThread {
                            e.javaClass.canonicalName?.let {
                                result.error(it, e.localizedMessage, null);
                            }
                        }
                    }
                } else {
                    a.runOnUiThread {
                        result.error(
                            "Unsupported",
                            "App Set ID is not supported on this Android version",
                            null
                        )
                    }
                }
            }

            else -> result.notImplemented()
        }
    }
}
