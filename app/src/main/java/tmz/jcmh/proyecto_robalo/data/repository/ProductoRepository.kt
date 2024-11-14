package tmz.jcmh.proyecto_robalo.data.repository

import tmz.jcmh.proyecto_robalo.data.dao.IProductoDAO
import tmz.jcmh.proyecto_robalo.data.models.Producto

class ProductoRepository(private val daoProductos: IProductoDAO) {

    suspend fun getAll(): List<Producto> {
        return daoProductos.getAll()
    }

    suspend fun getById(id: Int): Producto {
        return daoProductos.getById(id)
    }

    suspend fun insertRange(productos: List<Producto>) {
        daoProductos.insertAll(productos)
    }

    suspend fun insert(producto: Producto) {
        daoProductos.insert(producto)
    }

    suspend fun updateRange(productos: List<Producto>) {
        daoProductos.UpdateAll(productos)
    }

    suspend fun update(producto: Producto) {
        daoProductos.Update(producto)
    }

    suspend fun deleteRange(productos: List<Producto>) {
        daoProductos.DeleteAll(productos)
    }

    suspend fun delete(producto: Producto) {
        daoProductos.Delete(producto)
    }

}