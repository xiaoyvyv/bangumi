@file:Suppress("SpellCheckingInspection")

package com.xunlei.downloadlib.parameter


object XLConstant {
    @Retention(AnnotationRetention.SOURCE)
    annotation class BtSwitch {
        companion object {
            const val ALL = -1
            const val DHT = 1
            const val NONE = 0
        }
    }

    @Retention(AnnotationRetention.SOURCE)
    annotation class EmuleSwitch {
        companion object {
            const val ALL = -1
            const val NONE = 0
        }
    }

    @Retention(AnnotationRetention.SOURCE)
    annotation class QuickInfoState {
        companion object {
            const val QI_FAILED = 3
            const val QI_FINISH = 2
            const val QI_STOP = 0
            const val QI_TRY = 1
        }
    }

    @Retention(AnnotationRetention.SOURCE)
    annotation class ResourceType {
        companion object {
            const val ALL = -1
            const val NONE = 0
            const val ORIGIN = 1
            const val MIRROR = 2
            const val SIMILAR = 4
            const val P2P = 8
            const val DCDN = 16
            const val TORRENT = 32
        }
    }

    @Retention(AnnotationRetention.SOURCE)
    annotation class XLTaskStatus {
        companion object {
            const val TASK_IDLE = 0
            const val TASK_RUNNING = 1
            const val TASK_SUCCESS = 2
            const val TASK_FAILED = 3
            const val TASK_STOPPED = 4
        }
    }

    @Retention(AnnotationRetention.SOURCE)
    annotation class XLOriginResState {
        companion object {
            const val ORIGIN_RES_UNUSED = 0
            const val ORIGIN_RES_CHECKING = 1
            const val ORIGIN_RES_VALID_LINK = 2
            const val ORIGIN_RES_DEATH_LINK = 3
        }
    }

    @Retention(AnnotationRetention.SOURCE)
    annotation class XLTaskType {
        companion object {
            const val P2SP_TASK_TYP = 1
        }
    }

