package com.xiaoyv.common.api.exception

/**
 * Class: [NeedConfigException]
 *
 * @author why
 * @since 11/27/23
 */
class NeedConfigException(override val message: String?) : IllegalStateException(message)