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
        CategoriaEntity::class
    ],
    version = 6,  // ✅ Incrementar versión a 6
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun productoDao(): ProductoDao
    abstract fun categoriaDao(): CategoriaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "ui_navegacion_v6.db"  // ✅ Cambiar nombre

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
                    .addMigrations(MIGRATION_5_6)  // ✅ Cambiar a nueva migración
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                println("DEBUG: AppDatabase - Instancia creada exitosamente")
                instance
            }
        }

        // ✅ NUEVA Migración de versión 5 a 6
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Esta migración puede estar vacía si no hay cambios de esquema
                // O puedes agregar cambios si los necesitas
            }
        }

        private suspend fun initializeData(database: AppDatabase) {
            try {
                val userDao = database.userDao()
                val productoDao = database.productoDao()
                val categoriaDao = database.categoriaDao()

                println("DEBUG: AppDatabase - DAOs obtenidos")

                val userCount = userDao.count()
                val productoCount = productoDao.count()
                val categoriaCount = categoriaDao.count()

                println("DEBUG: AppDatabase - Conteo inicial - Usuarios: $userCount, Productos: $productoCount, Categorías: $categoriaCount")

                // ✅ INICIALIZAR USUARIOS SI NO EXISTEN
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

                // ✅ INICIALIZAR CATEGORÍAS SI NO EXISTEN
                if (categoriaCount == 0) {
                    println("DEBUG: AppDatabase - Insertando categorías de prueba...")

                    val categorias = listOf(
                        CategoriaEntity(
                            nombre = "Electrónicos",
                            descripcion = "Productos electrónicos y dispositivos tecnológicos",
                            fechaCreacion = System.currentTimeMillis(),
                            activa = 1
                        ),
                        CategoriaEntity(
                            nombre = "Ropa",
                            descripcion = "Prendas de vestir y accesorios de moda",
                            fechaCreacion = System.currentTimeMillis(),
                            activa = 1
                        ),
                        CategoriaEntity(
                            nombre = "Hogar",
                            descripcion = "Artículos para el hogar y decoración",
                            fechaCreacion = System.currentTimeMillis(),
                            activa = 1
                        ),
                        CategoriaEntity(
                            nombre = "Deportes",
                            descripcion = "Equipos y artículos deportivos",
                            fechaCreacion = System.currentTimeMillis(),
                            activa = 1
                        ),
                        CategoriaEntity(
                            nombre = "Alimentos",
                            descripcion = "Productos alimenticios y bebidas",
                            fechaCreacion = System.currentTimeMillis(),
                            activa = 1
                        ),
                        CategoriaEntity(
                            nombre = "Libros",
                            descripcion = "Libros y material educativo",
                            fechaCreacion = System.currentTimeMillis(),
                            activa = 1
                        ),
                        CategoriaEntity(
                            nombre = "Juguetes",
                            descripcion = "Juguetes y juegos para todas las edades",
                            fechaCreacion = System.currentTimeMillis(),
                            activa = 1
                        ),
                        CategoriaEntity(
                            nombre = "Salud y Belleza",
                            descripcion = "Productos de cuidado personal y belleza",
                            fechaCreacion = System.currentTimeMillis(),
                            activa = 1
                        ),
                        CategoriaEntity(
                            nombre = "Automotriz",
                            descripcion = "Repuestos y accesorios para vehículos",
                            fechaCreacion = System.currentTimeMillis(),
                            activa = 1
                        ),
                        CategoriaEntity(
                            nombre = "Oficina",
                            descripcion = "Artículos de oficina y papelería",
                            fechaCreacion = System.currentTimeMillis(),
                            activa = 1
                        )
                    )

                    categorias.forEach { categoria ->
                        val id = categoriaDao.insertar(categoria)
                        println("DEBUG: AppDatabase - Insertada categoría: ${categoria.nombre} (ID: $id)")
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
                            categoria = "Electrónicos",
                            proveedor = "Logitech"
                        ),
                        ProductoEntity(
                            nombre = "Teclado Mecánico RGB",
                            descripcion = "Teclado mecánico RGB con switches azules",
                            precio = 89.99,
                            stock = 15,
                            categoria = "Electrónicos",
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
                            categoria = "Electrónicos",
                            proveedor = "Sony"
                        ),
                        ProductoEntity(
                            nombre = "Camiseta Deportiva Nike",
                            descripcion = "Camiseta deportiva de alta calidad para running",
                            precio = 45.99,
                            stock = 30,
                            categoria = "Ropa",
                            proveedor = "Nike"
                        ),
                        ProductoEntity(
                            nombre = "Zapatillas Running Adidas",
                            descripcion = "Zapatillas profesionales para running",
                            precio = 89.99,
                            stock = 20,
                            categoria = "Deportes",
                            proveedor = "Adidas"
                        ),
                        ProductoEntity(
                            nombre = "Balón de Fútbol",
                            descripcion = "Balón oficial tamaño 5 para competencia",
                            precio = 29.99,
                            stock = 15,
                            categoria = "Deportes",
                            proveedor = "Puma"
                        ),
                        ProductoEntity(
                            nombre = "Sofá 3 Plazas",
                            descripcion = "Sofá moderno de 3 plazas color gris",
                            precio = 599.99,
                            stock = 5,
                            categoria = "Hogar",
                            proveedor = "Muebles Chile"
                        ),
                        ProductoEntity(
                            nombre = "Lámpara de Mesa LED",
                            descripcion = "Lámpara LED moderna con regulador de intensidad",
                            precio = 39.99,
                            stock = 25,
                            categoria = "Hogar",
                            proveedor = "Iluminación Home"
                        ),
                        ProductoEntity(
                            nombre = "Arroz Integral 1kg",
                            descripcion = "Arroz integral orgánico en paquete de 1kg",
                            precio = 3.99,
                            stock = 50,
                            categoria = "Alimentos",
                            proveedor = "Super Alimentos"
                        ),
                        ProductoEntity(
                            nombre = "Aceite de Oliva Extra Virgen",
                            descripcion = "Aceite de oliva extra virgen 500ml",
                            precio = 12.99,
                            stock = 40,
                            categoria = "Alimentos",
                            proveedor = "Aceites Premium"
                        ),
                        ProductoEntity(
                            nombre = "Clean Code: Manual de desarrollo ágil",
                            descripcion = "Libro sobre principios de desarrollo de software",
                            precio = 49.99,
                            stock = 10,
                            categoria = "Libros",
                            proveedor = "Editorial Técnica"
                        ),
                        ProductoEntity(
                            nombre = "Set de Lego Technic",
                            descripcion = "Set de construcción Lego para mayores de 10 años",
                            precio = 79.99,
                            stock = 8,
                            categoria = "Juguetes",
                            proveedor = "Lego"
                        ),
                        ProductoEntity(
                            nombre = "Crema Hidratante Facial",
                            descripcion = "Crema hidratante para piel seca 50ml",
                            precio = 24.99,
                            stock = 35,
                            categoria = "Salud y Belleza",
                            proveedor = "Dermatológica"
                        )
                    )

                    productos.forEach { producto ->
                        val id = productoDao.insert(producto)
                        println("DEBUG: AppDatabase - Insertado producto: ${producto.nombre} (ID: $id)")
                    }
                }

                // ✅ VERIFICACIÓN FINAL
                val allUsers = userDao.getAll()
                val allProductos = productoDao.getAll().first()
                val allCategorias = categoriaDao.obtenerTodas().first()

                println("DEBUG: AppDatabase - Verificación Final:")
                println("DEBUG: AppDatabase - Total usuarios: ${allUsers.size}")
                println("DEBUG: AppDatabase - Total productos: ${allProductos.size}")
                println("DEBUG: AppDatabase - Total categorías: ${allCategorias.size}")

                // Mostrar categorías
                allCategorias.forEach { categoria ->
                    println("DEBUG: AppDatabase - Categoría: ${categoria.nombre} | Descripción: ${categoria.descripcion}")
                }

                // Mostrar productos
                allProductos.forEach { producto ->
                    println("DEBUG: AppDatabase - Producto: ${producto.nombre} | Categoría: ${producto.categoria} | Precio: $${producto.precio} | Stock: ${producto.stock}")
                }

            } catch (e: Exception) {
                println("ERROR: AppDatabase - Error en initializeData: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}