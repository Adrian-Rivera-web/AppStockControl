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
import com.example.appstockcontrol_grupo_07.data.local.proveedor.ProveedorDao
import com.example.appstockcontrol_grupo_07.data.local.proveedor.ProveedorEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        ProductoEntity::class,
        CategoriaEntity::class,
        ProveedorEntity::class
    ],
    version = 7,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun productoDao(): ProductoDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun proveedorDao(): ProveedorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "ui_navegacion_v7.db"

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

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
                    .addMigrations(MIGRATION_6_7)
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance

                instance
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE proveedores (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nombre TEXT NOT NULL,
                        contacto TEXT NOT NULL,
                        telefono TEXT NOT NULL,
                        email TEXT NOT NULL,
                        direccion TEXT NOT NULL,
                        activo INTEGER NOT NULL
                    )
                    """
                )
            }
        }

        private suspend fun initializeData(database: AppDatabase) {
            try {
                val userDao = database.userDao()
                val productoDao = database.productoDao()
                val categoriaDao = database.categoriaDao()
                val proveedorDao = database.proveedorDao()


                val userCount = userDao.count()
                val productoCount = productoDao.count()
                val categoriaCount = categoriaDao.count()
                val proveedorCount = proveedorDao.count()



                if (userCount == 0) {

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

                    }
                }


                if (categoriaCount == 0) {


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


                if (proveedorCount == 0) {
                    println("DEBUG: AppDatabase - Insertando proveedores de prueba...")

                    val proveedores = listOf(
                        ProveedorEntity(
                            nombre = "ASUS Chile",
                            contacto = "Juan Pérez",
                            telefono = "+56912345678",
                            email = "ventas@asus.cl",
                            direccion = "Av. Providencia 123, Santiago",
                            activo = 1
                        ),
                        ProveedorEntity(
                            nombre = "Logitech",
                            contacto = "María González",
                            telefono = "+56987654321",
                            email = "chile@logitech.com",
                            direccion = "Av. Las Condes 456, Santiago",
                            activo = 1
                        ),
                        ProveedorEntity(
                            nombre = "Sony Chile",
                            contacto = "Carlos López",
                            telefono = "+56911223344",
                            email = "contacto@sony.cl",
                            direccion = "Mall Parque Arauco, Local 25",
                            activo = 1
                        ),
                        ProveedorEntity(
                            nombre = "Samsung Electronics",
                            contacto = "Ana Rodríguez",
                            telefono = "+56944556677",
                            email = "ventas@samsung.cl",
                            direccion = "Costanera Center, Nivel 2",
                            activo = 1
                        ),
                        ProveedorEntity(
                            nombre = "Nike Chile",
                            contacto = "Pedro Martínez",
                            telefono = "+56988990011",
                            email = "distribucion@nike.cl",
                            direccion = "Alto Las Condes, Local 15",
                            activo = 1
                        ),
                        ProveedorEntity(
                            nombre = "Adidas Chile",
                            contacto = "Laura Silva",
                            telefono = "+56922334455",
                            email = "info@adidas.cl",
                            direccion = "Mall Plaza Oeste, Nivel 1",
                            activo = 1
                        ),
                        ProveedorEntity(
                            nombre = "HP Chile",
                            contacto = "Roberto Navarro",
                            telefono = "+56966778899",
                            email = "soporte@hp.cl",
                            direccion = "Av. Kennedy 5412, Santiago",
                            activo = 1
                        ),
                        ProveedorEntity(
                            nombre = "Xiaomi Chile",
                            contacto = "Carmen Rojas",
                            telefono = "+56933445566",
                            email = "contacto@xiaomi.cl",
                            direccion = "Mall Plaza Egaña, Local 8",
                            activo = 1
                        ),
                        ProveedorEntity(
                            nombre = "Super Alimentos S.A.",
                            contacto = "Miguel Ángel Fuentes",
                            telefono = "+56977889900",
                            email = "pedidos@superalimentos.cl",
                            direccion = "Av. Matta 1234, Santiago",
                            activo = 1
                        ),
                        ProveedorEntity(
                            nombre = "Editorial Técnica Ltda.",
                            contacto = "Sofia Mendoza",
                            telefono = "+56911223344",
                            email = "ventas@editorialtecnica.cl",
                            direccion = "Av. Libertador Bernardo O'Higgins 256",
                            activo = 1
                        ),
                        ProveedorEntity(
                            nombre = "Muebles Chile S.A.",
                            contacto = "Fernando Castro",
                            telefono = "+56955667788",
                            email = "clientes@muebleschile.cl",
                            direccion = "Av. Vicuña Mackenna 123, Ñuñoa",
                            activo = 1
                        ),
                        ProveedorEntity(
                            nombre = "Iluminación Home",
                            contacto = "Patricia Vargas",
                            telefono = "+56999001122",
                            email = "cotizaciones@iluminacionhome.cl",
                            direccion = "Av. Irarrázaval 3456, Providencia",
                            activo = 1
                        ),
                        ProveedorEntity(
                            nombre = "Dermatológica Premium",
                            contacto = "Dr. Alejandro Soto",
                            telefono = "+56944332211",
                            email = "consultas@dermatologicapremium.cl",
                            direccion = "Av. Apoquindo 4500, Las Condes",
                            activo = 1
                        ),
                        ProveedorEntity(
                            nombre = "Lego Distribuidora",
                            contacto = "Andrés Guzmán",
                            telefono = "+56977665544",
                            email = "distribucion@legochile.cl",
                            direccion = "Mall Arauco Maipú, Local 12",
                            activo = 1
                        ),
                        ProveedorEntity(
                            nombre = "Aceites Premium S.A.",
                            contacto = "Marcela Torres",
                            telefono = "+56933221100",
                            email = "ventas@aceitespremium.cl",
                            direccion = "Av. La Dehesa 123, Lo Barnechea",
                            activo = 1
                        )
                    )

                    proveedores.forEach { proveedor ->
                        val id = proveedorDao.insertar(proveedor)
                        println("DEBUG: AppDatabase - Insertado proveedor: ${proveedor.nombre} (ID: $id)")
                    }
                }

                // ✅ INICIALIZAR PRODUCTOS SI NO EXISTEN
                if (productoCount == 0) {
                    println("DEBUG: AppDatabase - Insertando productos de prueba...")

                    val productos = listOf(
                        ProductoEntity(
                            nombre = "Laptop Gamer ASUS ROG",
                            descripcion = "Laptop para gaming de alta gama con RTX 4060, 16GB RAM, 1TB SSD",
                            precio = 1299.99,
                            stock = 8,
                            categoria = "Electrónicos",
                            proveedor = "ASUS Chile"
                        ),
                        ProductoEntity(
                            nombre = "Mouse Inalámbrico Logitech MX Master",
                            descripcion = "Mouse ergonómico inalámbrico con sensor óptico de alta precisión",
                            precio = 35.50,
                            stock = 25,
                            categoria = "Electrónicos",
                            proveedor = "Logitech"
                        ),
                        ProductoEntity(
                            nombre = "Teclado Mecánico RGB Redragon",
                            descripcion = "Teclado mecánico RGB con switches azules y retroiluminación personalizable",
                            precio = 89.99,
                            stock = 15,
                            categoria = "Electrónicos",
                            proveedor = "Logitech"
                        ),
                        ProductoEntity(
                            nombre = "Monitor 24\" Samsung Curvo",
                            descripcion = "Monitor Full HD 144Hz para gaming con panel VA curvado",
                            precio = 249.99,
                            stock = 12,
                            categoria = "Electrónicos",
                            proveedor = "Samsung Electronics"
                        ),
                        ProductoEntity(
                            nombre = "Auriculares Bluetooth Sony WH-1000XM4",
                            descripcion = "Auriculares inalámbricos con cancelación de ruido activa y 30h de batería",
                            precio = 199.99,
                            stock = 18,
                            categoria = "Electrónicos",
                            proveedor = "Sony Chile"
                        ),
                        ProductoEntity(
                            nombre = "Camiseta Deportiva Nike Dri-FIT",
                            descripcion = "Camiseta deportiva de alta calidad para running con tecnología Dri-FIT",
                            precio = 45.99,
                            stock = 30,
                            categoria = "Ropa",
                            proveedor = "Nike Chile"
                        ),
                        ProductoEntity(
                            nombre = "Zapatillas Running Adidas Ultraboost",
                            descripcion = "Zapatillas profesionales para running con amortiguación Boost",
                            precio = 89.99,
                            stock = 20,
                            categoria = "Deportes",
                            proveedor = "Adidas Chile"
                        ),
                        ProductoEntity(
                            nombre = "Balón de Fútbol Puma Official",
                            descripcion = "Balón oficial tamaño 5 para competencia con diseño profesional",
                            precio = 29.99,
                            stock = 15,
                            categoria = "Deportes",
                            proveedor = "Adidas Chile"
                        ),
                        ProductoEntity(
                            nombre = "Sofá 3 Plazas Moderno",
                            descripcion = "Sofá moderno de 3 plazas color gris con estructura en madera",
                            precio = 599.99,
                            stock = 5,
                            categoria = "Hogar",
                            proveedor = "Muebles Chile S.A."
                        ),
                        ProductoEntity(
                            nombre = "Lámpara de Mesa LED Inteligente",
                            descripcion = "Lámpara LED moderna con regulador de intensidad y control por app",
                            precio = 39.99,
                            stock = 25,
                            categoria = "Hogar",
                            proveedor = "Iluminación Home"
                        ),
                        ProductoEntity(
                            nombre = "Arroz Integral Orgánico 1kg",
                            descripcion = "Arroz integral orgánico en paquete de 1kg, cultivado naturalmente",
                            precio = 3.99,
                            stock = 50,
                            categoria = "Alimentos",
                            proveedor = "Super Alimentos S.A."
                        ),
                        ProductoEntity(
                            nombre = "Aceite de Oliva Extra Virgen 500ml",
                            descripcion = "Aceite de oliva extra virgen 500ml, primera prensada en frío",
                            precio = 12.99,
                            stock = 40,
                            categoria = "Alimentos",
                            proveedor = "Aceites Premium S.A."
                        ),
                        ProductoEntity(
                            nombre = "Clean Code: Manual de desarrollo ágil",
                            descripcion = "Libro sobre principios de desarrollo de software limpio y mantenible",
                            precio = 49.99,
                            stock = 10,
                            categoria = "Libros",
                            proveedor = "Editorial Técnica Ltda."
                        ),
                        ProductoEntity(
                            nombre = "Set de Lego Technic Bugatti Chiron",
                            descripcion = "Set de construcción Lego Technic Bugatti Chiron para mayores de 10 años",
                            precio = 79.99,
                            stock = 8,
                            categoria = "Juguetes",
                            proveedor = "Lego Distribuidora"
                        ),
                        ProductoEntity(
                            nombre = "Crema Hidratante Facial con Ácido Hialurónico",
                            descripcion = "Crema hidratante para piel seca 50ml con ácido hialurónico y vitamina E",
                            precio = 24.99,
                            stock = 35,
                            categoria = "Salud y Belleza",
                            proveedor = "Dermatológica Premium"
                        ),
                        ProductoEntity(
                            nombre = "Tablet Android Xiaomi Pad 6",
                            descripcion = "Tablet Android con 64GB de almacenamiento, pantalla 2.8K, 8GB RAM",
                            precio = 299.99,
                            stock = 10,
                            categoria = "Electrónicos",
                            proveedor = "Xiaomi Chile"
                        ),
                        ProductoEntity(
                            nombre = "Impresora Multifuncional HP LaserJet",
                            descripcion = "Impresora láser WiFi con escáner y copiadora, tóner incluido",
                            precio = 179.99,
                            stock = 6,
                            categoria = "Oficina",
                            proveedor = "HP Chile"
                        ),
                        ProductoEntity(
                            nombre = "Disco Duro Externo Seagate 1TB",
                            descripcion = "Disco duro externo USB 3.0 portátil, compatible con PC y Mac",
                            precio = 59.99,
                            stock = 30,
                            categoria = "Electrónicos",
                            proveedor = "HP Chile"
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
                val allProveedores = proveedorDao.obtenerTodos().first()

                println("DEBUG: AppDatabase - Verificación Final:")
                println("DEBUG: AppDatabase - Total usuarios: ${allUsers.size}")
                println("DEBUG: AppDatabase - Total productos: ${allProductos.size}")
                println("DEBUG: AppDatabase - Total categorías: ${allCategorias.size}")
                println("DEBUG: AppDatabase - Total proveedores: ${allProveedores.size}")

                // Mostrar categorías
                println("DEBUG: AppDatabase - Categorías disponibles:")
                allCategorias.forEach { categoria ->
                    println("DEBUG: AppDatabase - • ${categoria.nombre}: ${categoria.descripcion}")
                }

                // Mostrar proveedores
                println("DEBUG: AppDatabase - Proveedores disponibles:")
                allProveedores.forEach { proveedor ->
                    println("DEBUG: AppDatabase - • ${proveedor.nombre}: ${proveedor.contacto} - ${proveedor.telefono}")
                }

                // Mostrar productos
                println("DEBUG: AppDatabase - Productos disponibles:")
                allProductos.forEach { producto ->
                    println("DEBUG: AppDatabase - • ${producto.nombre} | Categoría: ${producto.categoria} | Proveedor: ${producto.proveedor} | Precio: $${producto.precio} | Stock: ${producto.stock}")
                }

            } catch (e: Exception) {
                println("ERROR: AppDatabase - Error en initializeData: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}