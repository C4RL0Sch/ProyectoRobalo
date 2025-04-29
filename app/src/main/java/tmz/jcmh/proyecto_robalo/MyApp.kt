package tmz.jcmh.proyecto_robalo

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import tmz.jcmh.proyecto_robalo.ui.productos.viewmodel.ProductosViewModel
import tmz.jcmh.proyecto_robalo.ui.usuarios.viewmodel.UsuariosViewModel

class MyApp : Application() {

    // Variable para almacenar la instancia del ViewModel
    lateinit var productoViewModel: ProductosViewModel
    lateinit var usuarioViewModel: UsuariosViewModel

    override fun onCreate() {
        super.onCreate()

        // Inicializa el ViewModel usando ViewModelProvider
        productoViewModel = ViewModelProvider(
            ViewModelStore(),
            ViewModelProvider.AndroidViewModelFactory.getInstance(this)
        ).get(ProductosViewModel::class.java)

        usuarioViewModel = ViewModelProvider(
            ViewModelStore(),
            ViewModelProvider.AndroidViewModelFactory.getInstance(this)
        ).get(UsuariosViewModel::class.java)

    }
}