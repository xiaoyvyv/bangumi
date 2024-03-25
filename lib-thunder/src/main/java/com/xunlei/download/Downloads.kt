@file:Suppress("SpellCheckingInspection", "MemberVisibilityCanBePrivate")

package com.xunlei.download

import android.content.Context
import android.net.Uri
import android.provider.BaseColumns
import com.xunlei.downloadlib.android.XLLog

object Downloads {
    private const val SQL_REMOVE_ALL_DOWNLOAD = "notificationpackage=? AND notificationclass=?"

    @JvmStatic
    fun removeAllDownloadsByPackage(
        context: Context,
        notificationPackage: String,
        notificationClass: String,
    ) {
        try {
            context.contentResolver.delete(
                Impl.CONTENT_URI,
                SQL_REMOVE_ALL_DOWNLOAD,
                arrayOf(notificationPackage, notificationClass)
            )
        } catch (e: Exception) {
            XLLog.printStackTrace(e)
        }
    }

    @JvmStatic
    fun removeAllDownloadsByPackage(
        context: Context,
        uri: Uri,
        notificationPackage: String,
        notificationClass: String,
    ) {
        try {
            context.contentResolver.delete(
                uri,
                SQL_REMOVE_ALL_DOWNLOAD,
                arrayOf(notificationPackage, notificationClass)
            )
        } catch (e: Exception) {
            XLLog.printStackTrace(e)
        }
    }

    /**
     * @noinspection unused
     */
    object Impl : BaseColumns {
        const val PERMISSION_ACCESS = "android.permission.ACCESS_DOWNLOAD_MANAGER"
        const val PERMISSION_ACCESS_ADVANCED = "android.permission.ACCESS_DOWNLOAD_MANAGER_ADVANCED"
        const val PERMISSION_ACCESS_ALL = "android.permission.ACCESS_ALL_DOWNLOADS"
        const val PERMISSION_CACHE = "android.permission.ACCESS_CACHE_FILESYSTEM"
        const val PERMISSION_CACHE_NON_PURGEABLE = "android.permission.DOWNLOAD_CACHE_NON_PURGEABLE"
        const val PERMISSION_NO_NOTIFICATION = "android.permission.DOWNLOAD_WITHOUT_NOTIFICATION"
        const val PERMISSION_SEND_INTENTS = "android.permission.SEND_DOWNLOAD_COMPLETED_INTENTS"
        const val PUBLICLY_ACCESSIBLE_DOWNLOADS_URI_SEGMENT = "public_downloads"

