package tmz.jcmh.proyecto_robalo.ui.usuarios.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tmz.jcmh.proyecto_robalo.data.database.DatabaseRobalo
import tmz.jcmh.proyecto_robalo.data.models.Usuario
import tmz.jcmh.proyecto_robalo.data.repository.ProductoRepository
import tmz.jcmh.proyecto_robalo.data.repository.UsuarioRepository
import java.io.File
import java.io.FileOutputStream

class UsuariosViewModel (application: Application) : AndroidViewModel(application){
    private val repository: UsuarioRepository
    val allUsuarios: LiveData<List<Usuario>>

    private val _logUser = MutableLiveData<Usuario?>()
    val logUser: LiveData<Usuario?> = _logUser

    private val _isLoged = MutableLiveData<Boolean>()
    val isLoged: LiveData<Boolean> = _isLoged

    // LiveData para controlar errores o mensajes
    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> = _mensaje

    init {
        val Dao = DatabaseRobalo.getDatabase(application).daoUsuarios
        repository = UsuarioRepository(Dao)
        allUsuarios = repository.getAll()

    }

    fun login(username: String, password: String){
        viewModelScope.launch {
            val user = repository.getByUser(username)
            if(user!=null && user.Password==password){
                _logUser.postValue(user)
                _isLoged.postValue(true)
            }
            else{
                _logUser.postValue(null)
                _isLoged.postValue(false)
            }
        }
    }

    suspend fun getByUser(user: String): Usuario{
        val usuario = repository.getByUser(user)
        return usuario
    }

    suspend fun insert(usuario: Usuario):Boolean{
        val count = repository.CountByUser(usuario.Usuario)

        if(count==0) {
            repository.insert(usuario)
            return true
        }
        else{
            return false
        }
    }

    fun Delete(usuario: Usuario){
        viewModelScope.launch {
            repository.delete(usuario)
        }
    }

    fun Update(usuario: Usuario){
        viewModelScope.launch {
            repository.update(usuario)
        }
    }

    fun saveImage(bitmap: Bitmap, Filename: String){
        viewModelScope.launch {
            val success = saveImageToInternalStorage(bitmap, Filename)
            if (success) {
                //_mensaje.value = "Imagen guardada correctamente"
            } else {
                //_mensaje.value = "Error al guardar la imagen"
            }
        }
    }

    suspend fun saveImageToInternalStorage(bitmap: Bitmap, Filename: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val filesDir = getApplication<Application>().filesDir
                val productosDir = File(filesDir, "usuarios")
                if (!productosDir.exists()) {
                    productosDir.mkdirs()
                }

                val imageFile = File(productosDir, "$Filename.png")
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

        val productosDir = File(filesDir, "usuarios")
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

        val productosDir = File(filesDir, "usuarios")
        if (!productosDir.exists()) {
            productosDir.mkdirs()
        }

        val imageFile = File(productosDir, "$Filename.png")
        return if (imageFile.exists()) imageFile else null
    }
}