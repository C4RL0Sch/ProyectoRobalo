package tmz.jcmh.proyecto_robalo.util

import android.app.Application
import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class InternalStorageManager {

    suspend fun saveImage(Dir: File, bitmap: Bitmap, Filename: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (!Dir.exists()) {
                    Dir.mkdirs()
                }

                val imageFile = File(Dir, "$Filename.png")
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

    fun deleteImageFile(Dir:File, Filename: String) {
        if (!Dir.exists()) {
            Dir.mkdirs()
        }

        val imageFile = File(Dir, "$Filename.png")

        if (imageFile.exists()) {
            imageFile.delete()
        }
    }

    fun getImageFile(Dir:File, Filename: String): File? {
        if (!Dir.exists()) {
            Dir.mkdirs()
        }

        val imageFile = File(Dir, "$Filename.png")
        return if (imageFile.exists()) imageFile else null
    }
}