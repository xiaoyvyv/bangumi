package com.xiaoyv.bangumi.shared.data.workflow.model

import com.xiaoyv.bangumi.shared.data.workflow.model.ActionArrayConfigKey.COUNT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionArrayConfigKey.DESCENDING
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionArrayConfigKey.END
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionArrayConfigKey.EXPECTED
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionArrayConfigKey.FIELD_PATH
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionArrayConfigKey.INDEX
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionArrayConfigKey.INITIAL_VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionArrayConfigKey.OPERATOR
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionArrayConfigKey.OTHER_VALUES
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionArrayConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionArrayConfigKey.SIZE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionArrayConfigKey.START
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionArrayConfigKey.VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionArrayConfigKey.VALUES
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionArrayFilterOperator.EQUALS
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionArrayFilterOperator.IS_EMPTY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionArrayFilterOperator.IS_NOT_EMPTY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionArrayFilterOperator.IS_NOT_NULL
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionArrayFilterOperator.IS_NULL
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionArrayFilterOperator.NOT_EQUALS
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionBilibiliConfigKey.IMG_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionBilibiliConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionBilibiliConfigKey.SUB_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionBilibiliConfigKey.URL
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionCapability.CLIPBOARD_WRITE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionCapability.CONFIRM_DIALOG
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionCapability.IMAGE_PREVIEW
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionCapability.INPUT_DIALOG
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionCapability.NETWORK
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionCapability.NETWORK_LOCAL_COOKIE_ACCESS
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionCapability.NOTIFICATION
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionCapability.OPEN_EXTERNAL_APP
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionCapability.OPEN_EXTERNAL_URL
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionCapability.OPEN_INTERNAL_WEB
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionCapability.SELECT_DIALOG
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionCapability.SHARE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionClipboardConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionClipboardConfigKey.TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionCodecConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionCodecConfigKey.TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionConfirmConfigKey.CANCEL_TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionConfirmConfigKey.CONFIRM_TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionConfirmConfigKey.MESSAGE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionConfirmConfigKey.TITLE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionControlConfigKey.CONDITION
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionControlConfigKey.LEFT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionControlConfigKey.MATCHED
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionControlConfigKey.MAX_STATUS_CODE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionControlConfigKey.MIN_STATUS_CODE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionControlConfigKey.RIGHT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionControlConfigKey.STATUS_CODE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionControlConfigKey.VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionCryptoConfigKey.ALGORITHM
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionCryptoConfigKey.KEY_BYTES
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionCryptoConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionCryptoConfigKey.SECRET
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionCryptoConfigKey.SECRET_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionCryptoConfigKey.TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionCsvConfigKey.DELIMITER
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionCsvConfigKey.HEADER_ROW
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionCsvConfigKey.ITEMS
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionCsvConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionCsvConfigKey.TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionDataConfigKey.ASSIGNMENTS
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionDataConfigKey.FROM_PATH
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionDataConfigKey.KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionDataConfigKey.MERGE_STRATEGY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionDataConfigKey.OBJECT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionDataConfigKey.OBJECTS
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionDataConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionDataConfigKey.PATH
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionDataConfigKey.PATHS
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionDataConfigKey.SEPARATOR
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionDataConfigKey.TEMPLATE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionDataConfigKey.TO_PATH
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionDataConfigKey.VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionDataConfigKey.VALUES
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionDataMergeStrategy.DEEP
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionDataMergeStrategy.DEEP_APPEND_ARRAYS
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionDataMergeStrategy.SHALLOW
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionDateConfigKey.COUNT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionDateConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionDateConfigKey.TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionDateConfigKey.TIMESTAMP
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionDateConfigKey.TIMESTAMP_LEFT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionDateConfigKey.TIMESTAMP_RIGHT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionDateConfigKey.UNIT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionFlowConfigKey.CASES
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionFlowConfigKey.CONDITION
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionFlowConfigKey.CONDITION_MET
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionFlowConfigKey.DATA
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionFlowConfigKey.DELAY_MILLIS
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionFlowConfigKey.ERROR
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionFlowConfigKey.INPUT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionFlowConfigKey.LEVEL
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionFlowConfigKey.MATCHED
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionFlowConfigKey.MESSAGE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionFlowConfigKey.OUTPUT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionFlowConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionFlowConfigKey.RESULT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionFlowConfigKey.RETRY_COUNT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionFlowConfigKey.RETRY_DELAY_MILLIS
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionFlowConfigKey.TIMEOUT_MILLIS
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionFlowConfigKey.VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionFlowConfigKey.VALUES
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionFlowConfigKey.VARIABLES
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionFlowConfigKey.WORKFLOW_ID
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHashAlgorithm.CRC32
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHashAlgorithm.MD5
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHashAlgorithm.SHA3_224
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHashAlgorithm.SHA3_256
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHashAlgorithm.SHA3_384
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHashAlgorithm.SHA3_512
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHashAlgorithm.SHA_1
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHashAlgorithm.SHA_224
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHashAlgorithm.SHA_256
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHashAlgorithm.SHA_384
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHashAlgorithm.SHA_512
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHashAlgorithm.SM3
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHtmlConfigKey.ATTRIBUTE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHtmlConfigKey.HTML
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHtmlConfigKey.NAME
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHtmlConfigKey.OPERATION
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHtmlConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHtmlConfigKey.SELECTOR
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHtmlQueryOperation.ALL_HTML
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHtmlQueryOperation.ALL_OUTER_HTML
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHtmlQueryOperation.ALL_TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHtmlQueryOperation.ATTRIBUTE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHtmlQueryOperation.COUNT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHtmlQueryOperation.EXISTS
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHtmlQueryOperation.HTML
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHtmlQueryOperation.OUTER_HTML
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHtmlQueryOperation.TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHttpBodyType.FORM_URL_ENCODED
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHttpBodyType.JSON
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHttpBodyType.TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHttpConfigKey.BODY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHttpConfigKey.BODY_TYPE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHttpConfigKey.CONTENT_TYPE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHttpConfigKey.HEADERS
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHttpConfigKey.METHOD
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHttpConfigKey.QUERY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHttpConfigKey.RETRY_COUNT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHttpConfigKey.RETRY_DELAY_MILLIS
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHttpConfigKey.TIMEOUT_MILLIS
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHttpConfigKey.URL
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHttpConfigKey.USE_LOCAL_COOKIE_STORAGE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHttpResponseKey.BODY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHttpResponseKey.CONTENT_TYPE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHttpResponseKey.HTML
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHttpResponseKey.IS_SUCCESS
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHttpResponseKey.RAW_BODY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHttpResponseKey.STATUS_CODE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHttpResponseKey.TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHttpResponseKey.TITLE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionImagePreviewConfigKey.IMAGES
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionImagePreviewConfigKey.INDEX
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionInputDialogConfigKey.CANCEL_TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionInputDialogConfigKey.CONFIRM_TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionInputDialogConfigKey.DEFAULT_VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionInputDialogConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionInputDialogConfigKey.SUBTITLE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionInputDialogConfigKey.TITLE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionJsonConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionJsonConfigKey.PATH
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionJsonConfigKey.SCHEMA
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionJsonConfigKey.SOURCE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionJsonConfigKey.TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionJsonConfigKey.VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionLoopConfigKey.CONDITION
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionLoopConfigKey.COUNT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionLoopConfigKey.ITEMS
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionLoopConfigKey.LOOP_ID
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionLoopConfigKey.MAX_ITERATIONS
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionLoopContextKey.INDEX
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionLoopContextKey.ITEM
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionLoopContextKey.ITERATION
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionMathConfigKey.DECIMALS
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionMathConfigKey.LEFT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionMathConfigKey.MAX
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionMathConfigKey.MIN
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionMathConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionMathConfigKey.RIGHT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionMathConfigKey.VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionMathConfigKey.VALUES
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionNotificationConfigKey.CONTENT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionNotificationConfigKey.TITLE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionObjectConfigKey.ENTRIES
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionObjectConfigKey.KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionObjectConfigKey.KEYS
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionObjectConfigKey.OBJECT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionObjectConfigKey.OBJECTS
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionObjectConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionObjectConfigKey.PATH
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionObjectConfigKey.VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionOpenAppConfigKey.FALLBACK_URL
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionOpenAppConfigKey.URI
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionOpenUrlConfigKey.URL
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionOpenWebConfigKey.URL
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionSelectDialogConfigKey.CANCEL_TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionSelectDialogConfigKey.CONFIRM_TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionSelectDialogConfigKey.DEFAULT_INDICES
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionSelectDialogConfigKey.DEFAULT_VALUES
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionSelectDialogConfigKey.INDEX
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionSelectDialogConfigKey.IS_MULTI_SELECT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionSelectDialogConfigKey.OPTIONS
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionSelectDialogConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionSelectDialogConfigKey.OUTPUT_MODE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionSelectDialogConfigKey.SUBTITLE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionSelectDialogConfigKey.TITLE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionSelectDialogConfigKey.VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionSelectOutputMode.BOTH
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionSelectOutputMode.INDEX
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionSelectOutputMode.VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionShareConfigKey.CONTENT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionShareConfigKey.TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionShareConfigKey.TITLE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionShareConfigKey.URL
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionStorageConfigKey.KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionStorageConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionStorageConfigKey.VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionSyncCookieConfigKey.TITLE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionSyncCookieConfigKey.URL
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionTextConfigKey.COUNT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionTextConfigKey.DELIMITER
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionTextConfigKey.ELLIPSIS
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionTextConfigKey.END_INDEX
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionTextConfigKey.FRACTION_DIGITS
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionTextConfigKey.GROUP_VALUES
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionTextConfigKey.IGNORE_CASE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionTextConfigKey.IS_REGEX
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionTextConfigKey.LIMIT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionTextConfigKey.MATCHED
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionTextConfigKey.MISSING_DELIMITER_VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionTextConfigKey.OBJECT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionTextConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionTextConfigKey.PAD_CHARACTER
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionTextConfigKey.PAD_END
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionTextConfigKey.PAD_LENGTH
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionTextConfigKey.PATTERN
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionTextConfigKey.REPLACEMENT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionTextConfigKey.SEPARATOR
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionTextConfigKey.START_INDEX
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionTextConfigKey.TEMPLATE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionTextConfigKey.TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionTextConfigKey.VALUES
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionToastConfigKey.MESSAGE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionToastConfigKey.TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionUrlConfigKey.BASE_URL
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionUrlConfigKey.KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionUrlConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionUrlConfigKey.QUERY_PARAMETERS
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionUrlConfigKey.URL
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionUrlConfigKey.VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionUrlParsedKey.FULL_URL
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionUrlParsedKey.HOST
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionUrlParsedKey.PATH
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionUrlParsedKey.PORT
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionUrlParsedKey.PROTOCOL
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionUrlParsedKey.QUERY_PARAMETERS
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionXmlConfigKey.DATA
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionXmlConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionXmlConfigKey.TEXT


