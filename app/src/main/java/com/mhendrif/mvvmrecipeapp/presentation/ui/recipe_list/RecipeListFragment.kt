package com.mhendrif.mvvmrecipeapp.presentation.ui.recipe_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
                        Box(
                                modifier = Modifier.background(color = MaterialTheme.colors.background)
                        ) {
                            if (loading && recipes.isEmpty()) {
                                LoadingRecipeListShimmer(imageHeight = 250.dp)
                            } else {
                                LazyColumn {
                                    itemsIndexed(
                                            items = recipes
                                    ) { index, recipe ->
                                        viewModel.onChangeRecipeScrollPosition(index)
                                        if (index + 1 >= (page * PAGE_SIZE) && !loading) {
                                            viewModel.onTriggerEvent(RecipeListEvent.NextPageEvent)
                                        }
                                        RecipeCard(recipe = recipe, onClick = {})
                                    }
                                }
                            }
                            CircularIndeterminateProgressBar(isDisplayed = loading, verticalBias = 0.3f)
                            DefaultSnackbar(
                                    snackbarHostState = scaffoldState.snackbarHostState,
                                    onDismiss = {
                                        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                                    },
                                    modifier = Modifier.align(Alignment.BottomCenter)
                            )
                        }
                    }
                }
            }
        }
    }
}