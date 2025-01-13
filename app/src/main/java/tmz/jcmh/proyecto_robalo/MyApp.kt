package tmz.jcmh.proyecto_robalo

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import tmz.jcmh.proyecto_robalo.ui.productos.viewmodel.ProductosViewModel

class MyApp : Application() {

    // Variable para almacenar la instancia del ViewModel
    lateinit var productoViewModel: ProductosViewModel

    override fun onCreate() {
        super.onCreate()

        // Inicializa el ViewModel usando ViewModelProvider
        productoViewModel = ViewModelProvider(
            ViewModelStore(),
            ViewModelProvider.AndroidViewModelFactory.getInstance(this)
        ).get(ProductosViewModel::class.java)
    }
}