package com.xiaoyv.script.i18n

import com.xiaoyv.utilcode.util.FileIOUtils
import com.xiaoyv.utilcode.util.FileUtils
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.Serializable


/**
 * LanguageParseAndroidScript
 *
 * @author why
 * @since 2022/5/11
 */
object LanguageScript {
    /**
     * strings.xml 头部和尾部
     */
    private const val XML_START = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<resources xmlns:tools=\"http://schemas.android.com/tools\" tools:ignore=\"MissingTranslation\">\n"
    private const val XML_END = "</resources>"

    /**
     * 多语言表格文件名称
     */
    private const val BANGUMI_FILE_NAME = "Bangumi 原生安卓 - 文档集.xlsx"

    /**
     * 解析的表名
     */
    private const val I18N_SHEET_NAME = "Bangumi for Android 多语言表"

    /**
     * 是否检查其它多语言特殊符和是否英语匹配
     */
    private const val CHECK_SYMBOL = true

    /**
     * 多语言表格 列 的索引对应的 INDEX
     */
    internal const val INDEX_FEATURE = 0
    internal const val INDEX_ANDROID_KEY = 1
    internal const val INDEX_LANGUAGE_ZH_CN = 2
    internal const val INDEX_LANGUAGE_ZH_TW = 3
    internal const val INDEX_LANGUAGE_JP = 4
    internal const val INDEX_LANGUAGE_EN = 5

    /**
     * 项目根目录路径
     */
    private val rootProjectDir = System.getProperty("user.dir")

    private val sp = File.separator

    /**
     * 下载的多语言表格文件
     */
    private val languageXlsxDir get() = rootProjectDir + "${sp}lib-script${sp}bgm"
    private val languageXlsx = File(languageXlsxDir, BANGUMI_FILE_NAME)

    /**
     * 渠道资源文件夹名称
     */
    private val channelList = arrayListOf<String>()

    private val String.isChannelKey: Boolean
        get() {
            channelList.forEach {
                if (this.startsWith(it)) {
                    return true
                }
            }
            return false
        }

    private val Int.indexToLanguageTip: String
        get() = when (this) {
            INDEX_LANGUAGE_ZH_CN -> "CN"
            INDEX_LANGUAGE_ZH_TW -> "TW"
            INDEX_LANGUAGE_JP -> "JP"
            INDEX_LANGUAGE_EN -> "EN"
            else -> ""
        }

    /**
     * 执行脚本
     */
    fun start() {
        println("解析多语言脚本执行开始...")

        // 文档不存在
        if (FileUtils.isFileExists(languageXlsx).not()) {
            System.err.println("多语言文档不存在")
            return
        }

        // 最新的多语言文档数据
        val webLanguageList = parseWebXlsx()

        // lib-i18n 模块 Res 文件夹路径
        val libStringResDir = rootProjectDir + "${sp}lib-i18n${sp}src${sp}main${sp}res"

        // 解析在线文档，同步多语言资源
        webLanguageList.generateAndroidXml(libStringResDir)

        println("解析多语言脚本执行完成！")
    }

    /**
     * 解析在线文档，转为多语言模型集合
     *
     * @return 多语言模型集合
     */
    private fun parseWebXlsx(): ArrayList<LanguageData> {
        val workbook = XSSFWorkbook(languageXlsx)
        val androidSheet = workbook.getSheet(I18N_SHEET_NAME)

        val webLanguageList = arrayListOf<LanguageData>()

        androidSheet.rowIterator().withIndex().forEach {
            val rowIndex = it.index
            if (rowIndex == 0 || rowIndex == 1) {
                return@forEach
            }

            val row = it.value
            val cellList = row.cellIterator().asSequence().toList()
            webLanguageList.add(cellList.toLanguageData())
        }
        return webLanguageList
    }


    /**
     * 获取 excel 一行所有的小表格，并转为一个多语言模型
     *
     * @return 多语言模型
     */
    private fun List<Cell>.toLanguageData() = LanguageData().apply {
        description = getOrNull(INDEX_FEATURE).text()
        androidKey = getOrNull(INDEX_ANDROID_KEY).text()
        languageZhCN = getOrNull(INDEX_LANGUAGE_ZH_CN).text()
        languageZhTW = getOrNull(INDEX_LANGUAGE_ZH_TW).text()
        languageJA = getOrNull(INDEX_LANGUAGE_JP).text()
        languageEN = getOrNull(INDEX_LANGUAGE_EN).text()
    }

