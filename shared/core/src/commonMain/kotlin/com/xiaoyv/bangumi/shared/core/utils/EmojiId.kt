package com.xiaoyv.bangumi.shared.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.bgm_01
import com.xiaoyv.bangumi.core_resource.resources.bgm_02
import com.xiaoyv.bangumi.core_resource.resources.bgm_03
import com.xiaoyv.bangumi.core_resource.resources.bgm_04
import com.xiaoyv.bangumi.core_resource.resources.bgm_05
import com.xiaoyv.bangumi.core_resource.resources.bgm_06
import com.xiaoyv.bangumi.core_resource.resources.bgm_07
import com.xiaoyv.bangumi.core_resource.resources.bgm_08
import com.xiaoyv.bangumi.core_resource.resources.bgm_09
import com.xiaoyv.bangumi.core_resource.resources.bgm_10
import com.xiaoyv.bangumi.core_resource.resources.bgm_11
import com.xiaoyv.bangumi.core_resource.resources.bgm_12
import com.xiaoyv.bangumi.core_resource.resources.bgm_13
import com.xiaoyv.bangumi.core_resource.resources.bgm_14
import com.xiaoyv.bangumi.core_resource.resources.bgm_15
import com.xiaoyv.bangumi.core_resource.resources.bgm_16
import com.xiaoyv.bangumi.core_resource.resources.bgm_17
import com.xiaoyv.bangumi.core_resource.resources.bgm_18
import com.xiaoyv.bangumi.core_resource.resources.bgm_19
import com.xiaoyv.bangumi.core_resource.resources.bgm_20
import com.xiaoyv.bangumi.core_resource.resources.bgm_200
import com.xiaoyv.bangumi.core_resource.resources.bgm_201
import com.xiaoyv.bangumi.core_resource.resources.bgm_202
import com.xiaoyv.bangumi.core_resource.resources.bgm_203
import com.xiaoyv.bangumi.core_resource.resources.bgm_204
import com.xiaoyv.bangumi.core_resource.resources.bgm_205
import com.xiaoyv.bangumi.core_resource.resources.bgm_206
import com.xiaoyv.bangumi.core_resource.resources.bgm_207
import com.xiaoyv.bangumi.core_resource.resources.bgm_208
import com.xiaoyv.bangumi.core_resource.resources.bgm_209
import com.xiaoyv.bangumi.core_resource.resources.bgm_21
import com.xiaoyv.bangumi.core_resource.resources.bgm_210
import com.xiaoyv.bangumi.core_resource.resources.bgm_211
import com.xiaoyv.bangumi.core_resource.resources.bgm_212
import com.xiaoyv.bangumi.core_resource.resources.bgm_213
import com.xiaoyv.bangumi.core_resource.resources.bgm_214
import com.xiaoyv.bangumi.core_resource.resources.bgm_215
import com.xiaoyv.bangumi.core_resource.resources.bgm_216
import com.xiaoyv.bangumi.core_resource.resources.bgm_217
import com.xiaoyv.bangumi.core_resource.resources.bgm_218
import com.xiaoyv.bangumi.core_resource.resources.bgm_219
import com.xiaoyv.bangumi.core_resource.resources.bgm_22
import com.xiaoyv.bangumi.core_resource.resources.bgm_220
import com.xiaoyv.bangumi.core_resource.resources.bgm_221
import com.xiaoyv.bangumi.core_resource.resources.bgm_222
import com.xiaoyv.bangumi.core_resource.resources.bgm_223
import com.xiaoyv.bangumi.core_resource.resources.bgm_224
import com.xiaoyv.bangumi.core_resource.resources.bgm_225
import com.xiaoyv.bangumi.core_resource.resources.bgm_226
import com.xiaoyv.bangumi.core_resource.resources.bgm_227
import com.xiaoyv.bangumi.core_resource.resources.bgm_228
import com.xiaoyv.bangumi.core_resource.resources.bgm_229
import com.xiaoyv.bangumi.core_resource.resources.bgm_23
import com.xiaoyv.bangumi.core_resource.resources.bgm_230
import com.xiaoyv.bangumi.core_resource.resources.bgm_231
import com.xiaoyv.bangumi.core_resource.resources.bgm_232
import com.xiaoyv.bangumi.core_resource.resources.bgm_233
import com.xiaoyv.bangumi.core_resource.resources.bgm_234
import com.xiaoyv.bangumi.core_resource.resources.bgm_235
import com.xiaoyv.bangumi.core_resource.resources.bgm_236
import com.xiaoyv.bangumi.core_resource.resources.bgm_237
import com.xiaoyv.bangumi.core_resource.resources.bgm_238
import com.xiaoyv.bangumi.core_resource.resources.bgm_500
import com.xiaoyv.bangumi.core_resource.resources.bgm_501
import com.xiaoyv.bangumi.core_resource.resources.bgm_502
import com.xiaoyv.bangumi.core_resource.resources.bgm_503
import com.xiaoyv.bangumi.core_resource.resources.bgm_504
import com.xiaoyv.bangumi.core_resource.resources.bgm_505
import com.xiaoyv.bangumi.core_resource.resources.bgm_506
import com.xiaoyv.bangumi.core_resource.resources.bgm_507
import com.xiaoyv.bangumi.core_resource.resources.bgm_508
import com.xiaoyv.bangumi.core_resource.resources.bgm_509
import com.xiaoyv.bangumi.core_resource.resources.bgm_510
import com.xiaoyv.bangumi.core_resource.resources.bgm_511
import com.xiaoyv.bangumi.core_resource.resources.bgm_512
import com.xiaoyv.bangumi.core_resource.resources.bgm_513
import com.xiaoyv.bangumi.core_resource.resources.bgm_514
import com.xiaoyv.bangumi.core_resource.resources.bgm_515
import com.xiaoyv.bangumi.core_resource.resources.bgm_516
import com.xiaoyv.bangumi.core_resource.resources.bgm_517
import com.xiaoyv.bangumi.core_resource.resources.bgm_518
import com.xiaoyv.bangumi.core_resource.resources.bgm_519
import com.xiaoyv.bangumi.core_resource.resources.bgm_520
import com.xiaoyv.bangumi.core_resource.resources.bgm_521
import com.xiaoyv.bangumi.core_resource.resources.bgm_522
import com.xiaoyv.bangumi.core_resource.resources.bgm_523
import com.xiaoyv.bangumi.core_resource.resources.bgm_524
import com.xiaoyv.bangumi.core_resource.resources.bgm_525
import com.xiaoyv.bangumi.core_resource.resources.bgm_526
import com.xiaoyv.bangumi.core_resource.resources.bgm_527
import com.xiaoyv.bangumi.core_resource.resources.bgm_528
import com.xiaoyv.bangumi.core_resource.resources.bgm_529
import com.xiaoyv.bangumi.core_resource.resources.tv_01
import com.xiaoyv.bangumi.core_resource.resources.tv_02
import com.xiaoyv.bangumi.core_resource.resources.tv_03
import com.xiaoyv.bangumi.core_resource.resources.tv_04
import com.xiaoyv.bangumi.core_resource.resources.tv_05
import com.xiaoyv.bangumi.core_resource.resources.tv_06
import com.xiaoyv.bangumi.core_resource.resources.tv_07
import com.xiaoyv.bangumi.core_resource.resources.tv_08
import com.xiaoyv.bangumi.core_resource.resources.tv_09
import com.xiaoyv.bangumi.core_resource.resources.tv_10
import com.xiaoyv.bangumi.core_resource.resources.tv_100
import com.xiaoyv.bangumi.core_resource.resources.tv_101
import com.xiaoyv.bangumi.core_resource.resources.tv_102
import com.xiaoyv.bangumi.core_resource.resources.tv_11
import com.xiaoyv.bangumi.core_resource.resources.tv_12
import com.xiaoyv.bangumi.core_resource.resources.tv_13
import com.xiaoyv.bangumi.core_resource.resources.tv_14
import com.xiaoyv.bangumi.core_resource.resources.tv_15
import com.xiaoyv.bangumi.core_resource.resources.tv_16
import com.xiaoyv.bangumi.core_resource.resources.tv_17
import com.xiaoyv.bangumi.core_resource.resources.tv_18
import com.xiaoyv.bangumi.core_resource.resources.tv_19
import com.xiaoyv.bangumi.core_resource.resources.tv_20
import com.xiaoyv.bangumi.core_resource.resources.tv_21
import com.xiaoyv.bangumi.core_resource.resources.tv_22
import com.xiaoyv.bangumi.core_resource.resources.tv_23
import com.xiaoyv.bangumi.core_resource.resources.tv_24
import com.xiaoyv.bangumi.core_resource.resources.tv_25
import com.xiaoyv.bangumi.core_resource.resources.tv_26
import com.xiaoyv.bangumi.core_resource.resources.tv_27
import com.xiaoyv.bangumi.core_resource.resources.tv_28
import com.xiaoyv.bangumi.core_resource.resources.tv_29
import com.xiaoyv.bangumi.core_resource.resources.tv_30
import com.xiaoyv.bangumi.core_resource.resources.tv_31
import com.xiaoyv.bangumi.core_resource.resources.tv_32
import com.xiaoyv.bangumi.core_resource.resources.tv_33
import com.xiaoyv.bangumi.core_resource.resources.tv_34
import com.xiaoyv.bangumi.core_resource.resources.tv_35
import com.xiaoyv.bangumi.core_resource.resources.tv_36
import com.xiaoyv.bangumi.core_resource.resources.tv_37
import com.xiaoyv.bangumi.core_resource.resources.tv_38
import com.xiaoyv.bangumi.core_resource.resources.tv_39
import com.xiaoyv.bangumi.core_resource.resources.tv_40
import com.xiaoyv.bangumi.core_resource.resources.tv_41
import com.xiaoyv.bangumi.core_resource.resources.tv_42
import com.xiaoyv.bangumi.core_resource.resources.tv_43
import com.xiaoyv.bangumi.core_resource.resources.tv_44
import com.xiaoyv.bangumi.core_resource.resources.tv_45
import com.xiaoyv.bangumi.core_resource.resources.tv_46
import com.xiaoyv.bangumi.core_resource.resources.tv_47
import com.xiaoyv.bangumi.core_resource.resources.tv_48
import com.xiaoyv.bangumi.core_resource.resources.tv_49
import com.xiaoyv.bangumi.core_resource.resources.tv_50
import com.xiaoyv.bangumi.core_resource.resources.tv_51
import com.xiaoyv.bangumi.core_resource.resources.tv_52
import com.xiaoyv.bangumi.core_resource.resources.tv_53
import com.xiaoyv.bangumi.core_resource.resources.tv_54
import com.xiaoyv.bangumi.core_resource.resources.tv_55
import com.xiaoyv.bangumi.core_resource.resources.tv_56
import com.xiaoyv.bangumi.core_resource.resources.tv_57
import com.xiaoyv.bangumi.core_resource.resources.tv_58
import com.xiaoyv.bangumi.core_resource.resources.tv_59
import com.xiaoyv.bangumi.core_resource.resources.tv_60
import com.xiaoyv.bangumi.core_resource.resources.tv_61
import com.xiaoyv.bangumi.core_resource.resources.tv_62
import com.xiaoyv.bangumi.core_resource.resources.tv_63
import com.xiaoyv.bangumi.core_resource.resources.tv_64
import com.xiaoyv.bangumi.core_resource.resources.tv_65
import com.xiaoyv.bangumi.core_resource.resources.tv_66
import com.xiaoyv.bangumi.core_resource.resources.tv_67
import com.xiaoyv.bangumi.core_resource.resources.tv_68
import com.xiaoyv.bangumi.core_resource.resources.tv_69
import com.xiaoyv.bangumi.core_resource.resources.tv_70
import com.xiaoyv.bangumi.core_resource.resources.tv_71
import com.xiaoyv.bangumi.core_resource.resources.tv_72
import com.xiaoyv.bangumi.core_resource.resources.tv_73
import com.xiaoyv.bangumi.core_resource.resources.tv_74
import com.xiaoyv.bangumi.core_resource.resources.tv_75
import com.xiaoyv.bangumi.core_resource.resources.tv_76
import com.xiaoyv.bangumi.core_resource.resources.tv_77
import com.xiaoyv.bangumi.core_resource.resources.tv_78
import com.xiaoyv.bangumi.core_resource.resources.tv_79
import com.xiaoyv.bangumi.core_resource.resources.tv_80
import com.xiaoyv.bangumi.core_resource.resources.tv_81
import com.xiaoyv.bangumi.core_resource.resources.tv_82
import com.xiaoyv.bangumi.core_resource.resources.tv_83
import com.xiaoyv.bangumi.core_resource.resources.tv_84
import com.xiaoyv.bangumi.core_resource.resources.tv_85
import com.xiaoyv.bangumi.core_resource.resources.tv_86
import com.xiaoyv.bangumi.core_resource.resources.tv_87
import com.xiaoyv.bangumi.core_resource.resources.tv_88
import com.xiaoyv.bangumi.core_resource.resources.tv_89
import com.xiaoyv.bangumi.core_resource.resources.tv_90
import com.xiaoyv.bangumi.core_resource.resources.tv_91
import com.xiaoyv.bangumi.core_resource.resources.tv_92
import com.xiaoyv.bangumi.core_resource.resources.tv_93
import com.xiaoyv.bangumi.core_resource.resources.tv_94
import com.xiaoyv.bangumi.core_resource.resources.tv_95
import com.xiaoyv.bangumi.core_resource.resources.tv_96
import com.xiaoyv.bangumi.core_resource.resources.tv_97
import com.xiaoyv.bangumi.core_resource.resources.tv_98
import com.xiaoyv.bangumi.core_resource.resources.tv_99
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.toPersistentList
import org.jetbrains.compose.resources.DrawableResource