        const val ACTION_DOWNLOAD_COMPLETED = "android.intent.action.DOWNLOAD_COMPLETED"
        const val ACTION_NOTIFICATION_CLICKED =
            "android.intent.action.DOWNLOAD_NOTIFICATION_CLICKED"
        const val COLUMN_ADDITION_LX_SPEED = "addition_lx_speed"
        const val COLUMN_ADDITION_VIP_SPEED = "addition_vip_speed"
        const val COLUMN_ALLOWED_NETWORK_TYPES = "allowed_network_types"
        const val COLUMN_ALLOW_AUTO_RESUME = "allow_auto_resume"
        const val COLUMN_ALLOW_METERED = "allow_metered"
        const val COLUMN_ALLOW_RES_TYPES = "allow_res_types"
        const val COLUMN_ALLOW_ROAMING = "allow_roaming"
        const val COLUMN_ALLOW_WRITE = "allow_write"
        const val COLUMN_APK_PACKAGE = "apk_package"
        const val COLUMN_APK_VERSION = "apk_version"
        const val COLUMN_APP_DATA = "entity"
        const val COLUMN_BT_INFO_HASH = "etag"
        const val COLUMN_BT_PARENT_ID = "bt_parent_id"
        const val COLUMN_BT_REAL_SUB_INDEX = "bt_real_sub_index"
        const val COLUMN_BT_SELECT_SET = "bt_select_set"
        const val COLUMN_BT_SUB_INDEX = "bt_sub_index"
        const val COLUMN_BT_SUB_IS_SELECTED = "bt_sub_is_selected"
        const val COLUMN_BYPASS_RECOMMENDED_SIZE_LIMIT = "bypass_recommended_size_limit"
        const val COLUMN_CDN_SPEED = "cdn_speed"
        const val COLUMN_CHANGE_ORIGIN_URL = "change_origin_url"
        const val COLUMN_CID = "cid"
        const val COLUMN_CONTROL = "control"
        const val COLUMN_COOKIE_DATA = "cookiedata"
        const val COLUMN_CREATE_TIME = "create_time"
        const val COLUMN_CURRENT_BYTES = "current_bytes"
        const val COLUMN_CUSTOM_FLAGS = "custom_flags"
        const val COLUMN_DCDN_RECEIVE_SIZE = "dcdn_receive_size"
        const val COLUMN_DCDN_SPEED = "dcdn_speed"
        const val COLUMN_DELETED = "deleted"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_DESTINATION = "destination"
        const val COLUMN_DOWNLOAD_DURATION = "download_duration"
        const val COLUMN_DOWNLOAD_FILE_COUNT = "download_file_count"
        const val COLUMN_DOWNLOAD_SPEED = "download_speed"
        const val COLUMN_ERROR_MSG = "errorMsg"
        const val COLUMN_EXTRA = "extra"
        const val COLUMN_FAILED_CONNECTIONS = "numfailed"
        const val COLUMN_FILE_NAME_HINT = "hint"
        const val COLUMN_FIRST_MEDIA_STATE = "fm_state"
        const val COLUMN_GCID = "gcid"
        const val COLUMN_GROUP_ID = "group_id"
        const val COLUMN_GROUP_PRIORITY = "group_priority"
        const val COLUMN_IS_DCDN_SPEEDUP = "is_dcdn_speedup"
        const val COLUMN_IS_LX_SPEEDUP = "is_lx_speedup"
        const val COLUMN_IS_PUBLIC_API = "is_public_api"
        const val COLUMN_IS_VIP_SPEEDUP = "is_vip_speedup"
        const val COLUMN_IS_VISIBLE_IN_DOWNLOADS_UI = "is_visible_in_downloads_ui"
        const val COLUMN_LAN_ACC_STATE = "lan_acc_state"
        const val COLUMN_LAN_PEER_INFO = "lan_peer_info"
        const val COLUMN_LAST_MODIFICATION = "lastmod"
        const val COLUMN_LAST_UPDATESRC = "lastUpdateSrc"
        const val COLUMN_LX_PROGRESS = "lx_progress"
        const val COLUMN_LX_RECEIVE_SIZE = "lx_receive_size"
        const val COLUMN_LX_STATUS = "lx_status"
        const val COLUMN_MEDIAPROVIDER_URI = "mediaprovider_uri"
        const val COLUMN_MEDIA_SCANNED = "scanned"
        const val COLUMN_MIME_TYPE = "mimetype"
        const val COLUMN_NOTIFICATION_CLASS = "notificationclass"
        const val COLUMN_NOTIFICATION_EXTRAS = "notificationextras"
        const val COLUMN_NOTIFICATION_PACKAGE = "notificationpackage"
        const val COLUMN_NO_INTEGRITY = "no_integrity"
        const val COLUMN_ORIGIN_ERRCODE = "origin_errcode"
        const val COLUMN_ORIGIN_RECEIVE_SIZE = "origin_receive_size"
        const val COLUMN_ORIGIN_SPEED = "origin_speed"
        const val COLUMN_OTHER_UID = "otheruid"
        const val COLUMN_P2P_RECEIVE_SIZE = "p2p_receive_size"
        const val COLUMN_P2P_SPEED = "p2p_speed"
        const val COLUMN_P2S_RECEIVE_SIZE = "p2s_receive_size"
        const val COLUMN_P2S_SPEED = "p2s_speed"
        const val COLUMN_PLAY_MODE = "play_mode"
        const val COLUMN_PREMIUM_BYTES = "premium_bytes"
        const val COLUMN_PREMIUM_COUNT = "premium_count"
        const val COLUMN_PREMIUM_EMERGENCY = "premium_emergency"
        const val COLUMN_PREMIUM_USING = "premium_using"
        const val COLUMN_PRIORITY = "priority"
        const val COLUMN_RANGE_INFO = "range_info"
        const val COLUMN_REFERER = "referer"
        const val COLUMN_RES_NON_VIP_TOTAL = "res_non_vip_total"
        const val COLUMN_RES_NON_VIP_USED_TOTAL = "res_non_vip_used_total"
        const val COLUMN_RES_TOTAL = "res_total"
        const val COLUMN_RES_USED_TOTAL = "res_used_total"
        const val COLUMN_SEQ_ID = "seq_id"
        const val COLUMN_SLOW_ACC_ERRNO = "slow_acc_errno"
        const val COLUMN_SLOW_ACC_SPEED = "slow_acc_speed"
        const val COLUMN_SLOW_ACC_STATUS = "slow_acc_status"
        const val COLUMN_STATUS = "status"
        const val COLUMN_SYNCRO_LX_TASK_TO_SERVER = "syncro_lxtask2server"
        const val COLUMN_TASK_ACC_TYPE = "acc_type"
        const val COLUMN_TASK_CARD_ID = "card_id"
        const val COLUMN_TASK_MAX_DOWNLOAD_SPEED = "task_max_download_speed"
        const val COLUMN_TASK_PRODUCT_TYPE = "product_type"
        const val COLUMN_TASK_TOKEN = "task_token"
        const val COLUMN_TASK_TYPE = "task_type"
        const val COLUMN_TASK_TYPE_EXT = "task_type_ext"
        const val COLUMN_TITLE = "title"
        const val COLUMN_TOTAL_BYTES = "total_bytes"
        const val COLUMN_TOTAL_FILE_COUNT = "total_file_count"
        const val COLUMN_URI = "uri"
        const val COLUMN_USER_AGENT = "useragent"
        const val COLUMN_VIP_ERRNO = "vip_errno"
        const val COLUMN_VIP_RECEIVE_SIZE = "vip_receive_size"
        const val COLUMN_VIP_STATUS = "vip_status"
        const val COLUMN_VIP_TRIAL_ERRNO = "vip_trial_errno"
        const val COLUMN_VIP_TRIAL_STATUS = "vip_trial_status"
        const val COLUMN_VISIBILITY = "visibility"
        const val COLUMN_XL_ORIGIN = "xl_origin"
        const val COLUMN_XUNLEI_SPDY = "xunlei_spdy"
        const val CONTROL_INVALID_TASK = 2
        const val CONTROL_PAUSED = 1
        const val CONTROL_RUN = 0
        const val CONTROL_SUSPEND = 10

