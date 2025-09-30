package com.xiaoyv.bangumi.shared.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode

val typography = Typography()

/*@Composable
fun loadAppFont(): FontFamily {
    return FontFamily(
        Font(Res.font.ma_400w, FontWeight.Thin),
        Font(Res.font.ma_400w, FontWeight.ExtraLight),
        Font(Res.font.ma_400w, FontWeight.Light),
        Font(Res.font.ma_400w, FontWeight.Normal),
        Font(Res.font.ma_400w, FontWeight.Medium),
        Font(Res.font.ma_400w, FontWeight.SemiBold),
        Font(Res.font.ma_400w, FontWeight.Bold),
    )
}*/

@Composable
fun rememberAppTypography(): Typography {
    if (LocalInspectionMode.current) return typography
    return typography
//    val fontFamily = loadAppFont()
//    return remember {
//        Typography(
//            displayLarge = typography.displayLarge.copy(fontFamily = fontFamily),
//            displayMedium = typography.displayMedium.copy(fontFamily = fontFamily),
//            displaySmall = typography.displaySmall.copy(fontFamily = fontFamily),
//            headlineLarge = typography.headlineLarge.copy(fontFamily = fontFamily),
//            headlineMedium = typography.headlineMedium.copy(fontFamily = fontFamily),
//            headlineSmall = typography.headlineSmall.copy(fontFamily = fontFamily),
//            titleLarge = typography.titleLarge.copy(fontFamily = fontFamily),
//            titleMedium = typography.titleMedium.copy(fontFamily = fontFamily),
//            titleSmall = typography.titleSmall.copy(fontFamily = fontFamily),
//            bodyLarge = typography.bodyLarge.copy(fontFamily = fontFamily),
//            bodyMedium = typography.bodyMedium.copy(fontFamily = fontFamily),
//            bodySmall = typography.bodySmall.copy(fontFamily = fontFamily),
//            labelLarge = typography.labelLarge.copy(fontFamily = fontFamily),
//            labelMedium = typography.labelMedium.copy(fontFamily = fontFamily),
//            labelSmall = typography.labelSmall.copy(fontFamily = fontFamily)
//        )
//    }
}

