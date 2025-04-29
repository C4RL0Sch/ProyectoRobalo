package tmz.jcmh.proyecto_robalo.ui.productos.view

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import tmz.jcmh.proyecto_robalo.MyApp
import tmz.jcmh.proyecto_robalo.R
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
            binding.btnDeleteImg.visibility = View.VISIBLE
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
        setContentView(binding.root)

        val medidas = resources.getStringArray(R.array.Medidas)
        val categorias = resources.getStringArray(R.array.Categorias)

        binding.SpinnerCategoria.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categorias)
        binding.SpinnerMedida.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, medidas)

        val codigo = intent.getStringExtra("codigo")

        if (codigo != null) {
            lifecycleScope.launch {
                producto = productoViewModel.getByCode(codigo)
                binding.txtCodigo.setText(producto.Codigo)
                binding.txtNombre.setText(producto.Nombre)
                binding.txtPresentacion.setText(producto.Presentacion)
                binding.txtPrecio.setText(producto.Precio.toString())
                binding.txtCantidad.setText(producto.Cantidad.toString())
                binding.txtMarca.setText(producto.Marca)
                binding.SpinnerMedida.setSelection(medidas.indexOf(producto.Medida))
                binding.SpinnerCategoria.setSelection(categorias.indexOf(producto.Categoria))

                val imageFile = productoViewModel.getImageFile(producto.Codigo ?: "")
                if (imageFile != null && imageFile.exists()) {
                    binding.imgNotFound.visibility = View.GONE
                    binding.img.visibility = View.VISIBLE
                    binding.img.setImageURI(Uri.fromFile(imageFile))
                    binding.btnDeleteImg.visibility = View.VISIBLE
                }
            }

            binding.btnCancel.setOnClickListener() {
                finish()
            }

            binding.btnEdit.setOnClickListener() {
                if (binding.txtCodigo.text.toString().isEmpty() || binding.txtNombre.text.toString()
                        .isEmpty() ||
                    binding.txtPresentacion.text.toString()
                        .isEmpty() || binding.txtPrecio.text.toString().isEmpty() ||
                    binding.txtCantidad.text.toString()
                        .isEmpty() || binding.txtMarca.text.toString().isEmpty()
                ) {
                    Toast.makeText(this, "DEBE LLENAR TODOS LOS CAMPOS", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                AlertDialog.Builder(this)
                    .setTitle("Confirmar edición")
                    .setMessage("¿Está seguro de que desea guardar los cambios?")
                    .setPositiveButton("Guardar") { dialog, _ ->
                        producto.Codigo = binding.txtCodigo.text.toString()
                        producto.Nombre = binding.txtNombre.text.toString()
                        producto.Marca = binding.txtMarca.text.toString()
                        producto.Categoria = binding.SpinnerCategoria.selectedItem.toString()
                        producto.Medida = binding.SpinnerMedida.selectedItem.toString()
                        producto.Presentacion = binding.txtPresentacion.text.toString()
                        producto.Precio = binding.txtPrecio.text.toString().toDouble()
                        producto.Cantidad = binding.txtCantidad.text.toString().toDouble()

                        productoViewModel.deleteImageFile(producto.Codigo ?: "")
                        if (binding.img.drawable != null) {
                            // El ImageView tiene imagen
                            val drawable = binding.img.drawable
                            val bitmap = (drawable as BitmapDrawable).bitmap

                            productoViewModel.saveImageToInternalStorage(
                                bitmap,
                                producto.Codigo ?: ""
                            )
                        }
                        productoViewModel.Update(producto)
                        dialog.dismiss()
                        finish()
                    }
                    .setNegativeButton("Cancelar") { dialog, _ ->
                        dialog.dismiss() // Cierra el diálogo sin hacer nada
                    }
                    .create()
                    .show()
            }

            binding.btnDelete.setOnClickListener() {
                AlertDialog.Builder(this)
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Está seguro de que desea eliminar permanentemente el producto?")
                    .setPositiveButton("Eliminar") { dialog, _ ->
                        productoViewModel.deleteImageFile(producto.Codigo ?: "")
                        productoViewModel.Delete(producto)
                        dialog.dismiss()
                        finish()
                    }
                    .setNegativeButton("Cancelar") { dialog, _ ->
                        dialog.dismiss() // Cierra el diálogo sin hacer nada
                    }
                    .create()
                    .show()
            }

            binding.btnGaleria.setOnClickListener() {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            binding.btnFoto.setOnClickListener() {
                solicitarPermisoCamara.launch(android.Manifest.permission.CAMERA)
            }

            binding.btnDeleteImg.setOnClickListener() {
                AlertDialog.Builder(this)
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Está seguro de que desea eliminar la imagen?")
                    .setPositiveButton("Eliminar") { dialog, _ ->
                        binding.imgNotFound.visibility = View.VISIBLE
                        binding.img.visibility = View.GONE
                        binding.btnDeleteImg.visibility = View.GONE
                        binding.img.setImageDrawable(null)
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancelar") { dialog, _ ->
                        dialog.dismiss() // Cierra el diálogo sin hacer nada
                    }
                    .create()
                    .show()
            }
        }
        else{
            finish()
        }

    }

    private val tomarFoto = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            uriFoto?.let {
                binding.imgNotFound.visibility = View.GONE
                binding.img.visibility = View.VISIBLE
                binding.btnDeleteImg.visibility = View.VISIBLE
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