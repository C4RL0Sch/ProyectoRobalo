package tmz.jcmh.proyecto_robalo.ui.usuarios.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tmz.jcmh.proyecto_robalo.MyApp
import tmz.jcmh.proyecto_robalo.data.models.Usuario
import tmz.jcmh.proyecto_robalo.data.repository.UsuarioRepository
import tmz.jcmh.proyecto_robalo.util.InternalStorageManager
import java.io.File

class UsuariosViewModel (application: Application) : AndroidViewModel(application){
    private val repository: UsuarioRepository
        get() = (getApplication() as MyApp).usuarioRepository

    val allUsuarios = repository.usuarios

    val logUser = repository.logUser

    // LiveData para controlar errores o mensajes
    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> = _mensaje

    val internalManager = InternalStorageManager()

    fun saveImage(bitmap: Bitmap, filename: String){
        viewModelScope.launch {
            val Dir = getApplication<Application>().filesDir
            val usuariosDir = File(Dir, "usuarios")
            val success = internalManager.saveImage(usuariosDir, bitmap, filename)
            if (success) {
                //_mensaje.value = "Imagen guardada correctamente"
            } else {
                //_mensaje.value = "Error al guardar la imagen"
            }
        }
    }

    fun deleteImageFile(filename: String) {
        val Dir = getApplication<Application>().filesDir
        val usuariosDir = File(Dir, "usuarios")
        internalManager.deleteImageFile(usuariosDir, filename)
    }

    fun getImageFile(filename: String): File? {
        val Dir = getApplication<Application>().filesDir
        val usuariosDir = File(Dir, "usuarios")
        return internalManager.getImageFile(usuariosDir, filename)
    }
}