/**
 * 内置流程节点类型。
 */
object ActionNodeType {
    // flow
    const val FLOW_START = "flow.start"
    const val FLOW_END = "flow.end"
    const val FLOW_DELAY = "flow.delay"
    const val FLOW_STOP = "flow.stop"
    const val FLOW_ASSERT = "flow.assert"
    const val FLOW_SWITCH = "flow.switch"
    const val FLOW_LOG = "flow.log"
    const val FLOW_DEBUG = "flow.debug"
    const val FLOW_TRY = "flow.try"
    const val FLOW_CATCH = "flow.catch"
    const val FLOW_FINALLY = "flow.finally"
    const val FLOW_CALL = "flow.call"
    const val FLOW_RETURN = "flow.return"
    const val FLOW_PARALLEL = "flow.parallel"
    const val FLOW_JOIN = "flow.join"
    const val FLOW_RETRY = "flow.retry"
    const val FLOW_TIMEOUT = "flow.timeout"
    const val FLOW_WAIT_UNTIL = "flow.wait_until"
    const val FLOW_RATE_LIMIT = "flow.rate_limit"

    // control
    const val CONDITION_IF = "control.if"
    const val CONDITION_EQUALS = "control.equals"
    const val CONDITION_NOT_EQUALS = "control.not_equals"
    const val CONDITION_GREATER_THAN = "control.greater_than"
    const val CONDITION_GREATER_THAN_OR_EQUALS = "control.greater_than_or_equals"
    const val CONDITION_LESS_THAN = "control.less_than"
    const val CONDITION_LESS_THAN_OR_EQUALS = "control.less_than_or_equals"
    const val CONDITION_AND = "control.and"
    const val CONDITION_OR = "control.or"
    const val CONDITION_NOT = "control.not"
    const val CONDITION_IS_NULL = "control.is_null"
    const val CONDITION_IS_EMPTY = "control.is_empty"
    const val HTTP_STATUS = "http.status"

