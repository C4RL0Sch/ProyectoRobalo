package tmz.jcmh.proyecto_robalo

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import tmz.jcmh.proyecto_robalo.data.repository.PedidoRepository
import tmz.jcmh.proyecto_robalo.data.repository.ProductoRepository
import tmz.jcmh.proyecto_robalo.data.repository.UsuarioRepository
import tmz.jcmh.proyecto_robalo.ui.productos.viewmodel.ProductosViewModel
import tmz.jcmh.proyecto_robalo.ui.usuarios.viewmodel.UsuariosViewModel

class MyApp : Application() {

    val productoRepository: ProductoRepository by lazy {
        ProductoRepository()
    }

    val usuarioRepository: UsuarioRepository by lazy {
        UsuarioRepository()
    }

    val pedidoRepository: PedidoRepository by lazy {
        PedidoRepository()
    }

    override fun onCreate() {
        super.onCreate()
    }
}