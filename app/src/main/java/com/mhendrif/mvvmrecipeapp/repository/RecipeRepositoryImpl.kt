package com.mhendrif.mvvmrecipeapp.repository

import com.mhendrif.mvvmrecipeapp.domain.model.Recipe
import com.mhendrif.mvvmrecipeapp.network.RetrofitService
import com.mhendrif.mvvmrecipeapp.network.model.RecipeDtoMapper

class RecipeRepositoryImpl(
    private val recipeService: RetrofitService,
    private val mapper: RecipeDtoMapper,
): RecipeRepository {

    override suspend fun search(token: String, page: Int, query: String): List<Recipe> {
        return mapper.fromEntityList(recipeService.search(token = token, page = page, query = query).recipes)
    }

    override suspend fun get(token: String, id: Int): Recipe {
        return mapper.mapToDomainModel(recipeService.get(token = token, id))
    }
}