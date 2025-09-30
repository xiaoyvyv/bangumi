@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef

/**
 * ```html
 * <select name="team_id" id="AdvSearchTeam">
 *     <option value="0">全部</option>
 *     <option value="117">動漫花園</option>
 *     <option value="823">拨雪寻春</option>
 *     <option value="801">NC-Raws</option>
 *     <option value="669">喵萌奶茶屋</option>
 *     <option value="803">Lilith-Raws</option>
 *     <option value="648">魔星字幕团</option>
 *     <option value="619">桜都字幕组</option>
 *     <option value="767">天月動漫&amp;發佈組</option>
 *     <option value="185">极影字幕社</option>
 *     <option value="657">LoliHouse</option>
 *     <option value="151">悠哈C9字幕社</option>
 *     <option value="749">幻月字幕组</option>
 *     <option value="390">天使动漫论坛</option>
 *     <option value="303">动漫国字幕组</option>
 *     <option value="241">幻樱字幕组</option>
 *     <option value="47">爱恋字幕社</option>
 *     <option value="805">DBD制作组</option>
 *     <option value="604">c.c动漫</option>
 *     <option value="550">萝莉社活动室</option>
 *     <option value="283">千夏字幕组</option>
 *     <option value="772">IET字幕組</option>
 *     <option value="288">诸神kamigami字幕组</option>
 *     <option value="804">霜庭云花Sub</option>
 *     <option value="755">GMTeam</option>
 *     <option value="454">风车字幕组</option>
 *     <option value="37">雪飄工作室(FLsnow)</option>
 *     <option value="764">MCE汉化组</option>
 *     <option value="488">丸子家族</option>
 *     <option value="731">星空字幕组</option>
 *     <option value="574">梦蓝字幕组</option>
 *     <option value="504">LoveEcho!</option>
 *     <option value="650">SweetSub</option>
 *     <option value="630">枫叶字幕组</option>
 *     <option value="479">Little Subbers!</option>
 *     <option value="321">轻之国度</option>
 *     <option value="649">云光字幕组</option>
 *     <option value="520">豌豆字幕组</option>
 *     <option value="626">驯兽师联盟</option>
 *     <option value="666">中肯字幕組</option>
 *     <option value="781">SW字幕组</option>
 *     <option value="576">银色子弹字幕组</option>
 *     <option value="434">风之圣殿</option>
 *     <option value="665">YWCN字幕组</option>
 *     <option value="228">KRL字幕组</option>
 *     <option value="49">华盟字幕社</option>
 *     <option value="627">波洛咖啡厅</option>
 *     <option value="88">动音漫影</option>
 *     <option value="581">VCB-Studio</option>
 *     <option value="407">DHR動研字幕組</option>
 *     <option value="719">80v08</option>
 *     <option value="732">肥猫压制</option>
 *     <option value="680">Little字幕组</option>
 *     <option value="613">AI-Raws</option>
 *     <option value="806">离谱Sub</option>
 *     <option value="812">虹咲学园烤肉同好会</option>
 *     <option value="636">ARIA吧汉化组</option>
 *     <option value="75">柯南事务所</option>
 *     <option value="821">百冬練習組</option>
 *     <option value="641">冷番补完字幕组</option>
 *     <option value="765">爱咕字幕组</option>
 *     <option value="822">極彩字幕组</option>
 *     <option value="217">AQUA工作室</option>
 *     <option value="592">未央阁联盟</option>
 *     <option value="703">届恋字幕组</option>
 *     <option value="808">夜莺家族</option>
 *     <option value="734">TD-RAWS</option>
 *     <option value="447">夢幻戀櫻</option>
 *     <option value="790">WBX-SUB</option>
 *     <option value="807">Liella!の烧烤摊</option>
 *     <option value="874">次梦动画</option>
 *     <option value="875">ACGN社区制作组</option>
 *     <option value="869">Akinyunki</option>
 *     <option value="870">Furretar</option>
 *     <option value="881">隣天使字幕组</option>
 *     <option value="868">Prejudice-Studio</option>
 *     <option value="873">艾莉希婭</option>
 *     <option value="879">幻诚字幕组</option>
 *     <option value="866">阿特拉斯字幕组</option>
 *     <option value="877">樱桃花字幕组&amp;sakura-hana</option>
 *     <option value="878">新星字幕组</option>
 *     <option value="880">零始字幕組</option>
 *     <option value="867">绿茶字幕组</option>
 *     <option value="871">光雨字幕組</option>
 *     <option value="814">Amor字幕组</option>
 *     <option value="813">MingYSub</option>
 *     <option value="835">小白GM</option>
 *     <option value="832">Sakura</option>
 *     <option value="845">PMFAN字幕组</option>
 *     <option value="817">EMe</option>
 *     <option value="818">Alchemist</option>
 *     <option value="819">黑岩射手吧字幕组</option>
 *     <option value="816">ANi</option>
 *     <option value="844">DBFC字幕组</option>
 *     <option value="836">MSB制作組</option>
 * </select>
 * ```
 */
