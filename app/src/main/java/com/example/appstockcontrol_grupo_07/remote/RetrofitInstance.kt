package com.example.appstockcontrol_grupo_07.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // Microservicio de usuario
    private const val BASE_URL_USUARIOS =
        "https://servicio-usuario-production-5137.up.railway.app/"

    // Microservicio de catalogo
    private const val BASE_URL_CATALOGO =
        "https://servicio-catalogo-production.up.railway.app/"
    // Microservicio de provedores
    private const val BASE_URL_PROVEEDORES =
        "https://serivicio-proovedores-production.up.railway.app/"
    // Microservicio de Movimiento inventario
    private const val BASE_URL_MOVIMIENTOS =
        "https://servicio-movimiento-production.up.railway.app/"
    private val retrofitUsuarios: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_USUARIOS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val retrofitCatalogo: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_CATALOGO)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private val retrofitProveedores: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_PROVEEDORES)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val retrofitMovimientos: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_MOVIMIENTOS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val usuarioApi: UsuarioApi by lazy {
        retrofitUsuarios.create(UsuarioApi::class.java)
    }

    val catalogoApi: CatalogoApi by lazy {
        retrofitCatalogo.create(CatalogoApi::class.java)
    }
    val proveedorApi: ProveedorApi by lazy {
        retrofitProveedores.create(ProveedorApi::class.java)
    }
    val movimientosApi: MovimientoApi by lazy {
        retrofitMovimientos.create(MovimientoApi::class.java)
    }
}
