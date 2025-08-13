package tmz.jcmh.proyecto_robalo.ui.usuarios.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tmz.jcmh.proyecto_robalo.MyApp
import tmz.jcmh.proyecto_robalo.data.repository.UsuarioRepository

class LoginViewModel (application: Application) : AndroidViewModel(application){
    private val repository: UsuarioRepository
        get() = (getApplication() as MyApp).usuarioRepository

    val logUser = repository.logUser

    private val _isLoged = MutableLiveData<Boolean>()
    val isLoged: LiveData<Boolean> = _isLoged

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> = _mensaje

    fun login(username: String, password: String){
        viewModelScope.launch {
            val user = repository.getByUser(username)
            if(user!=null && user.Password==password){
                repository.login(user)
                _isLoged.postValue(true)
            }
            else{
                _isLoged.postValue(false)
            }
        }
    }
}