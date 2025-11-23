package com.example.grocerywatch.di

import com.example.grocerywatch.data.db.GroceryDatabase
import com.example.grocerywatch.repository.GroceryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideGroceryRepository(database: GroceryDatabase): GroceryRepository {
        return GroceryRepository(
            listDao = database.groceryListDao(),
            itemDao = database.groceryItemDao(),
            priceHistoryDao = database.priceHistoryDao()
        )
    }
}
