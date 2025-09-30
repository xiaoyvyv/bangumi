@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.FeatureType
import com.xiaoyv.bangumi.shared.core.types.settings.SettingIndication
import com.xiaoyv.bangumi.shared.core.types.settings.SettingNavigationAnimation
import com.xiaoyv.bangumi.shared.core.types.settings.SettingTheme
import com.xiaoyv.bangumi.shared.core.types.settings.SettingUpdateChannel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class ComposeSetting(
    @SerialName("v") val live2d: Live2dConfig = Live2dConfig.Default,
    @SerialName("ui") val ui: UIConfig = UIConfig.Default,
    @SerialName("homeTab") val homeTab: HomeTabConfig = HomeTabConfig.Default,
    @SerialName("network") val network: NetworkConfig = NetworkConfig.Default,
) {

    @Serializable
    @Immutable
    data class Live2dConfig(
        @SerialName("enable") val enable: Boolean = false,
        @SerialName("voiceEnable") val voiceEnable: Boolean = false,
    ) {
        companion object {
            val Default = Live2dConfig()
        }
    }

    @Serializable
    @Immutable
    data class UIConfig(
        @field:SettingTheme
        @SerialName("theme") val theme: Int = SettingTheme.SYSTEM,

        @SerialName("filterDeleteComment") val filterDeleteComment: Boolean = false,
        @SerialName("filterBlockUserComment") val filterBlockUserComment: Boolean = false,
        @SerialName("forceOpenUrlInBrowser") val forceOpenUrlInBrowser: Boolean = false,
        @SerialName("cacheState") val cacheState: Boolean = true,

        @field:SettingIndication
        @SerialName("indication")
        val indication: Int = SettingIndication.RIPPLE,

        @field:SettingNavigationAnimation
        @SerialName("navigationAnimation") val navigationAnimation: Int = SettingNavigationAnimation.FADE,

        @SerialName("timeMachineGridLimit") val timeMachineGridLimit: Int = 10,
        @SerialName("trackingGridLineLimit") val trackingGridLineLimit: Int = 4,
    ) {
        companion object {
            val Default = UIConfig()
        }
    }

    @Serializable
    @Immutable
    data class HomeTabConfig(
        @SerialName("defaultSelected") val defaultSelected: Int = 0,
        @SerialName("tab1") @field:FeatureType val tab1: String = FeatureType.TYPE_HOME,
        @SerialName("tab2") @field:FeatureType val tab2: String = FeatureType.TYPE_TIMELINE,
        @SerialName("tab3") @field:FeatureType val tab3: String = FeatureType.TYPE_SUBJECT_BROWSER,
        @SerialName("tab4") @field:FeatureType val tab4: String = FeatureType.TYPE_RAKUEN,
        @SerialName("tab5") @field:FeatureType val tab5: String = FeatureType.TYPE_PROFILE,
    ) {
        companion object {
            val Default = HomeTabConfig()
        }
    }

    @Serializable
    @Immutable
    data class NetworkConfig(
        @SerialName("bgmHost") val bgmHost: String = "https://bgm.tv/",
        @SerialName("updateChannel") @field:SettingUpdateChannel val updateChannel: Int = SettingUpdateChannel.RELEASE,
        @SerialName("connectTimeoutMillis") val connectTimeoutMillis: Long = 15_000,
        @SerialName("socketTimeoutMillis") val socketTimeoutMillis: Long = 15_000,

        @SerialName("pixivImageHost") val pixivImageHost: String = "https://xget.xiaoyv.com.cn/pximg/",
        @SerialName("pixivClientId") val pixivClientId: String = "MOBrBDS8blbauoSck0ZfDbtuzpyT",
        @SerialName("pixivClientSecret") val pixivClientSecret: String = "lsACyCD94FhDUtGTXi3QzcFE2uU1hqtDaKeqrdwj",
        @SerialName("pixivVersion") val pixivVersion: String = "6.141.1",
        @SerialName("pixivTimeHashSecret") val pixivTimeHashSecret: String = "28c1fdd170a5204386cb1313c7077b34f83e4aaf4aa829ce78c231e05b0bae2c",

        @SerialName("douBanUA") val douBanUA: String = "api-client/1 com.douban.frodo/7.65.0(277) Android/33 product/coral vendor/Google model/Pixel 4 XL brand/google  rom/android  network/wifi  udid/0643fa6abfd3eaff076ff3ee603211ded11fc344  platform/mobile nd/1",
        @SerialName("douBanKey") val douBanKey: String = "bf7dddc7c9cfe6f7",
    ) {

        companion object {
            val Default = NetworkConfig()
        }
    }

    companion object {
        val Default = ComposeSetting()
    }
}
