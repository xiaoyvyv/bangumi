@file:Suppress("SpellCheckingInspection", "UnstableApiUsage")

import com.android.build.api.dsl.LibraryExtension
import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.android.kotlin.multiplatform.library")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

/*
android {
    experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    lint {
        targetSdk = libs.versions.android.targetSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].java.srcDirs("src/androidMain/java")
}*/


kotlin {
    jvmToolchain(21)

    applyDefaultHierarchyTemplate()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = frameworkBaseName
            isStatic = true
        }
    }

    jvm()

    androidLibrary {
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withJava()

        lint {
            targetSdk = libs.versions.android.targetSdk.get().toInt()
        }

        androidResources {
            enable = true
        }

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }

        enableCoreLibraryDesugaring = false

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    composeCompiler {
        reportsDestination = layout.buildDirectory.dir("compose_compiler")
        metricsDestination = layout.buildDirectory.dir("compose_compiler")
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)

            implementation(libs.bundles.coil3.android)
            implementation(libs.koin.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.android.driver)

            implementation(libs.compose.paging.common.android)

            implementation(libs.tinypinyin.android)
        }

        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.compose.resources)

            implementation(libs.bundles.compose.common)

            implementation(libs.cryptohash)
            implementation(libs.cryptorandom)

            implementation(libs.ksoup)
            implementation(libs.ksoup.engine.html)
            implementation(libs.ksoup.engine.entities)

            implementation(libs.tinypinyin.core)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.bundles.koin.common)

            implementation(libs.bundles.ktor.common)
            implementation(libs.bundles.ktorfit.common)

            implementation(libs.bundles.coil3.common)
            implementation(libs.bundles.zoomimage)
            implementation(libs.bundles.vico)
            implementation(libs.bundles.file.kit)
            implementation(libs.bundles.krop)
            implementation(libs.bundles.kotlinx)

            // Sqlite
            implementation(libs.sqldelight.runtime)

            // DataStore
            api(libs.androidx.datastore.preferences)
            api(libs.androidx.datastore.preferences.core)
            api(libs.androidx.datastore.core.okio)
        }

        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)

            implementation(libs.ktor.client.darwin)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.sqlite.driver)
            implementation(libs.tinypinyin.jvm)
        }

        all {
            languageSettings {
                optIn("kotlin.io.encoding.ExperimentalEncodingApi")
                optIn("kotlin.time.ExperimentalTime")
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
                optIn("kotlinx.coroutines.FlowPreview")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                optIn("androidx.compose.material3.ExperimentalMaterial3Api")
                optIn("androidx.compose.foundation.layout.ExperimentalLayoutApi")
                optIn("androidx.compose.foundation.ExperimentalFoundationApi")
                optIn("androidx.paging.ExperimentalPagingApi")
                optIn("org.orbitmvi.orbit.annotation.OrbitExperimental")

                compilerOptions {
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }
            }
        }
    }
}


compose.resources {
    publicResClass = false
    generateResClass = never
    packageOfResClass = "com.xiaoyv.bangumi.$composeResourceId.resources"
}

