package tmz.jcmh.proyecto_robalo.ui.productos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tmz.jcmh.proyecto_robalo.data.database.DatabaseRobalo
import tmz.jcmh.proyecto_robalo.data.models.Producto
import tmz.jcmh.proyecto_robalo.data.repository.ProductoRepository

class ProductosViewModel (application: Application) : AndroidViewModel(application) {

    private val repository: ProductoRepository
    val allProductos: LiveData<List<Producto>>
    lateinit var ProductoActual: MutableLiveData<Producto>

    init {
        val productoDao = DatabaseRobalo.getDatabase(application).daoProductos
        repository = ProductoRepository(productoDao)
        allProductos = repository.getAll()
    }

    //TODO REPORTE
    suspend fun getByCode(codigo: String): Producto{
        val producto= repository.getByCode(codigo)
        return producto
    }

    suspend fun insert(producto: Producto):Boolean {
        val count = repository.CountByCodigo(producto.Codigo)

        if(count==0) {
            repository.insert(producto)
            return true
        }
        else{
            return false
        }
    }

    fun Delete(producto: Producto){
        viewModelScope.launch {
            repository.delete(producto)
        }
    }

    fun Update(producto: Producto){
        viewModelScope.launch {
            repository.update(producto)
        }
    }
}