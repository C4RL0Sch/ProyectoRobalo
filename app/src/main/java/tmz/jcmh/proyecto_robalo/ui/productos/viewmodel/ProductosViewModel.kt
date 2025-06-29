package tmz.jcmh.proyecto_robalo.ui.productos.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tmz.jcmh.proyecto_robalo.data.models.Producto
import tmz.jcmh.proyecto_robalo.data.repository.ProductoRepository
import tmz.jcmh.proyecto_robalo.util.CloudStorageManager
import tmz.jcmh.proyecto_robalo.util.Event
import tmz.jcmh.proyecto_robalo.util.ExcelManager
import tmz.jcmh.proyecto_robalo.util.InternalStorageManager
import java.io.File
import java.io.IOException

class ProductosViewModel (application: Application) : AndroidViewModel(application) {

    private val repository: ProductoRepository
    val allProductos: LiveData<List<Producto>>

    // Tres listas distintas o un único objeto que las agrupe
    private val _productosNuevos = MutableLiveData<List<Producto>>()
    val productosNuevos: LiveData<List<Producto>> = _productosNuevos

    private val _productosModificados = MutableLiveData<List<Producto>>()
    val productosModificados: LiveData<List<Producto>> = _productosModificados

    private val _productosEliminados = MutableLiveData<List<Producto>>()
    val productosEliminados: LiveData<List<Producto>> = _productosEliminados

    // LiveData para controlar errores o mensajes
    private val _mensaje = MutableLiveData<Event<String>>()
    val mensaje: LiveData<Event<String>> = _mensaje

    val isLoading = MutableLiveData<Boolean>()
    val loadingMsg = MutableLiveData<String>()

    val lecturaFinalizada = MutableLiveData<Boolean>()

    val excel = ExcelManager()
    val internalManager = InternalStorageManager()
    val cloudManager = CloudStorageManager()

    init {
        repository = ProductoRepository()
        allProductos = repository.productos
        isLoading.value = false
        loadingMsg.value = "Espere por favor"
    }

    fun countByCode(codigo: String): Int?{
        val lista = allProductos.value?.toList()
        val cont = lista?.count { p -> p.Codigo.contains(codigo) == true }
        return cont
    }

    suspend fun getByCode(codigo: String): Producto{
        val producto = repository.getById(codigo)
        if(producto==null){
            return Producto()
        }
        return producto
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
                _mensaje.value = Event("Event(Error al actualizar el producto")
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
                _mensaje.postValue(Event("Producto eliminado correctamente"))
            }
            else{
                _mensaje.postValue(Event("Error al eliminar el producto"))
            }
            isLoading.value = false
        }
    }

    fun exportarExcel(uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val listaRegistros = allProductos.value!!.toMutableList()

                val workbook = excel.crearExcel(listaRegistros)

                // Escribir el archivo
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    workbook.write(outputStream)
                    workbook.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun importarExcel(uri: Uri, contentResolver: ContentResolver){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Leer productos del Excel
                val listaExcel = excel.leerProductosDesdeExcel(uri, contentResolver)

                // 2. Obtener productos actuales de la base
                val listaActual = allProductos.value?: emptyList() // Asegúrate que getAllNow() retorne List<Producto> de forma directa

                // 3. Identificar nuevos, modificados y eliminados
                //    Usamos Codigo como identificador único
                val mapaActual = listaActual.associateBy { it.Codigo }.toMutableMap()

                val tmpNuevos = mutableListOf<Producto>()
                val tmpModificados = mutableListOf<Producto>()

                for (productoExcel in listaExcel) {
                    val codigo = productoExcel.Codigo
                    if (mapaActual.containsKey(codigo)) {
                        // Ya existe en la base
                        val productoBD = mapaActual[codigo]!!

                        // Comparamos si hay cambios relevantes
                        if (productoBD != productoExcel) {
                            tmpModificados.add(productoExcel)
                        }
                        // Lo removemos del mapa para que no cuente como eliminado
                        mapaActual.remove(codigo)
                    } else {
                        // No existe -> es nuevo
                        tmpNuevos.add(productoExcel)
                    }
                }

                val tmpEliminados = mapaActual.values.toList() // Todo lo que queda no vino en Excel

                // 4. Publicar listas en LiveData
                _productosNuevos.postValue(tmpNuevos)
                _productosModificados.postValue(tmpModificados)
                _productosEliminados.postValue(tmpEliminados)

                // Avisar a la UI que se leyó correctamente
                _mensaje.postValue(Event("Archivo cargado. Revisa y confirma cambios."))

                lecturaFinalizada.postValue(true)

            } catch (e: Exception) {
                e.printStackTrace()
                //_mensaje.postValue("Ocurrió un error al leer el Excel. Verifica el formato.")
                _mensaje.postValue(Event(e.message!!))
                lecturaFinalizada.postValue(false)
            }
        }
    }

    fun confirmarCambiosImportacion() {
        viewModelScope.launch(Dispatchers.IO) {
            val nuevos = _productosNuevos.value.orEmpty()
            val modificados = _productosModificados.value.orEmpty()
            val eliminados = _productosEliminados.value.orEmpty()

            // Inserta nuevos
            nuevos.forEach { repository.insert(it) }

            // Actualiza modificados
            modificados.forEach { repository.update(it) }

            // Elimina no presentes
            eliminados.forEach { repository.delete(it) }

            // Limpia las listas, para no duplicar si el usuario vuelve a entrar
            _productosNuevos.postValue(emptyList())
            _productosModificados.postValue(emptyList())
            _productosEliminados.postValue(emptyList())

            // Y recarga la lista principal
            _mensaje.postValue(Event("Cambios aplicados con éxito."))
            lecturaFinalizada.postValue(false)
        }
    }

    fun saveCloudImage(contentResolver: ContentResolver, uri: Uri, Filename: String){
        viewModelScope.launch {
            val res = cloudManager.saveImage(contentResolver, "productos", uri, Filename)
            if(res==""){
                _mensaje.value = Event("Error al guardar la imagen")
            }
        }
    }

    fun deleteCloudImage(Filename: String){
        cloudManager.deleteImageFile("productos", Filename)
    }

    fun saveImageToInternalStorage(bitmap: Bitmap, Filename: String){
        viewModelScope.launch {
            val Dir = getApplication<Application>().filesDir
            val productosDir = File(Dir, "productos")
            internalManager.saveImage(productosDir, bitmap, Filename)
        }
    }

    fun deleteImageFile(Filename: String) {
        val Dir = getApplication<Application>().filesDir
        val productosDir = File(Dir, "productos")
        internalManager.deleteImageFile(productosDir, Filename)
    }

    fun getImageFile(Filename: String): File? {
        val Dir = getApplication<Application>().filesDir
        val productosDir = File(Dir, "productos")
        return internalManager.getImageFile(productosDir, Filename)
    }
}