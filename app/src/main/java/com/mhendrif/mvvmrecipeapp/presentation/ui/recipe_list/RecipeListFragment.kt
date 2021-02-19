package com.mhendrif.mvvmrecipeapp.presentation.ui.recipe_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mhendrif.mvvmrecipeapp.BaseApplication
import com.mhendrif.mvvmrecipeapp.presentation.components.*
import com.mhendrif.mvvmrecipeapp.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@AndroidEntryPoint
class RecipeListFragment : Fragment() {

    @Inject
    lateinit var application: BaseApplication

    private val viewModel: RecipeListViewModel by viewModels()

    @ExperimentalCoroutinesApi
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

                    Scaffold(
                            topBar = {
                                SearchAppBar(
                                        query = query,
                                        onQueryChanged = viewModel::onQueryChanged,
                                        onExecuteSearch = viewModel::newSearch,
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
                            bottomBar = {
                                MyBottomBar()
                            },
                            drawerContent = {
                                MyDrawer()
                            }
                    ) {
                        Box(
                            modifier = Modifier.background(color = MaterialTheme.colors.background)
                        ) {
                            if (loading) {
                                LoadingRecipeListShimmer(imageHeight = 250.dp)
                            } else {
                                LazyColumn {
                                    itemsIndexed(
                                            items = recipes
                                    ) { _, recipe ->
                                        RecipeCard(recipe = recipe, onClick = {})
                                    }
                                }
                            }
                            CircularIndeterminateProgressBar(isDisplayed = loading, verticalBias = 0.3f)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyBottomBar(){
    BottomNavigation(
            elevation = 12.dp
    ) {
        BottomNavigationItem(
                icon = { Icon(Icons.Default.BrokenImage) },
                selected = false,
                onClick = {}
        )
        BottomNavigationItem(
                icon = { Icon(Icons.Default.Search) },
                selected = true,
                onClick = {}
        )
        BottomNavigationItem(
                icon = { Icon(Icons.Default.AccountBalanceWallet) },
                selected = false,
                onClick = {}
        )
    }
}


@Composable
fun MyDrawer(){
    Column() {
        Text(text = "Item1")
        Text(text = "Item2")
        Text(text = "Item3")
        Text(text = "Item4")
        Text(text = "Item5")
    }
}