    @Retention(AnnotationRetention.SOURCE)
    annotation class XLErrorCode {
        companion object {
            const val ADD_RESOURCE_ERROR = 9122
            const val ALREADY_INIT = 9101
            const val APPNAME_APPKEY_ERROR = 9116
            const val ASYN_FILE_E_BASE = 111300
            const val ASYN_FILE_E_EMPTY_FILE = 111305
            const val ASYN_FILE_E_FILE_CLOSING = 111308
            const val ASYN_FILE_E_FILE_NOT_OPEN = 111303
            const val ASYN_FILE_E_FILE_REOPEN = 111304
            const val ASYN_FILE_E_FILE_SIZE_LESS = 111306
            const val ASYN_FILE_E_OP_BUSY = 111302
            const val ASYN_FILE_E_OP_NONE = 111301
            const val ASYN_FILE_E_TOO_MUCH_DATA = 111307
            const val BAD_DIR_PATH = 111083
            const val BT_SUB_TASK_NOT_SELECT = 9306
            const val BUFFER_OVERFLOW = 111039
            const val COMMON_ERRCODE_BASE = 111024
            const val CONF_MGR_ERRCODE_BASE = 111159
            const val CONTINUE_NO_NAME = 9115
            const val CORRECT_BYTES_TOO_MUCH = 111181
            const val CORRECT_CDN_ERROR = 111180
            const val CORRECT_TIMES_TOO_MUCH = 111179
            const val CREATE_FILE_FAIL = 111139
            const val CREATE_THREAD_ERROR = 9117
            const val DATA_MGR_ERRCODE_BASE = 111119
            const val DISK_FULL = 9110
            const val DISPATCHER_ERRCODE_BASE = 111118
            const val DNS_INVALID_ADDR = 111078
            const val DNS_NO_SERVER = 111077
            const val DOWNLOAD_MANAGER_ERROR = 9900
            const val DYNAMIC_PARAM_FAIL = 9114
            const val ERROR_INVALID_INADDR = 111050
            const val ERR_DPLAY_ALL_SEND_COMPLETE = 118000
            const val ERR_DPLAY_BROKEN_SOCKET_RECV = 118307
            const val ERR_DPLAY_BROKEN_SOCKET_SEND = 118306
            const val ERR_DPLAY_CLIENT_ACTIVE_DISCONNECT = 118001
            const val ERR_DPLAY_DO_DOWNLOAD_FAIL = 118305
            const val ERR_DPLAY_DO_READFILE_FAIL = 118311
            const val ERR_DPLAY_EV_SEND_TIMTOUT = 118310
            const val ERR_DPLAY_HANDLE_DOWNLOAD_FAILED = 118302
            const val ERR_DPLAY_NOT_FOUND = 118005
            const val ERR_DPLAY_PLAY_FILE_NOT_EXIST = 118304
            const val ERR_DPLAY_RECV_STATE_INVALID = 118308
            const val ERR_DPLAY_SEND_FAILED = 118300
            const val ERR_DPLAY_SEND_RANGE_INVALID = 118301
            const val ERR_DPLAY_SEND_STATE_INVALID = 118309
            const val ERR_DPLAY_TASK_FINISH_CANNT_DOWNLOAD = 118004
            const val ERR_DPLAY_TASK_FINISH_CONTINUE = 118003
            const val ERR_DPLAY_TASK_FINISH_DESTROY = 118002
            const val ERR_DPLAY_UNKNOW_HTTP_METHOD = 118303
            const val ERR_INVALID_ADDRESS_FAMILY = 116001
            const val ERR_P2P_ALLOC_MEM_ERR = 11313
            const val ERR_P2P_BROKER_CONNECT = 11308
            const val ERR_P2P_CONNECT_FAILED = 11311
            const val ERR_P2P_CONNECT_UPLOAD_SLOW = 11312
            const val ERR_P2P_HANDSHAKE_RESP_FAIL = 11303
            const val ERR_P2P_INVALID_COMMAND = 11309
            const val ERR_P2P_INVALID_PARAM = 11310
            const val ERR_P2P_NOT_SUPPORT_UDT = 11307
            const val ERR_P2P_REMOTE_UNKNOWN_MY_CMD = 11306
            const val ERR_P2P_REQUEST_RESP_FAIL = 11304
            const val ERR_P2P_SEND_HANDSHAKE = 11314
            const val ERR_P2P_UPLOAD_OVER_MAX = 11305
            const val ERR_P2P_VERSION_NOT_SUPPORT = 11301
            const val ERR_P2P_WAITING_CLOSE = 11302
            const val ERR_PTL_GET_PEERSN_FAILED = 112600
            const val ERR_PTL_PEER_OFFLINE = 112500
            const val ERR_PTL_PROTOCOL_NOT_SUPPORT = 112400
            const val FILE_CANNOT_TRUNCATE = 111084
            const val FILE_CFG_ERASE_ERROR = 111130
            const val FILE_CFG_MAGIC_ERROR = 111131
            const val FILE_CFG_READ_ERROR = 111132
            const val FILE_CFG_READ_HEADER_ERROR = 111134
            const val FILE_CFG_RESOLVE_ERROR = 111135
            const val FILE_CFG_TRY_FIX = 111129
            const val FILE_CFG_WRITE_ERROR = 111133
            const val FILE_CREATING = 111145
            const val FILE_EXISTED = 9109
            const val FILE_INVALID_PARA = 111144
            const val FILE_NAME_TOO_LONG = 9125
            const val FILE_NOT_EXIST = 111143
            const val FILE_PATH_TOO_LONG = 111120
            const val FILE_SIZE_NOT_BELIEVE = 111141
            const val FILE_SIZE_TOO_SMALL = 111142
            const val FILE_TOO_BIG = 111086
            const val FIL_INFO_INVALID_DATA = 111146
            const val FIL_INFO_RECVED_DATA = 111147
            const val FULL_PATH_NAME_OCCUPIED = 9128
            const val FULL_PATH_NAME_TOO_LONG = 9127
            const val FUNCTION_NOT_SUPPORT = 9123
            const val HTTP_HUB_CLIENT_E_BASE = 115100
            const val HTTP_SERVER_NOT_START = 9400
            const val INDEX_NOT_READY = 9303
            const val INSUFFICIENT_DISK_SPACE = 111085
            const val INVALID_ARGUMENT = 111041
            const val INVALID_ITERATOR = 111038
            const val INVALID_SOCKET_DESCRIPTOR = 111048
            const val INVALID_TIMER_INDEX = 111074
            const val IP6_ERRCODE_BASE = 116000
            const val IP6_INVALID_IN6ADDR = 116002
            const val IP6_NOT_SUPPORT_SSL = 116003
            const val MAP_DUPLICATE_KEY = 111036
            const val MAP_KEY_NOT_FOUND = 111037
            const val MAP_UNINIT = 111035
            const val NET_BROKEN_PIPE = 111170
            const val NET_CONNECTION_REFUSED = 111171
            const val NET_CONNECT_SSL_ERR = 111169
            const val NET_NORMAL_CLOSE = 111175
            const val NET_OP_CANCEL = 111173
            const val NET_REACTOR_ERRCODE_BASE = 111168
            const val NET_SSL_GET_FD_ERROR = 111172
            const val NET_UNKNOWN_ERROR = 111174
            const val NOT_FULL_PATH_NAME = 9404
            const val NOT_IMPLEMENT = 111057
            const val NO_ENOUGH_BUFFER = 9301
            const val NO_ERROR = 9000
            const val ONE_PATH_LEVEL_NAME_TOO_LONG = 9126
            const val OPEN_FILE_ERR = 111128
            const val OPEN_OLD_FILE_FAIL = 111140
            const val OUT_OF_FIXED_MEMORY = 111032
            const val OUT_OF_MEMORY = 111026
            const val P2P_PIPE_ERRCODE_BASE = 11300
            const val PARAM_ERROR = 9112
            const val PAUSE_TASK_WRITE_CFG_ERR = 117000
            const val PAUSE_TASK_WRITE_DATA_TIMEOUT = 117001
            const val PRIOR_TASK_FINISH = 9308
            const val PRIOR_TASK_NO_INDEX = 9307
            const val QUEUE_NO_ROOM = 111033
            const val READ_FILE_ERR = 111126
            const val RES_QUERY_E_BASE = 115000
            const val SCHEMA_NOT_SUPPORT = 9113
            const val SDK_NOT_INIT = 9102
            const val SETTINGS_ERR_CFG_FILE_NOT_EXIST = 111162
            const val SETTINGS_ERR_INVALID_FILE_NAME = 111161
            const val SETTINGS_ERR_INVALID_ITEM_NAME = 111164
            const val SETTINGS_ERR_INVALID_ITEM_VALUE = 111165
            const val SETTINGS_ERR_INVALID_LINE = 111163
            const val SETTINGS_ERR_ITEM_NOT_FOUND = 111167
            const val SETTINGS_ERR_LIST_EMPTY = 111166
            const val SETTINGS_ERR_UNKNOWN = 111160
            const val TARGET_THREAD_STOPING = 111025
            const val TASK_ALREADY_EXIST = 9103
            const val TASK_ALREADY_RUNNING = 9106
            const val TASK_ALREADY_STOPPED = 9105
            const val TASK_CONTROL_STRATEGY = 9501
            const val TASK_FAILURE_ACCOUNT_EXCEPTION = 111152
            const val TASK_FAILURE_ALL_SUBTASK_FAILED = 114009
            const val TASK_FAILURE_BTHUB_NO_RECORD = 114008
            const val TASK_FAILURE_CANNOT_START_SUBTASK = 114003
            const val TASK_FAILURE_COPYRIGHT_BLOCKING = 111154
            const val TASK_FAILURE_EMULE_NO_RECORD = 114101
            const val TASK_FAILURE_EROTIC_BLOCKING = 111156
            const val TASK_FAILURE_GET_TORRENT_FAILED = 114006
            const val TASK_FAILURE_NO_DATA_PIPE = 111136
            const val TASK_FAILURE_PARSE_TORRENT_FAILED = 114005
            const val TASK_FAILURE_PART_SUBTASK_FAILED = 114011
            const val TASK_FAILURE_QUERY_BT_HUB_FAILED = 114004
            const val TASK_FAILURE_QUERY_EMULE_HUB_FAILED = 114001
            const val TASK_FAILURE_REACTION_BLOCKING = 111155
            const val TASK_FAILURE_RESTRICTION = 111151
            const val TASK_FAILURE_RESTRICTION_AREA = 111153
            const val TASK_FAILURE_SAVE_TORRENT_FAILED = 114007
            const val TASK_FAILURE_SUBTASK_FAILED = 114002
            const val TASK_FAILURE_THEONLY_SUBTASK_FAILED = 114010
            const val TASK_FAIL_LONG_TIME_NO_RECV_DATA = 111176
            const val TASK_FILE_NAME_EMPTY = 9401
            const val TASK_FILE_NOT_VEDIO = 9402
            const val TASK_FILE_SIZE_TOO_LARGE = 111177
            const val TASK_FINISH = 9118
            const val TASK_NOT_EXIST = 9104
            const val TASK_NOT_IDLE = 9120
            const val TASK_NOT_RUNNING = 9119
            const val TASK_NOT_START = 9107
            const val TASK_NO_FILE_NAME = 9129
            const val TASK_NO_INDEX_NO_ORIGIN = 111148
            const val TASK_ORIGIN_NONEXISTENCE = 111149
            const val TASK_RETRY_ALWAY_FAIL = 111178
            const val TASK_STILL_RUNNING = 9108
            const val TASK_TYPE_NOT_SUPPORT = 9121
            const val TASK_UNKNOWN_ERROR = 9403
            const val TASK_USE_TOO_MUCH_MEM = 111031
            const val THUNDER_URL_PARSE_ERROR = 9305
            const val TOO_MUCH_TASK = 9111
            const val TORRENT_IMCOMPLETE = 9304
            const val TORRENT_PARSE_ERROR = 9302
            const val URL_IS_TOO_LONG = 111047
            const val URL_PARSER_ERROR = 111046
            const val VIDEO_CACHE_FINISH = 9410
            const val WRITE_FILE_ERR = 111127
        }
    }

