package dev.restifo.hide_and_seek.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle

/**
 * Utility class for handling environment variables in Android.
 */
object EnvironmentUtil {
    /**
     * Gets an environment variable from the system environment or from the manifest metadata.
     * 
     * @param context The application context
     * @param name The name of the environment variable
     * @param defaultValue The default value to return if the environment variable is not found
     * @return The value of the environment variable, or the default value if not found
     */
    fun getEnvironmentVariable(context: Context, name: String, defaultValue: String = ""): String {
        // First, try to get from system environment
        val envValue = System.getenv(name)
        if (!envValue.isNullOrEmpty()) {
            return envValue
        }
        
        // If not found, try to get from manifest metadata
        return try {
            val appInfo: ApplicationInfo = context.packageManager.getApplicationInfo(
                context.packageName, PackageManager.GET_META_DATA
            )
            val bundle: Bundle = appInfo.metaData ?: Bundle()
            bundle.getString(name) ?: defaultValue
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }
}