    // loop
    const val LOOP_REPEAT = "loop.repeat"
    const val LOOP_FOR_EACH = "loop.for_each"
    const val LOOP_WHILE = "loop.while"
    const val LOOP_NEXT = "loop.next"
    const val LOOP_BREAK = "loop.break"
    const val LOOP_CONTINUE = "loop.continue"

    // data
    const val TEMPLATE = "data.template"
    const val SET_VARIABLE = "data.set_variable"
    const val DATA_COALESCE = "data.coalesce"
    const val DATA_CONCAT = "data.concat"
    const val DATA_MERGE = "data.merge"
    const val DATA_ASSIGN = "data.assign"
    const val DATA_REMOVE = "data.remove"
    const val DATA_RENAME = "data.rename"
    const val DATA_PICK = "data.pick"
    const val DATA_UUID = "data.uuid"
    const val DATA_TO_NUMBER = "data.to_number"
    const val DATA_TO_STRING = "data.to_string"
    const val DATA_TO_BOOLEAN = "data.to_boolean"
    const val DATA_TYPE_OF = "data.type_of"

    // html
    const val HTML_QUERY = "html.query"
    const val HTML_QUERY_ALL = "html.query_all"
    const val HTML_TITLE = "html.title"
    const val HTML_TEXT = "html.text"
    const val HTML_META_CONTENT = "html.meta_content"
    const val HTML_LINKS = "html.links"
    const val HTML_ATTRIBUTES = "html.attributes"
    const val HTML_TABLE_TO_JSON = "html.table_to_json"

    // object
    const val OBJECT_GET = "object.get"
    const val OBJECT_SET = "object.set"
    const val OBJECT_REMOVE = "object.remove"
    const val OBJECT_OMIT = "object.omit"
    const val OBJECT_PICK = "object.pick"
    const val OBJECT_MERGE = "object.merge"
    const val OBJECT_KEYS = "object.keys"
    const val OBJECT_VALUES = "object.values"
    const val OBJECT_ENTRIES = "object.entries"
    const val OBJECT_FROM_ENTRIES = "object.from_entries"
    const val OBJECT_HAS_KEY = "object.has_key"
    const val OBJECT_IS_EMPTY = "object.is_empty"

    // url
    const val URL_PARSE = "url.parse"
    const val URL_BUILD = "url.build"
    const val URL_SET_QUERY_PARAM = "url.set_query_param"
    const val URL_GET_QUERY_PARAM = "url.get_query_param"

    // csv
    const val CSV_PARSE = "csv.parse"
    const val CSV_STRINGIFY = "csv.stringify"

    // xml
    const val XML_PARSE = "xml.parse"
    const val XML_STRINGIFY = "xml.stringify"

    // json
    const val JSON_EXTRACT = "json.extract"
    const val JSON_PARSE = "json.parse"
    const val JSON_STRINGIFY = "json.stringify"
    const val JSON_VALIDATE = "json.validate"
    const val JSON_SCHEMA_VALIDATE = "json.schema_validate"

    // codec
    const val CODEC_BASE64_ENCODE = "codec.base64_encode"
    const val CODEC_BASE64_DECODE = "codec.base64_decode"
    const val CODEC_BASE64_URL_ENCODE = "codec.base64_url_encode"
    const val CODEC_BASE64_URL_DECODE = "codec.base64_url_decode"
    const val CODEC_HEX_ENCODE = "codec.hex_encode"
    const val CODEC_HEX_DECODE = "codec.hex_decode"
    const val CODEC_URL_ENCODE = "codec.url_encode"
    const val CODEC_URL_DECODE = "codec.url_decode"
    const val CODEC_HTML_ESCAPE = "codec.html_escape"
    const val CODEC_HTML_UNESCAPE = "codec.html_unescape"

    // crypto
    const val CRYPTO_HASH = "crypto.hash"
    const val CRYPTO_HMAC = "crypto.hmac"
    const val CRYPTO_ENCRYPT = "crypto.encrypt"
    const val CRYPTO_DECRYPT = "crypto.decrypt"
    const val CRYPTO_RANDOM_BYTES = "crypto.random_bytes"
    const val CRYPTO_UUID = "crypto.uuid"

    // text
    const val TEXT_LENGTH = "text.length"
    const val TEXT_SPLIT = "text.split"
    const val TEXT_REGEX_MATCH = "text.regex_match"
    const val TEXT_SUBSTRING = "text.substring"
    const val TEXT_SUBSTRING_BEFORE = "text.substring_before"
    const val TEXT_SUBSTRING_AFTER = "text.substring_after"
    const val TEXT_TRIM = "text.trim"
    const val TEXT_REPLACE = "text.replace"
    const val TEXT_REPLACE_REGEX = "text.replace_regex"
    const val TEXT_JOIN = "text.join"
    const val TEXT_MATCH_ALL = "text.match_all"
    const val TEXT_PAD = "text.pad"
    const val TEXT_FORMAT_NUMBER = "text.format_number"
    const val TEXT_LOWERCASE = "text.lowercase"
    const val TEXT_UPPERCASE = "text.uppercase"
    const val TEXT_CAPITALIZE = "text.capitalize"
    const val TEXT_REPEAT = "text.repeat"
    const val TEXT_INDEX_OF = "text.index_of"
    const val TEXT_REVERSE = "text.reverse"
    const val TEXT_TEMPLATE = "text.template"
    const val TEXT_CONTAINS = "text.contains"
    const val TEXT_STARTS_WITH = "text.starts_with"
    const val TEXT_ENDS_WITH = "text.ends_with"
    const val TEXT_SLUGIFY = "text.slugify"
    const val TEXT_TRUNCATE = "text.truncate"

