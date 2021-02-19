package com.mhendrif.mvvmrecipeapp.presentation.ui.recipe_list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.mhendrif.mvvmrecipeapp.BaseApplication
import com.mhendrif.mvvmrecipeapp.presentation.components.*
import com.mhendrif.mvvmrecipeapp.presentation.theme.AppTheme
import com.mhendrif.mvvmrecipeapp.util.TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RecipeListFragment : Fragment() {

    @Inject
    lateinit var application: BaseApplication

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

//                    val showSnackbar = remember{ mutableStateOf(false)}
//                    Column{
//                        Button(
//                            onClick = {
//                                showSnackbar.value = true
//                            }
//                        ) {
//                            Text("Show snackbar")
//                        }
//                        SimpleSnackbarDemo(
//                            show = showSnackbar.value,
//                            onHideSnackbar = {
//                                showSnackbar.value = false
//                            }
//                        )
//                    }

                    val snackbarHostState = remember { SnackbarHostState() }
                    Column {
                        Button(
                                onClick = {
                                    lifecycleScope.launch {
                                        val time = System.currentTimeMillis()
                                        Log.d(TAG, "showing snackbar")
                                        snackbarHostState.showSnackbar(
                                                message = "Hey look a snackbar",
                                                actionLabel = "Hide",
                                                duration = SnackbarDuration.Short
                                        )
                                        Log.d(TAG, "done ${System.currentTimeMillis() - time}") // <-- Never called
                                    }
                                }
                        ) {
                            Text("Show snackbar")
                        }
                        DecoupledSnackbarDemo(snackbarHostState = snackbarHostState)
                    }

//                    Scaffold(
//                            topBar = {
//                                SearchAppBar(
//                                        query = query,
//                                        onQueryChanged = viewModel::onQueryChanged,
//                                        onExecuteSearch = viewModel::newSearch,
//                                        categories = getAllFoodCategories(),
//                                        selectedCategory = selectedCategory,
//                                        onSelectedCategoryChanged = viewModel::onSelectedCategoryChanged,
//                                        scrollPosition = categoryScrollPosition,
//                                        onChangeScrollPosition = viewModel::onChangeCategoryScrollPosition,
//                                        onToggleTheme = {
//                                            application.toggleLightTheme()
//                                        }
//                                )
//                            }
//                    ) {
//                        Box(
//                                modifier = Modifier.background(color = MaterialTheme.colors.background)
//                        ) {
//                            if (loading) {
//                                LoadingRecipeListShimmer(imageHeight = 250.dp)
//                            } else {
//                                LazyColumn {
//                                    itemsIndexed(
//                                            items = recipes
//                                    ) { _, recipe ->
//                                        RecipeCard(recipe = recipe, onClick = {})
//                                    }
//                                }
//                            }
//                            CircularIndeterminateProgressBar(isDisplayed = loading, verticalBias = 0.3f)
//                        }
//                    }
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun DecoupledSnackbarDemo(
        snackbarHostState: SnackbarHostState
) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val snackbar = createRef()
        SnackbarHost(
                modifier = Modifier.constrainAs(snackbar) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                hostState = snackbarHostState,
                snackbar = {
                    Snackbar(
                            action = {
                                TextButton(
                                        onClick = {
                                            snackbarHostState.currentSnackbarData?.dismiss()
                                        }
                                ) {
                                    Text(
                                            text = snackbarHostState.currentSnackbarData?.actionLabel
                                                    ?: "",
                                            style = TextStyle(color = Color.White)
                                    )
                                }
                            }
                    ) {
                        Text(snackbarHostState.currentSnackbarData?.message ?: "")
                    }
                }
        )
    }
}

@Composable
fun SimpleSnackbarDemo(
        show: Boolean,
        onHideSnackbar: () -> Unit,
) {
    ConstraintLayout(
            modifier = Modifier.fillMaxSize()
    ) {
        val snackbar = createRef()
        if (show) {
            Snackbar(
                    modifier = Modifier.constrainAs(snackbar) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                    action = {
                        Text(
                                "Hide",
                                modifier = Modifier.clickable(onClick = onHideSnackbar),
                                style = MaterialTheme.typography.h5
                        )
                    },
            ) {
                Text(text = "Hey look a snackbar")
            }
        }
    }
}
