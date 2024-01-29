package com.xiaoyv.common.api.response

import com.google.gson.annotations.SerializedName

data class MicrosoftJwtPayload(@SerializedName("exp") val expirationTime: Long)