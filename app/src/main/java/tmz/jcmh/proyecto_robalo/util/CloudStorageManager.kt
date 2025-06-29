package tmz.jcmh.proyecto_robalo.util

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class CloudStorageManager {
    private val storage = FirebaseStorage.getInstance()

    suspend fun saveImage(contentResolver: ContentResolver, Dir: String, uriFoto: Uri, Filename: String): String{
        return withContext(Dispatchers.IO) {
            try {
                val source = ImageDecoder.createSource(contentResolver, uriFoto)
                val bitmap = ImageDecoder.decodeBitmap(source)

                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 800, 800 * bitmap.height / bitmap.width, true)

                val baos = ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos) // Calidad: 75/100
                val imageData = baos.toByteArray()

                val ref = storage.reference.child("$Dir/$Filename.jpg")

                ref.putBytes(imageData).await()

                val url = ref.downloadUrl.await()

                url.toString()
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }
    }

    fun deleteImageFile(Dir: String, Filename: String){
        val ref = storage.reference.child("$Dir/$Filename.jpg")
        ref.delete()
    }

}