package tmz.jcmh.proyecto_robalo.ui.usuarios.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tmz.jcmh.proyecto_robalo.data.models.Usuario
import tmz.jcmh.proyecto_robalo.data.repository.UsuarioRepository
import tmz.jcmh.proyecto_robalo.util.InternalStorageManager
import java.io.File

class UsuariosViewModel (application: Application) : AndroidViewModel(application){
    private val repository: UsuarioRepository = UsuarioRepository()
    val allUsuarios: LiveData<List<Usuario>>

    private val _logUser = MutableLiveData<Usuario?>()
    val logUser: LiveData<Usuario?> = _logUser

    private val _isLoged = MutableLiveData<Boolean>()
    val isLoged: LiveData<Boolean> = _isLoged

    // LiveData para controlar errores o mensajes
    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> = _mensaje

    val internalManager = InternalStorageManager()

    init{
        allUsuarios = repository.usuarios
    }

    fun login(username: String, password: String){
        viewModelScope.launch {
            /*val user = repository.getByUser(username)
            if(user!=null && user.Password==password){
                _logUser.postValue(user)
                _isLoged.postValue(true)
            }
            else{
                _logUser.postValue(null)
                _isLoged.postValue(false)
            }*/
        }
    }

    fun isUserAvalible(usuario: String): Boolean{
        val lista = allUsuarios.value?.toList()
        val user = lista?.find { p-> p.Usuario == usuario }
        if(user == null){
            return true
        }
        else{
            return false
        }
    }

    suspend fun getByUser(user: String): Usuario{
        val usuario = repository.getByUser(user)
        return usuario
    }

    fun insert(usuario: Usuario){
        viewModelScope.launch {
            repository.insert(usuario)
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