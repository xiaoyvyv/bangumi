package com.xiaoyv.bangumi.shared.data.workflow.model.spec

import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayConfigKey.COUNT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayConfigKey.DESCENDING
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayConfigKey.END
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayConfigKey.EXPECTED
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayConfigKey.FIELD_PATH
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayConfigKey.INDEX
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayConfigKey.INITIAL_VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayConfigKey.OPERATOR
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayConfigKey.OTHER_VALUES
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayConfigKey.SIZE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayConfigKey.START
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayConfigKey.VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayConfigKey.VALUES
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayFilterOperator.EQUALS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayFilterOperator.IS_EMPTY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayFilterOperator.IS_NOT_EMPTY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayFilterOperator.IS_NOT_NULL
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayFilterOperator.IS_NULL
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayFilterOperator.NOT_EQUALS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionBilibiliConfigKey.IMG_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionBilibiliConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionBilibiliConfigKey.SUB_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionBilibiliConfigKey.URL
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionClipboardConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionClipboardConfigKey.TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCodecConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCodecConfigKey.TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionConfirmConfigKey.CANCEL_TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionConfirmConfigKey.CONFIRM_TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionConfirmConfigKey.MESSAGE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionConfirmConfigKey.TITLE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlConfigKey.CONDITION
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlConfigKey.LEFT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlConfigKey.MATCHED
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlConfigKey.MAX_STATUS_CODE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlConfigKey.MIN_STATUS_CODE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlConfigKey.RIGHT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlConfigKey.STATUS_CODE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlConfigKey.VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCryptoConfigKey.ALGORITHM
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCryptoConfigKey.KEY_BYTES
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCryptoConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCryptoConfigKey.SECRET
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCryptoConfigKey.SECRET_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCryptoConfigKey.TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCsvConfigKey.DELIMITER
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCsvConfigKey.HEADER_ROW
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCsvConfigKey.ITEMS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCsvConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCsvConfigKey.TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataConfigKey.ASSIGNMENTS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataConfigKey.FROM_PATH
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataConfigKey.KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataConfigKey.MERGE_STRATEGY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataConfigKey.OBJECT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataConfigKey.OBJECTS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataConfigKey.PATH
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataConfigKey.PATHS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataConfigKey.SEPARATOR
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataConfigKey.TEMPLATE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataConfigKey.TO_PATH
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataConfigKey.VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataConfigKey.VALUES
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataMergeStrategy.DEEP
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataMergeStrategy.DEEP_APPEND_ARRAYS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataMergeStrategy.SHALLOW
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDateConfigKey.COUNT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDateConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDateConfigKey.TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDateConfigKey.TIMESTAMP
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDateConfigKey.TIMESTAMP_LEFT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDateConfigKey.TIMESTAMP_RIGHT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDateConfigKey.UNIT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionErrorKey.CAUSE_CLASS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionErrorKey.CAUSE_MESSAGE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionErrorKey.CODE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionErrorKey.CONFIG
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionErrorKey.CONFIG_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionErrorKey.DETAILS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionErrorKey.ERROR
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionErrorKey.HINT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionErrorKey.ISSUES
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionErrorKey.MESSAGE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionErrorKey.NODE_ID
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionErrorKey.NODE_LABEL
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionErrorKey.NODE_TYPE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionErrorKey.SIDE_EFFECT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionErrorKey.WORKFLOW_ID
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionErrorKey.WORKFLOW_NAME
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFileConfigKey.APPEND
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFileConfigKey.FROM_PATH
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFileConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFileConfigKey.PATH
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFileConfigKey.PATHS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFileConfigKey.TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFileConfigKey.TO_PATH
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey.CASES
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey.CONDITION
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey.CONDITION_MET
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey.DATA
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey.DELAY_MILLIS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey.ERROR
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey.INPUT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey.LEVEL
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey.MATCHED
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey.MESSAGE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey.OUTPUT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey.RESULT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey.RETRY_COUNT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey.RETRY_DELAY_MILLIS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey.TIMEOUT_MILLIS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey.VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey.VALUES
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey.VARIABLES
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey.WORKFLOW_ID
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHashAlgorithm.CRC32
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHashAlgorithm.MD5
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHashAlgorithm.SHA3_224
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHashAlgorithm.SHA3_256
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHashAlgorithm.SHA3_384
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHashAlgorithm.SHA3_512
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHashAlgorithm.SHA_1
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHashAlgorithm.SHA_224
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHashAlgorithm.SHA_256
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHashAlgorithm.SHA_384
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHashAlgorithm.SHA_512
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHashAlgorithm.SM3
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHtmlConfigKey.ATTRIBUTE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHtmlConfigKey.HTML
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHtmlConfigKey.NAME
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHtmlConfigKey.OPERATION
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHtmlConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHtmlConfigKey.SELECTOR

