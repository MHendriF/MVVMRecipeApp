package com.mhendrif.mvvmrecipeapp.presentation.ui.recipe_list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.mhendrif.mvvmrecipeapp.repository.RecipeRepository
import javax.inject.Named

class RecipeListViewModel
@ViewModelInject
constructor(
    private val repository: RecipeRepository,
    @Named("auth_token") private val token: String
    ) : ViewModel() {

    init {
        println("VIEWMODEL: $repository")
        println("VIEWMODEL: $token")
    }

    fun getRepo() = repository

    fun getToken() = token

}