    /**
     * 获取 excel 单个小表格的内容文案
     *
     * @return 首尾去空格，并替换特定符号
     */
    private fun Cell?.text(): String {
        val cell = this ?: return ""
        runCatching {
            return cell.toString().trim()
        }
        return ""
    }

    /**
     * 多语言模型集合 生成各个多语言目录下的 strings.xml
     *
     * @param saveResDirPath 生成的 res 目录路径
     * @param isChannel 是为渠道包资源构建
     */
    private fun List<LanguageData>.generateAndroidXml(
        saveResDirPath: String,
        isChannel: Boolean = false,
    ) {
        val listOf = arrayListOf(INDEX_LANGUAGE_ZH_TW, INDEX_LANGUAGE_JP, INDEX_LANGUAGE_EN)

        checkSpecialSymbol(stringDataList = this, checkIndex = listOf, symbol = "%s")
        checkSpecialSymbol(stringDataList = this, checkIndex = listOf, symbol = "%d")
        checkSpecialSymbol(stringDataList = this, checkIndex = listOf, symbol = "%f")

        val languageZhCnXml = saveResDirPath + sp + "values" + sp + "strings.xml"
        val languageEnXml = saveResDirPath + sp + "values-en" + sp + "strings.xml"
        val languageZhTwXml = saveResDirPath + sp + "values-zh-rTW" + sp + "strings.xml"
        val languageJpXml = saveResDirPath + sp + "values-ja" + sp + "strings.xml"

        val languageZhCnStringBuilder = StringBuilder().apply { append(XML_START) }
        val languageZhTwStringBuilder = StringBuilder().apply { append(XML_START) }
        val languageJpStringBuilder = StringBuilder().apply { append(XML_START) }
        val languageEnStringBuilder = StringBuilder().apply { append(XML_START) }

        runCatching {
            forEach {
                // 注释
                var description = it.description.replace("\n", "\t")
                // Android Key
                val key = it.androidKey

                // 没有 注释 和 Key 则跳过
                if (key.isBlank() && description.isBlank()) {
                    return@forEach
                }
                // 默认构建时，渠道包的 Key 也跳过，不在默认渠道生成
                if (isChannel.not() && key.isChannelKey) {
                    return@forEach
                }

                // 注释生成
                if (description.isNotBlank()) {
                    // <!-- # xxx # -->
                    description = "\n    <!-- $description -->\n"
                    languageZhCnStringBuilder.append(description)
                    languageZhTwStringBuilder.append(description)
                    languageJpStringBuilder.append(description)
                    languageEnStringBuilder.append(description)

                    // 仅有注释生成后则跳过
                    if (it.hasLanguage.not() || key.isBlank()) {
                        return@forEach
                    }
                }

                languageZhCnStringBuilder.appendXmlItem(it.languageZhCN.toXmlItem(key))
                languageZhTwStringBuilder.appendXmlItem(it.languageZhTW.toXmlItem(key))
                languageJpStringBuilder.appendXmlItem(it.languageJA.toXmlItem(key))
                languageEnStringBuilder.appendXmlItem(it.languageEN.toXmlItem(key))
            }
        }.onFailure {
            println("languageEnStringBuilder: Error")
            it.printStackTrace()
        }

        // 闭包
        languageZhCnStringBuilder.append(XML_END)
        languageZhTwStringBuilder.append(XML_END)
        languageJpStringBuilder.append(XML_END)
        languageEnStringBuilder.append(XML_END)

        FileIOUtils.writeFileFromString(languageZhCnXml, languageZhCnStringBuilder.toString())
        FileIOUtils.writeFileFromString(languageZhTwXml, languageZhTwStringBuilder.toString())
        FileIOUtils.writeFileFromString(languageJpXml, languageJpStringBuilder.toString())
        FileIOUtils.writeFileFromString(languageEnXml, languageEnStringBuilder.toString())
    }

    /**
     * 拼接多语言条目
     */
    private fun StringBuilder.appendXmlItem(xmlItem: String?) {
        if (xmlItem.isNullOrBlank()) return
        append(xmlItem).append("\n")
    }