import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpBodyType.FORM_URL_ENCODED
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpBodyType.JSON
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpBodyType.TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpConfigKey.BODY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpConfigKey.BODY_TYPE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpConfigKey.CONTENT_TYPE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpConfigKey.FILE_NAME
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpConfigKey.HEADERS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpConfigKey.METHOD
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpConfigKey.PATH
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpConfigKey.QUERY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpConfigKey.RETRY_COUNT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpConfigKey.RETRY_DELAY_MILLIS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpConfigKey.TIMEOUT_MILLIS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpConfigKey.URL
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpConfigKey.USE_LOCAL_COOKIE_STORAGE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpResponseKey.BODY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpResponseKey.CONTENT_TYPE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpResponseKey.FILE_NAME
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpResponseKey.FILE_PATH
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpResponseKey.HTML
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpResponseKey.IS_SUCCESS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpResponseKey.RAW_BODY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpResponseKey.STATUS_CODE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpResponseKey.TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpResponseKey.TITLE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionImagePreviewConfigKey.IMAGES
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionImagePreviewConfigKey.INDEX
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionInputDialogConfigKey.CANCEL_TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionInputDialogConfigKey.CONFIRM_TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionInputDialogConfigKey.DEFAULT_VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionInputDialogConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionInputDialogConfigKey.SUBTITLE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionInputDialogConfigKey.TITLE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionJsonConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionJsonConfigKey.PATH
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionJsonConfigKey.SCHEMA
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionJsonConfigKey.SOURCE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionJsonConfigKey.TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionJsonConfigKey.VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionLoopConfigKey.CONDITION
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionLoopConfigKey.COUNT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionLoopConfigKey.ITEMS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionLoopConfigKey.LOOP_ID
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionLoopConfigKey.MAX_ITERATIONS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionLoopContextKey.INDEX
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionLoopContextKey.ITEM
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionLoopContextKey.ITERATION
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionLoopContextKey.LOOP
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionMathConfigKey.DECIMALS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionMathConfigKey.LEFT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionMathConfigKey.MAX
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionMathConfigKey.MIN
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionMathConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionMathConfigKey.RIGHT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionMathConfigKey.VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionMathConfigKey.VALUES
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNotificationConfigKey.CONTENT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNotificationConfigKey.TITLE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionObjectConfigKey.ENTRIES
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionObjectConfigKey.KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionObjectConfigKey.KEYS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionObjectConfigKey.OBJECT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionObjectConfigKey.OBJECTS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionObjectConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionObjectConfigKey.PATH
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionObjectConfigKey.VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionOpenAppConfigKey.FALLBACK_URL
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionOpenAppConfigKey.URI
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionOpenUrlConfigKey.URL
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionOpenWebConfigKey.URL
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionProgressDialogConfigKey.ACTION
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionProgressDialogConfigKey.MAX_PROGRESS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionProgressDialogConfigKey.MESSAGE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionProgressDialogConfigKey.MODE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionProgressDialogConfigKey.PROGRESS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionProgressDialogConfigKey.TASK_ID
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionProgressDialogConfigKey.TITLE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSelectDialogConfigKey.CANCEL_TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSelectDialogConfigKey.CONFIRM_TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSelectDialogConfigKey.DEFAULT_INDICES
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSelectDialogConfigKey.DEFAULT_VALUES
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSelectDialogConfigKey.INDEX
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSelectDialogConfigKey.INDICES
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSelectDialogConfigKey.IS_MULTI_SELECT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSelectDialogConfigKey.OPTIONS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSelectDialogConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSelectDialogConfigKey.OUTPUT_MODE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSelectDialogConfigKey.SUBTITLE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSelectDialogConfigKey.TITLE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSelectDialogConfigKey.VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSelectDialogConfigKey.VALUES
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSelectOutputMode.BOTH
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSelectOutputMode.INDEX
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSelectOutputMode.VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionShareConfigKey.CONTENT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionShareConfigKey.TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionShareConfigKey.TITLE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionShareConfigKey.URL
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionStorageConfigKey.KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionStorageConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionStorageConfigKey.VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSyncCookieConfigKey.HEADERS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSyncCookieConfigKey.TITLE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSyncCookieConfigKey.URL
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSyncCookieConfigKey.USER_AGENT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey.COUNT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey.DELIMITER
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey.ELLIPSIS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey.END_INDEX
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey.FRACTION_DIGITS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey.GROUP_VALUES
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey.IGNORE_CASE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey.IS_REGEX
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey.LIMIT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey.MATCHED
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey.MISSING_DELIMITER_VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey.OBJECT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey.PAD_CHARACTER
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey.PAD_END
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey.PAD_LENGTH
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey.PATTERN
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey.REPLACEMENT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey.SEPARATOR
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey.START_INDEX
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey.TEMPLATE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey.TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey.VALUES
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionToastConfigKey.MESSAGE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionToastConfigKey.TEXT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionUrlConfigKey.BASE_URL
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionUrlConfigKey.KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionUrlConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionUrlConfigKey.QUERY_PARAMETERS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionUrlConfigKey.URL
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionUrlConfigKey.VALUE
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionUrlParsedKey.FULL_URL
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionUrlParsedKey.HOST
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionUrlParsedKey.PATH
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionUrlParsedKey.PORT
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionUrlParsedKey.PROTOCOL
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionUrlParsedKey.QUERY_PARAMETERS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionVideoPreviewConfigKey.HEADERS
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionVideoPreviewConfigKey.URL
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionXmlConfigKey.DATA
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionXmlConfigKey.OUTPUT_KEY
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionXmlConfigKey.TEXT