tasks.register("generateMvi") {
    group = "bangumi"

    doLast {
        val parentName = project.parent?.name.orEmpty()
            .let { if (it == "features") "" else it }
            .uppercaseFirstChar()
        val moduleName =
            parentName + project.name.split("_").joinToString { it.uppercaseFirstChar() }
        val androidExt =
            requireNotNull(project.extensions.findByName("android")) as LibraryExtension
        val namespace = androidExt.namespace.orEmpty()
        val namespaceDir = layout.projectDirectory
            .dir("src/commonMain/kotlin/${namespace.replace(".", "/")}")
            .also { mkdir(it) }

        if (namespaceDir.asFile.listFiles()?.isNotEmpty() == true) {
            throw GradleException("Please clean dir：$namespaceDir")
        }

        val businessDir = namespaceDir.dir("business").also { mkdir(it) }

        val classFileEvent = businessDir.file("${moduleName}Event.kt")
        val classFileSideEffect = businessDir.file("${moduleName}SideEffect.kt")
        val classFileState = businessDir.file("${moduleName}State.kt")
        val classFileViewModel = businessDir.file("${moduleName}ViewModel.kt")
        val classFileNavigator = namespaceDir.file("${moduleName}Navigator.kt")
        val classFileScreen = namespaceDir.file("${moduleName}Screen.kt")

        classFileEvent.asFile.writeText(
            "package ${namespace}.business\n" +
                    "\n" +
                    "import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen\n" +
                    "\n" +
                    "/**\n" +
                    " * [${moduleName}Event]\n" +
                    " *\n" +
                    " * @author why\n" +
                    " * @since 2025/1/12\n" +
                    " */\n" +
                    "sealed class ${moduleName}Event {\n" +
                    "    sealed class UI : ${moduleName}Event() {\n" +
                    "        data object OnNavUp : UI()\n" +
                    "        data class OnNavScreen(val screen: Screen) : UI()\n" +
                    "    }\n" +
                    "\n" +
                    "    sealed class Action : ${moduleName}Event() {\n" +
                    "        data class OnRefresh(val loading : Boolean) : Action()\n" +
                    "    }\n" +
                    "}"
        )

        classFileSideEffect.asFile.writeText(
            "package ${namespace}.business\n" +
                    "\n" +
                    "/**\n" +
                    " * [${moduleName}SideEffect]\n" +
                    " *\n" +
                    " * @author why\n" +
                    " * @since 2025/1/12\n" +
                    " */\n" +
                    "sealed class ${moduleName}SideEffect {\n" +
                    "\n" +
                    "}"
        )

        classFileState.asFile.writeText(
            "package ${namespace}.business\n" +
                    "\n" +
                    "import androidx.compose.runtime.Immutable\n" +
                    "\n" +
                    "/**\n" +
                    " * [${moduleName}State]\n" +
                    " *\n" +
                    " * @author why\n" +
                    " * @since 2025/1/12\n" +
                    " */\n" +
                    "@Immutable\n" +
                    "data class ${moduleName}State(\n" +
                    "    val title: String = \"\"\n" +
                    ")\n"
        )

        classFileNavigator.asFile.writeText(
            "package ${namespace}\n" +
                    "\n" +
                    "import androidx.navigation.NavGraphBuilder\n" +
                    "import androidx.navigation.NavHostController\n" +
                    "import androidx.navigation.compose.composable\n" +
                    "import com.xiaoyv.bangumi.shared.core.utils.debounce\n" +
                    "import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen\n" +
                    "\n" +
                    "\n" +
                    "data class ${moduleName}Arguments(val id: Long) {\n" +
                    "    constructor(savedStateHandle: SavedStateHandle) : this(\n" +
                    "        id = savedStateHandle.getLong(EXTRA_ID)\n" +
                    "    )\n" +
                    "}" +
                    "\n" +
                    "fun NavHostController.navigate${moduleName}(screen: Screen.${moduleName}) = debounce(screen.route) {\n" +
                    "    navigate(screen.route)\n" +
                    "}\n" +
                    "\n" +
                    "fun NavGraphBuilder.add${moduleName}Screen(\n" +
                    "    onNavUp: () -> Unit,\n" +
                    "    onNavScreen: (Screen) -> Unit\n" +
                    ") {\n" +
                    "    composable(route = Screen.${moduleName}.route) {\n" +
                    "        ${moduleName}Route(\n" +
                    "            onNavUp = onNavUp,\n" +
                    "            onNavScreen = onNavScreen\n" +
                    "        )\n" +
                    "    }\n" +
                    "}"
        )

        classFileViewModel.asFile.writeText(
            "package ${namespace}.business\n" +
                    "\n" +
                    "import androidx.lifecycle.SavedStateHandle\n" +
                    "import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel\n" +
                    "\n" +
                    "/**\n" +
                    " * [${moduleName}ViewModel]\n" +
                    " *\n" +
                    " * @author why\n" +
                    " * @since 2025/1/12\n" +
                    " */\n" +
                    "class ${moduleName}ViewModel(savedStateHandle: SavedStateHandle) :\n" +
                    "    BaseViewModel<${moduleName}State, ${moduleName}SideEffect, ${moduleName}Event.Action>(savedStateHandle) {\n" +
                    "\n" +
                    "    override fun initSate(onCreate: Boolean) = ${moduleName}State()\n" +
                    "\n" +
                    "    override fun onEvent(event: ${moduleName}Event.Action) {\n" +
                    "        when (event) {\n" +
                    "            is ${moduleName}Event.Action.OnRefresh -> refresh(loading = event.loading)\n" +
                    "        }\n" +
                    "    }\n" +
                    "\n" +
                    "}"
        )

        classFileScreen.asFile.writeText(
            "package ${namespace}\n" +
                    "\n" +
                    "import androidx.compose.foundation.layout.fillMaxSize\n" +
                    "import androidx.compose.foundation.layout.padding\n" +
                    "import androidx.compose.foundation.rememberScrollState\n" +
                    "import androidx.compose.foundation.verticalScroll\n" +
                    "import androidx.compose.material3.Scaffold\n" +
                    "import androidx.compose.material3.TopAppBarDefaults\n" +
                    "import androidx.compose.runtime.Composable\n" +
                    "import androidx.compose.runtime.getValue\n" +
                    "import androidx.compose.ui.Modifier\n" +
                    "import androidx.compose.ui.input.nestedscroll.nestedScroll\n" +
                    "import com.xiaoyv.bangumi.core_resource.resources.Res\n" +
                    "import com.xiaoyv.bangumi.core_resource.resources.login_title\n" +
                    "import ${namespace}.business.${moduleName}Event\n" +
                    "import ${namespace}.business.${moduleName}State\n" +
                    "import ${namespace}.business.${moduleName}ViewModel\n" +
                    "import com.xiaoyv.bangumi.shared.core.mvi.BaseState\n" +
                    "import org.orbitmvi.orbit.compose.collectAsState\n" +
                    "import com.xiaoyv.bangumi.shared.ui.component.bar.BgmLargeTopAppBar\n" +
                    "import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout\n" +
                    "import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen\n" +
                    "import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect\n" +
                    "import org.jetbrains.compose.resources.stringResource\n" +
                    "import org.koin.compose.viewmodel.koinViewModel\n" +
                    "\n" +
                    "@Composable\n" +
                    "fun ${moduleName}Route(\n" +
                    "    viewModel: ${moduleName}ViewModel = koinViewModel<${moduleName}ViewModel>(),\n" +
                    "    onNavUp: () -> Unit,\n" +
                    "    onNavScreen: (Screen) -> Unit,\n" +
                    ") {\n" +
                    "    val baseState by viewModel.collectAsState()\n" +
                    "\n" +
                    "    viewModel.collectBaseSideEffect {\n" +
                    "\n" +
                    "    }\n" +
                    "\n" +
                    "    ${moduleName}Screen(\n" +
                    "        baseState = baseState,\n" +
                    "        onActionEvent = viewModel::onEvent,\n" +
                    "        onUiEvent = {\n" +
                    "            when (it) {\n" +
                    "                is ${moduleName}Event.UI.OnNavUp -> onNavUp()\n" +
                    "                is ${moduleName}Event.UI.OnNavScreen -> onNavScreen(it.screen)\n" +
                    "            }\n" +
                    "        },\n" +
                    "    )\n" +
                    "}\n" +
                    "\n" +
                    "@Composable\n" +
                    "private fun ${moduleName}Screen(\n" +
                    "    baseState: BaseState<${moduleName}State>,\n" +
                    "    onUiEvent: (${moduleName}Event.UI) -> Unit,\n" +
                    "    onActionEvent: (${moduleName}Event.Action) -> Unit\n" +
                    ") {\n" +
                    "\n" +
                    "    Scaffold(\n" +
                    "        modifier = Modifier.fillMaxSize(),\n" +
                    "        topBar = {\n" +
                    "            BgmTopAppBar(\n" +
                    "                title = stringResource(Res.string.login_title),\n" +
                    "                onNavigationClick = { onUiEvent(${moduleName}Event.UI.OnNavUp) }\n" +
                    "            )\n" +
                    "        }\n" +
                    "    ) {\n" +
                    "        StateLayout(\n" +
                    "            modifier = Modifier\n" +
                    "                .fillMaxSize()\n" +
                    "                .padding(it),\n" +
                    "            onRefresh = { onActionEvent(${moduleName}Event.Action.OnRefresh(it)) },\n" +
                    "            baseState = baseState,\n" +
                    "        ) { state ->\n" +
                    "            ${moduleName}ScreenContent(state, onUiEvent, onActionEvent)\n" +
                    "        }\n" +
                    "    }\n" +
                    "}\n" +
                    "\n" +
                    "\n" +
                    "@Composable\n" +
                    "private fun ${moduleName}ScreenContent(\n" +
                    "    state: ${moduleName}State,\n" +
                    "    onUiEvent: (${moduleName}Event.UI) -> Unit,\n" +
                    "    onActionEvent: (${moduleName}Event.Action) -> Unit\n" +
                    ") {\n" +
                    "\n" +
                    "}\n" +
                    "\n"
        )
    }
}