package com.example.appstockcontrol_grupo_07.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.appstockcontrol_grupo_07.data.local.user.UserDao
import com.example.appstockcontrol_grupo_07.data.local.user.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UserEntity::class],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "ui_navegacion_v3.db"

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                println("DEBUG: AppDatabase - Creando nueva instancia de BD")

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            println("DEBUG: AppDatabase - onCreate() ejecutado")

                            // Usamos la instancia que acabamos de construir
                            val database = INSTANCE
                            if (database != null) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    initializeData(database)
                                }
                            } else {
                                println("DEBUG: AppDatabase - INSTANCE es nula en onCreate")
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                println("DEBUG: AppDatabase - Instancia creada exitosamente")
                instance
            }
        }

        private suspend fun initializeData(database: AppDatabase) {
            try {
                val dao = database.userDao()
                println("DEBUG: AppDatabase - DAO obtenido")

                val count = dao.count()
                println("DEBUG: AppDatabase - Conteo inicial: $count")

                if (count == 0) {
                    println("DEBUG: AppDatabase - Insertando usuarios de prueba...")

                    val users = listOf(
                        UserEntity(
                            name = "Adrian",
                            email = "ad.rivera@duocuc.cl",
                            phone = "+56936778106",
                            password = "Admin_123",
                            address = "Duoc UC",
                            isAdmin = true
                        ),
                        UserEntity(
                            name = "Freddy Rivera",
                            email = "fre.rivera@duocuc.cl",
                            phone = "+56930303816",
                            password = "Usuario_123",
                            address = "Av. Principal 123",
                            isAdmin = false
                        )
                    )

                    users.forEach { user ->
                        val id = dao.insert(user)
                        println("DEBUG: AppDatabase - Insertado: ${user.email} (ID: $id), isAdmin: ${user.isAdmin}")
                    }

                    // Verificar inserción
                    val allUsers = dao.getAll()
                    println("DEBUG: AppDatabase - Verificación - Total usuarios: ${allUsers.size}")
                    allUsers.forEach {
                        println("DEBUG: AppDatabase - En BD: ${it.email} | isAdmin: ${it.isAdmin}")
                    }
                } else {
                    println("DEBUG: AppDatabase - Ya existen usuarios, mostrando info:")
                    val allUsers = dao.getAll()
                    allUsers.forEach {
                        println("DEBUG: AppDatabase - Existente: ${it.email} | isAdmin: ${it.isAdmin}")
                    }
                }
            } catch (e: Exception) {
                println("ERROR: AppDatabase - Error en initializeData: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}