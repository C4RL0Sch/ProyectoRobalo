package tmz.jcmh.proyecto_robalo.utils

import android.os.Environment
import android.util.Log
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import tmz.jcmh.proyecto_robalo.data.models.Producto
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class Excel {

    fun crearExcel(listaRegistros: MutableList<Producto>){
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        val fileName = "registros.xlsx"

        //Crear un nuevo libro Excel
        val workbook = XSSFWorkbook()

        //crear una hoja de trabajo
        val sheet: Sheet = workbook.createSheet("Hoja 1")

        //crear una fila en la hoja
        val headerRow = sheet.createRow(0)

        //crear celdas en la fila
        var cell = headerRow.createCell(0)
        cell.setCellValue("Producto")

        cell = headerRow.createCell(1)
        cell.setCellValue("Presentación")

        cell = headerRow.createCell(2)
        cell.setCellValue("Precio")

        cell = headerRow.createCell(3)
        cell.setCellValue("Cantidad")

        for(index in listaRegistros.indices){
            val row = sheet.createRow(index+1)
            row.createCell(0).setCellValue(listaRegistros[index].Nombre)
            row.createCell(1).setCellValue(listaRegistros[index].Presentacion)
            row.createCell(2).setCellValue(listaRegistros[index].Precio)
            row.createCell(3).setCellValue(listaRegistros[index].Cantidad.toDouble())
        }

        //Guardar el excel
        try {
            val fileOutputStream = FileOutputStream(
                File(path, fileName)
            )
            workbook.write(fileOutputStream)
            fileOutputStream.close()
            workbook.close()
        }
        catch (e: IOException){
            e.printStackTrace()
        }
    }

    fun leerExcel():MutableList<Producto>{
        val fileName = "registros.xlsx"

        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath+"/"+fileName

        val lista = arrayListOf<String>()
        val listaNueva = arrayListOf<Producto>()

        try {
            val fileInputStream = FileInputStream(path)
            val workbook = WorkbookFactory.create(fileInputStream)
            val sheet: Sheet = workbook.getSheetAt(0)

            val rows = sheet.iterator()
            while (rows.hasNext()) {
                val currentRow = rows.next()

                // Iterar sobre celdas de la fila actual
                val cellsInRow = currentRow.iterator()
                while (cellsInRow.hasNext()) {
                    val currentCell = cellsInRow.next()

                    // Obtener valor de la celda como String
                    val cellValue: String = when (currentCell.cellType) {
                        CellType.STRING -> currentCell.stringCellValue
                        CellType.NUMERIC -> currentCell.numericCellValue.toString()
                        CellType.BOOLEAN -> currentCell.booleanCellValue.toString()
                        else -> ""
                    }

                    lista.add(cellValue)
                }
                listaNueva.add(
                    Producto(
                        lista[0],
                        lista[1],
                        lista[2].toDouble(),
                        lista[3].toInt()
                    )
                )
                lista.clear()
            }

            workbook.close()
            fileInputStream.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return listaNueva
    }

}