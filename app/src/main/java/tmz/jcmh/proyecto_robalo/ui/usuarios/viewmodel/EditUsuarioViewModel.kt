package tmz.jcmh.proyecto_robalo.ui.usuarios.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tmz.jcmh.proyecto_robalo.MyApp
import tmz.jcmh.proyecto_robalo.data.models.Usuario
import tmz.jcmh.proyecto_robalo.data.repository.UsuarioRepository
import tmz.jcmh.proyecto_robalo.util.CloudStorageManager
import tmz.jcmh.proyecto_robalo.util.Event
import tmz.jcmh.proyecto_robalo.util.InternalStorageManager
import java.io.File

class EditUsuarioViewModel (application: Application) : AndroidViewModel(application){
    private val repository: UsuarioRepository
        get() = (getApplication() as MyApp).usuarioRepository

    val allUsuarios = repository.usuarios

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> = _mensaje

    val isLoading = MutableLiveData<Boolean>()
    val loadingMsg = MutableLiveData<String>()

    val internalManager = InternalStorageManager()
    val cloudManager = CloudStorageManager()

    fun isOtherUserAvalible(usuarioActual: String, usuarioNuevo: String): Boolean{
        if(usuarioActual == usuarioNuevo)
            return true

        val lista = allUsuarios.value?.toList()
        val user = lista?.find { p-> p.Usuario == usuarioNuevo}
        if(user == null)
            return true
        else
            return false
    }

    suspend fun getByUser(user: String): Usuario{
        val usuario = repository.getByUser(user)
        return usuario
    }

    fun Delete(usuario: Usuario){
        viewModelScope.launch {
            isLoading.value = true
            loadingMsg.value = "Eliminando el usuario"
            val res = repository.delete(usuario)
            if(res){
                _mensaje.value = "Usuario eliminado correctamente"
            }
            else{
                _mensaje.value = "Error al eliminar el usuario"
            }
            isLoading.value = false
        }
    }

    fun Update(usuario: Usuario){
        viewModelScope.launch {
            isLoading.value = true
            loadingMsg.value = "Actualizando el usuario"
            val res = repository.update(usuario)
            if(res){
                _mensaje.value = "Usuario actualizado correctamente"
            }
            else{
                _mensaje.value = "Error al actualizar el usuario"
            }
            isLoading.value = false
        }
    }

    fun UpdateWithImage(usuario: Usuario, contentResolver: ContentResolver, uri: Uri){
        viewModelScope.launch {
            isLoading.value = true
            loadingMsg.value = "Actualizando el usuario"
            val resp = cloudManager.saveImage(contentResolver, "usuarios", uri, usuario.Usuario)
            if(resp==""){
                _mensaje.value = "No se puedo actualizar la imagen"
            }
            else{
                usuario.imgUrl  = resp
            }

            val res = repository.update(usuario)
            if(res){
                _mensaje.value = "Usuario actualizado correctamente"
            }
            else{
                _mensaje.value = "Error al actualizar el usuario"
            }
            isLoading.value = false
        }
    }

    fun deleteCloudImage(Filename: String){
        cloudManager.deleteImageFile("usuarios", Filename)
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