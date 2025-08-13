package tmz.jcmh.proyecto_robalo.ui.productos.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tmz.jcmh.proyecto_robalo.MyApp
import tmz.jcmh.proyecto_robalo.data.models.Producto
import tmz.jcmh.proyecto_robalo.data.repository.ProductoRepository
import tmz.jcmh.proyecto_robalo.util.CloudStorageManager
import tmz.jcmh.proyecto_robalo.util.Event
import java.io.IOException

class AddProductoViewModel (application: Application) : AndroidViewModel(application) {
    private val repository: ProductoRepository
        get() = (getApplication() as MyApp).productoRepository

    val allProductos = repository.productos

    private val _mensaje = MutableLiveData<Event<String>>()
    val mensaje: LiveData<Event<String>> = _mensaje

    val isLoading = MutableLiveData<Boolean>()
    val loadingMsg = MutableLiveData<String>()

    val cloudManager = CloudStorageManager()

    init {
        isLoading.value = false
        loadingMsg.value = "Espere por favor"
    }

    fun countByCode(codigo: String): Int?{
        val lista = allProductos.value?.toList()
        val cont = lista?.count { p -> p.Codigo.contains(codigo) == true }
        return cont
    }

    fun insert(producto: Producto) {
        viewModelScope.launch {
            val prod = repository.getById(producto.Codigo)

            if (prod == null) {
                isLoading.value = true
                loadingMsg.value = "Guardando el producto"
                val resp = repository.insert(producto)
                if(!resp){
                    _mensaje.value = Event("No se puedo guardar la el producto")
                }
                else{
                    _mensaje.value = Event("Guardado correctamente")
                }
                isLoading.value = false
            } else {
                throw IOException("Error al guardar el producto")
            }
        }
    }

    fun insertWithImage(producto: Producto, contentResolver: ContentResolver, uri: Uri){
        viewModelScope.launch {
            val prod = repository.getById(producto.Codigo)

            if (prod == null) {
                isLoading.value = true
                loadingMsg.value = "Guardando el producto"
                val res = cloudManager.saveImage(contentResolver, "productos", uri, producto.Codigo)
                if(res==""){
                    _mensaje.value = Event("No se puedo guardar la imagen")
                }
                else{
                    producto.imgUrl  = res
                }

                val resp = repository.insert(producto)
                if(!resp){
                    _mensaje.value = Event("No se puedo guardar la el producto")
                }
                else{
                    _mensaje.value = Event("Guardado correctamente")
                }
                isLoading.value = false
            } else {
                _mensaje.value = Event("Ya existe un Producto con el mismo codigo")
            }
        }
    }
}