/**
 * 节点失败与错误输出（errorOutput）以及异常诊断使用的统一 Key 常量。
 *
 * @property ERROR 包含错误信息的 JsonObject 根节点名
 * @property CODE 机器可读的错误代码（Error Code）
 * @property MESSAGE 人类可读的排错提示文本
 * @property NODE_ID 发生错误的节点 ID
 * @property NODE_TYPE 发生错误的节点类型
 * @property NODE_LABEL 发生错误的节点展示名称
 * @property WORKFLOW_ID 目标工作流 ID
 * @property WORKFLOW_NAME 目标工作流名称
 * @property CONFIG_KEY 触发故障的节点配置项键名
 * @property DETAILS 异常快照与上下文数据
 * @property CAUSE_CLASS 底层抛出的异常类名
 * @property CAUSE_MESSAGE 底层抛出的异常描述信息
 * @property HINT 指导解决案例错误的踩坑建议
 * @property CONFIG 节点的输入配置快照
 * @property SIDE_EFFECT 副作用类型标识
 * @property ISSUES 静态校验发现的问题列表
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
    const val CONFIG = "config"
    const val SIDE_EFFECT = "sideEffect"
    const val ISSUES = "issues"
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
    const val IMAGE = "image"
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
    const val FILE_NAME = "fileName"
    const val FILE_PATH = "filePath"
    const val COUNT = "count"
    const val ITEMS = "items"
    const val MAX_ITERATIONS = "maxIterations"
    const val LOOP = "loop"
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
    const val PROGRESS = "progress"
    const val MAX_PROGRESS = "maxProgress"
    const val PROGRESS_MODE = "progressMode"
    const val ACTION = "action"
    const val TASK_ID = "taskId"
    const val CONTENT = "content"
    const val ERROR = "error"
    const val VARIABLES = "variables"
    const val RESULT = "result"
    const val CONDITION_MET = "conditionMet"
    const val GROUP_VALUES = "groupValues"
    const val APPEND = "append"

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
    const val SHA3_512 = "sha512"
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
    const val CLASS_NAME = "className"
    const val ELEMENT = "element"
    const val ELEMENTS = "elements"
}

/**
 * 流程控制节点的配置键。
 *
 * @property DELAY_MILLIS 延时等待毫秒数
 * @property CONDITION 流程断言或分支判断条件表达式
 * @property MESSAGE 断言失败或流程停止时的日志/提示消息
 * @property LEVEL 日志打印输出级别
 * @property RETRY_COUNT 失败自动重试最大次数
 * @property RETRY_DELAY_MILLIS 每次自动重试间隔毫秒数
 * @property TIMEOUT_MILLIS 节点/分支运行超时毫秒数
 * @property WORKFLOW_ID 子工作流调用的工作流 ID
 * @property OUTPUT_KEY 结果输出的目标变量 Key
 * @property VALUE 子工作流输入或控制计算参数值
 * @property CASES Switch 条件匹配分支键值对映射
 * @property MATCHED 匹配到的分支名称
 * @property DATA 断言或调试附带的数据
 * @property VALUES 汇合节点收集的数据数组
 * @property ERROR 异常捕获节点输出的错误对象
 * @property VARIABLES 环境变量快照 Key
 * @property INPUT 只读业务输入 JSON 数据 Key
 * @property OUTPUT 子工作流返回的结果数据
 * @property RESULT 计算运行结果 Key
 * @property CONDITION_MET 条件断言是否成立标志
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
 * 条件逻辑判断节点的配置键。
 *
 * @property LEFT 表达式或条件比较的左操作数
 * @property RIGHT 表达式或条件比较的右操作数
 * @property VALUE 逻辑计算的目标操作数
 * @property CONDITION 条件判断表达式
 * @property MATCHED 条件判断是否匹配标志
 * @property STATUS_CODE HTTP 状态码
 * @property MIN_STATUS_CODE 匹配的状态码区间最小值
 * @property MAX_STATUS_CODE 匹配的状态码区间最大值
 * @property OUTPUT_KEY 比较/计算结果输出的目标变量 Key
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
 * 循环控制节点的配置键。
 *
 * @property COUNT 循环重复执行的目标总次数
 * @property ITEMS 被遍历的数组/集合列表
 * @property CONDITION 循环继续执行的条件表达式
 * @property MAX_ITERATIONS 保护性最大循环迭代次数
 * @property LOOP_ID 关联的目标循环 ID
 */
