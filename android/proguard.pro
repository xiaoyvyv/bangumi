
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*

# Repackage classes to bgm
-repackageclasses bgm

# Lists classes and members that are not confused
-printseeds build/proguard/seeds.txt
# List the code that was removed from the apk
-printusage build/proguard/unused.txt
# Lists the mapping before and after the obfuscation
-printmapping build/proguard/mapping.txt

-dontwarn com.google.android.gms.common.annotation.NoNullnessRewrite


-keep class androidx.compose.runtime.** { *; }
-keep class androidx.collection.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class androidx.compose.ui.text.platform.ReflectionUtil { *; }

# We're excluding Material 2 from the project as we're using Material 3
-dontwarn androidx.compose.material.**

# Kotlinx coroutines rules seems to be outdated with the latest version of Kotlin and Proguard
-keep class kotlinx.coroutines.** { *; }

-keep class androidx.datastore.preferences.** { *; }
-keep class io.ktor.** { *; }
-keep class coil3.** { *; }
-keep class ui.navigation.** { *; }

-dontwarn io.ktor.network.sockets.**

# Okhttp
-dontwarn java.lang.management.**
-dontwarn javax.annotation.**
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.**
-dontwarn okhttp3.internal.Util
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-keep class okio.** { *; }

# Webview
-keep class org.cef.** { *; }
-keep class kotlinx.coroutines.swing.SwingDispatcherFactory

# Keep DataStore Preferences classes
-keep class androidx.datastore.preferences.** { *; }

# Ksoup
-keep class com.mohamedrejeb.ksoup.html.**{ *; }
-keep class com.fleeksoft.ksoup.**{ *; }

# Live2D
-keep class com.live2d.sdk.cubism.core.**{ *; }