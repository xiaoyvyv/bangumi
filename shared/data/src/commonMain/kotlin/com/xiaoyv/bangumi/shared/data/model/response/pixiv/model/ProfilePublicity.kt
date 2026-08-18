package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param gender
 * @param region
 * @param birthDay
 * @param birthYear
 * @param job
 * @param pawoo
 */
@Serializable

data class ProfilePublicity(

    @SerialName(value = "gender")
    val gender: String = "",

    @SerialName(value = "region")
    val region: String = "",

    @SerialName(value = "birth_day")
    val birthDay: String = "",

    @SerialName(value = "birth_year")
    val birthYear: String = "",

    @SerialName(value = "job")
    val job: String = "",

    @SerialName(value = "pawoo")
    val pawoo: Boolean? = null

)

