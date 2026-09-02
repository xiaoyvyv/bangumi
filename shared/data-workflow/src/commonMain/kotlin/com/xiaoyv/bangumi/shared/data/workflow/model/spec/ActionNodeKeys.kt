package com.xiaoyv.bangumi.shared.data.workflow.model.spec

/**
 * 节点失败与错误输出（errorOutput）以及异常诊断使用的统一 Key 常量。
 */
object ActionErrorKey {
    const val ERROR = "error"
    const val CODE = "code"
    const val MESSAGE = "message"
    const val NODE_ID = "nodeId"
    const val NODE_TYPE = "nodeType"
    const val NODE_LABEL = "nodeLabel"
    const val WORKFLOW_ID = "workflowId"
    const val WORKFLOW_NAME = "workflowName"
    const val CONFIG_KEY = "configKey"
    const val DETAILS = "details"
    const val CAUSE_CLASS = "causeClass"
    const val CAUSE_MESSAGE = "causeMessage"
    const val HINT = "hint"
}

/**
 * 内置节点工作流 JSON 的统一配置键与协议字符串私有主注册表。
 */
internal object ActionNodeConfigKey {
    const val HTML = "html"
    const val SELECTOR = "selector"
    const val OPERATION = "operation"
    const val ATTRIBUTE = "attribute"
    const val TEMPLATE = "template"
    const val SOURCE = "source"
    const val PATH = "path"
    const val KEY = "key"
    const val KEYS = "keys"
    const val ENTRIES = "entries"
    const val VALUE = "value"
    const val VALUES = "values"
    const val INDICES = "indices"
    const val OUTPUT_KEY = "outputKey"
    const val CONDITION = "condition"
    const val LEFT = "left"
    const val RIGHT = "right"
    const val MATCHED = "matched"
    const val SEPARATOR = "separator"
    const val URL = "url"
    const val IMG_KEY = "imgKey"
    const val SUB_KEY = "subKey"
    const val URI = "uri"
    const val FALLBACK_URL = "fallbackUrl"
    const val MESSAGE = "message"
    const val METHOD = "method"
    const val QUERY = "query"
    const val HEADERS = "headers"
    const val USER_AGENT = "userAgent"
    const val BODY = "body"
    const val BODY_TYPE = "bodyType"
    const val CONTENT_TYPE = "contentType"
    const val TIMEOUT_MILLIS = "timeoutMillis"
    const val RETRY_COUNT = "retryCount"
    const val RETRY_DELAY_MILLIS = "retryDelayMillis"
    const val USE_LOCAL_COOKIE_STORAGE = "useLocalCookieStorage"
    const val COUNT = "count"
    const val ITEMS = "items"
    const val MAX_ITERATIONS = "maxIterations"
    const val LOOP_ID = "loopId"
    const val TEXT = "text"
    const val DELIMITER = "delimiter"
    const val IS_REGEX = "isRegex"
    const val LIMIT = "limit"
    const val PATTERN = "pattern"
    const val START_INDEX = "startIndex"
    const val END_INDEX = "endIndex"
    const val MISSING_DELIMITER_VALUE = "missingDelimiterValue"
    const val INDEX = "index"
    const val FIELD_PATH = "fieldPath"
    const val OPERATOR = "operator"
    const val EXPECTED = "expected"
    const val OBJECT = "object"
    const val OBJECTS = "objects"
    const val REPLACEMENT = "replacement"
    const val START = "start"
    const val END = "end"
    const val DESCENDING = "descending"
    const val DELAY_MILLIS = "delayMillis"
    const val CASES = "cases"
    const val STATUS_CODE = "statusCode"
    const val MIN_STATUS_CODE = "minStatusCode"
    const val MAX_STATUS_CODE = "maxStatusCode"
    const val NAME = "name"
    const val DATA = "data"
    const val LEVEL = "level"
    const val FROM_PATH = "fromPath"
    const val TO_PATH = "toPath"
    const val PATHS = "paths"
    const val ASSIGNMENTS = "assignments"
    const val MERGE_STRATEGY = "mergeStrategy"
    const val TIMESTAMP = "timestamp"
    const val ALGORITHM = "algorithm"
    const val SECRET = "secret"
    const val SECRET_KEY = "secretKey"
    const val KEY_BYTES = "keyBytes"
    const val SIZE = "size"
    const val UNIT = "unit"
    const val MIN = "min"
    const val MAX = "max"
    const val DECIMALS = "decimals"
    const val IGNORE_CASE = "ignoreCase"
    const val TIMESTAMP_LEFT = "timestampLeft"
    const val TIMESTAMP_RIGHT = "timestampRight"
    const val BASE_URL = "baseUrl"
    const val QUERY_PARAMETERS = "queryParameters"
    const val HEADER_ROW = "headerRow"
    const val ELLIPSIS = "ellipsis"
    const val OTHER_VALUES = "otherValues"
    const val SCHEMA = "schema"
    const val PAD_LENGTH = "padLength"
    const val PAD_CHARACTER = "padCharacter"
    const val PAD_END = "padEnd"
    const val FRACTION_DIGITS = "fractionDigits"
    const val WORKFLOW_ID = "workflowId"
    const val INPUT = "input"
    const val OUTPUT = "output"
    const val INITIAL_VALUE = "initialValue"
    const val TITLE = "title"
    const val SUBTITLE = "subtitle"
    const val DEFAULT_VALUE = "defaultValue"
    const val DEFAULT_VALUES = "defaultValues"
    const val DEFAULT_INDICES = "defaultIndices"
    const val OPTIONS = "options"
    const val IS_MULTI_SELECT = "isMultiSelect"
    const val OUTPUT_MODE = "outputMode"
    const val CONFIRM_TEXT = "confirmText"
    const val CANCEL_TEXT = "cancelText"
    const val CONTENT = "content"
    const val ERROR = "error"
    const val VARIABLES = "variables"
    const val RESULT = "result"
    const val CONDITION_MET = "conditionMet"
    const val GROUP_VALUES = "groupValues"

