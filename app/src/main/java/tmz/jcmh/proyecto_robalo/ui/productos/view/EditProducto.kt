package tmz.jcmh.proyecto_robalo.ui.productos.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import tmz.jcmh.proyecto_robalo.MyApp
import tmz.jcmh.proyecto_robalo.data.models.Producto
import tmz.jcmh.proyecto_robalo.databinding.ActivityEditProductoBinding
import tmz.jcmh.proyecto_robalo.ui.productos.viewmodel.ProductosViewModel
import java.io.File

class EditProducto : AppCompatActivity() {
    private lateinit var binding: ActivityEditProductoBinding
    val productoViewModel: ProductosViewModel
        get() = (application as MyApp).productoViewModel

    private lateinit var producto: Producto

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){ uri ->
        if(uri != null){
            binding.imgNotFound.visibility = View.GONE
            binding.img.visibility = View.VISIBLE
            binding.img.setImageURI(uri)
        }
    }

    private var uriFoto: Uri? = null

    private val solicitarPermisoCamara = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            abrirCamara()
        } else {
            Toast.makeText(this, "Se requiere permiso para usar la cámara", Toast.LENGTH_SHORT).show()
        }
    }

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

        binding.btnGaleria.setOnClickListener(){
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnFoto.setOnClickListener(){
            solicitarPermisoCamara.launch(android.Manifest.permission.CAMERA)
        }

    }

    private val tomarFoto = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            uriFoto?.let {
                binding.imgNotFound.visibility = View.GONE
                binding.img.visibility = View.VISIBLE
                binding.img.setImageURI(it)
            }
        } else {
            Toast.makeText(this, "No se tomó ninguna foto", Toast.LENGTH_SHORT).show()
        }
    }

    private fun abrirCamara() {
        uriFoto = crearUriTemporal()
        tomarFoto.launch(uriFoto!!)
    }

    private fun crearUriTemporal(): Uri {
        val archivoFoto = File.createTempFile("IMG_", ".jpg", externalCacheDir).apply {
            createNewFile()
        }
        return FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.provider",
            archivoFoto
        )
    }
}