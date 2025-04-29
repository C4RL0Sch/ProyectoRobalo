package tmz.jcmh.proyecto_robalo.util

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import tmz.jcmh.proyecto_robalo.data.models.Producto
import java.io.IOException

class ExcelManager() {

    fun crearExcel(lista: List<Producto>): XSSFWorkbook{
        val listaRegistros = lista.toMutableList()

        val workbook = XSSFWorkbook()
        val sheet: Sheet = workbook.createSheet("Productos")

        // Crear encabezados
        val headerRow = sheet.createRow(0)
        headerRow.createCell(0).setCellValue("Código")
        headerRow.createCell(1).setCellValue("Producto")
        headerRow.createCell(2).setCellValue("Marca")
        headerRow.createCell(3).setCellValue("Categoria")
        headerRow.createCell(4).setCellValue("Presentación")
        headerRow.createCell(5).setCellValue("Medida")
        headerRow.createCell(6).setCellValue("Precio")
        headerRow.createCell(7).setCellValue("Cantidad")

        // Rellenar los datos
        for (index in listaRegistros.indices) {
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(listaRegistros[index].Codigo)
            row.createCell(1).setCellValue(listaRegistros[index].Nombre)
            row.createCell(2).setCellValue(listaRegistros[index].Marca)
            row.createCell(3).setCellValue(listaRegistros[index].Categoria)
            row.createCell(4).setCellValue(listaRegistros[index].Presentacion)
            row.createCell(5).setCellValue(listaRegistros[index].Medida)
            row.createCell(6).setCellValue(listaRegistros[index].Precio?:0.0)
            row.createCell(7).setCellValue(listaRegistros[index].Cantidad?:0.0)
        }

        return workbook
    }

    fun leerProductosDesdeExcel(uri: Uri, contentResolver: ContentResolver): List<Producto> {
        val lista = mutableListOf<Producto>()

        contentResolver.openInputStream(uri).use { inputStream ->
            val workbook = XSSFWorkbook(inputStream)
            val sheet: Sheet = workbook.getSheetAt(0)

            // Empieza a leer desde la fila 1 (suponiendo fila 0 es encabezado)
            for (i in 1..sheet.lastRowNum) {
                val row = sheet.getRow(i) ?: continue

                val codigo = obtenerValorCelda(row.getCell(0))
                val nombre = obtenerValorCelda(row.getCell(1))
                val marca = obtenerValorCelda(row.getCell(2))
                val categoria = obtenerValorCelda(row.getCell(3))
                val presentacion = obtenerValorCelda(row.getCell(4))
                val medida = obtenerValorCelda(row.getCell(5))
                val precio = obtenerValorCelda(row.getCell(6)).toDoubleOrNull() ?: 0.0
                val cantidad = obtenerValorCelda(row.getCell(7)).toDoubleOrNull()?: 0.0

                // Si faltan valores importantes, omite la fila
                if (codigo.isEmpty() || nombre.isEmpty()) continue

                // Agrega el producto a la lista
                val producto = Producto(
                    Codigo = codigo,
                    Nombre = nombre,
                    Marca = marca,
                    Categoria = categoria,
                    Presentacion = presentacion,
                    Medida = medida,
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
}