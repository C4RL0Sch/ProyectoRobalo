package tmz.jcmh.proyecto_robalo.ui.productos.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import tmz.jcmh.proyecto_robalo.data.models.Producto
import tmz.jcmh.proyecto_robalo.databinding.ActivityEditProductoBinding
import tmz.jcmh.proyecto_robalo.ui.productos.viewmodel.ProductosViewModel

class EditProducto : AppCompatActivity() {
    private lateinit var binding: ActivityEditProductoBinding
    private val productoViewModel: ProductosViewModel by viewModels()
    private lateinit var producto: Producto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProductoBinding.inflate(layoutInflater)

        val codigo = intent.getStringExtra("codigo")

        if (codigo != null) {
            lifecycleScope.launch {
                producto = productoViewModel.getByCode(codigo)

                binding.txtCodigo.setText(producto.Codigo)
                binding.txtNombre.setText(producto.Nombre)
                binding.txtPresentacion.setText(producto.Presentacion)
                binding.txtPrecio.setText(producto.Precio.toString())
                binding.txtCantidad.setText(producto.Cantidad.toString())
            }
        }

        setContentView(binding.root)

        binding.btnCancel.setOnClickListener(){
            finish()
        }

        binding.btnEdit.setOnClickListener(){
            if(binding.txtCodigo.text.toString().isEmpty() || binding.txtNombre.text.toString().isEmpty() || binding.txtPresentacion.text.toString().isEmpty() ||
                binding.txtPrecio.text.toString().isEmpty() || binding.txtCantidad.text.toString().isEmpty()){
                Toast.makeText(this, "DEBE LLENAR TODOS LOS CAMPOS", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            producto.Nombre = binding.txtNombre.text.toString()
            producto.Presentacion = binding.txtPresentacion.text.toString()
            producto.Precio = binding.txtPrecio.text.toString().toDouble()
            producto.Cantidad = binding.txtCantidad.text.toString().toInt()

            productoViewModel.Update(producto)
            finish()
        }

        binding.btnDelete.setOnClickListener(){
            productoViewModel.Delete(producto)
            finish()
        }

    }
}