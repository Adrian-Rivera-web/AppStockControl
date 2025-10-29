package com.example.appstockcontrol_grupo_07.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.appstockcontrol_grupo_07.data.local.user.UserDao
import com.example.appstockcontrol_grupo_07.data.local.user.UserEntity
import com.example.appstockcontrol_grupo_07.data.local.producto.ProductoDao
import com.example.appstockcontrol_grupo_07.data.local.producto.ProductoEntity
import com.example.appstockcontrol_grupo_07.data.local.categoria.CategoriaDao
import com.example.appstockcontrol_grupo_07.data.local.categoria.CategoriaEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        ProductoEntity::class,
        CategoriaEntity::class  // Agregar la entidad Categoria
    ],
    version = 5,  // Incrementar versión porque agregamos nueva tabla
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun productoDao(): ProductoDao
    abstract fun categoriaDao(): CategoriaDao  // Agregar el DAO de Categoria

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "ui_navegacion_v5.db"  // Incrementar versión

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
                    .addMigrations(MIGRATION_4_5)  // Agregar migración
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                println("DEBUG: AppDatabase - Instancia creada exitosamente")
                instance
            }
        }

        // Migración de versión 4 a 5 - agregar tabla categorias
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE categorias (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nombre TEXT NOT NULL,
                        descripcion TEXT NOT NULL,
                        fecha_creacion INTEGER NOT NULL,
                        activa INTEGER NOT NULL
                    )
                    """
                )

                // Insertar algunas categorías por defecto
                database.execSQL(
                    """
                    INSERT INTO categorias (nombre, descripcion, fecha_creacion, activa) 
                    VALUES 
                    ('Electrónicos', 'Productos electrónicos y dispositivos', ${System.currentTimeMillis()}, 1),
                    ('Ropa', 'Prendas de vestir y accesorios', ${System.currentTimeMillis()}, 1),
                    ('Hogar', 'Artículos para el hogar', ${System.currentTimeMillis()}, 1),
                    ('Deportes', 'Equipos y artículos deportivos', ${System.currentTimeMillis()}, 1),
                    ('Alimentos', 'Productos alimenticios y bebidas', ${System.currentTimeMillis()}, 1),
                    ('Libros', 'Libros y material educativo', ${System.currentTimeMillis()}, 1)
                    """
                )
            }
        }

        private suspend fun initializeData(database: AppDatabase) {
            try {
                val userDao = database.userDao()
                val productoDao = database.productoDao()
                val categoriaDao = database.categoriaDao()  // Agregar categoriaDao

                println("DEBUG: AppDatabase - DAOs obtenidos")

                val userCount = userDao.count()
                val productoCount = productoDao.count()
                val categoriaCount = categoriaDao.count()

                println("DEBUG: AppDatabase - Conteo inicial - Usuarios: $userCount, Productos: $productoCount, Categorías: $categoriaCount")

                // Inicializar usuarios si no existen
                if (userCount == 0) {
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
                        val id = userDao.insert(user)
                        println("DEBUG: AppDatabase - Insertado usuario: ${user.email} (ID: $id), isAdmin: ${user.isAdmin}")
                    }
                }

                // ✅ INICIALIZAR PRODUCTOS SI NO EXISTEN
                if (productoCount == 0) {
                    println("DEBUG: AppDatabase - Insertando productos de prueba...")

                    val productos = listOf(
                        ProductoEntity(
                            nombre = "Laptop Gamer ASUS",
                            descripcion = "Laptop para gaming de alta gama con RTX 4060",
                            precio = 1299.99,
                            stock = 8,
                            categoria = "Electrónicos",
                            proveedor = "ASUS Chile"
                        ),
                        ProductoEntity(
                            nombre = "Mouse Inalámbrico Logitech",
                            descripcion = "Mouse ergonómico inalámbrico con sensor óptico",
                            precio = 35.50,
                            stock = 25,
                            categoria = "Accesorios",
                            proveedor = "Logitech"
                        ),
                        ProductoEntity(
                            nombre = "Teclado Mecánico RGB",
                            descripcion = "Teclado mecánico RGB con switches azules",
                            precio = 89.99,
                            stock = 15,
                            categoria = "Accesorios",
                            proveedor = "Redragon"
                        ),
                        ProductoEntity(
                            nombre = "Monitor 24\" Samsung",
                            descripcion = "Monitor Full HD 144Hz para gaming",
                            precio = 249.99,
                            stock = 12,
                            categoria = "Electrónicos",
                            proveedor = "Samsung"
                        ),
                        ProductoEntity(
                            nombre = "Auriculares Bluetooth Sony",
                            descripcion = "Auriculares inalámbricos con cancelación de ruido",
                            precio = 199.99,
                            stock = 18,
                            categoria = "Audio",
                            proveedor = "Sony"
                        ),
                        ProductoEntity(
                            nombre = "Tablet Android 10\"",
                            descripcion = "Tablet Android con 64GB de almacenamiento",
                            precio = 299.99,
                            stock = 10,
                            categoria = "Electrónicos",
                            proveedor = "Xiaomi"
                        ),
                        ProductoEntity(
                            nombre = "Impresora Multifuncional",
                            descripcion = "Impresora láser WiFi con escáner y copiadora",
                            precio = 179.99,
                            stock = 6,
                            categoria = "Oficina",
                            proveedor = "HP"
                        ),
                        ProductoEntity(
                            nombre = "Disco Duro Externo 1TB",
                            descripcion = "Disco duro externo USB 3.0 portátil",
                            precio = 59.99,
                            stock = 30,
                            categoria = "Almacenamiento",
                            proveedor = "Western Digital"
                        )
                    )

                    productos.forEach { producto ->
                        val id = productoDao.insert(producto)
                        println("DEBUG: AppDatabase - Insertado producto: ${producto.nombre} (ID: $id)")
                    }
                }

                // ✅ VERIFICACIÓN CORREGIDA - Usar .first() para obtener la lista del Flow
                val allUsers = userDao.getAll()
                val allProductos = productoDao.getAll().first()
                val allCategorias = categoriaDao.obtenerTodas().first()  // Agregar categorías

                println("DEBUG: AppDatabase - Verificación - Total usuarios: ${allUsers.size}")
                println("DEBUG: AppDatabase - Verificación - Total productos: ${allProductos.size}")
                println("DEBUG: AppDatabase - Verificación - Total categorías: ${allCategorias.size}")

                // ✅ CORREGIDO: Ahora allProductos es List<ProductoEntity>, podemos usar forEach
                allProductos.forEach { producto ->
                    println("DEBUG: AppDatabase - Producto: ${producto.nombre} | Precio: $${producto.precio} | Stock: ${producto.stock}")
                }

                // Mostrar categorías
                allCategorias.forEach { categoria ->
                    println("DEBUG: AppDatabase - Categoría: ${categoria.nombre} | Descripción: ${categoria.descripcion}")
                }

            } catch (e: Exception) {
                println("ERROR: AppDatabase - Error en initializeData: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}