data class BgmEmoji(
    val number: Int,
    val image: DrawableResource,
    val smileId: String,
)

val bgmReactionKey by lazy {
    mapOf(
        "0" to Res.drawable.tv_44,
        "54" to Res.drawable.tv_15,
        "62" to Res.drawable.tv_23,
        "79" to Res.drawable.tv_40,
        "80" to Res.drawable.tv_41,
        "85" to Res.drawable.tv_46,
        "88" to Res.drawable.tv_49,
        "90" to Res.drawable.tv_51,
        "104" to Res.drawable.tv_65,
        "122" to Res.drawable.tv_83,
        "140" to Res.drawable.tv_101,
        "141" to Res.drawable.tv_102
    )
}

val bgmEmojis by lazy {
    mapOf(
        "17" to BgmEmoji(smileId = "17", image = Res.drawable.bgm_01, number = 1),
        "18" to BgmEmoji(smileId = "18", image = Res.drawable.bgm_02, number = 2),
        "19" to BgmEmoji(smileId = "19", image = Res.drawable.bgm_03, number = 3),
        "20" to BgmEmoji(smileId = "20", image = Res.drawable.bgm_04, number = 4),
        "21" to BgmEmoji(smileId = "21", image = Res.drawable.bgm_05, number = 5),
        "22" to BgmEmoji(smileId = "22", image = Res.drawable.bgm_06, number = 6),
        "23" to BgmEmoji(smileId = "23", image = Res.drawable.bgm_07, number = 7),
        "24" to BgmEmoji(smileId = "24", image = Res.drawable.bgm_08, number = 8),
        "25" to BgmEmoji(smileId = "25", image = Res.drawable.bgm_09, number = 9),
        "26" to BgmEmoji(smileId = "26", image = Res.drawable.bgm_10, number = 10),
        "27" to BgmEmoji(smileId = "27", image = Res.drawable.bgm_11, number = 11),
        "28" to BgmEmoji(smileId = "28", image = Res.drawable.bgm_12, number = 12),
        "29" to BgmEmoji(smileId = "29", image = Res.drawable.bgm_13, number = 13),
        "30" to BgmEmoji(smileId = "30", image = Res.drawable.bgm_14, number = 14),
        "31" to BgmEmoji(smileId = "31", image = Res.drawable.bgm_15, number = 15),
        "32" to BgmEmoji(smileId = "32", image = Res.drawable.bgm_16, number = 16),
        "33" to BgmEmoji(smileId = "33", image = Res.drawable.bgm_17, number = 17),
        "34" to BgmEmoji(smileId = "34", image = Res.drawable.bgm_18, number = 18),
        "35" to BgmEmoji(smileId = "35", image = Res.drawable.bgm_19, number = 19),
        "36" to BgmEmoji(smileId = "36", image = Res.drawable.bgm_20, number = 20),
        "37" to BgmEmoji(smileId = "37", image = Res.drawable.bgm_21, number = 21),
        "38" to BgmEmoji(smileId = "38", image = Res.drawable.bgm_22, number = 22),
        "39" to BgmEmoji(smileId = "39", image = Res.drawable.bgm_23, number = 23),

        "40" to BgmEmoji(smileId = "40", image = Res.drawable.tv_01, number = 24),
        "41" to BgmEmoji(smileId = "41", image = Res.drawable.tv_02, number = 25),
        "42" to BgmEmoji(smileId = "42", image = Res.drawable.tv_03, number = 26),
        "43" to BgmEmoji(smileId = "43", image = Res.drawable.tv_04, number = 27),
        "44" to BgmEmoji(smileId = "44", image = Res.drawable.tv_05, number = 28),
        "45" to BgmEmoji(smileId = "45", image = Res.drawable.tv_06, number = 29),
        "46" to BgmEmoji(smileId = "46", image = Res.drawable.tv_07, number = 30),
        "47" to BgmEmoji(smileId = "47", image = Res.drawable.tv_08, number = 31),
        "48" to BgmEmoji(smileId = "48", image = Res.drawable.tv_09, number = 32),
        "49" to BgmEmoji(smileId = "49", image = Res.drawable.tv_10, number = 33),
        "50" to BgmEmoji(smileId = "50", image = Res.drawable.tv_11, number = 34),
        "51" to BgmEmoji(smileId = "51", image = Res.drawable.tv_12, number = 35),
        "52" to BgmEmoji(smileId = "52", image = Res.drawable.tv_13, number = 36),
        "53" to BgmEmoji(smileId = "53", image = Res.drawable.tv_14, number = 37),
        "54" to BgmEmoji(smileId = "54", image = Res.drawable.tv_15, number = 38),
        "55" to BgmEmoji(smileId = "55", image = Res.drawable.tv_16, number = 39),
        "56" to BgmEmoji(smileId = "56", image = Res.drawable.tv_17, number = 40),
        "57" to BgmEmoji(smileId = "57", image = Res.drawable.tv_18, number = 41),
        "58" to BgmEmoji(smileId = "58", image = Res.drawable.tv_19, number = 42),
        "59" to BgmEmoji(smileId = "59", image = Res.drawable.tv_20, number = 43),
        "60" to BgmEmoji(smileId = "60", image = Res.drawable.tv_21, number = 44),
        "61" to BgmEmoji(smileId = "61", image = Res.drawable.tv_22, number = 45),
        "62" to BgmEmoji(smileId = "62", image = Res.drawable.tv_23, number = 46),
        "63" to BgmEmoji(smileId = "63", image = Res.drawable.tv_24, number = 47),
        "64" to BgmEmoji(smileId = "64", image = Res.drawable.tv_25, number = 48),
        "65" to BgmEmoji(smileId = "65", image = Res.drawable.tv_26, number = 49),
        "66" to BgmEmoji(smileId = "66", image = Res.drawable.tv_27, number = 50),
        "67" to BgmEmoji(smileId = "67", image = Res.drawable.tv_28, number = 51),
        "68" to BgmEmoji(smileId = "68", image = Res.drawable.tv_29, number = 52),
        "69" to BgmEmoji(smileId = "69", image = Res.drawable.tv_30, number = 53),
        "70" to BgmEmoji(smileId = "70", image = Res.drawable.tv_31, number = 54),
        "71" to BgmEmoji(smileId = "71", image = Res.drawable.tv_32, number = 55),
        "72" to BgmEmoji(smileId = "72", image = Res.drawable.tv_33, number = 56),
        "73" to BgmEmoji(smileId = "73", image = Res.drawable.tv_34, number = 57),
        "74" to BgmEmoji(smileId = "74", image = Res.drawable.tv_35, number = 58),
        "75" to BgmEmoji(smileId = "75", image = Res.drawable.tv_36, number = 59),
        "76" to BgmEmoji(smileId = "76", image = Res.drawable.tv_37, number = 60),
        "77" to BgmEmoji(smileId = "77", image = Res.drawable.tv_38, number = 61),
        "78" to BgmEmoji(smileId = "78", image = Res.drawable.tv_39, number = 62),
        "79" to BgmEmoji(smileId = "79", image = Res.drawable.tv_40, number = 63),
        "80" to BgmEmoji(smileId = "80", image = Res.drawable.tv_41, number = 64),
        "81" to BgmEmoji(smileId = "81", image = Res.drawable.tv_42, number = 65),
        "82" to BgmEmoji(smileId = "82", image = Res.drawable.tv_43, number = 66),
        "83" to BgmEmoji(smileId = "83", image = Res.drawable.tv_44, number = 67),
        "84" to BgmEmoji(smileId = "84", image = Res.drawable.tv_45, number = 68),
        "85" to BgmEmoji(smileId = "85", image = Res.drawable.tv_46, number = 69),
        "86" to BgmEmoji(smileId = "86", image = Res.drawable.tv_47, number = 70),
        "87" to BgmEmoji(smileId = "87", image = Res.drawable.tv_48, number = 71),
        "88" to BgmEmoji(smileId = "88", image = Res.drawable.tv_49, number = 72),
        "89" to BgmEmoji(smileId = "89", image = Res.drawable.tv_50, number = 73),
        "90" to BgmEmoji(smileId = "90", image = Res.drawable.tv_51, number = 74),
        "91" to BgmEmoji(smileId = "91", image = Res.drawable.tv_52, number = 75),
        "92" to BgmEmoji(smileId = "92", image = Res.drawable.tv_53, number = 76),
        "93" to BgmEmoji(smileId = "93", image = Res.drawable.tv_54, number = 77),
        "94" to BgmEmoji(smileId = "94", image = Res.drawable.tv_55, number = 78),
        "95" to BgmEmoji(smileId = "95", image = Res.drawable.tv_56, number = 79),
        "96" to BgmEmoji(smileId = "96", image = Res.drawable.tv_57, number = 80),
        "97" to BgmEmoji(smileId = "97", image = Res.drawable.tv_58, number = 81),
        "98" to BgmEmoji(smileId = "98", image = Res.drawable.tv_59, number = 82),
        "99" to BgmEmoji(smileId = "99", image = Res.drawable.tv_60, number = 83),
        "100" to BgmEmoji(smileId = "100", image = Res.drawable.tv_61, number = 84),
        "101" to BgmEmoji(smileId = "101", image = Res.drawable.tv_62, number = 85),
        "102" to BgmEmoji(smileId = "102", image = Res.drawable.tv_63, number = 86),
        "103" to BgmEmoji(smileId = "103", image = Res.drawable.tv_64, number = 87),
        "104" to BgmEmoji(smileId = "104", image = Res.drawable.tv_65, number = 88),
        "105" to BgmEmoji(smileId = "105", image = Res.drawable.tv_66, number = 89),
        "106" to BgmEmoji(smileId = "106", image = Res.drawable.tv_67, number = 90),
        "107" to BgmEmoji(smileId = "107", image = Res.drawable.tv_68, number = 91),
        "108" to BgmEmoji(smileId = "108", image = Res.drawable.tv_69, number = 92),
        "109" to BgmEmoji(smileId = "109", image = Res.drawable.tv_70, number = 93),
        "110" to BgmEmoji(smileId = "110", image = Res.drawable.tv_71, number = 94),
        "111" to BgmEmoji(smileId = "111", image = Res.drawable.tv_72, number = 95),
        "112" to BgmEmoji(smileId = "112", image = Res.drawable.tv_73, number = 96),
        "113" to BgmEmoji(smileId = "113", image = Res.drawable.tv_74, number = 97),
        "114" to BgmEmoji(smileId = "114", image = Res.drawable.tv_75, number = 98),
        "115" to BgmEmoji(smileId = "115", image = Res.drawable.tv_76, number = 99),
        "116" to BgmEmoji(smileId = "116", image = Res.drawable.tv_77, number = 100),
        "117" to BgmEmoji(smileId = "117", image = Res.drawable.tv_78, number = 101),
        "118" to BgmEmoji(smileId = "118", image = Res.drawable.tv_79, number = 102),
        "119" to BgmEmoji(smileId = "119", image = Res.drawable.tv_80, number = 103),
        "120" to BgmEmoji(smileId = "120", image = Res.drawable.tv_81, number = 104),
        "121" to BgmEmoji(smileId = "121", image = Res.drawable.tv_82, number = 105),
        "122" to BgmEmoji(smileId = "122", image = Res.drawable.tv_83, number = 106),
        "123" to BgmEmoji(smileId = "123", image = Res.drawable.tv_84, number = 107),
        "124" to BgmEmoji(smileId = "124", image = Res.drawable.tv_85, number = 108),
        "125" to BgmEmoji(smileId = "125", image = Res.drawable.tv_86, number = 109),
        "126" to BgmEmoji(smileId = "126", image = Res.drawable.tv_87, number = 110),
        "127" to BgmEmoji(smileId = "127", image = Res.drawable.tv_88, number = 111),
        "128" to BgmEmoji(smileId = "128", image = Res.drawable.tv_89, number = 112),
        "129" to BgmEmoji(smileId = "129", image = Res.drawable.tv_90, number = 113),
        "130" to BgmEmoji(smileId = "130", image = Res.drawable.tv_91, number = 114),
        "131" to BgmEmoji(smileId = "131", image = Res.drawable.tv_92, number = 115),
        "132" to BgmEmoji(smileId = "132", image = Res.drawable.tv_93, number = 116),
        "133" to BgmEmoji(smileId = "133", image = Res.drawable.tv_94, number = 117),
        "134" to BgmEmoji(smileId = "134", image = Res.drawable.tv_95, number = 118),
        "135" to BgmEmoji(smileId = "135", image = Res.drawable.tv_96, number = 119),
        "136" to BgmEmoji(smileId = "136", image = Res.drawable.tv_97, number = 120),
        "137" to BgmEmoji(smileId = "137", image = Res.drawable.tv_98, number = 121),
        "138" to BgmEmoji(smileId = "138", image = Res.drawable.tv_99, number = 122),
        "139" to BgmEmoji(smileId = "139", image = Res.drawable.tv_100, number = 123),
        "140" to BgmEmoji(smileId = "140", image = Res.drawable.tv_101, number = 124),
        "141" to BgmEmoji(smileId = "141", image = Res.drawable.tv_102, number = 125),

        "200" to BgmEmoji(smileId = "200", image = Res.drawable.bgm_200, number = 200),
        "201" to BgmEmoji(smileId = "201", image = Res.drawable.bgm_201, number = 201),
        "202" to BgmEmoji(smileId = "202", image = Res.drawable.bgm_202, number = 202),
        "203" to BgmEmoji(smileId = "203", image = Res.drawable.bgm_203, number = 203),
        "204" to BgmEmoji(smileId = "204", image = Res.drawable.bgm_204, number = 204),
        "205" to BgmEmoji(smileId = "205", image = Res.drawable.bgm_205, number = 205),
        "206" to BgmEmoji(smileId = "206", image = Res.drawable.bgm_206, number = 206),
        "207" to BgmEmoji(smileId = "207", image = Res.drawable.bgm_207, number = 207),
        "208" to BgmEmoji(smileId = "208", image = Res.drawable.bgm_208, number = 208),
        "209" to BgmEmoji(smileId = "209", image = Res.drawable.bgm_209, number = 209),
        "210" to BgmEmoji(smileId = "210", image = Res.drawable.bgm_210, number = 210),
        "211" to BgmEmoji(smileId = "211", image = Res.drawable.bgm_211, number = 211),
        "212" to BgmEmoji(smileId = "212", image = Res.drawable.bgm_212, number = 212),
        "213" to BgmEmoji(smileId = "213", image = Res.drawable.bgm_213, number = 213),
        "214" to BgmEmoji(smileId = "214", image = Res.drawable.bgm_214, number = 214),
        "215" to BgmEmoji(smileId = "215", image = Res.drawable.bgm_215, number = 215),
        "216" to BgmEmoji(smileId = "216", image = Res.drawable.bgm_216, number = 216),
        "217" to BgmEmoji(smileId = "217", image = Res.drawable.bgm_217, number = 217),
        "218" to BgmEmoji(smileId = "218", image = Res.drawable.bgm_218, number = 218),
        "219" to BgmEmoji(smileId = "219", image = Res.drawable.bgm_219, number = 219),
        "220" to BgmEmoji(smileId = "220", image = Res.drawable.bgm_220, number = 220),
        "221" to BgmEmoji(smileId = "221", image = Res.drawable.bgm_221, number = 221),
        "222" to BgmEmoji(smileId = "222", image = Res.drawable.bgm_222, number = 222),
        "223" to BgmEmoji(smileId = "223", image = Res.drawable.bgm_223, number = 223),
        "224" to BgmEmoji(smileId = "224", image = Res.drawable.bgm_224, number = 224),
        "225" to BgmEmoji(smileId = "225", image = Res.drawable.bgm_225, number = 225),
        "226" to BgmEmoji(smileId = "226", image = Res.drawable.bgm_226, number = 226),
        "227" to BgmEmoji(smileId = "227", image = Res.drawable.bgm_227, number = 227),
        "228" to BgmEmoji(smileId = "228", image = Res.drawable.bgm_228, number = 228),
        "229" to BgmEmoji(smileId = "229", image = Res.drawable.bgm_229, number = 229),
        "230" to BgmEmoji(smileId = "230", image = Res.drawable.bgm_230, number = 230),
        "231" to BgmEmoji(smileId = "231", image = Res.drawable.bgm_231, number = 231),
        "232" to BgmEmoji(smileId = "232", image = Res.drawable.bgm_232, number = 232),
        "233" to BgmEmoji(smileId = "233", image = Res.drawable.bgm_233, number = 233),
        "234" to BgmEmoji(smileId = "234", image = Res.drawable.bgm_234, number = 234),
        "235" to BgmEmoji(smileId = "235", image = Res.drawable.bgm_235, number = 235),
        "236" to BgmEmoji(smileId = "236", image = Res.drawable.bgm_236, number = 236),
        "237" to BgmEmoji(smileId = "237", image = Res.drawable.bgm_237, number = 237),
        "238" to BgmEmoji(smileId = "238", image = Res.drawable.bgm_238, number = 238),

        "500" to BgmEmoji(smileId = "500", image = Res.drawable.bgm_500, number = 500),
        "501" to BgmEmoji(smileId = "501", image = Res.drawable.bgm_501, number = 501),
        "502" to BgmEmoji(smileId = "502", image = Res.drawable.bgm_502, number = 502),
        "503" to BgmEmoji(smileId = "503", image = Res.drawable.bgm_503, number = 503),
        "504" to BgmEmoji(smileId = "504", image = Res.drawable.bgm_504, number = 504),
        "505" to BgmEmoji(smileId = "505", image = Res.drawable.bgm_505, number = 505),
        "506" to BgmEmoji(smileId = "506", image = Res.drawable.bgm_506, number = 506),
        "507" to BgmEmoji(smileId = "507", image = Res.drawable.bgm_507, number = 507),
        "508" to BgmEmoji(smileId = "508", image = Res.drawable.bgm_508, number = 508),
        "509" to BgmEmoji(smileId = "509", image = Res.drawable.bgm_509, number = 509),
        "510" to BgmEmoji(smileId = "510", image = Res.drawable.bgm_510, number = 510),
        "511" to BgmEmoji(smileId = "511", image = Res.drawable.bgm_511, number = 511),
        "512" to BgmEmoji(smileId = "512", image = Res.drawable.bgm_512, number = 512),
        "513" to BgmEmoji(smileId = "513", image = Res.drawable.bgm_513, number = 513),
        "514" to BgmEmoji(smileId = "514", image = Res.drawable.bgm_514, number = 514),
        "515" to BgmEmoji(smileId = "515", image = Res.drawable.bgm_515, number = 515),
        "516" to BgmEmoji(smileId = "516", image = Res.drawable.bgm_516, number = 516),
        "517" to BgmEmoji(smileId = "517", image = Res.drawable.bgm_517, number = 517),
        "518" to BgmEmoji(smileId = "518", image = Res.drawable.bgm_518, number = 518),
        "519" to BgmEmoji(smileId = "519", image = Res.drawable.bgm_519, number = 519),
        "520" to BgmEmoji(smileId = "520", image = Res.drawable.bgm_520, number = 520),
        "521" to BgmEmoji(smileId = "521", image = Res.drawable.bgm_521, number = 521),
        "522" to BgmEmoji(smileId = "522", image = Res.drawable.bgm_522, number = 522),
        "523" to BgmEmoji(smileId = "523", image = Res.drawable.bgm_523, number = 523),
        "524" to BgmEmoji(smileId = "524", image = Res.drawable.bgm_524, number = 524),
        "525" to BgmEmoji(smileId = "525", image = Res.drawable.bgm_525, number = 525),
        "526" to BgmEmoji(smileId = "526", image = Res.drawable.bgm_526, number = 526),
        "527" to BgmEmoji(smileId = "527", image = Res.drawable.bgm_527, number = 527),
        "528" to BgmEmoji(smileId = "528", image = Res.drawable.bgm_528, number = 528),
        "529" to BgmEmoji(smileId = "529", image = Res.drawable.bgm_529, number = 529),
    )
}

@Composable
fun rememberBgmEmojis(): SerializeList<BgmEmoji> {
    return remember { bgmEmojis.values.filter { it.number in (1..23) }.toPersistentList() }
}

@Composable
fun rememberTvEmojis(): SerializeList<BgmEmoji> {
    return remember { bgmEmojis.values.filter { it.number in (24..125) }.toPersistentList() }
}

@Composable
fun rememberTvExtend1Emojis(): SerializeList<BgmEmoji> {
    return remember { bgmEmojis.values.filter { it.number in (200..238) }.toPersistentList() }
}

@Composable
fun rememberTvExtend2Emojis(): SerializeList<BgmEmoji> {
    return remember { bgmEmojis.values.filter { it.number in (500..529) }.toPersistentList() }
}