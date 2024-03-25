package com.xunlei.downloadlib.android

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Environment
import android.telephony.TelephonyManager
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.xunlei.download.Downloads
import org.json.JSONException
import org.json.JSONObject
import java.io.File


/**
 * Class: [XLTool]
 *
 * @author why
 * @since 3/22/24
 */
object XLTool {
    enum class NetWorkCarrier {
        UNKNOWN,
        CMCC,
        CU,
        CT
    }

    /**
     *15 位 IMEI, 12 位 mac
     */
    val fetchGuid: String
        @JvmStatic
        get() = "00000000000000000000000000000000"

    val fetchPeerId: String
        @JvmStatic
        get() = "5A30235DC7749A4V"

    @JvmStatic
    fun getNetworkType(context: Context): Int {
        val service = ContextCompat.getSystemService(context, ConnectivityManager::class.java)
        val activeNetworkInfo = service?.activeNetworkInfo ?: return 0
        val type = activeNetworkInfo.type
        if (type == ConnectivityManager.TYPE_WIFI) return 9
        if (type != ConnectivityManager.TYPE_MOBILE) return 5
        val i = when (activeNetworkInfo.subtype) {
            1, 2, 4, 7, 11 -> return 2
            3, 5, 6, 8, 9, 10, 12, 14, 15 -> return 3
            13 -> 4
            else -> 0
        }
        return i
    }

    /**
     * 获取运营商
     */
    @JvmStatic
    @SuppressLint("MissingPermission", "HardwareIds")
    fun getNetWorkCarrier(context: Context): NetWorkCarrier {
        runCatching {
            val service = ContextCompat.getSystemService(context, TelephonyManager::class.java)

            // 移动
            val subscriberId = service?.subscriberId.orEmpty()
            if (subscriberId.startsWith("46000") || subscriberId.startsWith("46002")) {
                return NetWorkCarrier.CMCC
            }
            // 联通
            if (subscriberId.startsWith("46001")) {
                return NetWorkCarrier.CU
            }
            // 移动
            if (subscriberId.startsWith("46003")) {
                return NetWorkCarrier.CT
            }
        }
        return NetWorkCarrier.UNKNOWN
    }

    @JvmStatic
    fun parseJsonMap(str: String): Map<String, Any> {
        val hashMap: MutableMap<String, Any> = HashMap()
        try {
            val sonObject = JSONObject(str)
            val keys = sonObject.keys()
            while (keys.hasNext()) {
                val name = keys.next() as String
                hashMap[name] = sonObject.getString(name)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return hashMap
    }

    /**
     * 判断文件是否在外部储存目录
     */
    fun isSaveExternalStorage(dest: Int, filename: String): Boolean {
        runCatching {
            if (!TextUtils.isEmpty(filename) && dest == Downloads.Impl.DESTINATION_FILE_URI) {
                val uriPath = Uri.parse(filename).path ?: return false
                return !File(uriPath).canonicalPath.startsWith(Environment.getExternalStorageDirectory().canonicalPath)
            }
        }
        return false
    }
}