    // HTTP & Response Keys
    const val RAW_BODY = "rawBody"
    const val IS_SUCCESS = "isSuccess"
    const val JSON = "json"
    const val FORM_URL_ENCODED = "formUrlEncoded"

    // URL Parsed & Context Keys
    const val PROTOCOL = "protocol"
    const val HOST = "host"
    const val PORT = "port"
    const val FULL_URL = "fullUrl"
    const val ITEM = "item"
    const val ITERATION = "iteration"

    // Merge Strategy
    const val SHALLOW = "shallow"
    const val DEEP = "deep"
    const val DEEP_APPEND_ARRAYS = "deep_append_arrays"

    // Html Query Operation
    const val OUTER_HTML = "outer_html"
    const val EXISTS = "exists"
    const val ALL_TEXT = "all_text"
    const val ALL_HTML = "all_html"
    const val ALL_OUTER_HTML = "all_outer_html"

    // Hash Algorithm
    const val SHA_256 = "sha256"
    const val SHA_512 = "sha512"
    const val MD5 = "md5"
    const val SHA_1 = "sha1"
    const val SHA_224 = "sha224"
    const val SHA_384 = "sha384"
    const val SHA3_224 = "sha3224"
    const val SHA3_256 = "sha3256"
    const val SHA3_384 = "sha3384"
    const val SHA3_512 = "sha3512"
    const val SM3 = "sm3"
    const val CRC32 = "crc32"

    // Filter Operator
    const val EQUALS = "equals"
    const val NOT_EQUALS = "not_equals"
    const val IS_NULL = "is_null"
    const val IS_NOT_NULL = "is_not_null"
    const val IS_EMPTY = "is_empty"
    const val IS_NOT_EMPTY = "is_not_empty"

    const val IMAGES = "images"
    const val BOTH = "both"
}

object ActionFlowConfigKey {
    const val DELAY_MILLIS = ActionNodeConfigKey.DELAY_MILLIS
    const val CONDITION = ActionNodeConfigKey.CONDITION
    const val MESSAGE = ActionNodeConfigKey.MESSAGE
    const val LEVEL = ActionNodeConfigKey.LEVEL
    const val RETRY_COUNT = ActionNodeConfigKey.RETRY_COUNT
    const val RETRY_DELAY_MILLIS = ActionNodeConfigKey.RETRY_DELAY_MILLIS
    const val TIMEOUT_MILLIS = ActionNodeConfigKey.TIMEOUT_MILLIS
    const val WORKFLOW_ID = ActionNodeConfigKey.WORKFLOW_ID
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
    const val VALUE = ActionNodeConfigKey.VALUE
    const val CASES = ActionNodeConfigKey.CASES
    const val MATCHED = ActionNodeConfigKey.MATCHED
    const val DATA = ActionNodeConfigKey.DATA
    const val VALUES = ActionNodeConfigKey.VALUES
    const val ERROR = ActionNodeConfigKey.ERROR
    const val VARIABLES = ActionNodeConfigKey.VARIABLES
    const val INPUT = ActionNodeConfigKey.INPUT
    const val OUTPUT = ActionNodeConfigKey.OUTPUT
    const val RESULT = ActionNodeConfigKey.RESULT
    const val CONDITION_MET = ActionNodeConfigKey.CONDITION_MET
}

