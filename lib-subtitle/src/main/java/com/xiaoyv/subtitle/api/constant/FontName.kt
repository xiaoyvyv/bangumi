package com.xiaoyv.subtitle.api.constant

/**
 * Enum all the supported font names of the application
 */
enum class FontName(private val fontName: String) {
    Arial("Arial"),
    CourierNew("Courier New"),
    Times("Times"),
    Helvetica("Helvetica"),
    DroidSans("Droid Sans"),
    Cursive("cursive"),
    Monospace("monospace"),
    Serif("serif"),
    SansSerif("sans-serif"),
    Fantasy("fantasy"),
    Courier("Courier"),
    Georgia("Georgia"),
    LucidaConsole("Lucida Console"),
    Papyrus("Papyrus"),
    Tahoma("Tahoma"),
    TeX("TeX"),
    Verdana("Verdana"),
    Verona("Verona"),
    SimSun("SimSun"),
    Ubuntu("Ubuntu"),
    UbuntuMono("Ubuntu Mono"),
    FreeMono("FreeMono"),
    LiberationSerif("Liberation Serif"),
    Purisa("Purisa"),
    TimesNewRoman("Times New Roman");

    override fun toString(): String {
        return fontName
    }
}
