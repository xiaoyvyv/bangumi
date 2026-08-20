import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class GenerateMviTask : DefaultTask() {

    @get:Input
    abstract val moduleName: Property<String>

    @get:Input
    abstract val namespace: Property<String>

    @get:OutputDirectory
    abstract val namespaceDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val mName = moduleName.get()
        val ns = namespace.get()
        val nDir = namespaceDir.get().asFile

        if (nDir.exists() && nDir.listFiles()?.isNotEmpty() == true) {
            throw GradleException("Please clean dir：$nDir")
        }

        if (!nDir.exists() && !nDir.mkdirs()) {
            throw GradleException("Failed to create directory: $nDir")
        }

        val businessDir = File(nDir, "business")
        if (!businessDir.exists() && !businessDir.mkdirs()) {
            throw GradleException("Failed to create directory: $businessDir")
        }

        val classFileEvent = File(businessDir, "${mName}Event.kt")
        val classFileSideEffect = File(businessDir, "${mName}SideEffect.kt")
        val classFileState = File(businessDir, "${mName}State.kt")
        val classFileViewModel = File(businessDir, "${mName}ViewModel.kt")
        val classFileNavigator = File(nDir, "${mName}Navigator.kt")
        val classFileScreen = File(nDir, "${mName}Screen.kt")

        classFileEvent.writeText(
            "package ${ns}.business\n" +
                    "\n" +
                    "import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen\n" +
                    "\n" +
                    "/**\n" +
                    " * [${mName}Event]\n" +
                    " *\n" +
                    " * @author why\n" +
                    " * @since 2025/1/12\n" +
                    " */\n" +
                    "sealed class ${mName}Event {\n" +
                    "    sealed class UI : ${mName}Event() {\n" +
                    "        data object OnNavUp : UI()\n" +
                    "        data class OnNavScreen(val screen: Screen) : UI()\n" +
                    "    }\n" +
                    "\n" +
                    "    sealed class Action : ${mName}Event() {\n" +
                    "        data class OnRefresh(val loading : Boolean) : Action()\n" +
                    "    }\n" +
                    "}"
        )

        classFileSideEffect.writeText(
            "package ${ns}.business\n" +
                    "\n" +
                    "/**\n" +
                    " * [${mName}SideEffect]\n" +
                    " *\n" +
                    " * @author why\n" +
                    " * @since 2025/1/12\n" +
                    " */\n" +
                    "sealed class ${mName}SideEffect {\n" +
                    "\n" +
                    "}"
        )

        classFileState.writeText(
            "package ${ns}.business\n" +
                    "\n" +
                    "import androidx.compose.runtime.Immutable\n" +
                    "\n" +
                    "/**\n" +
                    " * [${mName}State]\n" +
                    " *\n" +
                    " * @author why\n" +
                    " * @since 2025/1/12\n" +
                    " */\n" +
                    "@Immutable\n" +
                    "data class ${mName}State(\n" +
                    "    val id: Long = 0\n" +
                    ")\n"
        )

        classFileNavigator.writeText(
            "package ${ns}\n" +
                    "\n" +
                    "import ${ns}.business.${mName}ViewModel\n" +
                    "import org.koin.core.module.dsl.viewModelOf\n" +
                    "import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy\n" +
                    "import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen\n" +
                    "import com.xiaoyv.bangumi.shared.ui.component.navigation.navScope\n" +
                    "import com.xiaoyv.bangumi.shared.ui.component.navigation.navigator\n" +
                    "import org.koin.compose.viewmodel.koinViewModel\n" +
                    "import org.koin.core.parameter.parametersOf\n" +
                    "import org.koin.dsl.module\n" +
                    "import org.koin.dsl.navigation3.navigation\n" +
                    "\n" +
                    "val ${mName.replaceFirstChar { it.toString().lowercase() }}Module = module {\n" +
                    "    viewModelOf(::${mName}ViewModel)\n" +
                    "\n" +
                    "    navScope {\n" +
                    "        navigation<Screen.${mName}> { key ->\n" +
                    "            ${mName}Route(\n" +
                    "                viewModel = koinViewModel { parametersOf(key) },\n" +
                    "                onNavScreen = { navigator.navigate(it) },\n" +
                    "                onNavUp = { navigator.goBack() }\n" +
                    "            )\n" +
                    "        }\n" +
                    "    }\n" +
                    "}\n"
        )

        classFileViewModel.writeText(
            "package ${ns}.business\n" +
                    "\n" +
                    "import androidx.lifecycle.SavedStateHandle\n" +
                    "import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel\n" +
                    "\n" +
                    "/**\n" +
                    " * [${mName}ViewModel]\n" +
                    " *\n" +
                    " * @author why\n" +
                    " * @since 2025/1/12\n" +
                    " */\n" +
                    "class ${mName}ViewModel : BaseViewModel<${mName}State, ${mName}SideEffect, ${mName}Event.Action>() {\n" +
                    "\n" +
                    "    override fun createInitialState() = ${mName}State()\n" +
                    "\n" +
                    "    override fun onEvent(event: ${mName}Event.Action) {\n" +
                    "        when (event) {\n" +
                    "            is ${mName}Event.Action.OnRefresh -> refresh(loading = event.loading)\n" +
                    "        }\n" +
                    "    }\n" +
                    "\n" +
                    "}"
        )

        classFileScreen.writeText(
            "package ${ns}\n" +
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
                    "import com.xiaoyv.bangumi.core_resource.resources.app_name\n" +
                    "import ${ns}.business.${mName}Event\n" +
                    "import ${ns}.business.${mName}State\n" +
                    "import ${ns}.business.${mName}ViewModel\n" +
                    "import com.xiaoyv.bangumi.shared.core.mvi.UiState\n" +
                    "import org.orbitmvi.orbit.compose.collectAsState\n" +
                    "import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar\n" +
                    "import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout\n" +
                    "import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen\n" +
                    "import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect\n" +
                    "import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn\n" +
                    "import org.jetbrains.compose.resources.stringResource\n" +
                    "import androidx.compose.ui.tooling.preview.Preview\n" +
                    "import org.koin.compose.viewmodel.koinViewModel\n" +
                    "\n" +
                    "@Composable\n" +
                    "fun ${mName}Route(\n" +
                    "    viewModel: ${mName}ViewModel ,\n" +
                    "    onNavUp: () -> Unit,\n" +
                    "    onNavScreen: (Screen) -> Unit,\n" +
                    ") {\n" +
                    "    val uiState by viewModel.collectAsState()\n" +
                    "\n" +
                    "    viewModel.collectBaseSideEffect {\n" +
                    "\n" +
                    "    }\n" +
                    "\n" +
                    "    ${mName}Screen(\n" +
                    "        uiState = uiState,\n" +
                    "        onActionEvent = viewModel::onEvent,\n" +
                    "        onUiEvent = {\n" +
                    "            when (it) {\n" +
                    "                is ${mName}Event.UI.OnNavUp -> onNavUp()\n" +
                    "                is ${mName}Event.UI.OnNavScreen -> onNavScreen(it.screen)\n" +
                    "            }\n" +
                    "        },\n" +
                    "    )\n" +
                    "}\n" +
                    "\n" +
                    "@Composable\n" +
                    "private fun ${mName}Screen(\n" +
                    "    uiState: UiState<${mName}State>,\n" +
                    "    onUiEvent: (${mName}Event.UI) -> Unit,\n" +
                    "    onActionEvent: (${mName}Event.Action) -> Unit\n" +
                    ") {\n" +
                    "\n" +
                    "    Scaffold(\n" +
                    "        modifier = Modifier.fillMaxSize(),\n" +
                    "        topBar = {\n" +
                    "            BgmTopAppBar(\n" +
                    "                title = stringResource(Res.string.app_name),\n" +
                    "                onNavigationClick = { onUiEvent(${mName}Event.UI.OnNavUp) }\n" +
                    "            )\n" +
                    "        }\n" +
                    "    ) {\n" +
                    "        StateLayout(\n" +
                    "            modifier = Modifier\n" +
                    "                .fillMaxSize()\n" +
                    "                .padding(it),\n" +
                    "            onRefresh = { loading -> onActionEvent(${mName}Event.Action.OnRefresh(loading)) },\n" +
                    "            uiState = uiState,\n" +
                    "        ) { state ->\n" +
                    "            ${mName}ScreenContent(state, onUiEvent, onActionEvent)\n" +
                    "        }\n" +
                    "    }\n" +
                    "}\n" +
                    "\n" +
                    "\n" +
                    "@Composable\n" +
                    "private fun ${mName}ScreenContent(\n" +
                    "    state: ${mName}State,\n" +
                    "    onUiEvent: (${mName}Event.UI) -> Unit,\n" +
                    "    onActionEvent: (${mName}Event.Action) -> Unit\n" +
                    ") {\n" +
                    "\n" +
                    "}\n" +
                    "\n" +
                    "@Composable\n" +
                    "@Preview\n" +
                    "private fun Preview${mName}Screen() {\n" +
                    "    PreviewColumn(modifier = Modifier.fillMaxSize()) {\n" +
                    "        ${mName}Screen(\n" +
                    "            uiState = UiState(\n" +
                    "                ${mName}State()\n" +
                    "            ),\n" +
                    "            onUiEvent = {},\n" +
                    "            onActionEvent = {}\n" +
                    "        )\n" +
                    "    }\n" +
                    "}\n"
        )
    }
}

