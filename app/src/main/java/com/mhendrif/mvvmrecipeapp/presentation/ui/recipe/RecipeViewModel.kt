package com.mhendrif.mvvmrecipeapp.presentation.ui.recipe

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhendrif.mvvmrecipeapp.domain.model.Recipe
import com.mhendrif.mvvmrecipeapp.presentation.ui.recipe.RecipeEvent.GetRecipeEvent
import com.mhendrif.mvvmrecipeapp.repository.RecipeRepository
import com.mhendrif.mvvmrecipeapp.util.TAG
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Named

const val STATE_KEY_RECIPE = "recipe.state.recipe.key"

class RecipeViewModel
@ViewModelInject
constructor(
        private val recipeRepository: RecipeRepository,
        @Named("auth_token") private val token: String,
        @Assisted private val state: SavedStateHandle,
) : ViewModel() {
    val recipe: MutableState<Recipe?> = mutableStateOf(null)
    val loading = mutableStateOf(false)

    init {
        state.get<Int>(STATE_KEY_RECIPE)?.let { recipeId ->
            onTriggerEvent(GetRecipeEvent(recipeId))
        }
    }

    fun onTriggerEvent(event: GetRecipeEvent) {
        viewModelScope.launch {
            try {
                when(event) {
                    is GetRecipeEvent -> {
                        if (recipe.value == null) {
                            getRecipe(event.id)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "launchJob: Exception: ${e}, ${e.cause}")
                e.printStackTrace()
            }
        }

    }

    private suspend fun getRecipe(id: Int) {
        loading.value = true
        delay(1000)
        val recipe = recipeRepository.get(token = token, id = id)
        this.recipe.value = recipe
        
        state.set(STATE_KEY_RECIPE, recipe.id)
        loading.value = false
    }
}