package com.motar11.grocerywatch.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.motar11.grocerywatch.data.local.dao.CategoryDao
import com.motar11.grocerywatch.data.local.dao.GroceryItemDao
import com.motar11.grocerywatch.data.local.dao.GroceryListDao
import com.motar11.grocerywatch.data.local.dao.PriceHistoryDao
import com.motar11.grocerywatch.data.local.database.GroceryDatabase
import com.motar11.grocerywatch.data.repository.GroceryRepository
import com.motar11.grocerywatch.data.repository.GroceryRepositoryImpl
import com.motar11.grocerywatch.util.SampleDataSeeder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        callback: SeederCallback
    ): GroceryDatabase = Room.databaseBuilder(
        context,
        GroceryDatabase::class.java,
        "grocery_watch.db"
    )
        .addCallback(callback)
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideSeederCallback(
        seeder: SampleDataSeeder,
        @ApplicationScope scope: CoroutineScope
    ): SeederCallback = SeederCallback(seeder, scope)

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Provides
    fun provideListDao(database: GroceryDatabase): GroceryListDao = database.groceryListDao()

    @Provides
    fun provideItemDao(database: GroceryDatabase): GroceryItemDao = database.groceryItemDao()

    @Provides
    fun providePriceDao(database: GroceryDatabase): PriceHistoryDao = database.priceHistoryDao()

    @Provides
    fun provideCategoryDao(database: GroceryDatabase): CategoryDao = database.categoryDao()

    @Provides
    @Singleton
    fun provideRepository(
        listDao: GroceryListDao,
        itemDao: GroceryItemDao,
        priceDao: PriceHistoryDao,
        categoryDao: CategoryDao
    ): GroceryRepository = GroceryRepositoryImpl(listDao, itemDao, priceDao, categoryDao)
}

class SeederCallback(
    private val seeder: SampleDataSeeder,
    private val scope: CoroutineScope
) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        scope.launch { seeder.seed() }
    }
}
