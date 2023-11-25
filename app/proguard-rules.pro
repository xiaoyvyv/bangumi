-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
# 混淆时不使用大小写混合，混淆后的类名为小写
-dontusemixedcaseclassnames
# 混淆时记录日志
-verbose
# 忽略警告
-ignorewarnings
# 保留 Annotation 不混淆 这在 JSON 实体映射时，反射过程中非常重要
-keepattributes *Annotation*,InnerClasses
# 保留 代码|行号|异常，方便异常信息的追踪，Release 可以选择是否打开
-keepattributes SourceFile,LineNumberTable,Exceptions
# 保留 JavascriptInterface
-keepattributes JavascriptInterface

# 禁止混淆泛型，项目会读取信息初始化
-keepattributes Signature

# seeds.txt 列出未混淆的类和成员
-printseeds build/proguard/seeds.txt
# usage.txt 列出从apk中删除的代码
-printusage build/proguard/unused.txt
# mapping.txt 列出混淆前后的映射
-printmapping build/proguard/mapping.txt

## 指定外部模糊字典
#-obfuscationdictionary ./proguard-keys.txt
## 指定 class 模糊字典
#-classobfuscationdictionary ./proguard-keys.txt
## 指定 package 模糊字典
#-packageobfuscationdictionary ./proguard-keys.txt

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

# Retrofit
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

-keep,allowobfuscation,allowshrinking class retrofit2.Response

# 项目
-keep class * implements androidx.viewbinding.ViewBinding {*;}
-keep class * extends androidx.lifecycle.ViewModel{*;}

-keep class * extends com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel{*;}
-keep class * extends com.xiaoyv.widget.binder.BaseQuickBindingAdapter
-keep class * extends com.xiaoyv.widget.binder.BaseQuickBindingHolder
-keep class * extends com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

-keep class com.xiaoyv.blueprint.base.**
-keep class com.xiaoyv.widget.binder.**