object ActionLoopConfigKey {
    const val COUNT = ActionNodeConfigKey.COUNT
    const val ITEMS = ActionNodeConfigKey.ITEMS
    const val CONDITION = ActionNodeConfigKey.CONDITION
    const val MAX_ITERATIONS = ActionNodeConfigKey.MAX_ITERATIONS
    const val LOOP_ID = ActionNodeConfigKey.LOOP_ID
}

/**
 * 循环内部帧与上下文变量键。
 *
 * @property LOOP 最内层循环帧根变量名
 * @property ITEM 当前循环迭代项
 * @property INDEX 当前循环迭代索引（从 0 开始）
 * @property ITERATION 当前循环迭代轮数（从 1 开始）
 */
object ActionLoopContextKey {
    const val LOOP = ActionNodeConfigKey.LOOP
    const val ITEM = ActionNodeConfigKey.ITEM
    const val INDEX = ActionNodeConfigKey.INDEX
    const val ITERATION = ActionNodeConfigKey.ITERATION
}

/**
 * 变量与数据处理节点的配置键。
 *
 * @property TEMPLATE 包含 `${...}` 占位符的模板字符串
 * @property KEY 保存或设置的全局变量名
 * @property VALUE 设置的变量值或常量表达式
 * @property VALUES 拼接/合并的多个数据值列表
 * @property SEPARATOR 字符串拼接的分隔符
 * @property OBJECT 被赋值或合并的目标 JSON 对象
 * @property OBJECTS 参与合并的多个 JSON 对象列表
 * @property PATH 深层属性读取或删除的 JSONPath
 * @property PATHS 批量提取的 JSONPath 路径映射
 * @property FROM_PATH 移动/重命名变量的源路径
 * @property TO_PATH 移动/重命名变量的目标路径
 * @property ASSIGNMENTS 批量变量赋值的键值映射表
 * @property MERGE_STRATEGY JSON 对象合并策略（shallow / deep）
 * @property OUTPUT_KEY 最终数据结果保存的目标变量 Key
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
 * 数据合并策略常量。
 *
 * @property SHALLOW 浅度合并（覆盖顶层同名属性）
 * @property DEEP 深度合并（递归合并嵌套 JSON 对象）
 * @property DEEP_APPEND_ARRAYS 深度合并并追加数组元素
 */
object ActionDataMergeStrategy {
    const val SHALLOW = ActionNodeConfigKey.SHALLOW
    const val DEEP = ActionNodeConfigKey.DEEP
    const val DEEP_APPEND_ARRAYS = ActionNodeConfigKey.DEEP_APPEND_ARRAYS
}

/**
 * HTML DOM 解析节点的配置键。
 *
 * @property HTML 待解析的 HTML 网页源码文本
 * @property SELECTOR CSS 元素选择器表达式
 * @property OPERATION DOM 元素提取操作指令
 * @property ATTRIBUTE 读取的 HTML 标签属性名称
 * @property NAME 提取字段的目标名称
 * @property OUTPUT_KEY 解析提取结果保存的目标变量 Key
 */
object ActionHtmlConfigKey {
    const val SOURCE = ActionNodeConfigKey.SOURCE
    const val HTML = ActionNodeConfigKey.HTML
    const val ELEMENT = ActionNodeConfigKey.ELEMENT
    const val ELEMENTS = ActionNodeConfigKey.ELEMENTS
    const val SELECTOR = ActionNodeConfigKey.SELECTOR
    const val OPERATION = ActionNodeConfigKey.OPERATION
    const val ATTRIBUTE = ActionNodeConfigKey.ATTRIBUTE
    const val NAME = ActionNodeConfigKey.NAME
    const val BASE_URL = ActionNodeConfigKey.BASE_URL
    const val INDEX = ActionNodeConfigKey.INDEX
    const val CLASS_NAME = ActionNodeConfigKey.CLASS_NAME
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}


/**
 * JSON 对象处理节点的配置键。
 *
 * @property OBJECT 待操作的目标 JSON 对象
 * @property OBJECTS 参与合并的 JSON 对象数组
 * @property PATH 访问属性的深层路径
 * @property KEY 提取或操作的对象属性键名
 * @property KEYS 提取或剔除的对象属性键名数组
 * @property VALUE 属性设置的新值
 * @property ENTRIES 键值对 Entry 列表
 * @property OUTPUT_KEY 处理结果保存的目标变量 Key
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
 * URL 操作与生成节点的配置键。
 *
 * @property URL 待解析或修改的完整 URL 地址
 * @property BASE_URL 构造 URL 的基础域名与路径
 * @property QUERY_PARAMETERS Query 查询参数映射表
 * @property KEY Query 参数的键名
 * @property VALUE Query 参数的值
 * @property OUTPUT_KEY URL 操作结果保存的目标变量 Key
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
 * Bilibili WBI 签名节点的配置键。
 *
 * @property URL 待加签的原始 API URL
 * @property IMG_KEY WBI img_key 秘钥字段
 * @property SUB_KEY WBI sub_key 秘钥字段
 * @property OUTPUT_KEY 签名后的 URL 保存的目标变量 Key
 */
