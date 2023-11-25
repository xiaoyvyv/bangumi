# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# seeds.txt 列出未混淆的类和成员
-printseeds build/proguard/seeds.txt
# usage.txt 列出从apk中删除的代码
-printusage build/proguard/unused.txt
# mapping.txt 列出混淆前后的映射
-printmapping build/proguard/mapping.txt

# 指定外部模糊字典
-obfuscationdictionary ./proguard-keys.txt
# 指定 class 模糊字典
-classobfuscationdictionary ./proguard-keys.txt
# 指定 package 模糊字典
-packageobfuscationdictionary ./proguard-keys.txt

-dontwarn java.awt.**
-dontwarn java.beans.**
-dontwarn javax.imageio.**
-dontwarn javax.lang.model.**
-dontwarn androidx.**
-dontwarn sun.misc.**

# Gson
-keep class com.google.gson.** {*;}
-keep class com.google.**{*;}
-keep class com.google.gson.stream.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keep class * implements androidx.viewbinding.ViewBinding {*;}
-keep class * extends androidx.lifecycle.ViewModel
