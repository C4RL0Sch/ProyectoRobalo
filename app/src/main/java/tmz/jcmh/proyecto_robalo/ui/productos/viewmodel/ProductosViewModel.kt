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
import kotlinx.coroutines.withContext
import tmz.jcmh.proyecto_robalo.data.models.Producto
import tmz.jcmh.proyecto_robalo.data.repository.ProductoRepository
import tmz.jcmh.proyecto_robalo.util.ExcelManager
import tmz.jcmh.proyecto_robalo.util.InternalStorageManager
import java.io.File
import java.io.FileOutputStream
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
    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> = _mensaje

    val lecturaFinalizada = MutableLiveData<Boolean>()

    val excel = ExcelManager()
    val internalManager = InternalStorageManager()

    init {
        repository = ProductoRepository()
        allProductos = repository.productos
    }

    fun countByCode(codigo: String): Int?{
        val lista = allProductos.value?.toList()
        val cont = lista?.filter { p-> p.Codigo?.contains(codigo) == true }?.count()
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
            val prod = repository.getById(producto.Codigo ?: "")

            if (prod == null) {
                repository.insert(producto)
            } else {
                throw IOException("Error al guardar el producto")
            }
        }
    }

    fun Delete(producto: Producto){
        viewModelScope.launch {
            val res = repository.delete(producto)
            if(res){
                _mensaje.postValue("Producto eliminado correctamente")
            }
            else{
                _mensaje.postValue("Error al eliminar el producto")
            }
        }
    }

    fun Update(producto: Producto){
        viewModelScope.launch {
            val res = repository.update(producto)
            if(res){
                _mensaje.postValue("Producto actualizado correctamente")
            }
            else{
                _mensaje.postValue("Error al actualizar el producto")
            }
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
                _mensaje.postValue("Archivo cargado. Revisa y confirma cambios.")

                lecturaFinalizada.postValue(true)

            } catch (e: Exception) {
                e.printStackTrace()
                //_mensaje.postValue("Ocurrió un error al leer el Excel. Verifica el formato.")
                _mensaje.postValue(e.message)
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
            _mensaje.postValue("Cambios aplicados con éxito.")
            lecturaFinalizada.postValue(false)
        }
    }

    fun saveImageToInternalStorage(bitmap: Bitmap, Filename: String){
        viewModelScope.launch {
            val Dir = getApplication<Application>().filesDir
            val productosDir = File(Dir, "productos")
            val success = internalManager.saveImage(productosDir, bitmap, Filename)
            if (success) {
                //_mensaje.value = "Imagen guardada correctamente"
            } else {
                //_mensaje.value = "Error al guardar la imagen"
            }
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