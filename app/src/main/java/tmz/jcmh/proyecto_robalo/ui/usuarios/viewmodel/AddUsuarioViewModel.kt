package tmz.jcmh.proyecto_robalo.ui.usuarios.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tmz.jcmh.proyecto_robalo.MyApp
import tmz.jcmh.proyecto_robalo.data.models.Producto
import tmz.jcmh.proyecto_robalo.data.models.Usuario
import tmz.jcmh.proyecto_robalo.data.repository.UsuarioRepository
import tmz.jcmh.proyecto_robalo.util.CloudStorageManager
import tmz.jcmh.proyecto_robalo.util.Event

class AddUsuarioViewModel (application: Application) : AndroidViewModel(application) {
    private val repository: UsuarioRepository
        get() = (getApplication() as MyApp).usuarioRepository

    val allUsuarios = repository.usuarios

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> = _mensaje

    val isLoading = MutableLiveData<Boolean>()
    val loadingMsg = MutableLiveData<String>()

    val cloudManager = CloudStorageManager()

    init {
        isLoading.value = false
        loadingMsg.value = "Espere por favor"
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

    fun insert(usuario: Usuario){
        viewModelScope.launch {
            isLoading.value = true
            loadingMsg.value = "Guardando el usuario"
            val resp = repository.insert(usuario)
            if(!resp){
                _mensaje.value = "No se puedo guardar la el usuario"
            }
            else{
                _mensaje.value = "Guardado correctamente"
            }
            isLoading.value = false
        }
    }

    fun insertWithImage(usuario: Usuario, contentResolver: ContentResolver, uri: Uri) {
        viewModelScope.launch {
            isLoading.value = true
            loadingMsg.value = "Guardando el usuario"
            val res = cloudManager.saveImage(contentResolver, "usuarios", uri, usuario.Usuario)
            if(res==""){
                _mensaje.value = "No se puedo guardar la imagen"
            }
            else{
                usuario.imgUrl  = res
            }

            val resp = repository.insert(usuario)
            if(!resp){
                _mensaje.value = "No se puedo guardar la el usuario"
            }
            else{
                _mensaje.value = "Guardado correctamente"
            }
            isLoading.value = false
        }
    }
}