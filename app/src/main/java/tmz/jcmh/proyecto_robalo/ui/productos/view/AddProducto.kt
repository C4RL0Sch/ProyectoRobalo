package tmz.jcmh.proyecto_robalo.ui.productos.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import tmz.jcmh.proyecto_robalo.R
import tmz.jcmh.proyecto_robalo.data.models.Producto
import tmz.jcmh.proyecto_robalo.data.repository.ProductoRepository
import tmz.jcmh.proyecto_robalo.databinding.ActivityAddProductoBinding

class AddProducto(private val repository: ProductoRepository) : AppCompatActivity() {
    private lateinit var binding: ActivityAddProductoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCancelar.setOnClickListener(){
            finish()
        }

        binding.btnGuardar.setOnClickListener(){
            val nuevo = Producto(
                null,
                binding.txtNombre.text.toString(),
                binding.txtPresentacion.text.toString(),
                binding.txtPrecio.text.toString().toDouble(),
                binding.txtCantidad.text.toString().toInt(),
            )

            if(nuevo.Nombre.isEmpty() || nuevo.Presentacion.isEmpty() ||
                nuevo.Precio.toString().isEmpty() || nuevo.Cantidad.toString().isEmpty()){
                Toast.makeText(this, "DEBE LLENAR TODOS LOS CAMPOS", Toast.LENGTH_LONG).show()
            }else{
                binding.txtNombre.setText("")
                binding.txtPresentacion.setText("")
                binding.txtPrecio.setText("")
                binding.txtCantidad.setText("")

                insertarProducto(nuevo)
            }
        }

    }

    fun insertarProducto(producto: Producto){
        lifecycleScope.launch {
            repository.insert(producto)
        }
    }
}