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

class EditProductoViewModel (application: Application) : AndroidViewModel(application) {
    private val repository: ProductoRepository
        get() = (getApplication() as MyApp).productoRepository

    private val _mensaje = MutableLiveData<Event<String>>()
    val mensaje: LiveData<Event<String>> = _mensaje

    val isLoading = MutableLiveData<Boolean>()
    val loadingMsg = MutableLiveData<String>()

    val cloudManager = CloudStorageManager()

    init {
        isLoading.value = false
        loadingMsg.value = "Espere por favor"
    }

    suspend fun getByCode(codigo: String): Producto{
        val producto = repository.getById(codigo)
        if(producto==null){
            return Producto()
        }
        return producto
    }

    fun Update(producto: Producto){
        viewModelScope.launch {
            isLoading.value = true
            loadingMsg.value = "Actualizando el producto"
            val res = repository.update(producto)
            if(res){
                _mensaje.value = Event("Producto actualizado correctamente")
            }
            else{
                _mensaje.value = Event("Error al actualizar el producto")
            }
            isLoading.value = false
        }
    }

    fun UpdateWithImage(producto: Producto, contentResolver: ContentResolver, uri: Uri){
        viewModelScope.launch {
            isLoading.value = true
            loadingMsg.value = "Actualizando el producto"
            val resp = cloudManager.saveImage(contentResolver, "productos", uri, producto.Codigo)
            if(resp==""){
                _mensaje.value = Event("No se puedo actualizar la imagen")
            }
            else{
                producto.imgUrl  = resp
            }

            val res = repository.update(producto)
            if(res){
                _mensaje.value = Event("Producto actualizado correctamente")
            }
            else{
                _mensaje.value = Event("Error al actualizar el producto")
            }
            isLoading.value = false
        }
    }

    fun Delete(producto: Producto){
        viewModelScope.launch {
            isLoading.value = true
            loadingMsg.value = "Eliminando el producto"
            val res = repository.delete(producto)
            if(res){
                _mensaje.value = Event("Producto eliminado correctamente")
            }
            else{
                _mensaje.value = Event("Error al eliminar el producto")
            }
            isLoading.value = false
        }
    }

    fun deleteCloudImage(Filename: String){
        cloudManager.deleteImageFile("productos", Filename)
    }
}