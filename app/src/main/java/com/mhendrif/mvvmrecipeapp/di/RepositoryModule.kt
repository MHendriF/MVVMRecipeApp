package com.mhendrif.mvvmrecipeapp.di

import com.mhendrif.mvvmrecipeapp.network.RecipeService
import com.mhendrif.mvvmrecipeapp.network.model.RecipeDtoMapper
import com.mhendrif.mvvmrecipeapp.repository.RecipeRepository
import com.mhendrif.mvvmrecipeapp.repository.RecipeRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRecipeRepository(
        recipeService: RecipeService,
        recipeMapper: RecipeDtoMapper,
    ): RecipeRepository {
        return RecipeRepositoryImpl(
            recipeService = recipeService,
            mapper = recipeMapper
        )
    }
}