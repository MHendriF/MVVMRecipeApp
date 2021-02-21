package com.mhendrif.mvvmrecipeapp.presentation.ui.recipe_list

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhendrif.mvvmrecipeapp.domain.model.Recipe
import com.mhendrif.mvvmrecipeapp.presentation.ui.recipe_list.RecipeListEvent.NewSearchEvent
import com.mhendrif.mvvmrecipeapp.presentation.ui.recipe_list.RecipeListEvent.NextPageEvent
import com.mhendrif.mvvmrecipeapp.repository.RecipeRepository
import com.mhendrif.mvvmrecipeapp.util.TAG
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Named

const val PAGE_SIZE = 30

class RecipeListViewModel
@ViewModelInject
constructor(
    private val repository: RecipeRepository,
    @Named("auth_token") private val token: String
    ) : ViewModel() {

    val recipes: MutableState<List<Recipe>> = mutableStateOf(listOf())
    val query = mutableStateOf("")
    val selectedCategory: MutableState<FoodCategory?> = mutableStateOf(null)
    var categoryScrollPosition: Float = 0f
    val loading = mutableStateOf(false)
    val page = mutableStateOf(1)
    private var recipeListScrollPosition = 0

    init {
        onTriggerEvent(NewSearchEvent)
    }

    fun onTriggerEvent(event: RecipeListEvent) {
        viewModelScope.launch {
            try {
                when(event) {
                    is NewSearchEvent -> {
                        newSearch()
                    }
                    is NextPageEvent -> {
                        nextPage()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "onTriggerEvent: Exception: $e, ${e.cause}")
            }
        }
    }

    // Use case 1
    private suspend fun newSearch() {
        loading.value = true
        resetSearchState()
        delay(2000)

        val result = repository.search(
            token = token,
            page = 1,
            query = query.value
        )
        recipes.value = result
        loading.value = false
    }

    // Use case 2
    private suspend fun nextPage() {
        if (recipeListScrollPosition + 1 >= (page.value * PAGE_SIZE)) {
            loading.value = true
            incrementPage()
            Log.d(TAG, "nextPage: triggered: ${page.value}")
            delay(1000)

            if (page.value > 1) {
                val result = repository.search(
                        token = token,
                        page = page.value,
                        query = query.value
                )
                appendRecipes(result)
                Log.d(TAG, "search: appending")
            }
            loading.value = false
        }
    }

    /**
     * Append new recipes to the current list of recipes
     */
    private fun appendRecipes(recipes: List<Recipe>){
        val current = ArrayList(this.recipes.value)
        current.addAll(recipes)
        this.recipes.value = current
    }

    private fun incrementPage() {
        page.value = page.value + 1
    }

    fun onChangeRecipeScrollPosition(position: Int) {
        recipeListScrollPosition = position
    }

    fun onQueryChanged(query: String) {
        this.query.value = query
    }

    fun onSelectedCategoryChanged(category: String){
        val newCategory = getFoodCategory(category)
        selectedCategory.value = newCategory
        onQueryChanged(category)
    }

    fun onChangeCategoryScrollPosition(position: Float){
        categoryScrollPosition = position
    }

    private fun resetSearchState() {
        recipes.value = listOf()
        page.value = 1
        onChangeRecipeScrollPosition(0)
        if (selectedCategory.value?.value == query.value)
            clearSelectedCategory()
    }

    private fun clearSelectedCategory() {
        selectedCategory.value = null
    }
}