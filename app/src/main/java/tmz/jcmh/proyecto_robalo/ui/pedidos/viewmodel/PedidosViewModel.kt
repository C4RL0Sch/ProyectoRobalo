package tmz.jcmh.proyecto_robalo.ui.pedidos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import tmz.jcmh.proyecto_robalo.MyApp

class PedidosViewModel (application: Application) : AndroidViewModel(application) {
    private val repository = (getApplication() as MyApp).pedidoRepository
    private val productosRepository = (getApplication() as MyApp).productoRepository

    val pedidos = repository.pedidos

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> = _mensaje
}