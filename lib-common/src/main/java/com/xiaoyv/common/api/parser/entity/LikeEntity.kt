package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Class: [LikeEntity]
 *
 * ```
 * {
 *   "2273678": {
 *     "132": {
 *       "type": 8,
 *       "main_id": 362716,
 *       "value": "132",
 *       "total": 2,
 *       "emoji": "93",
 *       "users": [
 *         {
 *           "username": "777600",
 *           "nickname": "hoshino"
 *         },
 *         {
 *           "username": "missminmay",
 *           "nickname": "envys"
 *         }
 *       ]
 *     }
 *   },
 *   "2244621": {
 *     "54": {
 *       "type": 8,
 *       "main_id": 362716,
 *       "value": "54",
 *       "total": 12,
 *       "emoji": "15",
 *       "users": [
 *         {
 *           "username": "704504",
 *           "nickname": "1毫米的上善不举"
 *         },
 *         {
 *           "username": "766063",
 *           "nickname": "雪融"
 *         },
 *         {
 *           "username": "538966",
 *           "nickname": "Catash"
 *         },
 *         {
 *           "username": "wlx321",
 *           "nickname": "wlx321"
 *         },
 *         {
 *           "username": "aivwe",
 *           "nickname": "Aivwe"
 *         },
 *         {
 *           "username": "485362",
 *           "nickname": "volitent"
 *         },
 *         {
 *           "username": "794768",
 *           "nickname": "阿克曼·易昭"
 *         },
 *         {
 *           "username": "737822",
 *           "nickname": "健那绿染液"
 *         },
 *         {
 *           "username": "666490",
 *           "nickname": "K"
 *         },
 *         {
 *           "username": "822992",
 *           "nickname": "风见鸡"
 *         },
 *         {
 *           "username": "764071",
 *           "nickname": "昴"
 *         },
 *         {
 *           "username": "kitauji_fightoo",
 *           "nickname": "@Kumiko"
 *         }
 *       ]
 *     },
 *     "104": {
 *       "type": 8,
 *       "main_id": 362716,
 *       "value": "104",
 *       "total": 1,
 *       "emoji": "65",
 *       "users": [
 *         {
 *           "username": "jimlee0824",
 *           "nickname": "NeKo"
 *         }
 *       ]
 *     }
 *   },
 *   "2046605": {
 *     "54": {
 *       "type": 8,
 *       "main_id": 362716,
 *       "value": "54",
 *       "total": 1,
 *       "emoji": "15",
 *       "users": [
 *         {
 *           "username": "704504",
 *           "nickname": "1毫米的上善不举"
 *         }
 *       ]
 *     }
 *   },
 *   "1806204": {
 *     "54": {
 *       "type": 8,
 *       "main_id": 362716,
 *       "value": "54",
 *       "total": 1,
 *       "emoji": "15",
 *       "users": [
 *         {
 *           "username": "788428",
 *           "nickname": "反弹力"
 *         }
 *       ]
 *     },
 *     "140": {
 *       "type": 8,
 *       "main_id": 362716,
 *       "value": "140",
 *       "total": 7,
 *       "emoji": "101",
 *       "users": [
 *         {
 *           "username": "773472",
 *           "nickname": "lafe"
 *         },
 *         {
 *           "username": "795058",
 *           "nickname": "CliffKo"
 *         },
 *         {
 *           "username": "764220",
 *           "nickname": "AIX"
 *         },
 *         {
 *           "username": "670782",
 *           "nickname": "奏响"
 *         },
 *         {
 *           "username": "223543",
 *           "nickname": "Admin"
 *         },
 *         {
 *           "username": "809254",
 *           "nickname": "実現喵"
 *         },
 *         {
 *           "username": "837364",
 *           "nickname": "xiaoyv"
 *         }
 *       ],
 *       "selected": true
 *     },
 *     "0": {
 *       "type": 8,
 *       "main_id": 362716,
 *       "value": "0",
 *       "total": 1,
 *       "emoji": "44",
 *       "users": [
 *         {
 *           "username": "324332",
 *           "nickname": "关恒逸冰"
 *         }
 *       ]
 *     }
 *   },
 *   "2448811": {
 *     "140": {
 *       "type": 8,
 *       "main_id": 362716,
 *       "value": "140",
 *       "total": 7,
 *       "emoji": "101",
 *       "users": [
 *         {
 *           "username": "815637",
 *           "nickname": "迪迪喂"
 *         },
 *         {
 *           "username": "818336",
 *           "nickname": "mn1an"
 *         },
 *         {
 *           "username": "776947",
 *           "nickname": "kledi"
 *         },
 *         {
 *           "username": "806928",
 *           "nickname": "3457"
 *         },
 *         {
 *           "username": "808594",
 *           "nickname": "IT钉子户"
 *         },
 *         {
 *           "username": "730333",
 *           "nickname": "面包屑"
 *         },
 *         {
 *           "username": "704146",
 *           "nickname": "屿"
 *         }
 *       ]
 *     }
 *   },
 *   "2452052": {
 *     "140": {
 *       "type": 8,
 *       "main_id": 362716,
 *       "value": "140",
 *       "total": 2,
 *       "emoji": "101",
 *       "users": [
 *         {
 *           "username": "815637",
 *           "nickname": "迪迪喂"
 *         },
 *         {
 *           "username": "808594",
 *           "nickname": "IT钉子户"
 *         }
 *       ]
 *     }
 *   },
 *   "2416420": {
 *     "140": {
 *       "type": 8,
 *       "main_id": 362716,
 *       "value": "140",
 *       "total": 1,
 *       "emoji": "101",
 *       "users": [
 *         {
 *           "username": "815637",
 *           "nickname": "迪迪喂"
 *         }
 *       ]
 *     },
 *     "104": {
 *       "type": 8,
 *       "main_id": 362716,
 *       "value": "104",
 *       "total": 1,
 *       "emoji": "65",
 *       "users": [
 *         {
 *           "username": "808594",
 *           "nickname": "IT钉子户"
 *         }
 *       ]
 *     }
 *   },
 *   "2448774": {
 *     "79": {
 *       "type": 8,
 *       "main_id": 362716,
 *       "value": "79",
 *       "total": 1,
 *       "emoji": "40",
 *       "users": [
 *         {
 *           "username": "815637",
 *           "nickname": "迪迪喂"
 *         }
 *       ]
 *     },
 *     "140": {
 *       "type": 8,
 *       "main_id": 362716,
 *       "value": "140",
 *       "total": 1,
 *       "emoji": "101",
 *       "users": [
 *         {
 *           "username": "808594",
 *           "nickname": "IT钉子户"
 *         }
 *       ]
 *     }
 *   },
 *   "1921477": {
 *     "54": {
 *       "type": 8,
 *       "main_id": 362716,
 *       "value": "54",
 *       "total": 1,
 *       "emoji": "15",
 *       "users": [
 *         {
 *           "username": "ziergaixv",
 *           "nickname": "梓尔盖绪"
 *         }
 *       ]
 *     }
 *   },
 *   "2508824": {
 *     "140": {
 *       "type": 8,
 *       "main_id": 362716,
 *       "value": "140",
 *       "total": 1,
 *       "emoji": "101",
 *       "users": [
 *         {
 *           "username": "815637",
 *           "nickname": "迪迪喂"
 *         }
 *       ]
 *     },
 *     "79": {
 *       "type": 8,
 *       "main_id": 362716,
 *       "value": "79",
 *       "total": 1,
 *       "emoji": "40",
 *       "users": [
 *         {
 *           "username": "808594",
 *           "nickname": "IT钉子户"
 *         }
 *       ]
 *     }
 *   },
 *   "2135782": {
 *     "54": {
 *       "type": 8,
 *       "main_id": 362716,
 *       "value": "54",
 *       "total": 1,
 *       "emoji": "15",
 *       "users": [
 *         {
 *           "username": "777783",
 *           "nickname": "悲伤丸"
 *         }
 *       ]
 *     }
 *   },
 *   "1902473": {
 *     "140": {
 *       "type": 8,
 *       "main_id": 362716,
 *       "value": "140",
 *       "total": 1,
 *       "emoji": "101",
 *       "users": [
 *         {
 *           "username": "822795",
 *           "nickname": "Zero"
 *         }
 *       ]
 *     }
 *   },
 *   "1953770": {
 *     "79": {
 *       "type": 8,
 *       "main_id": 362716,
 *       "value": "79",
 *       "total": 1,
 *       "emoji": "40",
 *       "users": [
 *         {
 *           "username": "796244",
 *           "nickname": "yanglolo"
 *         }
 *       ]
 *     }
 *   }
 * }
 * ```
 *
 * @author why
 * @since 12/2/23
 */
@Keep
@Parcelize
data class LikeEntity(val i: Int = 10) : HashMap<String, Map<String, LikeEntity.LikeAction>>(i),
    Parcelable {

    @Keep
    @Parcelize
    data class LikeAction(
        @SerializedName("emoji")
        var emoji: String? = "",
        @SerializedName("main_id")
        var mainId: Int = 0,
        @SerializedName("total")
        var total: Int = 0,
        @SerializedName("type")
        var type: Int = 0,
        @SerializedName("users")
        var users: List<User>? = listOf(),
        @SerializedName("value")
        var value: String? = ""
    ) : Parcelable

    @Keep
    @Parcelize
    data class User(
        @SerializedName("nickname")
        var nickname: String? = null,
        @SerializedName("username")
        var username: String? = null
    ) : Parcelable
}