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
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import tmz.jcmh.proyecto_robalo.data.database.DatabaseRobalo
import tmz.jcmh.proyecto_robalo.data.models.Producto
import tmz.jcmh.proyecto_robalo.data.repository.ProductoRepository
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

    fun exportarExcel(uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val listaRegistros = allProductos.value!!.toMutableList()

                val workbook = XSSFWorkbook()
                val sheet: Sheet = workbook.createSheet("Productos")

                // Crear encabezados
                val headerRow = sheet.createRow(0)
                headerRow.createCell(0).setCellValue("Código")
                headerRow.createCell(1).setCellValue("Producto")
                headerRow.createCell(2).setCellValue("Presentación")
                headerRow.createCell(3).setCellValue("Precio")
                headerRow.createCell(4).setCellValue("Cantidad")

                // Rellenar los datos
                for (index in listaRegistros.indices) {
                    val row = sheet.createRow(index + 1)
                    row.createCell(0).setCellValue(listaRegistros[index].Codigo)
                    row.createCell(1).setCellValue(listaRegistros[index].Nombre)
                    row.createCell(2).setCellValue(listaRegistros[index].Presentacion)
                    row.createCell(3).setCellValue(listaRegistros[index].Precio)
                    row.createCell(4).setCellValue(listaRegistros[index].Cantidad.toDouble())
                }

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
                val listaExcel = leerProductosDesdeExcel(uri, contentResolver)

                // 2. Obtener productos actuales de la base
                val listaActual = repository.getAllNow() // Asegúrate que getAllNow() retorne List<Producto> de forma directa

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
                        if (!productosSonIgualesSinId(productoBD, productoExcel)) {
                            productoExcel.id = productoBD.id
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
                _mensaje.postValue("Archivo cargado. Previsualiza y confirma cambios.")

                lecturaFinalizada.postValue(true)

            } catch (e: Exception) {
                e.printStackTrace()
                //_mensaje.postValue("Ocurrió un error al leer el Excel. Verifica el formato.")
                _mensaje.postValue(e.message)
                lecturaFinalizada.postValue(false)
            }
        }
    }

    private fun productosSonIgualesSinId(prod1: Producto, prod2: Producto): Boolean {
        return prod1.Codigo == prod2.Codigo &&
                prod1.Nombre == prod2.Nombre &&
                prod1.Presentacion == prod2.Presentacion &&
                prod1.Precio == prod2.Precio &&
                prod1.Cantidad == prod2.Cantidad
    }

    private fun leerProductosDesdeExcel(uri: Uri, contentResolver: ContentResolver): List<Producto> {
        val lista = mutableListOf<Producto>()

        contentResolver.openInputStream(uri).use { inputStream ->
            val workbook = XSSFWorkbook(inputStream)
            val sheet: Sheet = workbook.getSheetAt(0)

            // Empieza a leer desde la fila 1 (suponiendo fila 0 es encabezado)
            for (i in 1..sheet.lastRowNum) {
                val row = sheet.getRow(i) ?: continue

                val codigo = obtenerValorCelda(row.getCell(0))
                val nombre = obtenerValorCelda(row.getCell(1))
                val presentacion = obtenerValorCelda(row.getCell(2))
                val precio = obtenerValorCelda(row.getCell(3)).toDoubleOrNull() ?: 0.0
                val cantidad = obtenerValorCelda(row.getCell(4)).toDoubleOrNull()?.toInt() ?: 0

                // Si faltan valores importantes, omite la fila
                if (codigo.isEmpty() || nombre.isEmpty()) continue

                // Agrega el producto a la lista
                val producto = Producto(
                    Codigo = codigo,
                    Nombre = nombre,
                    Presentacion = presentacion,
                    Precio = precio,
                    Cantidad = cantidad
                )


                lista.add(producto)
            }
            workbook.close()
        }
        return lista
    }

    private fun obtenerValorCelda(celda: Cell?): String {
        return when {
            celda == null -> ""
            celda.cellType == CellType.STRING -> celda.stringCellValue
            celda.cellType == CellType.NUMERIC -> celda.numericCellValue.toString()
            celda.cellType == CellType.BOOLEAN -> celda.booleanCellValue.toString()
            celda.cellType == CellType.FORMULA -> celda.cellFormula
            else -> ""
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
            // (si tu 'allProductos' es un LiveData que viene de un DAO con Flow o similar,
            //  se actualizará solo. Si no, puedes forzar una recarga)
            _mensaje.postValue("Cambios aplicados con éxito.")
            lecturaFinalizada.postValue(false)
        }
    }

    fun saveImageToInternalStorage(bitmap: Bitmap, Filename: String){
        viewModelScope.launch {
            val success = saveImage(bitmap, Filename)
            if (success) {
                //_mensaje.value = "Imagen guardada correctamente"
            } else {
                //_mensaje.value = "Error al guardar la imagen"
            }
        }
    }

    suspend fun saveImage(bitmap: Bitmap, Filename: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val filesDir = getApplication<Application>().filesDir
                val productosDir = File(filesDir, "productos")
                if (!productosDir.exists()) {
                    productosDir.mkdirs()
                }

                val imageFile = File(productosDir, "$Filename")
                FileOutputStream(imageFile).use { fos ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                }
                true // Indica que la operación fue exitosa
            } catch (e: Exception) {
                e.printStackTrace()
                false // Indica que ocurrió un error
            }
        }
    }

    fun deleteImageFile(Filename: String) {
        val filesDir = getApplication<Application>().filesDir

        val productosDir = File(filesDir, "productos")
        if (!productosDir.exists()) {
            productosDir.mkdirs()
        }

        val imageFile = File(productosDir, "$Filename.png")

        if (imageFile.exists()) {
            imageFile.delete()
        }
    }

    fun getImageFile(Filename: String): File? {
        val filesDir = getApplication<Application>().filesDir

        val productosDir = File(filesDir, "productos")
        if (!productosDir.exists()) {
            productosDir.mkdirs()
        }

        val imageFile = File(productosDir, "$Filename.png")
        return if (imageFile.exists()) imageFile else null
    }
}