    enum class XLQueryIndexStatus {
        QIS_UNQUERY,
        QIS_QUERYING,
        QIS_QUERY_HAVE_INFO,
        QIS_QUERY_HAVENT_INFO
    }

    enum class XLResStrategy {
        RUS_PRIOR_USE
    }

    enum class XLCreateTaskMode {
        NEW_TASK,
        CONTINUE_TASK
    }

    enum class XLDownloadHeaderState {
        GDHS_UNKOWN,
        GDHS_REQUESTING,
        GDHS_SUCCESS,
        GDHS_ERROR
    }

    enum class XLManagerStatus {
        MANAGER_UNINIT,
        MANAGER_INIT_FAIL,
        MANAGER_RUNNING
    }

    enum class XLNetWorkCarrier {
        NWC_Unknow,
        NWC_CMCC,
        NWC_CU,
        NWC_CT
    }

    enum class CreateTaskSense(val code: Int, val value: String) {
        SENSE_MANUAL(1, "manual"),
        SENSE_EMPTY(1, "empty"),
        SENSE_BROWSER(3, "browser"),
        SENSE_BT(4, "bt"),
        SENSE_CLIPBOARD(5, "clipboard"),
        SENSE_CLOUD_DOWNLOAD(6, "cloud_download"),
        SENSE_NORMAL_DOWNLOAD(7, "normal_download"),
        SENSE_CLOUD_FILE(8, "cloud_file"),
        SENSE_TASK(9, "task"),
        SENSE_SNIFF(10, "sniff"),
        SENSE_EXTERNAL(11, "external")
    }
}