        const val DESTINATION_EXTERNAL = 0
        const val DESTINATION_CACHE_PARTITION = 1
        const val DESTINATION_CACHE_PARTITION_PURGEABLE = 2
        const val DESTINATION_CACHE_PARTITION_NOROAMING = 3
        const val DESTINATION_FILE_URI = 4
        const val DESTINATION_SYSTEMCACHE_PARTITION = 5
        const val DESTINATION_NON_DOWNLOADMANAGER_DOWNLOAD = 6

        const val LAST_UPDATESRC_DONT_NOTIFY_DOWNLOADSVC = 1
        const val LAST_UPDATESRC_NOT_RELEVANT = 0
        const val MIN_ARTIFICIAL_ERROR_STATUS = 488

        const val STATUS_INSERT = 0
        const val STATUS_DELETE = 1
        const val STATUS_PENDING = 190
        const val STATUS_PENDING_PLAY_STAT = 191
        const val STATUS_RUNNING = 192
        const val STATUS_PAUSED_BY_APP = 193
        const val STATUS_WAITING_TO_RETRY = 194
        const val STATUS_WAITING_FOR_NETWORK = 195
        const val STATUS_QUEUED_FOR_WIFI = 196
        const val STATUS_INSUFFICIENT_SPACE_ERROR = 198
        const val STATUS_DEVICE_NOT_FOUND_ERROR = 199
        const val STATUS_SUCCESS = 200
        const val STATUS_BAD_REQUEST = 400
        const val STATUS_NOT_ACCEPTABLE = 406
        const val STATUS_LENGTH_REQUIRED = 411
        const val STATUS_PRECONDITION_FAILED = 412
        const val STATUS_FILE_ALREADY_EXISTS_ERROR = 488
        const val STATUS_CANNOT_RESUME = 489
        const val STATUS_CANCELED = 490
        const val STATUS_UNKNOWN_ERROR = 491
        const val STATUS_FILE_ERROR = 492
        const val STATUS_UNHANDLED_REDIRECT = 493
        const val STATUS_UNHANDLED_HTTP_CODE = 494
        const val STATUS_HTTP_DATA_ERROR = 495
        const val STATUS_HTTP_EXCEPTION = 496
        const val STATUS_TOO_MANY_REDIRECTS = 497
        const val STATUS_BLOCKED = 498
        const val STATUS_XUNLEI_SPDY_EXCEPTION = 499
        const val STATUS_PEER_NOT_FOUND_ERROR = 500
        const val STATUS_TIME_OUT = 501
        const val STATUS_DOWNLOADSDK_NOT_INIT = 502
        const val STATUS_LX_VIP_ERROR_START = 600
        const val STATUS_TASK_INVALID = 601

