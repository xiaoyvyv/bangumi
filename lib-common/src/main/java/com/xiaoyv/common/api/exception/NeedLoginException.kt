package com.xiaoyv.common.api.exception

/**
 * Class: [NeedLoginException]
 *
 * @author why
 * @since 11/27/23
 */
class NeedLoginException(override val message: String?) : IllegalStateException(message)