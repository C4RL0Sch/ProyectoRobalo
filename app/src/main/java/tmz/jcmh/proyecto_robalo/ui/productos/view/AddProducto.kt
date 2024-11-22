package tmz.jcmh.proyecto_robalo.ui.productos.view

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import tmz.jcmh.proyecto_robalo.R
import tmz.jcmh.proyecto_robalo.data.models.Producto
import tmz.jcmh.proyecto_robalo.data.repository.ProductoRepository
import tmz.jcmh.proyecto_robalo.databinding.ActivityAddProductoBinding
import tmz.jcmh.proyecto_robalo.ui.productos.viewmodel.ProductosViewModel

class AddProducto() : AppCompatActivity() {
    private lateinit var binding: ActivityAddProductoBinding
    private val productoViewModel: ProductosViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCancelar.setOnClickListener(){
            finish()
        }

        binding.btnGuardar.setOnClickListener(){

            if(binding.txtCodigo.text.toString().isEmpty() || binding.txtNombre.text.toString().isEmpty() || binding.txtPresentacion.text.toString().isEmpty() ||
                binding.txtPrecio.text.toString().isEmpty() || binding.txtCantidad.text.toString().isEmpty()){
                Toast.makeText(this, "DEBE LLENAR TODOS LOS CAMPOS", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nuevo = Producto(
                null,
                binding.txtCodigo.text.toString(),
                binding.txtNombre.text.toString(),
                binding.txtPresentacion.text.toString(),
                binding.txtPrecio.text.toString().toDouble(),
                binding.txtCantidad.text.toString().toInt(),
            )

            lifecycleScope.launch {
                if(!productoViewModel.insert(nuevo)){
                    Toast.makeText(getContext(), "HAY OTRO PRODUCTO CON EL MISMO CÓDIGO", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                binding.txtCodigo.setText("")
                binding.txtNombre.setText("")
                binding.txtPresentacion.setText("")
                binding.txtPrecio.setText("")
                binding.txtCantidad.setText("")
                finish()
            }
        }

    }

    private fun getContext(): Context {
        return this
    }
}