object ActionBilibiliConfigKey {
    const val URL = ActionNodeConfigKey.URL
    const val IMG_KEY = ActionNodeConfigKey.IMG_KEY
    const val SUB_KEY = ActionNodeConfigKey.SUB_KEY
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

/**
 * URL 解析结果数据结构的字段 Key。
 *
 * @property PROTOCOL URL 传输协议（如 https / http）
 * @property HOST URL 域名或 IP
 * @property PORT 端口号
 * @property PATH 路径部分
 * @property FULL_URL 完整规范化 URL
 * @property QUERY_PARAMETERS 解析出的查询参数键值对象
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
 * CSV 格式解析与生成节点的配置键。
 *
 * @property TEXT 待解析的 CSV 格式文本
 * @property DELIMITER 列分隔符（默认逗号 `,`）
 * @property HEADER_ROW 第一行是否作为标题行
 * @property ITEMS 序列化导出为 CSV 的对象数组
 * @property OUTPUT_KEY 解析或生成的 CSV 结果目标变量 Key
 */
object ActionCsvConfigKey {
    const val TEXT = ActionNodeConfigKey.TEXT
    const val DELIMITER = ActionNodeConfigKey.DELIMITER
    const val HEADER_ROW = ActionNodeConfigKey.HEADER_ROW
    const val ITEMS = ActionNodeConfigKey.ITEMS
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

/**
 * XML 格式解析与生成节点的配置键。
 *
 * @property TEXT 待解析的 XML 格式文本
 * @property DATA 转为 XML 的结构化数据对象
 * @property OUTPUT_KEY 解析/生成结果保存的目标变量 Key
 */
object ActionXmlConfigKey {
    const val TEXT = ActionNodeConfigKey.TEXT
    const val DATA = ActionNodeConfigKey.DATA
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

/**
 * JSON 结构化数据提取与校验节点的配置键。
 *
 * @property SOURCE 原始 JSON 结构化文本或对象
 * @property PATH 属性提取的 JSONPath 表达式
 * @property VALUE 校验的预估 JSON 值
 * @property TEXT 待解析或反序列化的 JSON 文本
 * @property SCHEMA 用于校验的 JSON Schema
 * @property OUTPUT_KEY 提取/解析结果保存的目标变量 Key
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
 * 编解码与转码节点的配置键。
 *
 * @property TEXT 输入的操作文本内容
 * @property OUTPUT_KEY 编解码转换结果保存的目标变量 Key
 */
object ActionCodecConfigKey {
    const val TEXT = ActionNodeConfigKey.TEXT
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

/**
 * 哈希计算与加密算法节点的配置键。
 *
 * @property TEXT 待进行哈希或加密计算的明文文本
 * @property ALGORITHM 使用的哈希/对称加密算法名
 * @property SECRET HMAC 计算使用的秘钥
 * @property SECRET_KEY 对称加密/解密的 Key
 * @property KEY_BYTES 秘钥 Byte 数组
 * @property OUTPUT_KEY 计算结果保存的目标变量 Key
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
 * 支持的哈希算法常量。
 *
 * @property SHA_256 SHA-256 摘要算法
 * @property SHA_512 SHA-512 摘要算法
 * @property MD5 MD5 摘要算法
 * @property SHA_1 SHA-1 摘要算法
 * @property SHA_224 SHA-224 摘要算法
 * @property SHA_384 SHA-384 摘要算法
 * @property SHA3_224 SHA3-224 摘要算法
 * @property SHA3_256 SHA3-256 摘要算法
 * @property SHA3_384 SHA3-384 摘要算法
 * @property SHA3_512 SHA3-512 摘要算法
 * @property SM3 国密 SM3 摘要算法
 * @property CRC32 CRC32 循环冗余校验
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
 * 文本与正则处理节点的配置键。
 *
 * @property TEXT 输入的操作目标文本
 * @property TEMPLATE 待插值求值的文本模板
 * @property OBJECT 占位符插值的数据对象
 * @property VALUES 拼接的字符串数组
 * @property OUTPUT_KEY 文本处理结果保存的目标变量 Key
 * @property DELIMITER 文本切割的分隔符
 * @property SEPARATOR 数组拼接连接符
 * @property IS_REGEX 分割/替换是否使用正则模式
 * @property LIMIT 切割产生的最大元素数量
 * @property PATTERN 正则表达式匹配模式
 * @property REPLACEMENT 正则匹配替换的目标文本
 * @property START_INDEX 切片的起始字符索引
 * @property END_INDEX 切片的结束字符索引
 * @property MISSING_DELIMITER_VALUE 未匹配到分隔符时的默认兜底值
 * @property ELLIPSIS 超长截断时末尾追加的省略符号
 * @property MATCHED 正则表达式是否匹配成功标识
 * @property GROUP_VALUES 正则捕获组提取到的文本列表
 * @property IGNORE_CASE 匹配或比较时是否忽略大小写
 * @property PAD_LENGTH 填充补全后的总目标长度
 * @property PAD_CHARACTER 填充用于补全的字符
 * @property PAD_END 是否在文本末尾填充（true 为末尾，false 为头部）
 * @property FRACTION_DIGITS 格式化数字保留的小数位数
 * @property COUNT 文本重复拼接的次数
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
 * 数组与集合过滤处理节点的配置键。
 *
 * @property VALUES 操作的目标数组/列表
 * @property OTHER_VALUES 参与求交集/差集/拼接的另一个数组
 * @property VALUE 插入/追加/包含校验的目标元素
 * @property OUTPUT_KEY 处理结果保存的目标变量 Key
 * @property INDEX 插入/删除/获取的目标元素索引
 * @property SIZE 截取或分块的数组段长度
 * @property START 数组切片的起始索引
 * @property END 数组切片的结束索引
 * @property COUNT 取出/丢弃/采样的元素数量
 * @property INITIAL_VALUE 归约计算的初始累计值
 * @property FIELD_PATH 对象数组排序或过滤引用的属性路径
 * @property OPERATOR 过滤匹配使用的判断运算符
 * @property EXPECTED 过滤比较的目标预估值
 * @property DESCENDING 是否降序排列（true 为降序，false 为升序）
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
 * 数组过滤断言判断运算符。
 *
 * @property EQUALS 等于
 * @property NOT_EQUALS 不等于
 * @property IS_NULL 为 null
 * @property IS_NOT_NULL 不为 null
 * @property IS_EMPTY 为空数组/字符串
 * @property IS_NOT_EMPTY 不为空数组/字符串
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
 * 日期与时间处理节点的配置键。
 *
 * @property TEXT 待解析或格式化的日期时间字符串
 * @property TIMESTAMP 毫秒时间戳
 * @property TIMESTAMP_LEFT 计算相差时间的时间戳 A
 * @property TIMESTAMP_RIGHT 计算相差时间的时间戳 B
 * @property COUNT 增加/减少的时间偏移量
 * @property UNIT 时间偏移或相差的单位（如 days, hours, millis）
 * @property OUTPUT_KEY 日期处理结果保存的目标变量 Key
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
 * 算术与数学计算节点的配置键。
 *
 * @property VALUE 基础计算的目标数值
 * @property VALUES 参与求和/求平均/求极值的数值列表
 * @property LEFT 二元算术运算的左操作数
 * @property RIGHT 二元算术运算的右操作数
 * @property MIN 数值区间限制的最小值
 * @property MAX 数值区间限制的最大值
 * @property DECIMALS 保留舍入的小数位数
 * @property OUTPUT_KEY 计算结果保存的目标变量 Key
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
 * HTTP 网络请求节点的配置键。
 *
 * @property URL 请求的目标 URL 地址
 * @property METHOD HTTP 请求方法（GET, POST, PUT, DELETE 等）
 * @property HEADERS 请求头信息映射表
 * @property QUERY 查询参数 Query 映射表
 * @property BODY POST/PUT 请求体内容
 * @property BODY_TYPE 请求体数据类型（json / text / formUrlEncoded）
 * @property CONTENT_TYPE 自定义 Content-Type Header
 * @property TIMEOUT_MILLIS 请求超时限制毫秒数
 * @property RETRY_COUNT 请求失败自动重试次数
 * @property RETRY_DELAY_MILLIS 重试请求的时间间隔毫秒数
 * @property USE_LOCAL_COOKIE_STORAGE 是否使用应用本地 Cookie 存储
 * @property PATH 下载文件保存的目录相对路径
 * @property FILE_NAME 下载文件名，省略时根据响应头、URL 或 MIME 类型自动推断
 * @property OUTPUT_KEY 下载结果保存的目标变量 Key
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
    const val PATH = ActionNodeConfigKey.PATH
    const val FILE_NAME = ActionNodeConfigKey.FILE_NAME
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

/**
 * HTTP 请求 Body 类型。
 *
 * @property JSON JSON 格式主体 (`application/json`)
 * @property TEXT 纯文本格式主体 (`text/plain`)
 * @property FORM_URL_ENCODED 表单格式主体 (`application/x-www-form-urlencoded`)
 */
object ActionHttpBodyType {
    const val JSON = ActionNodeConfigKey.JSON
    const val TEXT = ActionNodeConfigKey.TEXT
    const val FORM_URL_ENCODED = ActionNodeConfigKey.FORM_URL_ENCODED
}

/**
 * HTTP 响应体数据结构的字段 Key。
 *
 * @property STATUS_CODE 响应 HTTP 状态码（如 200, 404）
 * @property IS_SUCCESS 请求是否成功（状态码 200..299）
 * @property CONTENT_TYPE 响应头的 Content-Type
 * @property RAW_BODY 原始未解析的响应体字符串
 * @property BODY 自动解析（JSON/HTML/Text）后的响应体数据
 * @property HTML 解析后的 Jsoup Document/HTML 标记
 * @property TEXT 纯文本格式响应体
 * @property TITLE 网页响应的标题内容
 * @property FILE_NAME 下载文件名
 * @property FILE_PATH 下载文件在工作流文件沙箱内的相对路径
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
    const val FILE_NAME = ActionNodeConfigKey.FILE_NAME
    const val FILE_PATH = ActionNodeConfigKey.FILE_PATH
}

/**
 * 本地持久化存储节点的配置键。
 *
 * @property KEY 本地 Preferences 读写的唯一键名
 * @property VALUE 写入本地 Preferences 的数据值
 * @property OUTPUT_KEY 读取本地存储结果保存的目标变量 Key
 */
object ActionStorageConfigKey {
    const val KEY = ActionNodeConfigKey.KEY
    const val VALUE = ActionNodeConfigKey.VALUE
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

/**
 * 文件系统节点的配置键。
 *
 * @property PATH 文件或目录的目标相对路径
 * @property PATHS 待压缩的文件或目录相对路径列表
 * @property FROM_PATH 复制/移动源文件或目录路径
 * @property TO_PATH 复制/移动目标文件或目录路径
 * @property TEXT 写入文件的文本内容
 * @property APPEND 是否追加写入模式（true 为追加，false 为覆盖）
 * @property OUTPUT_KEY 文件算子处理结果保存的目标变量 Key
 */
object ActionFileConfigKey {
    const val PATH = ActionNodeConfigKey.PATH
    const val PATHS = ActionNodeConfigKey.PATHS
    const val FROM_PATH = ActionNodeConfigKey.FROM_PATH
    const val TO_PATH = ActionNodeConfigKey.TO_PATH
    const val TEXT = ActionNodeConfigKey.TEXT
    const val APPEND = ActionNodeConfigKey.APPEND
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

/**
 * 外部浏览器唤起节点的配置键。
 *
 * @property URL 唤起外部系统浏览器打开的目标 URL
 */
object ActionOpenUrlConfigKey {
    const val URL = ActionNodeConfigKey.URL
}

/**
 * 第三方 App 唤起节点的配置键。
 *
 * @property URI 调起第三方 App 的 Schema URI
 * @property FALLBACK_URL 无法调起 App 时回退打开的网页 URL
 */
object ActionOpenAppConfigKey {
    const val URI = ActionNodeConfigKey.URI
    const val FALLBACK_URL = ActionNodeConfigKey.FALLBACK_URL
}

/**
 * 内置 WebView 节点的配置键。
 *
 * @property URL 应用内 WebView 打开的目标 URL
 */
object ActionOpenWebConfigKey {
    const val URL = ActionNodeConfigKey.URL
}

/**
 * Toast 提示节点的配置键。
 *
 * @property MESSAGE 弹出 Toast 提示的消息文本
 * @property TEXT 弹出 Toast 提示的内容文本
 */
object ActionToastConfigKey {
    const val MESSAGE = ActionNodeConfigKey.MESSAGE
    const val TEXT = ActionNodeConfigKey.TEXT
}

/**
 * 剪贴板读写节点的配置键。
 *
 * @property TEXT 复制到系统剪贴板的文本
 * @property OUTPUT_KEY 读取剪贴板结果保存的目标变量 Key
 */
object ActionClipboardConfigKey {
    const val TEXT = ActionNodeConfigKey.TEXT
    const val OUTPUT_KEY = ActionNodeConfigKey.OUTPUT_KEY
}

/**
 * 二次确认对话框节点的配置键。
 *
 * @property TITLE 弹窗标题
 * @property MESSAGE 弹窗提示正文消息
 * @property CONFIRM_TEXT 确认按钮文本
 * @property CANCEL_TEXT 取消按钮文本
 */
object ActionConfirmConfigKey {
    const val TITLE = ActionNodeConfigKey.TITLE
    const val MESSAGE = ActionNodeConfigKey.MESSAGE
    const val CONFIRM_TEXT = ActionNodeConfigKey.CONFIRM_TEXT
    const val CANCEL_TEXT = ActionNodeConfigKey.CANCEL_TEXT
}

/**
 * 输入框对话框节点的配置键。
 *
 * @property TITLE 弹窗标题
 * @property SUBTITLE 弹窗副标题/提示说明
 * @property DEFAULT_VALUE 输入框初始默认文本
 * @property CONFIRM_TEXT 确认按钮文本
 * @property CANCEL_TEXT 取消按钮文本
 * @property OUTPUT_KEY 用户输入文本保存的目标变量 Key
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
 * 选择列表对话框节点的配置键。
 *
 * @property TITLE 弹窗标题
 * @property SUBTITLE 弹窗副标题说明
 * @property OPTIONS 可选的选项数据列表
 * @property DEFAULT_VALUES 多选默认选中的值数组
 * @property DEFAULT_INDICES 多选默认选中的索引数组
 * @property IS_MULTI_SELECT 是否支持多选模式（true 为多选，false 为单选）
 * @property OUTPUT_MODE 选择输出模式（value / index / both）
 * @property CONFIRM_TEXT 确认按钮文本
 * @property CANCEL_TEXT 取消按钮文本
 * @property OUTPUT_KEY 用户选择结果保存的目标变量 Key
 * @property VALUE 选项包含的值 Key
 * @property VALUES 用户选中的多个值 Key
 * @property INDEX 选项对应的索引号 Key
 * @property INDICES 用户选中的多个索引号 Key
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
    const val IMAGE = ActionNodeConfigKey.IMAGE
    const val VALUES = ActionNodeConfigKey.VALUES
    const val INDEX = ActionNodeConfigKey.INDEX
    const val INDICES = ActionNodeConfigKey.INDICES
}

/**
 * 进度对话框节点的配置键。
 *
 * @property ACTION 进度对话框的操作动作，支持 show、update、dismiss
 * @property TASK_ID 进度任务的唯一标识（可选，默认用于匹配单个或特定任务）
 * @property TITLE 进度任务标题
 * @property MESSAGE 进度任务说明
 * @property MODE 进度显示模式，支持普通与精确模式
 * @property PROGRESS 精确模式的当前进度
 * @property MAX_PROGRESS 精确模式的总进度
 */
object ActionProgressDialogConfigKey {
    const val ACTION = ActionNodeConfigKey.ACTION
    const val TASK_ID = ActionNodeConfigKey.TASK_ID
    const val TITLE = ActionNodeConfigKey.TITLE
    const val MESSAGE = ActionNodeConfigKey.MESSAGE
    const val MODE = ActionNodeConfigKey.PROGRESS_MODE
    const val PROGRESS = ActionNodeConfigKey.PROGRESS
    const val MAX_PROGRESS = ActionNodeConfigKey.MAX_PROGRESS
}

/**
 * 进度对话框操作动作。
 */
object ActionProgressDialogAction {
    const val SHOW = "show"
    const val UPDATE = "update"
    const val DISMISS = "dismiss"
}

/**
 * 进度对话框显示模式。
 */
object ActionProgressDialogMode {
    const val INDETERMINATE = "indeterminate"
    const val DETERMINATE = "determinate"
}

/**
 * 系统通知栏消息节点的配置键。
 *
 * @property TITLE 通知栏标题
 * @property CONTENT 通知栏正文内容
 */
object ActionNotificationConfigKey {
    const val TITLE = ActionNodeConfigKey.TITLE
    const val CONTENT = ActionNodeConfigKey.CONTENT
}

/**
 * 系统分享面板节点的配置键。
 *
 * @property TITLE 分享弹窗标题
 * @property TEXT 分享的描述文本
 * @property CONTENT 分享的主体正文
 * @property URL 分享的链接地址
 */
object ActionShareConfigKey {
    const val TITLE = ActionNodeConfigKey.TITLE
    const val TEXT = ActionNodeConfigKey.TEXT
    const val CONTENT = ActionNodeConfigKey.CONTENT
    const val URL = ActionNodeConfigKey.URL
}

/**
 * 大图预览画廊节点的配置键。
 *
 * @property INDEX 预览时初始定位的图片索引
 * @property IMAGES 画廊展示的图片 URL 列表
 */
object ActionImagePreviewConfigKey {
    const val INDEX = ActionNodeConfigKey.INDEX
    const val IMAGES = ActionNodeConfigKey.IMAGES
}

/**
 * 视频预览节点的配置键。
 *
 * @property URL 视频流或文件 URL
 * @property HEADERS 播放请求携带的可选 Header 信息
 */
object ActionVideoPreviewConfigKey {
    const val URL = ActionNodeConfigKey.URL
    const val HEADERS = ActionNodeConfigKey.HEADERS
}

/**
 * Web Cookie 同步节点的配置键。
 *
 * @property URL 需同步 Cookie 的网页 URL
 * @property TITLE 同步 Cookie 页面展示的标题
 * @property HEADERS 同步携带的 Header 信息
 * @property USER_AGENT 自定义 User-Agent
 */
object ActionSyncCookieConfigKey {
    const val URL = ActionNodeConfigKey.URL
    const val TITLE = ActionNodeConfigKey.TITLE
    const val HEADERS = ActionNodeConfigKey.HEADERS
    const val USER_AGENT = ActionNodeConfigKey.USER_AGENT
}

/**
 * 选择对话框输出模式选项。
 *
 * @property VALUE 仅输出选中的 Option Value
 * @property INDEX 仅输出选中的 Option Index
 * @property BOTH 同时输出 Value 与 Index
 */
object ActionSelectOutputMode {
    const val VALUE = ActionNodeConfigKey.VALUE
    const val INDEX = ActionNodeConfigKey.INDEX
    const val BOTH = ActionNodeConfigKey.BOTH
}
