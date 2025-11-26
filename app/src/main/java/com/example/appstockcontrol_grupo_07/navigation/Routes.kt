package com.example.appstockcontrol_grupo_07.navigation

sealed class Route(val path: String) {
    data object Home : Route("home")
    data object HomeAdmin : Route("homeAdmin")
    data object Login : Route("login")
    data object Register : Route("registro")
    data object Perfil : Route("perfil")
    data object Productos : Route("productos")
    data object ListaProductos : Route("listaProductos")
    data object Categoria : Route("categoria")
    data object ListaCategoria : Route("listaCategoria")
    data object FormularioCategoria : Route("formularioCategoria")
    data object Entradas_y_Salidas_Productos : Route("entrada_y_salidas_productos")
    data object Entradas : Route("entradas")
    data object Salidas: Route("salidas")
    data object Proveedores: Route("proveedores")
    data object FormularioProducto: Route("formularioProducto")
    data object FormularioProveedores: Route("formularioProveedores")
    data object ListaProveedores : Route("listaProveedores")
    data object Usuario : Route("usuario")
    data object ReportesInventario : Route("reportes_inventario")
    data object DetalleProducto : Route("detalleProducto")

}