    // array
    const val ARRAY_LENGTH = "array.length"
    const val ARRAY_CREATE = "array.create"
    const val ARRAY_APPEND = "array.append"
    const val ARRAY_INSERT_AT = "array.insert_at"
    const val ARRAY_REMOVE_AT = "array.remove_at"
    const val ARRAY_FILTER = "array.filter"
    const val ARRAY_MAP = "array.map"
    const val ARRAY_FLAT_MAP = "array.flat_map"
    const val ARRAY_CONCAT = "array.concat"
    const val ARRAY_ZIP = "array.zip"
    const val ARRAY_TAKE = "array.take"
    const val ARRAY_DROP = "array.drop"
    const val ARRAY_CONTAINS = "array.contains"
    const val ARRAY_FIND = "array.find"
    const val ARRAY_DISTINCT = "array.distinct"
    const val ARRAY_SORT = "array.sort"
    const val ARRAY_REVERSE = "array.reverse"
    const val ARRAY_SLICE = "array.slice"
    const val ARRAY_FLATTEN = "array.flatten"
    const val ARRAY_GROUP_BY = "array.group_by"
    const val ARRAY_REDUCE = "array.reduce"
    const val ARRAY_FIRST = "array.first"
    const val ARRAY_LAST = "array.last"
    const val ARRAY_SUM = "array.sum"
    const val ARRAY_AVG = "array.avg"
    const val ARRAY_MIN = "array.min"
    const val ARRAY_MAX = "array.max"
    const val ARRAY_CHUNK = "array.chunk"
    const val ARRAY_SHUFFLE = "array.shuffle"
    const val ARRAY_SAMPLE = "array.sample"
    const val ARRAY_INDEX_OF = "array.index_of"
    const val ARRAY_INTERSECTION = "array.intersection"
    const val ARRAY_DIFFERENCE = "array.difference"

    // date
    const val DATE_NOW = "date.now"
    const val DATE_FORMAT = "date.format"
    const val DATE_PARSE = "date.parse"
    const val DATE_ADD = "date.add"
    const val DATE_SUBTRACT = "date.subtract"
    const val DATE_DIFF = "date.diff"
    const val DATE_RELATIVE_TIME = "date.relative_time"
    const val DATE_GET_COMPONENT = "date.get_component"

    // math
    const val MATH_ADD = "math.add"
    const val MATH_SUBTRACT = "math.subtract"
    const val MATH_MULTIPLY = "math.multiply"
    const val MATH_DIVIDE = "math.divide"
    const val MATH_MODULO = "math.modulo"
    const val MATH_MIN = "math.min"
    const val MATH_MAX = "math.max"
    const val MATH_POW = "math.pow"
    const val MATH_SQRT = "math.sqrt"
    const val MATH_SUM = "math.sum"
    const val MATH_AVG = "math.avg"
    const val MATH_LOG = "math.log"
    const val MATH_EXP = "math.exp"
    const val MATH_NEGATE = "math.negate"
    const val MATH_ROUND = "math.round"
    const val MATH_FLOOR = "math.floor"
    const val MATH_CEIL = "math.ceil"
    const val MATH_ABS = "math.abs"
    const val MATH_RANDOM = "math.random"
    const val MATH_CLAMP = "math.clamp"

    // http
    const val HTTP_REQUEST = "http.request"

    // storage
    const val STORAGE_PREFERENCES_GET = "storage.preferences_get"
    const val STORAGE_PREFERENCES_SET = "storage.preferences_set"
    const val STORAGE_PREFERENCES_DELETE = "storage.preferences_delete"
    const val STORAGE_PREFERENCES_HAS = "storage.preferences_has"
    const val STORAGE_PREFERENCES_CLEAR = "storage.preferences_clear"

    // action
    const val OPEN_EXTERNAL_URL = "action.open_external_url"
    const val OPEN_EXTERNAL_APP = "action.open_external_app"
    const val OPEN_INTERNAL_WEB = "action.open_internal_web"
    const val SHOW_TOAST = "action.show_toast"
    const val WRITE_CLIPBOARD = "action.write_clipboard"
    const val READ_CLIPBOARD = "action.read_clipboard"
    const val UI_CONFIRM = "ui.confirm"
    const val UI_INPUT_DIALOG = "ui.input_dialog"
    const val UI_SELECT_DIALOG = "ui.select_dialog"
    const val SYSTEM_SHARE = "system.share"
    const val SYSTEM_NOTIFICATION = "system.notification"
    const val SYSTEM_VIBRATE = "system.vibrate"
    const val IMAGE_PREVIEW = "ui.image_preview"
    const val SYNC_COOKIE = "action.sync_cookie"

    // bilibili
    const val BILIBILI_SIGN_URL = "bilibili.sign_url"
}

/**
 * 工作流控制端口标识。
 *
 * 节点定义、图校验和执行引擎必须使用同一组标识，避免导出的工作流协议随实现位置漂移。
 */
object ActionControlPortId {
    const val IN = "in"
    const val NEXT = "next"
    const val SUCCESS = "success"
    const val FAILURE = "failure"
    const val TRUE = "true"
    const val FALSE = "false"
    const val BODY = "body"
    const val COMPLETED = "completed"
    const val MATCHED = "matched"
    const val DEFAULT = "default"
    const val TRY = "try"
    const val CATCH = "catch"
    const val FINALLY = "finally"
    const val BRANCHES = "branches"
}

/**
 * 工作流需要向用户声明的高风险能力。
 *
 * @property OPEN_EXTERNAL_URL 打开外部浏览器
 * @property OPEN_EXTERNAL_APP 打开第三方应用
 * @property OPEN_INTERNAL_WEB 打开应用内网页
 * @property NETWORK 发起 HTTP/HTTPS 网络请求
 * @property NETWORK_LOCAL_COOKIE_ACCESS 发起请求时读取和更新应用本地 Cookie
 * @property CLIPBOARD_WRITE 写入系统剪贴板
 * @property CONFIRM_DIALOG 弹出确认交互对话框
 * @property INPUT_DIALOG 弹出文本输入对话框
 * @property SELECT_DIALOG 弹出列表选择对话框
 * @property SHARE 调起系统原生分享
 * @property NOTIFICATION 发送系统桌面/横幅通知
 * @property IMAGE_PREVIEW 打开图片预览
 */
object ActionCapability {
    const val OPEN_EXTERNAL_URL = "open_external_url"
    const val OPEN_EXTERNAL_APP = "open_external_app"
    const val OPEN_INTERNAL_WEB = "open_internal_web"
    const val NETWORK = "network"
    const val NETWORK_LOCAL_COOKIE_ACCESS = "network_local_cookie_access"
    const val NETWORK_COOKIE_SYNC = "network_cookie_sync"
    const val CLIPBOARD_WRITE = "clipboard_write"
    const val CONFIRM_DIALOG = "confirm_dialog"
    const val INPUT_DIALOG = "input_dialog"
    const val SELECT_DIALOG = "select_dialog"
    const val SHARE = "share"
    const val NOTIFICATION = "notification"
    const val IMAGE_PREVIEW = "image_preview"
}

