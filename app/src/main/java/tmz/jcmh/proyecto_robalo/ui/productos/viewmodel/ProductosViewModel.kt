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
import tmz.jcmh.proyecto_robalo.MyApp
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
        get() = (getApplication() as MyApp).productoRepository

    val allProductos = repository.productos

    private val _productosNuevos = repository._productosNuevos
    val productosNuevos = repository.productosNuevos

    private val _productosModificados = repository._productosModificados
    val productosModificados = repository.productosModificados

    private val _productosEliminados = repository._productosEliminados
    val productosEliminados = repository.productosEliminados

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
        isLoading.value = false
        loadingMsg.value = "Espere por favor"
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
                _productosNuevos.value = tmpNuevos
                _productosModificados.value = tmpModificados
                _productosEliminados.value = tmpEliminados

                // Avisar a la UI que se leyó correctamente
                _mensaje.value = Event("Archivo cargado. Revisa y confirma cambios.")

                lecturaFinalizada.postValue(true)

            } catch (e: Exception) {
                e.printStackTrace()
                _mensaje.value = Event("Ocurrió un error al leer el Excel. Verifica el formato.")
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