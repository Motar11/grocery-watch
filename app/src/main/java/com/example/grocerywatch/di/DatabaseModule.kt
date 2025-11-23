package com.example.grocerywatch.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.grocerywatch.data.db.GroceryDatabase
import com.example.grocerywatch.util.SampleData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob())

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        scope: CoroutineScope
    ): GroceryDatabase {
        lateinit var database: GroceryDatabase
        val callback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                scope.launch {
                    populate(database)
                }
            }
        }

        database = Room.databaseBuilder(
            context,
            GroceryDatabase::class.java,
            "grocery_watch.db"
        )
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()
        return database
    }

    private suspend fun populate(database: GroceryDatabase) {
        val listDao = database.groceryListDao()
        val itemDao = database.groceryItemDao()
        val historyDao = database.priceHistoryDao()

        SampleData.lists.forEach { listDao.insert(it) }
        SampleData.items.forEach { itemDao.insert(it) }
        SampleData.history.forEach { historyDao.insert(it) }
    }
}