/**
 * 内置节点工作流 JSON 的统一配置键与协议字符串私有主注册表。
 *
 * 仅用于内部定义各分组的 `ConfigKey` 及协议数据对象，外部禁止直接引用。
 */
private object ActionNodeConfigKey {
    // Config Keys
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

/**
 * 流程控制节点使用的配置键。
 *
 * @property DELAY_MILLIS 延迟时间（毫秒）
 * @property CONDITION 判断条件
 * @property MESSAGE 日志或断言消息
 * @property LEVEL 日志级别
 * @property RETRY_COUNT 重试次数
 * @property RETRY_DELAY_MILLIS 重试延迟（毫秒）
 * @property TIMEOUT_MILLIS 超时时间（毫秒）
 * @property WORKFLOW_ID 子工作流 ID
 * @property OUTPUT_KEY 结果输出路径键
 * @property VALUE 匹配值或参数值
 * @property CASES Switch 分支列表
 * @property MATCHED 是否匹配成功
 * @property DATA 附带数据
 * @property VALUES 批量值列表
 * @property ERROR 捕获到的异常信息
 * @property VARIABLES 环境变量或上下文变量映射
 * @property INPUT 节点输入参数
 * @property OUTPUT 节点输出结果
 * @property RESULT 结果数据
 * @property CONDITION_MET 条件是否达成
 */
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

/**
 * 条件与逻辑比较节点使用的配置键。
 *
 * @property LEFT 比较左值
 * @property RIGHT 比较右值
 * @property VALUE 目标值
 * @property CONDITION 条件表达式
 * @property MATCHED 匹配结果
 * @property STATUS_CODE HTTP 状态码
 * @property MIN_STATUS_CODE 最小匹配状态码
 * @property MAX_STATUS_CODE 最大匹配状态码
 */
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

/**
 * 循环节点配置键。
 *
 * @property COUNT 重复循环固定次数
 * @property ITEMS 待迭代的集合对象
 * @property CONDITION 循环判断条件表达式
 * @property MAX_ITERATIONS 安全防死循环的最大迭代次数
 * @property LOOP_ID 循环作用域标识
 */
object ActionLoopConfigKey {
    const val COUNT = ActionNodeConfigKey.COUNT
    const val ITEMS = ActionNodeConfigKey.ITEMS
    const val CONDITION = ActionNodeConfigKey.CONDITION
    const val MAX_ITERATIONS = ActionNodeConfigKey.MAX_ITERATIONS
    const val LOOP_ID = ActionNodeConfigKey.LOOP_ID
}

/**
 * 注入到 [ActionExecutionContext.loop] 的当前循环运行数据键。
 *
 * @property ITEM 当前循环迭代元素
 * @property INDEX 当前循环 0-based 索引
 * @property ITERATION 当前循环 1-based 迭代序号
 */
object ActionLoopContextKey {
    const val ITEM = ActionNodeConfigKey.ITEM
    const val INDEX = ActionNodeConfigKey.INDEX
    const val ITERATION = ActionNodeConfigKey.ITERATION
}

/**
 * 数据变换与变量节点使用的配置键。
 *
 * @property TEMPLATE 字符串模板
 * @property KEY 变量名或属性名
 * @property VALUE 变量值或设置属性值
 * @property VALUES 批量设置或合并的数据集合
 * @property SEPARATOR 字符串拼接分隔符
 * @property OBJECT 目标 JSON 对象
 * @property OBJECTS 待合并的多个 JSON 对象
 * @property PATH JSONPath 或属性提取路径
 * @property PATHS 批量提取属性路径列表
 * @property FROM_PATH 目标属性原路径
 * @property TO_PATH 目标属性新路径
 * @property ASSIGNMENTS 属性映射赋值字典
 * @property MERGE_STRATEGY 合并算法策略
 * @property OUTPUT_KEY 结果输出路径键
 */
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

/**
 * 数据合并策略。
 *
 * @property SHALLOW 浅层覆盖合并
 * @property DEEP 递归深层合并
 * @property DEEP_APPEND_ARRAYS 递归深层合并且对数组进行追加而非替换
 */
object ActionDataMergeStrategy {
    const val SHALLOW = ActionNodeConfigKey.SHALLOW
    const val DEEP = ActionNodeConfigKey.DEEP
    const val DEEP_APPEND_ARRAYS = ActionNodeConfigKey.DEEP_APPEND_ARRAYS
}

/**
 * HTML 解析节点使用的配置键。
 *
 * @property HTML 原始 HTML 文本
 * @property SELECTOR CSS 选择器表达式
 * @property OPERATION 解析提取操作类型
 * @property ATTRIBUTE 提取目标的 DOM 属性名称
 * @property NAME 提取目标的元数据名称（如 meta name）
 * @property OUTPUT_KEY 结果输出路径键
 */
object ActionHtmlConfigKey {
    const val HTML = ActionNodeConfigKey.HTML
    const val SELECTOR = ActionNodeConfigKey.SELECTOR
    const val OPERATION = ActionNodeConfigKey.OPERATION
    const val ATTRIBUTE = ActionNodeConfigKey.ATTRIBUTE
    const val NAME = ActionNodeConfigKey.NAME
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

/**
 * [ActionNodeType.HTML_QUERY] 与 [ActionNodeType.HTML_QUERY_ALL] 支持的 HTML 查询操作。
 *
 * @property TEXT 返回第一个匹配元素的文本
 * @property HTML 返回第一个匹配元素的内部 HTML
 * @property OUTER_HTML 返回第一个匹配元素及其子元素的完整 HTML
 * @property ATTRIBUTE 返回第一个匹配元素的指定属性
 * @property COUNT 返回匹配元素的数量
 * @property EXISTS 返回是否至少匹配到一个元素
 * @property ALL_TEXT 返回所有匹配元素拼接后的文本
 * @property ALL_HTML 返回所有匹配元素拼接后的内部 HTML
 * @property ALL_OUTER_HTML 返回所有匹配元素拼接后的完整 HTML
 */
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

/**
 * JSON 对象节点使用的配置键。
 *
 * @property OBJECT 目标 JSON 对象
 * @property OBJECTS 待操作的多个对象列表
 * @property PATH 属性点号表达式或路径
 * @property KEY 对象的属性键
 * @property KEYS 欲包含或剔除的多个属性键列表
 * @property VALUE 对象的属性值
 * @property ENTRIES 键值对元组列表
 * @property OUTPUT_KEY 结果输出路径键
 */
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

/**
 * URL 节点使用的配置键。
 *
 * @property URL 完整 URL 字符串
 * @property BASE_URL 基础基准 URL 地址
 * @property QUERY_PARAMETERS URL 查询参数字典
 * @property KEY 查询参数键名
 * @property VALUE 查询参数值
 * @property OUTPUT_KEY 结果输出路径键
 */
object ActionUrlConfigKey {
    const val URL = ActionNodeConfigKey.URL
    const val BASE_URL = ActionNodeConfigKey.BASE_URL
    const val QUERY_PARAMETERS = ActionNodeConfigKey.QUERY_PARAMETERS
    const val KEY = ActionNodeConfigKey.KEY
    const val VALUE = ActionNodeConfigKey.VALUE
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

/**
 * bilibili.sign_url 节点使用的配置键。
 *
 * @property URL 待加签名的原始 URL 链接
 * @property IMG_KEY WBI 签名的 imgKey (可选，默认 7cd084941338484aae1ad9425b84077c)
 * @property SUB_KEY WBI 签名的 subKey (可选，默认 4932caff0ff746eab6f01bf08b70ac45)
 * @property OUTPUT_KEY 结果输出路径键
 */
object ActionBilibiliConfigKey {
    const val URL = ActionNodeConfigKey.URL
    const val IMG_KEY = ActionNodeConfigKey.IMG_KEY
    const val SUB_KEY = ActionNodeConfigKey.SUB_KEY
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

/**
 * [ActionNodeType.URL_PARSE] 输出对象中各个稳定属性字段的键名。
 *
 * @property PROTOCOL 协议（如 https）
 * @property HOST 主机域名或 IP
 * @property PORT 端口号
 * @property PATH 路径串
 * @property FULL_URL 规范化后的全量 URL
 * @property QUERY_PARAMETERS 查询参数字典映射
 */
object ActionUrlParsedKey {
    const val PROTOCOL = ActionNodeConfigKey.PROTOCOL
    const val HOST = ActionNodeConfigKey.HOST
    const val PORT = ActionNodeConfigKey.PORT
    const val PATH = ActionNodeConfigKey.PATH
    const val FULL_URL = ActionNodeConfigKey.FULL_URL
    const val QUERY_PARAMETERS = ActionNodeConfigKey.QUERY_PARAMETERS
}

/**
 * CSV 节点使用的配置键。
 *
 * @property TEXT CSV 格式化文本
 * @property DELIMITER 单元格列分隔符
 * @property HEADER_ROW 是否首行为表头列名
 * @property ITEMS 行数据对象集合
 * @property OUTPUT_KEY 结果输出路径键
 */
object ActionCsvConfigKey {
    const val TEXT = ActionNodeConfigKey.TEXT
    const val DELIMITER = ActionNodeConfigKey.DELIMITER
    const val HEADER_ROW = ActionNodeConfigKey.HEADER_ROW
    const val ITEMS = ActionNodeConfigKey.ITEMS
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

/**
 * XML 节点使用的配置键。
 *
 * @property TEXT 原始 XML 字符串
 * @property DATA 欲序列化为 XML 的目标数据结构
 * @property OUTPUT_KEY 结果输出路径键
 */
object ActionXmlConfigKey {
    const val TEXT = ActionNodeConfigKey.TEXT
    const val DATA = ActionNodeConfigKey.DATA
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

/**
 * JSON 提取、解析与校验节点使用的配置键。
 *
 * @property SOURCE 原始数据源
 * @property PATH JSONPath 表达式路径
 * @property VALUE 解析或替换的值
 * @property TEXT 待解析的 JSON 字符串
 * @property SCHEMA JSON Schema 校验规范文档
 * @property OUTPUT_KEY 结果输出路径键
 */
object ActionJsonConfigKey {
    const val SOURCE = ActionNodeConfigKey.SOURCE
    const val PATH = ActionNodeConfigKey.PATH
    const val VALUE = ActionNodeConfigKey.VALUE
    const val TEXT = ActionNodeConfigKey.TEXT
    const val SCHEMA = ActionNodeConfigKey.SCHEMA
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

/**
 * 编解码节点使用的配置键。
 *
 * @property TEXT 输入待编码/解码文本
 * @property OUTPUT_KEY 结果输出路径键
 */
object ActionCodecConfigKey {
    const val TEXT = ActionNodeConfigKey.TEXT
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

/**
 * 加密与解密节点使用的配置键。
 *
 * @property TEXT 输入纯文本或密文
 * @property ALGORITHM 对称/非对称加密加解密算法名
 * @property SECRET 密钥字符串
 * @property SECRET_KEY 专有 Secret Key
 * @property KEY_BYTES 密钥字节数组
 * @property OUTPUT_KEY 结果输出路径键
 */
object ActionCryptoConfigKey {
    const val TEXT = ActionNodeConfigKey.TEXT
    const val ALGORITHM = ActionNodeConfigKey.ALGORITHM
    const val SECRET = ActionNodeConfigKey.SECRET
    const val SECRET_KEY = ActionNodeConfigKey.SECRET_KEY
    const val KEY_BYTES = ActionNodeConfigKey.KEY_BYTES
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

/**
 * 摘要节点支持的哈希算法。
 *
 * @property SHA_256 SHA-256 哈希
 * @property SHA_512 SHA-512 哈希
 * @property MD5 MD5 哈希
 * @property SHA_1 SHA-1 哈希
 * @property SHA_224 SHA-224 哈希
 * @property SHA_384 SHA-384 哈希
 * @property SHA3_224 SHA3-224 哈希
 * @property SHA3_256 SHA3-256 哈希
 * @property SHA3_384 SHA3-384 哈希
 * @property SHA3_512 SHA3-512 哈希
 * @property SM3 SM3 哈希
 * @property CRC32 CRC32 校验和
 */
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

/**
 * 文本处理节点使用的配置键。
 *
 * @property TEXT 待操作的主文本
 * @property VALUES 待合并或拼接的多个文本集合
 * @property OUTPUT_KEY 结果输出路径键
 * @property DELIMITER 拆分分隔字符串
 * @property SEPARATOR 连接拼接分隔符
 * @property IS_REGEX 分隔或正则匹配是否使用正则表达式
 * @property LIMIT 拆分或匹配上限数
 * @property PATTERN 正则表达式模式
 * @property REPLACEMENT 正则替换目标文本
 * @property START_INDEX 子串截取起始索引
 * @property END_INDEX 子串截取结束索引
 * @property MISSING_DELIMITER_VALUE 未查找到分隔符时的默认填充值
 * @property ELLIPSIS 超长截断时的省略符号（如 ...）
 * @property MATCHED 是否正则匹配成功
 * @property GROUP_VALUES 正则捕获组结果列表
 * @property IGNORE_CASE 比较或搜索时是否忽略大小写
 * @property PAD_LENGTH 补充填满的目标总长度
 * @property PAD_CHARACTER 用于补充对齐的单字符
 * @property PAD_END 是否在尾部补齐（为 false 则在头部补齐）
 * @property FRACTION_DIGITS 保留格式化的精细小数位数
 * @property COUNT 重复拼接次数
 * @property TEMPLATE 待渲染的文本模板
 * @property OBJECT 模版占位符替换字典对象
 */
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

/**
 * 数组处理节点使用的配置键。
 *
 * @property VALUES 待操作的目标数组
 * @property OTHER_VALUES 进行求交集/差集比较的第二个数组
 * @property VALUE 待插入、查找或追加的单项值
 * @property OUTPUT_KEY 结果输出路径键
 * @property INDEX 指定操作的数组下标索引
 * @property SIZE 切块的分组每块元素数量
 * @property START 数组切片起始下标
 * @property END 数组切片结束下标
 * @property COUNT 随机抽样的元素个数
 * @property INITIAL_VALUE Reduce 归约计算的初始累加值
 * @property FIELD_PATH 分组或过滤依赖的对象属性字段路径
 * @property OPERATOR 元素条件过滤的比较运算符
 * @property EXPECTED 条件过滤期望的对比值
 * @property DESCENDING 排序是否按降序排列
 */
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

/**
 * [ActionNodeType.ARRAY_FILTER] 支持的筛选运算。
 *
 * @property EQUALS 等于
 * @property NOT_EQUALS 不等于
 * @property IS_NULL 为 Null
 * @property IS_NOT_NULL 不为 Null
 * @property IS_EMPTY 为空（针对空串或空集合）
 * @property IS_NOT_EMPTY 不为空
 */
object ActionArrayFilterOperator {
    const val EQUALS = ActionNodeConfigKey.EQUALS
    const val NOT_EQUALS = ActionNodeConfigKey.NOT_EQUALS
    const val IS_NULL = ActionNodeConfigKey.IS_NULL
    const val IS_NOT_NULL = ActionNodeConfigKey.IS_NOT_NULL
    const val IS_EMPTY = ActionNodeConfigKey.IS_EMPTY
    const val IS_NOT_EMPTY = ActionNodeConfigKey.IS_NOT_EMPTY
}

/**
 * 日期时间节点使用的配置键。
 *
 * @property TEXT 待解析或格式化的日期时间字符串
 * @property TIMESTAMP 毫秒级时间戳
 * @property TIMESTAMP_LEFT 时间差比较的第一个时间戳
 * @property TIMESTAMP_RIGHT 时间差比较的第二个时间戳
 * @property COUNT 时间增减的数值（如增加 3 天）
 * @property UNIT 时间增减或计算差距的单位（days, hours, minutes...）
 * @property OUTPUT_KEY 结果输出路径键
 */
object ActionDateConfigKey {
    const val TEXT = ActionNodeConfigKey.TEXT
    const val TIMESTAMP = ActionNodeConfigKey.TIMESTAMP
    const val TIMESTAMP_LEFT = ActionNodeConfigKey.TIMESTAMP_LEFT
    const val TIMESTAMP_RIGHT = ActionNodeConfigKey.TIMESTAMP_RIGHT
    const val COUNT = ActionNodeConfigKey.COUNT
    const val UNIT = ActionNodeConfigKey.UNIT
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

/**
 * 数学计算节点使用的配置键。
 *
 * @property VALUE 单值计算的目标数值
 * @property VALUES 多数值计算的集合列表
 * @property LEFT 双值计算的左操作数
 * @property RIGHT 双值计算的右操作数
 * @property MIN 数值限幅 Clamp 的最小值下限
 * @property MAX 数值限幅 Clamp 的最大值上限
 * @property DECIMALS 四舍五入保留的小数点位数
 * @property OUTPUT_KEY 结果输出路径键
 */
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

/**
 * HTTP 请求节点使用的配置键。
 *
 * @property URL 请求目标 URL 地址
 * @property METHOD HTTP 请求方法（GET, POST, PUT, DELETE...）
 * @property HEADERS 自定义 HTTP 请求头 Dict
 * @property QUERY Query Url 查询参数 Dict
 * @property BODY HTTP 请求体数据
 * @property BODY_TYPE 请求体类型协议值（json, text, formUrlEncoded）
 * @property CONTENT_TYPE 请求头 Content-Type 覆盖声明
 * @property TIMEOUT_MILLIS 网络请求超时时间（毫秒）
 * @property RETRY_COUNT 请求失败重试次数
 * @property RETRY_DELAY_MILLIS 请求重试间隔时间（毫秒）
 * @property USE_LOCAL_COOKIE_STORAGE 是否允许请求携带并更新应用本地 Cookie；启用时需要额外声明 Cookie 访问权限
 */
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

/**
 * HTTP 请求体编码类型。
 *
 * @property JSON 以 JSON 文本发送请求体
 * @property TEXT 以纯文本发送请求体
 * @property FORM_URL_ENCODED 以 application/x-www-form-urlencoded 格式发送对象请求体
 */
object ActionHttpBodyType {
    const val JSON = ActionNodeConfigKey.JSON
    const val TEXT = ActionNodeConfigKey.TEXT
    const val FORM_URL_ENCODED = ActionNodeConfigKey.FORM_URL_ENCODED
}

/**
 * HTTP 节点标准输出对象的字段名。
 *
 * @property STATUS_CODE HTTP 响应状态码
 * @property IS_SUCCESS 状态码是否位于 2xx 成功区间
 * @property CONTENT_TYPE 响应 Content-Type 文本
 * @property RAW_BODY 未解析的响应原始文本
 * @property BODY 规范化或自动 JSON 解析后的响应体对象
 * @property HTML HTML 响应的原始 HTML
 * @property TEXT HTML 响应提取出的纯文本
 * @property TITLE HTML 文档标题
 */
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

/**
 * 工作流持久化存储节点使用的配置键。
 *
 * @property KEY 存储项键名
 * @property VALUE 欲存入的持久化目标值
 * @property OUTPUT_KEY 结果输出路径键
 */
object ActionStorageConfigKey {
    const val KEY = ActionNodeConfigKey.KEY
    const val VALUE = ActionNodeConfigKey.VALUE
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

/**
 * 打开外部浏览器节点（open.external_url）使用的配置键。
 *
 * @property URL 欲在外部浏览器中打开的目标网址
 */
object ActionOpenUrlConfigKey {
    const val URL = ActionNodeConfigKey.URL
}

/**
 * 打开第三方应用节点（open.external_app）使用的配置键。
 *
 * @property URI 欲调起的 DeepLink / Schema URI
 * @property FALLBACK_URL 应用未安装时的回退 Web 网址
 */
object ActionOpenAppConfigKey {
    const val URI = ActionNodeConfigKey.URI
    const val FALLBACK_URL = ActionNodeConfigKey.FALLBACK_URL
}

/**
 * 打开应用内网页节点（open.internal_web）使用的配置键。
 *
 * @property URL 欲在应用内 Web 视图中打开的目标网址
 */
object ActionOpenWebConfigKey {
    const val URL = ActionNodeConfigKey.URL
}

/**
 * 弹出 Toast 提示节点（ui.show_toast）使用的配置键。
 *
 * @property MESSAGE 弹出提示的消息文本内容
 * @property TEXT 兼容使用消息文本内容
 */
object ActionToastConfigKey {
    const val MESSAGE = ActionNodeConfigKey.MESSAGE
    const val TEXT = ActionNodeConfigKey.TEXT
}

/**
 * 系统剪贴板节点（system.write_clipboard / system.read_clipboard）使用的配置键。
 *
 * @property TEXT 写入系统剪贴板的纯文本
 * @property OUTPUT_KEY 读取剪贴板结果的节点输出路径键
 */
object ActionClipboardConfigKey {
    const val TEXT = ActionNodeConfigKey.TEXT
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

/**
 * 二次确认弹窗节点（ui.confirm）使用的配置键。
 *
 * @property TITLE 确认弹窗标题
 * @property MESSAGE 确认弹窗提示正文消息
 * @property CONFIRM_TEXT 自定义确认按钮文本
 * @property CANCEL_TEXT 自定义取消按钮文本
 */
object ActionConfirmConfigKey {
    const val TITLE = ActionNodeConfigKey.TITLE
    const val MESSAGE = ActionNodeConfigKey.MESSAGE
    const val CONFIRM_TEXT = ActionNodeConfigKey.CONFIRM_TEXT
    const val CANCEL_TEXT = ActionNodeConfigKey.CANCEL_TEXT
}

/**
 * 文本输入弹窗节点（ui.input_dialog）使用的配置键。
 *
 * @property TITLE 输入弹窗标题
 * @property SUBTITLE 输入框上方的提示副标题
 * @property DEFAULT_VALUE 输入框初始默认文本
 * @property CONFIRM_TEXT 自定义确认按钮文本
 * @property CANCEL_TEXT 自定义取消按钮文本
 * @property OUTPUT_KEY 提交文本结果在工作流中的输出路径键
 */
object ActionInputDialogConfigKey {
    const val TITLE = ActionNodeConfigKey.TITLE
    const val SUBTITLE = ActionNodeConfigKey.SUBTITLE
    const val DEFAULT_VALUE = ActionNodeConfigKey.DEFAULT_VALUE
    const val CONFIRM_TEXT = ActionNodeConfigKey.CONFIRM_TEXT
    const val CANCEL_TEXT = ActionNodeConfigKey.CANCEL_TEXT
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

/**
 * 列表选择弹窗节点（ui.select_dialog）使用的配置键。
 *
 * @property TITLE 选择弹窗标题
 * @property SUBTITLE 列表上方的提示副标题
 * @property OPTIONS 可供勾选的选项 JSON 数组 [{"title":"xxx", "value":"xxx"}]
 * @property DEFAULT_VALUES 默认勾选的 value 集合
 * @property DEFAULT_INDICES 默认勾选的 0-based 索引集合
 * @property IS_MULTI_SELECT 是否为多选模式（false: 点击条目即完成；true: 多选勾选框 + 确定/取消按钮）
 * @property OUTPUT_MODE 结果输出模式（value: 输出选中的值, index: 输出选中的索引, both: 输出包含值与索引的结构）
 * @property CONFIRM_TEXT 多选模式下的自定义确认按钮文本
 * @property CANCEL_TEXT 多选模式下的自定义取消按钮文本
 * @property OUTPUT_KEY 选择结果在工作流中的输出路径键
 * @property VALUE 选项模型中的取值字段 Key
 * @property INDEX 选项模型中的索引字段 Key
 */
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

/**
 * 系统桌面/横幅通知节点（system.notification）使用的配置键。
 *
 * @property TITLE 通知栏标题
 * @property CONTENT 通知栏正文内容
 */
object ActionNotificationConfigKey {
    const val TITLE = ActionNodeConfigKey.TITLE
    const val CONTENT = ActionNodeConfigKey.CONTENT
}

/**
 * 系统原生分享节点（system.share）使用的配置键。
 *
 * @property TITLE 分享卡片标题
 * @property TEXT 分享到外部应用的内容纯文本
 * @property CONTENT 兼容使用的内容纯文本
 * @property URL 欲分享附带的目标链接
 */
object ActionShareConfigKey {
    const val TITLE = ActionNodeConfigKey.TITLE
    const val TEXT = ActionNodeConfigKey.TEXT
    const val CONTENT = ActionNodeConfigKey.CONTENT
    const val URL = ActionNodeConfigKey.URL
}

/**
 * 大图预览画廊节点（ui.image_preview）使用的配置键。
 *
 * @property INDEX 画廊初始画面的 0-based 索引
 * @property IMAGES 预览图片 URL 集合（JSON 数组）
 */
object ActionImagePreviewConfigKey {
    const val INDEX = ActionNodeConfigKey.INDEX
    const val IMAGES = ActionNodeConfigKey.IMAGES
}

/**
 * 网页 Cookie 同步弹窗节点（action.sync_cookie）使用的配置键。
 *
 * @property URL 需打开并同步 Cookie 的目标 Web 地址
 * @property TITLE 弹窗标题
 */
object ActionSyncCookieConfigKey {
    const val URL = ActionNodeConfigKey.URL
    const val TITLE = ActionNodeConfigKey.TITLE
    const val HEADERS = ActionNodeConfigKey.HEADERS
    const val USER_AGENT = ActionNodeConfigKey.USER_AGENT
}



/**
 * 列表选择输出模式选项常量。
 *
 * @property VALUE 仅输出选中的 value 文本（单选为 String，多选为 String 数组）
 * @property INDEX 仅输出选中的 0-based 索引（单选为 Int，多选为 Int 数组）
 * @property BOTH 同时输出包含 index 与 value 的结构化对象
 */
object ActionSelectOutputMode {
    const val VALUE = ActionNodeConfigKey.VALUE
    const val INDEX = ActionNodeConfigKey.INDEX
    const val BOTH = ActionNodeConfigKey.BOTH
}