    /**
     * 内容文本转为单条资源节点
     * 这里可以对文本内容替换和编码处理
     *
     * - &lt; < 小于号
     * - &gt; > 大于号
     * - &amp; & 和
     *
     * @return <string name="key">encodeText</string>
     */
    private fun String.toXmlItem(key: String): String? {
        val encodeText = this
            // & 需要最先替换
            .replace("&", "&amp;")
            .replace("\n", "\\n")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\\'", "'")
            .replace("'", "\\'")
            .replace("“", "\"")
            .replace("”", "\"")
            .replace("\\\"", "\"")
            .replace("\"", "\\\"")
            .replace("s%", "%s")
            .replace("@%", "%s")
            .replace("%@", "%s")
            .trim()

        if (encodeText.isBlank()) {
            return null
        }
        return """    <string name="$key">$encodeText</string>"""
    }

    /**
     * 校验多语言资源的特殊符号是否正常匹配
     * 如：%s 等
     */
    private fun checkSpecialSymbol(
        stringDataList: List<LanguageData>,
        checkIndex: List<Int>,
        symbol: String
    ) {
        if (!CHECK_SYMBOL) return

        // 过滤出包含检测符号的条目
        val needCheckItems = stringDataList.filter { it.languageZhCN.contains(symbol) }

        val errorLanguages = arrayListOf<String>()

        needCheckItems.onEach { language ->
            // 英语标准栏目的特殊符匹配数目
            val enXmlLine = language.languageZhCN.toXmlItem(language.androidKey).orEmpty().trim()
            val enSymbolCount = symbol.toRegex().findAll(enXmlLine).count()

            // 检测其它语言特殊符匹配数目是否同英语条目一样多
            checkIndex.onEach checkFor@{ index ->
                val dataByIndex = language.getDataByIndex(index).trim()
                if (dataByIndex.isBlank()) {
                    return@checkFor
                }

                val targetLanguageXmlLine =
                    dataByIndex.toXmlItem(language.androidKey).orEmpty().trim()

                val targetLanguageSymbolCount =
                    symbol.toRegex().findAll(targetLanguageXmlLine).count()

                if (enSymbolCount != targetLanguageSymbolCount) {
                    errorLanguages.add(
                        "多语言特征不匹配：key => ${String.format("%35s", language.androidKey)}，" +
                                "原文包含 $enSymbolCount 个 $symbol，" +
                                "语言（${index.indexToLanguageTip}）匹配到了 $targetLanguageSymbolCount 个，" +
                                "请检查：=> $dataByIndex"
                    )
                }
            }
        }

        errorLanguages.forEach {
            System.err.println(it)
        }

        if (errorLanguages.isNotEmpty()) {
            throw Exception("多语言符号（$symbol）检测不通过：共 ${errorLanguages.size} 条，请检查并修复表格文档内容")
        }
    }

}

/**
 * 多语言模型
 */
data class LanguageData(
    var description: String = "",
    var androidKey: String = "",
    var iosKey: String = "",
    var languageZhCN: String = "",
    var languageZhTW: String = "",
    var languageJA: String = "",
    var languageEN: String = "",
) : Serializable {

    /**
     * 指定模型的多语言覆盖的本模型
     */
    fun fillWith(newItem: LanguageData) {
        this.languageZhCN = newItem.languageZhCN.ifBlank { this.languageZhCN }
        this.languageZhTW = newItem.languageZhTW.ifBlank { this.languageZhTW }
        this.languageJA = newItem.languageJA.ifBlank { this.languageJA }
        this.languageEN = newItem.languageEN.ifBlank { this.languageEN }
    }

    /**
     * 根据 INDEX 获取 LanguageData 模型数据
     */
    fun getDataByIndex(it: Int): String {
        when (it) {
            LanguageScript.INDEX_FEATURE -> return description
            LanguageScript.INDEX_ANDROID_KEY -> return androidKey
            LanguageScript.INDEX_LANGUAGE_ZH_CN -> return languageZhCN
            LanguageScript.INDEX_LANGUAGE_ZH_TW -> return languageZhTW
            LanguageScript.INDEX_LANGUAGE_JP -> return languageJA
            LanguageScript.INDEX_LANGUAGE_EN -> return languageEN
        }
        return ""
    }

    /**
     * 是否有语言文案
     */
    val hasLanguage: Boolean
        get() = languageZhCN.isNotBlank() ||
                languageEN.isNotBlank() ||
                languageZhTW.isNotBlank() ||
                languageJA.isNotBlank()
}