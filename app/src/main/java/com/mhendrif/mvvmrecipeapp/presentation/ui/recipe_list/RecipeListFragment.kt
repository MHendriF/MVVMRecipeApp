package com.mhendrif.mvvmrecipeapp.presentation.ui.recipe_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.*
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.mhendrif.mvvmrecipeapp.BaseApplication
import com.mhendrif.mvvmrecipeapp.presentation.components.*
import com.mhendrif.mvvmrecipeapp.presentation.components.util.SnackbarController
import com.mhendrif.mvvmrecipeapp.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RecipeListFragment : Fragment() {

    @Inject
    lateinit var application: BaseApplication

    private val snackbarController = SnackbarController(lifecycleScope)

    private val viewModel: RecipeListViewModel by viewModels()

    @ExperimentalMaterialApi
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setContent {

                AppTheme(darkTheme = application.isDark.value) {
                    val recipes = viewModel.recipes.value
                    val query = viewModel.query.value
                    val selectedCategory = viewModel.selectedCategory.value
                    val categoryScrollPosition = viewModel.categoryScrollPosition
                    val loading = viewModel.loading.value
                    val page = viewModel.page.value
                    val scaffoldState = rememberScaffoldState()

                    Scaffold(
                            topBar = {
                                SearchAppBar(
                                        query = query,
                                        onQueryChanged = viewModel::onQueryChanged,
                                        onExecuteSearch = {
                                            if (viewModel.selectedCategory.value?.value == "Milk") {
                                                snackbarController.getScope().launch {
                                                    snackbarController.showSnackbar(
                                                            scaffoldState = scaffoldState,
                                                            message = "Invalid category: Milk",
                                                            actionLabel = "Hide"
                                                    )
                                                }
                                            } else run {
                                                viewModel.onTriggerEvent(RecipeListEvent.NewSearchEvent)
                                            }
                                        },
                                        categories = getAllFoodCategories(),
                                        selectedCategory = selectedCategory,
                                        onSelectedCategoryChanged = viewModel::onSelectedCategoryChanged,
                                        scrollPosition = categoryScrollPosition,
                                        onChangeScrollPosition = viewModel::onChangeCategoryScrollPosition,
                                        onToggleTheme = {
                                            application.toggleLightTheme()
                                        }
                                )
                            },
                            scaffoldState = scaffoldState,
                            snackbarHost = {
                                scaffoldState.snackbarHostState
                            }
                    ) {
                        RecipeList(
                                loading = loading,
                                recipes = recipes,
                                onChangeRecipeScrollPosition = { viewModel::onChangeRecipeScrollPosition },
                                page = page,
                                onNextPage = { viewModel.onTriggerEvent(RecipeListEvent.NextPageEvent) },
                                scaffoldState = scaffoldState,
                                snackbarController = snackbarController,
                                navController = findNavController()
                        )
                    }
                }
            }
        }
    }
}