        const val VISIBILITY_HIDDEN = 2
        const val VISIBILITY_VISIBLE = 0
        const val VISIBILITY_VISIBLE_NOTIFY_COMPLETED = 1
        const val _DATA = "_data"

        @JvmStatic
        val CONTENT_URI by lazy {
            requireNotNull(Uri.parse("content://downloads/my_downloads"))
        }

        @JvmStatic
        val ALL_DOWNLOADS_CONTENT_URI by lazy {
            requireNotNull(Uri.parse("content://downloads/all_downloads"))
        }

        @JvmStatic
        val PUBLICLY_ACCESSIBLE_DOWNLOADS_URI by lazy {
            requireNotNull(Uri.parse("content://downloads/public_downloads"))
        }

        fun isNotificationToBeDisplayed(status: Int): Boolean {
            return status == 1 || status == 3
        }

        fun isStatusClientError(status: Int): Boolean {
            return status in 400..499
        }

        fun isStatusCompleted(status: Int): Boolean {
            return (status in 200..299) || status >= 400
        }

        fun isStatusError(status: Int): Boolean {
            return status >= 400
        }

        fun isStatusInformational(status: Int): Boolean {
            return status in 100..200
        }

        fun isStatusServerError(status: Int): Boolean {
            return status in 500..599
        }

        fun isStatusSuccess(status: Int): Boolean {
            return status in 200..299
        }

        fun statusToString(statusCode: Int): String {
            return when (statusCode) {
                STATUS_PENDING -> "PENDING"
                STATUS_BAD_REQUEST -> "BAD_REQUEST"
                STATUS_NOT_ACCEPTABLE -> "NOT_ACCEPTABLE"
                STATUS_LENGTH_REQUIRED -> "LENGTH_REQUIRED"
                STATUS_PRECONDITION_FAILED -> "PRECONDITION_FAILED"
                STATUS_RUNNING -> "RUNNING"
                STATUS_PAUSED_BY_APP -> "PAUSED_BY_APP"
                STATUS_WAITING_TO_RETRY -> "WAITING_TO_RETRY"
                STATUS_WAITING_FOR_NETWORK -> "WAITING_FOR_NETWORK"
                STATUS_QUEUED_FOR_WIFI -> "QUEUED_FOR_WIFI"
                STATUS_INSUFFICIENT_SPACE_ERROR -> "INSUFFICIENT_SPACE_ERROR"
                STATUS_DEVICE_NOT_FOUND_ERROR -> "DEVICE_NOT_FOUND_ERROR"
                STATUS_SUCCESS -> "SUCCESS"
                STATUS_FILE_ALREADY_EXISTS_ERROR -> "FILE_ALREADY_EXISTS_ERROR"
                STATUS_CANNOT_RESUME -> "CANNOT_RESUME"
                STATUS_CANCELED -> "CANCELED"
                STATUS_UNKNOWN_ERROR -> "UNKNOWN_ERROR"
                STATUS_FILE_ERROR -> "FILE_ERROR"
                STATUS_UNHANDLED_REDIRECT -> "UNHANDLED_REDIRECT"
                STATUS_UNHANDLED_HTTP_CODE -> "UNHANDLED_HTTP_CODE"
                STATUS_HTTP_DATA_ERROR -> "HTTP_DATA_ERROR"
                STATUS_HTTP_EXCEPTION -> "HTTP_EXCEPTION"
                STATUS_TOO_MANY_REDIRECTS -> "TOO_MANY_REDIRECTS"
                STATUS_BLOCKED -> "BLOCKED"
                STATUS_XUNLEI_SPDY_EXCEPTION -> "XUNLEI_SPDY_EXCEPTION"
                STATUS_PEER_NOT_FOUND_ERROR -> "PEER_NOT_FOUND_ERROR"
                STATUS_TIME_OUT -> "time_out"
                else -> statusCode.toString()
            }
        }

        object RequestHeaders {
            const val COLUMN_DOWNLOAD_ID = "download_id"
            const val COLUMN_HEADER = "header"
            const val COLUMN_VALUE = "value"
            const val HEADERS_DB_TABLE = "request_headers"
            const val INSERT_KEY_PREFIX = "http_header_"
            const val URI_SEGMENT = "headers"
        }
    }
}