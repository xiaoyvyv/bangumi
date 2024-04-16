package com.xiaoyv.subtitle.api.parser.exception

class InvalidSRTSubException : InvalidSubException {
    constructor()
    constructor(message: String?) : super(message)
    constructor(message: Throwable?) : super(message)
    constructor(message: String?, error: Throwable?) : super(message, error)

    constructor(
        message: String?,
        error: Throwable?,
        enableSuppression: Boolean,
        writableStackTrace: Boolean
    ) : super(
        message,
        error,
        enableSuppression,
        writableStackTrace
    )
}