object ActionControlConfigKey {
    const val LEFT = ActionNodeConfigKey.LEFT
    const val RIGHT = ActionNodeConfigKey.RIGHT
    const val VALUE = ActionNodeConfigKey.VALUE
    const val CONDITION = ActionNodeConfigKey.CONDITION
    const val MATCHED = ActionNodeConfigKey.MATCHED
    const val STATUS_CODE = ActionNodeConfigKey.STATUS_CODE
    const val MIN_STATUS_CODE = ActionNodeConfigKey.MIN_STATUS_CODE
    const val MAX_STATUS_CODE = ActionNodeConfigKey.MAX_STATUS_CODE
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

object ActionLoopConfigKey {
    const val COUNT = ActionNodeConfigKey.COUNT
    const val ITEMS = ActionNodeConfigKey.ITEMS
    const val CONDITION = ActionNodeConfigKey.CONDITION
    const val MAX_ITERATIONS = ActionNodeConfigKey.MAX_ITERATIONS
    const val LOOP_ID = ActionNodeConfigKey.LOOP_ID
}

object ActionLoopContextKey {
    const val ITEM = ActionNodeConfigKey.ITEM
    const val INDEX = ActionNodeConfigKey.INDEX
    const val ITERATION = ActionNodeConfigKey.ITERATION
}

object ActionDataConfigKey {
    const val TEMPLATE = ActionNodeConfigKey.TEMPLATE
    const val KEY = ActionNodeConfigKey.KEY
    const val VALUE = ActionNodeConfigKey.VALUE
    const val VALUES = ActionNodeConfigKey.VALUES
    const val SEPARATOR = ActionNodeConfigKey.SEPARATOR
    const val OBJECT = ActionNodeConfigKey.OBJECT
    const val OBJECTS = ActionNodeConfigKey.OBJECTS
    const val PATH = ActionNodeConfigKey.PATH
    const val PATHS = ActionNodeConfigKey.PATHS
    const val FROM_PATH = ActionNodeConfigKey.FROM_PATH
    const val TO_PATH = ActionNodeConfigKey.TO_PATH
    const val ASSIGNMENTS = ActionNodeConfigKey.ASSIGNMENTS
    const val MERGE_STRATEGY = ActionNodeConfigKey.MERGE_STRATEGY
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

object ActionDataMergeStrategy {
    const val SHALLOW = ActionNodeConfigKey.SHALLOW
    const val DEEP = ActionNodeConfigKey.DEEP
    const val DEEP_APPEND_ARRAYS = ActionNodeConfigKey.DEEP_APPEND_ARRAYS
}

object ActionHtmlConfigKey {
    const val HTML = ActionNodeConfigKey.HTML
    const val SELECTOR = ActionNodeConfigKey.SELECTOR
    const val OPERATION = ActionNodeConfigKey.OPERATION
    const val ATTRIBUTE = ActionNodeConfigKey.ATTRIBUTE
    const val NAME = ActionNodeConfigKey.NAME
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

object ActionHtmlQueryOperation {
    const val TEXT = ActionNodeConfigKey.TEXT
    const val HTML = ActionNodeConfigKey.HTML
    const val OUTER_HTML = ActionNodeConfigKey.OUTER_HTML
    const val ATTRIBUTE = ActionNodeConfigKey.ATTRIBUTE
    const val COUNT = ActionNodeConfigKey.COUNT
    const val EXISTS = ActionNodeConfigKey.EXISTS
    const val ALL_TEXT = ActionNodeConfigKey.ALL_TEXT
    const val ALL_HTML = ActionNodeConfigKey.ALL_HTML
    const val ALL_OUTER_HTML = ActionNodeConfigKey.ALL_OUTER_HTML
}

object ActionObjectConfigKey {
    const val OBJECT = ActionNodeConfigKey.OBJECT
    const val OBJECTS = ActionNodeConfigKey.OBJECTS
    const val PATH = ActionNodeConfigKey.PATH
    const val KEY = ActionNodeConfigKey.KEY
    const val KEYS = ActionNodeConfigKey.KEYS
    const val VALUE = ActionNodeConfigKey.VALUE
    const val ENTRIES = ActionNodeConfigKey.ENTRIES
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

object ActionUrlConfigKey {
    const val URL = ActionNodeConfigKey.URL
    const val BASE_URL = ActionNodeConfigKey.BASE_URL
    const val QUERY_PARAMETERS = ActionNodeConfigKey.QUERY_PARAMETERS
    const val KEY = ActionNodeConfigKey.KEY
    const val VALUE = ActionNodeConfigKey.VALUE
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

object ActionBilibiliConfigKey {
    const val URL = ActionNodeConfigKey.URL
    const val IMG_KEY = ActionNodeConfigKey.IMG_KEY
    const val SUB_KEY = ActionNodeConfigKey.SUB_KEY
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

object ActionUrlParsedKey {
    const val PROTOCOL = ActionNodeConfigKey.PROTOCOL
    const val HOST = ActionNodeConfigKey.HOST
    const val PORT = ActionNodeConfigKey.PORT
    const val PATH = ActionNodeConfigKey.PATH
    const val FULL_URL = ActionNodeConfigKey.FULL_URL
    const val QUERY_PARAMETERS = ActionNodeConfigKey.QUERY_PARAMETERS
}

object ActionCsvConfigKey {
    const val TEXT = ActionNodeConfigKey.TEXT
    const val DELIMITER = ActionNodeConfigKey.DELIMITER
    const val HEADER_ROW = ActionNodeConfigKey.HEADER_ROW
    const val ITEMS = ActionNodeConfigKey.ITEMS
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

object ActionXmlConfigKey {
    const val TEXT = ActionNodeConfigKey.TEXT
    const val DATA = ActionNodeConfigKey.DATA
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

object ActionJsonConfigKey {
    const val SOURCE = ActionNodeConfigKey.SOURCE
    const val PATH = ActionNodeConfigKey.PATH
    const val VALUE = ActionNodeConfigKey.VALUE
    const val TEXT = ActionNodeConfigKey.TEXT
    const val SCHEMA = ActionNodeConfigKey.SCHEMA
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

object ActionCodecConfigKey {
    const val TEXT = ActionNodeConfigKey.TEXT
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

object ActionCryptoConfigKey {
    const val TEXT = ActionNodeConfigKey.TEXT
    const val ALGORITHM = ActionNodeConfigKey.ALGORITHM
    const val SECRET = ActionNodeConfigKey.SECRET
    const val SECRET_KEY = ActionNodeConfigKey.SECRET_KEY
    const val KEY_BYTES = ActionNodeConfigKey.KEY_BYTES
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

object ActionHashAlgorithm {
    const val SHA_256 = ActionNodeConfigKey.SHA_256
    const val SHA_512 = ActionNodeConfigKey.SHA_512
    const val MD5 = ActionNodeConfigKey.MD5
    const val SHA_1 = ActionNodeConfigKey.SHA_1
    const val SHA_224 = ActionNodeConfigKey.SHA_224
    const val SHA_384 = ActionNodeConfigKey.SHA_384
    const val SHA3_224 = ActionNodeConfigKey.SHA3_224
    const val SHA3_256 = ActionNodeConfigKey.SHA3_256
    const val SHA3_384 = ActionNodeConfigKey.SHA3_384
    const val SHA3_512 = ActionNodeConfigKey.SHA3_512
    const val SM3 = ActionNodeConfigKey.SM3
    const val CRC32 = ActionNodeConfigKey.CRC32
}

object ActionTextConfigKey {
    const val TEXT = ActionNodeConfigKey.TEXT
    const val TEMPLATE = ActionNodeConfigKey.TEMPLATE
    const val OBJECT = ActionNodeConfigKey.OBJECT
    const val VALUES = ActionNodeConfigKey.VALUES
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
    const val DELIMITER = ActionNodeConfigKey.DELIMITER
    const val SEPARATOR = ActionNodeConfigKey.SEPARATOR
    const val IS_REGEX = ActionNodeConfigKey.IS_REGEX
    const val LIMIT = ActionNodeConfigKey.LIMIT
    const val PATTERN = ActionNodeConfigKey.PATTERN
    const val REPLACEMENT = ActionNodeConfigKey.REPLACEMENT
    const val START_INDEX = ActionNodeConfigKey.START_INDEX
    const val END_INDEX = ActionNodeConfigKey.END_INDEX
    const val MISSING_DELIMITER_VALUE = ActionNodeConfigKey.MISSING_DELIMITER_VALUE
    const val ELLIPSIS = ActionNodeConfigKey.ELLIPSIS
    const val MATCHED = ActionNodeConfigKey.MATCHED
    const val GROUP_VALUES = ActionNodeConfigKey.GROUP_VALUES
    const val IGNORE_CASE = ActionNodeConfigKey.IGNORE_CASE
    const val PAD_LENGTH = ActionNodeConfigKey.PAD_LENGTH
    const val PAD_CHARACTER = ActionNodeConfigKey.PAD_CHARACTER
    const val PAD_END = ActionNodeConfigKey.PAD_END
    const val FRACTION_DIGITS = ActionNodeConfigKey.FRACTION_DIGITS
    const val COUNT = ActionNodeConfigKey.COUNT
}

object ActionArrayConfigKey {
    const val VALUES = ActionNodeConfigKey.VALUES
    const val OTHER_VALUES = ActionNodeConfigKey.OTHER_VALUES
    const val VALUE = ActionNodeConfigKey.VALUE
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
    const val INDEX = ActionNodeConfigKey.INDEX
    const val SIZE = ActionNodeConfigKey.SIZE
    const val START = ActionNodeConfigKey.START
    const val END = ActionNodeConfigKey.END
    const val COUNT = ActionNodeConfigKey.COUNT
    const val INITIAL_VALUE = ActionNodeConfigKey.INITIAL_VALUE
    const val FIELD_PATH = ActionNodeConfigKey.FIELD_PATH
    const val OPERATOR = ActionNodeConfigKey.OPERATOR
    const val EXPECTED = ActionNodeConfigKey.EXPECTED
    const val DESCENDING = ActionNodeConfigKey.DESCENDING
}

object ActionArrayFilterOperator {
    const val EQUALS = ActionNodeConfigKey.EQUALS
    const val NOT_EQUALS = ActionNodeConfigKey.NOT_EQUALS
    const val IS_NULL = ActionNodeConfigKey.IS_NULL
    const val IS_NOT_NULL = ActionNodeConfigKey.IS_NOT_NULL
    const val IS_EMPTY = ActionNodeConfigKey.IS_EMPTY
    const val IS_NOT_EMPTY = ActionNodeConfigKey.IS_NOT_EMPTY
}

object ActionDateConfigKey {
    const val TEXT = ActionNodeConfigKey.TEXT
    const val TIMESTAMP = ActionNodeConfigKey.TIMESTAMP
    const val TIMESTAMP_LEFT = ActionNodeConfigKey.TIMESTAMP_LEFT
    const val TIMESTAMP_RIGHT = ActionNodeConfigKey.TIMESTAMP_RIGHT
    const val COUNT = ActionNodeConfigKey.COUNT
    const val UNIT = ActionNodeConfigKey.UNIT
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

object ActionMathConfigKey {
    const val VALUE = ActionNodeConfigKey.VALUE
    const val VALUES = ActionNodeConfigKey.VALUES
    const val LEFT = ActionNodeConfigKey.LEFT
    const val RIGHT = ActionNodeConfigKey.RIGHT
    const val MIN = ActionNodeConfigKey.MIN
    const val MAX = ActionNodeConfigKey.MAX
    const val DECIMALS = ActionNodeConfigKey.DECIMALS
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

object ActionHttpConfigKey {
    const val URL = ActionNodeConfigKey.URL
    const val METHOD = ActionNodeConfigKey.METHOD
    const val HEADERS = ActionNodeConfigKey.HEADERS
    const val QUERY = ActionNodeConfigKey.QUERY
    const val BODY = ActionNodeConfigKey.BODY
    const val BODY_TYPE = ActionNodeConfigKey.BODY_TYPE
    const val CONTENT_TYPE = ActionNodeConfigKey.CONTENT_TYPE
    const val TIMEOUT_MILLIS = ActionNodeConfigKey.TIMEOUT_MILLIS
    const val RETRY_COUNT = ActionNodeConfigKey.RETRY_COUNT
    const val RETRY_DELAY_MILLIS = ActionNodeConfigKey.RETRY_DELAY_MILLIS
    const val USE_LOCAL_COOKIE_STORAGE = ActionNodeConfigKey.USE_LOCAL_COOKIE_STORAGE
}

object ActionHttpBodyType {
    const val JSON = ActionNodeConfigKey.JSON
    const val TEXT = ActionNodeConfigKey.TEXT
    const val FORM_URL_ENCODED = ActionNodeConfigKey.FORM_URL_ENCODED
}

object ActionHttpResponseKey {
    const val STATUS_CODE = ActionNodeConfigKey.STATUS_CODE
    const val IS_SUCCESS = ActionNodeConfigKey.IS_SUCCESS
    const val CONTENT_TYPE = ActionNodeConfigKey.CONTENT_TYPE
    const val RAW_BODY = ActionNodeConfigKey.RAW_BODY
    const val BODY = ActionNodeConfigKey.BODY
    const val HTML = ActionNodeConfigKey.HTML
    const val TEXT = ActionNodeConfigKey.TEXT
    const val TITLE = ActionNodeConfigKey.TITLE
}

object ActionStorageConfigKey {
    const val KEY = ActionNodeConfigKey.KEY
    const val VALUE = ActionNodeConfigKey.VALUE
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

object ActionOpenUrlConfigKey {
    const val URL = ActionNodeConfigKey.URL
}

object ActionOpenAppConfigKey {
    const val URI = ActionNodeConfigKey.URI
    const val FALLBACK_URL = ActionNodeConfigKey.FALLBACK_URL
}

object ActionOpenWebConfigKey {
    const val URL = ActionNodeConfigKey.URL
}

object ActionToastConfigKey {
    const val MESSAGE = ActionNodeConfigKey.MESSAGE
    const val TEXT = ActionNodeConfigKey.TEXT
}

object ActionClipboardConfigKey {
    const val TEXT = ActionNodeConfigKey.TEXT
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

object ActionConfirmConfigKey {
    const val TITLE = ActionNodeConfigKey.TITLE
    const val MESSAGE = ActionNodeConfigKey.MESSAGE
    const val CONFIRM_TEXT = ActionNodeConfigKey.CONFIRM_TEXT
    const val CANCEL_TEXT = ActionNodeConfigKey.CANCEL_TEXT
}

object ActionInputDialogConfigKey {
    const val TITLE = ActionNodeConfigKey.TITLE
    const val SUBTITLE = ActionNodeConfigKey.SUBTITLE
    const val DEFAULT_VALUE = ActionNodeConfigKey.DEFAULT_VALUE
    const val CONFIRM_TEXT = ActionNodeConfigKey.CONFIRM_TEXT
    const val CANCEL_TEXT = ActionNodeConfigKey.CANCEL_TEXT
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

object ActionSelectDialogConfigKey {
    const val TITLE = ActionNodeConfigKey.TITLE
    const val SUBTITLE = ActionNodeConfigKey.SUBTITLE
    const val OPTIONS = ActionNodeConfigKey.OPTIONS
    const val DEFAULT_VALUES = ActionNodeConfigKey.DEFAULT_VALUES
    const val DEFAULT_INDICES = ActionNodeConfigKey.DEFAULT_INDICES
    const val IS_MULTI_SELECT = ActionNodeConfigKey.IS_MULTI_SELECT
    const val OUTPUT_MODE = ActionNodeConfigKey.OUTPUT_MODE
    const val CONFIRM_TEXT = ActionNodeConfigKey.CONFIRM_TEXT
    const val CANCEL_TEXT = ActionNodeConfigKey.CANCEL_TEXT
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
    const val VALUE = ActionNodeConfigKey.VALUE
    const val VALUES = ActionNodeConfigKey.VALUES
    const val INDEX = ActionNodeConfigKey.INDEX
    const val INDICES = ActionNodeConfigKey.INDICES
}

object ActionNotificationConfigKey {
    const val TITLE = ActionNodeConfigKey.TITLE
    const val CONTENT = ActionNodeConfigKey.CONTENT
}

object ActionShareConfigKey {
    const val TITLE = ActionNodeConfigKey.TITLE
    const val TEXT = ActionNodeConfigKey.TEXT
    const val CONTENT = ActionNodeConfigKey.CONTENT
    const val URL = ActionNodeConfigKey.URL
}

object ActionImagePreviewConfigKey {
    const val INDEX = ActionNodeConfigKey.INDEX
    const val IMAGES = ActionNodeConfigKey.IMAGES
}

object ActionSyncCookieConfigKey {
    const val URL = ActionNodeConfigKey.URL
    const val TITLE = ActionNodeConfigKey.TITLE
    const val HEADERS = ActionNodeConfigKey.HEADERS
    const val USER_AGENT = ActionNodeConfigKey.USER_AGENT
}

object ActionSelectOutputMode {
    const val VALUE = ActionNodeConfigKey.VALUE
    const val INDEX = ActionNodeConfigKey.INDEX
    const val BOTH = ActionNodeConfigKey.BOTH
}
