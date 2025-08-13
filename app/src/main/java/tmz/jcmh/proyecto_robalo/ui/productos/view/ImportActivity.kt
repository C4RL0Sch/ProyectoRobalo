package tmz.jcmh.proyecto_robalo.ui.productos.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import tmz.jcmh.proyecto_robalo.MyApp
import tmz.jcmh.proyecto_robalo.R
import tmz.jcmh.proyecto_robalo.databinding.ActivityImportBinding
import tmz.jcmh.proyecto_robalo.ui.productos.adapter.ProductoResumen_Adapter
import tmz.jcmh.proyecto_robalo.ui.productos.viewmodel.ProductosViewModel

class ImportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImportBinding
    val viewModel: ProductosViewModel by viewModels()

    // Adapters para cada RecyclerView
    private lateinit var nuevosAdapter: ProductoResumen_Adapter
    private lateinit var editadosAdapter: ProductoResumen_Adapter
    private lateinit var eliminadosAdapter: ProductoResumen_Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa los adaptadores una sola vez
        nuevosAdapter = ProductoResumen_Adapter(emptyList())
        editadosAdapter = ProductoResumen_Adapter(emptyList())
        eliminadosAdapter = ProductoResumen_Adapter(emptyList())

        // Asocia los adaptadores a los RecyclerView
        binding.rvListaNuevos.adapter = nuevosAdapter
        binding.rvListaEditados.adapter = editadosAdapter
        binding.rvListaEliminados.adapter = eliminadosAdapter

        // Observa los cambios en el LiveData y actualiza las listas del adaptador
        viewModel.productosNuevos.observe(this) { nuevos ->
            nuevosAdapter.updateList(nuevos)
        }

        viewModel.productosModificados.observe(this) { editados ->
            editadosAdapter.updateList(editados)
        }

        viewModel.productosEliminados.observe(this) { eliminados ->
            eliminadosAdapter.updateList(eliminados)
        }

        // Observa mensajes (para Toast u otras notificaciones)
        viewModel.mensaje.observe(this) { event ->
            event.getContentIfNotHandled()?.let { msj ->
                Toast.makeText(this, msj, Toast.LENGTH_LONG).show()
            }
        }

        // Botones Confirmar/Cancelar
        binding.btnConfirmar.setOnClickListener {
            //Llama al método que aplica los cambios en la BD
            viewModel.confirmarCambiosImportacion()
            finish()
        }

        binding.btnCancelar.setOnClickListener {
            // Cierra la actividad sin aplicar los cambios
            finish()
        }
    }

}