@IntDef(
    MagnetGardenTeam.TEAM_ALL,
    MagnetGardenTeam.TEAM_DMM,
    MagnetGardenTeam.TEAM_DORAEMON,
    MagnetGardenTeam.TEAM_NC_RAWS,
    MagnetGardenTeam.TEAM_MANGA_HOUSE,
    MagnetGardenTeam.TEAM_LILITH,
    MagnetGardenTeam.TEAM_MOXING,
    MagnetGardenTeam.TEAM_YINGDU,
    MagnetGardenTeam.TEAM_TMOON,
    MagnetGardenTeam.TEAM_JIYING,
    MagnetGardenTeam.TEAM_LOLIHOUSE,
    MagnetGardenTeam.TEAM_YUHA,
    MagnetGardenTeam.TEAM_HUANYUE,
    MagnetGardenTeam.TEAM_TSDM,
    MagnetGardenTeam.TEAM_DM_GUO,
    MagnetGardenTeam.TEAM_HUANYING,
    MagnetGardenTeam.TEAM_AILIAN,
    MagnetGardenTeam.TEAM_DBD,
    MagnetGardenTeam.TEAM_CC,
    MagnetGardenTeam.TEAM_LOLI,
    MagnetGardenTeam.TEAM_QIAXIA,
    MagnetGardenTeam.TEAM_IET,
    MagnetGardenTeam.TEAM_KAMIGAMI,
    MagnetGardenTeam.TEAM_SHUANGTING,
    MagnetGardenTeam.TEAM_GM,
    MagnetGardenTeam.TEAM_WINDMILL,
    MagnetGardenTeam.TEAM_FLSNOW,
    MagnetGardenTeam.TEAM_MCE,
    MagnetGardenTeam.TEAM_MARUKO,
    MagnetGardenTeam.TEAM_XINGKONG,
    MagnetGardenTeam.TEAM_MENGLAN,
    MagnetGardenTeam.TEAM_LOVEECHO,
    MagnetGardenTeam.TEAM_SWEETSUB,
    MagnetGardenTeam.TEAM_FENGYE,
    MagnetGardenTeam.TEAM_LITTLE_SUBBERS,
    MagnetGardenTeam.TEAM_LIGHTKINGDOM,
    MagnetGardenTeam.TEAM_YUNGUANG,
    MagnetGardenTeam.TEAM_PEA,
    MagnetGardenTeam.TEAM_TRAINER,
    MagnetGardenTeam.TEAM_ZHONGKEN,
    MagnetGardenTeam.TEAM_SW,
    MagnetGardenTeam.TEAM_SILVER_BULLET,
    MagnetGardenTeam.TEAM_WIND_TEMPLE,
    MagnetGardenTeam.TEAM_YWCN,
    MagnetGardenTeam.TEAM_KRL,
    MagnetGardenTeam.TEAM_HUAMENG,
    MagnetGardenTeam.TEAM_BOLO,
    MagnetGardenTeam.TEAM_DONYIN,
    MagnetGardenTeam.TEAM_VCB,
    MagnetGardenTeam.TEAM_DHR,
    MagnetGardenTeam.TEAM_80V08,
    MagnetGardenTeam.TEAM_CAT,
    MagnetGardenTeam.TEAM_LITTLE,
    MagnetGardenTeam.TEAM_AI,
    MagnetGardenTeam.TEAM_LIPU,
    MagnetGardenTeam.TEAM_NIJIGASAKI,
    MagnetGardenTeam.TEAM_ARIA,
    MagnetGardenTeam.TEAM_CONAN,
    MagnetGardenTeam.TEAM_BAIDONG,
    MagnetGardenTeam.TEAM_LENGFAN,
    MagnetGardenTeam.TEAM_AIGU,
    MagnetGardenTeam.TEAM_JICAI,
    MagnetGardenTeam.TEAM_AQUA,
    MagnetGardenTeam.TEAM_WEIYANG,
    MagnetGardenTeam.TEAM_JIELIAN,
    MagnetGardenTeam.TEAM_YEYING,
    MagnetGardenTeam.TEAM_TDRAWS,
    MagnetGardenTeam.TEAM_MENGHUAN,
    MagnetGardenTeam.TEAM_WBX,
    MagnetGardenTeam.TEAM_LIELLA,
    MagnetGardenTeam.TEAM_CIMENG,
    MagnetGardenTeam.TEAM_ACGN,
    MagnetGardenTeam.TEAM_AKINYUNKI,
    MagnetGardenTeam.TEAM_FURRETAR,
    MagnetGardenTeam.TEAM_LINTIANSHI,
    MagnetGardenTeam.TEAM_PREJUDICE,
    MagnetGardenTeam.TEAM_AILIXIYA,
    MagnetGardenTeam.TEAM_HUANCHENG,
    MagnetGardenTeam.TEAM_ATLAS,
    MagnetGardenTeam.TEAM_SAKURA_HANA,
    MagnetGardenTeam.TEAM_XINXING,
    MagnetGardenTeam.TEAM_ZERO,
    MagnetGardenTeam.TEAM_LVCHA,
    MagnetGardenTeam.TEAM_GUANGYU,
    MagnetGardenTeam.TEAM_AMOR,
    MagnetGardenTeam.TEAM_MINGY,
    MagnetGardenTeam.TEAM_XIAOBAI,
    MagnetGardenTeam.TEAM_SAKURA,
    MagnetGardenTeam.TEAM_PMFAN,
    MagnetGardenTeam.TEAM_EME,
    MagnetGardenTeam.TEAM_ALCHEMIST,
    MagnetGardenTeam.TEAM_BLACKROCK,
    MagnetGardenTeam.TEAM_ANI,
    MagnetGardenTeam.TEAM_DBFC,
    MagnetGardenTeam.TEAM_MSB,
)
@Retention(AnnotationRetention.SOURCE)
annotation class MagnetGardenTeam {
    companion object {
        const val TEAM_ALL = 0
        const val TEAM_DMM = 117               // 動漫花園
        const val TEAM_DORAEMON = 823          // 拨雪寻春
        const val TEAM_NC_RAWS = 801           // NC-Raws
        const val TEAM_MANGA_HOUSE = 669       // 喵萌奶茶屋
        const val TEAM_LILITH = 803            // Lilith-Raws
        const val TEAM_MOXING = 648            // 魔星字幕团
        const val TEAM_YINGDU = 619            // 桜都字幕组
        const val TEAM_TMOON = 767             // 天月動漫&發佈組
        const val TEAM_JIYING = 185            // 极影字幕社
        const val TEAM_LOLIHOUSE = 657         // LoliHouse
        const val TEAM_YUHA = 151              // 悠哈C9字幕社
        const val TEAM_HUANYUE = 749           // 幻月字幕组
        const val TEAM_TSDM = 390              // 天使动漫论坛
        const val TEAM_DM_GUO = 303            // 动漫国字幕组
        const val TEAM_HUANYING = 241          // 幻樱字幕组
        const val TEAM_AILIAN = 47             // 爱恋字幕社
        const val TEAM_DBD = 805               // DBD制作组
        const val TEAM_CC = 604                // c.c动漫
        const val TEAM_LOLI = 550              // 萝莉社活动室
        const val TEAM_QIAXIA = 283            // 千夏字幕组
        const val TEAM_IET = 772               // IET字幕組
        const val TEAM_KAMIGAMI = 288          // 诸神kamigami字幕组
        const val TEAM_SHUANGTING = 804        // 霜庭云花Sub
        const val TEAM_GM = 755                // GMTeam
        const val TEAM_WINDMILL = 454          // 风车字幕组
        const val TEAM_FLSNOW = 37             // 雪飄工作室(FLsnow)
        const val TEAM_MCE = 764               // MCE汉化组
        const val TEAM_MARUKO = 488            // 丸子家族
        const val TEAM_XINGKONG = 731          // 星空字幕组
        const val TEAM_MENGLAN = 574           // 梦蓝字幕组
        const val TEAM_LOVEECHO = 504          // LoveEcho!
        const val TEAM_SWEETSUB = 650          // SweetSub
        const val TEAM_FENGYE = 630            // 枫叶字幕组
        const val TEAM_LITTLE_SUBBERS = 479    // Little Subbers!
        const val TEAM_LIGHTKINGDOM = 321      // 轻之国度
        const val TEAM_YUNGUANG = 649          // 云光字幕组
        const val TEAM_PEA = 520               // 豌豆字幕组
        const val TEAM_TRAINER = 626           // 驯兽师联盟
        const val TEAM_ZHONGKEN = 666          // 中肯字幕組
        const val TEAM_SW = 781                // SW字幕组
        const val TEAM_SILVER_BULLET = 576     // 银色子弹字幕组
        const val TEAM_WIND_TEMPLE = 434       // 风之圣殿
        const val TEAM_YWCN = 665              // YWCN字幕组
        const val TEAM_KRL = 228               // KRL字幕组
        const val TEAM_HUAMENG = 49            // 华盟字幕社
        const val TEAM_BOLO = 627              // 波洛咖啡厅
        const val TEAM_DONYIN = 88             // 动音漫影
        const val TEAM_VCB = 581               // VCB-Studio
        const val TEAM_DHR = 407               // DHR動研字幕組
        const val TEAM_80V08 = 719             // 80v08
        const val TEAM_CAT = 732               // 肥猫压制
        const val TEAM_LITTLE = 680            // Little字幕组
        const val TEAM_AI = 613                // AI-Raws
        const val TEAM_LIPU = 806              // 离谱Sub
        const val TEAM_NIJIGASAKI = 812        // 虹咲学园烤肉同好会
        const val TEAM_ARIA = 636              // ARIA吧汉化组
        const val TEAM_CONAN = 75              // 柯南事务所
        const val TEAM_BAIDONG = 821           // 百冬練習組
        const val TEAM_LENGFAN = 641           // 冷番补完字幕组
        const val TEAM_AIGU = 765              // 爱咕字幕组
        const val TEAM_JICAI = 822             // 極彩字幕组
        const val TEAM_AQUA = 217              // AQUA工作室
        const val TEAM_WEIYANG = 592           // 未央阁联盟
        const val TEAM_JIELIAN = 703           // 届恋字幕组
        const val TEAM_YEYING = 808            // 夜莺家族
        const val TEAM_TDRAWS = 734            // TD-RAWS
        const val TEAM_MENGHUAN = 447          // 夢幻戀櫻
        const val TEAM_WBX = 790               // WBX-SUB
        const val TEAM_LIELLA = 807            // Liella!の烧烤摊
        const val TEAM_CIMENG = 874            // 次梦动画
        const val TEAM_ACGN = 875              // ACGN社区制作组
        const val TEAM_AKINYUNKI = 869         // Akinyunki
        const val TEAM_FURRETAR = 870          // Furretar
        const val TEAM_LINTIANSHI = 881        // 隣天使字幕组
        const val TEAM_PREJUDICE = 868         // Prejudice-Studio
        const val TEAM_AILIXIYA = 873          // 艾莉希婭
        const val TEAM_HUANCHENG = 879         // 幻诚字幕组
        const val TEAM_ATLAS = 866             // 阿特拉斯字幕组
        const val TEAM_SAKURA_HANA = 877       // 樱桃花字幕组&sakura-hana
        const val TEAM_XINXING = 878           // 新星字幕组
        const val TEAM_ZERO = 880              // 零始字幕組
        const val TEAM_LVCHA = 867             // 绿茶字幕组
        const val TEAM_GUANGYU = 871           // 光雨字幕組
        const val TEAM_AMOR = 814              // Amor字幕组
        const val TEAM_MINGY = 813             // MingYSub
        const val TEAM_XIAOBAI = 835           // 小白GM
        const val TEAM_SAKURA = 832            // Sakura
        const val TEAM_PMFAN = 845             // PMFAN字幕组
        const val TEAM_EME = 817               // EMe
        const val TEAM_ALCHEMIST = 818         // Alchemist
        const val TEAM_BLACKROCK = 819         // 黑岩射手吧字幕组
        const val TEAM_ANI = 816               // ANi
        const val TEAM_DBFC = 844              // DBFC字幕组
        const val TEAM_MSB = 836               // MSB制作組

        fun string(@MagnetGardenTeam type: Int): String {
            return when (type) {
                TEAM_ALL -> "全部汉化组"
                TEAM_DMM -> "動漫花園"
                TEAM_DORAEMON -> "拨雪寻春"
                TEAM_NC_RAWS -> "NC-Raws"
                TEAM_MANGA_HOUSE -> "喵萌奶茶屋"
                TEAM_LILITH -> "Lilith-Raws"
                TEAM_MOXING -> "魔星字幕团"
                TEAM_YINGDU -> "桜都字幕组"
                TEAM_TMOON -> "天月動漫&發佈組"
                TEAM_JIYING -> "极影字幕社"
                TEAM_LOLIHOUSE -> "LoliHouse"
                TEAM_YUHA -> "悠哈C9字幕社"
                TEAM_HUANYUE -> "幻月字幕组"
                TEAM_TSDM -> "天使动漫论坛"
                TEAM_DM_GUO -> "动漫国字幕组"
                TEAM_HUANYING -> "幻樱字幕组"
                TEAM_AILIAN -> "爱恋字幕社"
                TEAM_DBD -> "DBD制作组"
                TEAM_CC -> "c.c动漫"
                TEAM_LOLI -> "萝莉社活动室"
                TEAM_QIAXIA -> "千夏字幕组"
                TEAM_IET -> "IET字幕組"
                TEAM_KAMIGAMI -> "诸神kamigami字幕组"
                TEAM_SHUANGTING -> "霜庭云花Sub"
                TEAM_GM -> "GMTeam"
                TEAM_WINDMILL -> "风车字幕组"
                TEAM_FLSNOW -> "雪飄工作室(FLsnow)"
                TEAM_MCE -> "MCE汉化组"
                TEAM_MARUKO -> "丸子家族"
                TEAM_XINGKONG -> "星空字幕组"
                TEAM_MENGLAN -> "梦蓝字幕组"
                TEAM_LOVEECHO -> "LoveEcho!"
                TEAM_SWEETSUB -> "SweetSub"
                TEAM_FENGYE -> "枫叶字幕组"
                TEAM_LITTLE_SUBBERS -> "Little Subbers!"
                TEAM_LIGHTKINGDOM -> "轻之国度"
                TEAM_YUNGUANG -> "云光字幕组"
                TEAM_PEA -> "豌豆字幕组"
                TEAM_TRAINER -> "驯兽师联盟"
                TEAM_ZHONGKEN -> "中肯字幕組"
                TEAM_SW -> "SW字幕组"
                TEAM_SILVER_BULLET -> "银色子弹字幕组"
                TEAM_WIND_TEMPLE -> "风之圣殿"
                TEAM_YWCN -> "YWCN字幕组"
                TEAM_KRL -> "KRL字幕组"
                TEAM_HUAMENG -> "华盟字幕社"
                TEAM_BOLO -> "波洛咖啡厅"
                TEAM_DONYIN -> "动音漫影"
                TEAM_VCB -> "VCB-Studio"
                TEAM_DHR -> "DHR動研字幕組"
                TEAM_80V08 -> "80v08"
                TEAM_CAT -> "肥猫压制"
                TEAM_LITTLE -> "Little字幕组"
                TEAM_AI -> "AI-Raws"
                TEAM_LIPU -> "离谱Sub"
                TEAM_NIJIGASAKI -> "虹咲学园烤肉同好会"
                TEAM_ARIA -> "ARIA吧汉化组"
                TEAM_CONAN -> "柯南事务所"
                TEAM_BAIDONG -> "百冬練習組"
                TEAM_LENGFAN -> "冷番补完字幕组"
                TEAM_AIGU -> "爱咕字幕组"
                TEAM_JICAI -> "極彩字幕组"
                TEAM_AQUA -> "AQUA工作室"
                TEAM_WEIYANG -> "未央阁联盟"
                TEAM_JIELIAN -> "届恋字幕组"
                TEAM_YEYING -> "夜莺家族"
                TEAM_TDRAWS -> "TD-RAWS"
                TEAM_MENGHUAN -> "夢幻戀櫻"
                TEAM_WBX -> "WBX-SUB"
                TEAM_LIELLA -> "Liella!の烧烤摊"
                TEAM_CIMENG -> "次梦动画"
                TEAM_ACGN -> "ACGN社区制作组"
                TEAM_AKINYUNKI -> "Akinyunki"
                TEAM_FURRETAR -> "Furretar"
                TEAM_LINTIANSHI -> "隣天使字幕组"
                TEAM_PREJUDICE -> "Prejudice-Studio"
                TEAM_AILIXIYA -> "艾莉希婭"
                TEAM_HUANCHENG -> "幻诚字幕组"
                TEAM_ATLAS -> "阿特拉斯字幕组"
                TEAM_SAKURA_HANA -> "樱桃花字幕组&sakura-hana"
                TEAM_XINXING -> "新星字幕组"
                TEAM_ZERO -> "零始字幕組"
                TEAM_LVCHA -> "绿茶字幕组"
                TEAM_GUANGYU -> "光雨字幕組"
                TEAM_AMOR -> "Amor字幕组"
                TEAM_MINGY -> "MingYSub"
                TEAM_XIAOBAI -> "小白GM"
                TEAM_SAKURA -> "Sakura"
                TEAM_PMFAN -> "PMFAN字幕组"
                TEAM_EME -> "EMe"
                TEAM_ALCHEMIST -> "Alchemist"
                TEAM_BLACKROCK -> "黑岩射手吧字幕组"
                TEAM_ANI -> "ANi"
                TEAM_DBFC -> "DBFC字幕组"
                TEAM_MSB -> "MSB制作組"
                else -> "未知"
            }
        }
    }
}
