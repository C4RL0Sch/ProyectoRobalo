package tmz.jcmh.proyecto_robalo.ui.productos.view

import android.app.ProgressDialog
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import tmz.jcmh.proyecto_robalo.MyApp
import tmz.jcmh.proyecto_robalo.R
import tmz.jcmh.proyecto_robalo.data.models.Producto
import tmz.jcmh.proyecto_robalo.databinding.ActivityAddProductoBinding
import tmz.jcmh.proyecto_robalo.ui.productos.viewmodel.ProductosViewModel
import java.io.File
import java.io.IOException

class AddProducto() : AppCompatActivity() {
    private lateinit var binding: ActivityAddProductoBinding
    val productoViewModel: ProductosViewModel
        get() = (application as MyApp).productoViewModel

    private lateinit var progressDialog : ProgressDialog

    private var uriFoto: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        val medidas = resources.getStringArray(R.array.Medidas)
        val categorias = resources.getStringArray(R.array.Categorias)

        binding.SpinnerCategoria.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categorias)
        binding.SpinnerMedida.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, medidas)

        binding.txtNombre.addTextChangedListener(){
            val text = binding.txtNombre.text.toString().trim()
            var cod = ""
            if(text.contains(" ") && text.length>=3)
            {
                val words = text.split(" ")
                for(word in words){
                    cod+= word[0].uppercaseChar()
                }
            }
            else if(text.length>=3){
                cod = text.substring(0,3).uppercase()
            }
            if (cod!=""){
                val cont = productoViewModel.countByCode(cod)
                if(cont!=null && cont>0){
                    cod = "$cod$cont"
                }
            }
            binding.txtCodigo.setText(cod)
        }

        productoViewModel.mensaje.observe(this){ event ->
            event.peekContent().let{ msj ->
                if (msj == "Guardado correctamente"){
                    finish()
                }
            }
            event.getContentIfNotHandled()?.let { msj ->
                Toast.makeText(this, msj, Toast.LENGTH_LONG).show()
            }
        }

        productoViewModel.loadingMsg.observe(this) { message ->
            progressDialog.setMessage(message)
        }

        productoViewModel.isLoading.observe(this){ isLoading ->
            if(isLoading){
                progressDialog.show()
            }
            else{
                progressDialog.dismiss()
            }
        }

        binding.btnCancelar.setOnClickListener(){
            finish()
        }

        binding.btnGuardar.setOnClickListener(){
            try{
                if(!validarCampos()){
                    return@setOnClickListener
                }

                val nuevo = Producto(
                    binding.txtCodigo.text.toString(),
                    binding.txtNombre.text.toString(),
                    binding.txtMarca.text.toString(),
                    binding.SpinnerCategoria.selectedItem.toString(),
                    binding.txtPresentacion.text.toString(),
                    binding.SpinnerMedida.selectedItem.toString(),
                    binding.txtPrecio.text.toString().toDouble(),
                    binding.txtCantidad.text.toString().toDouble()
                )

                guardarProducto(nuevo)
            }
            catch(e: IOException){
                Toast.makeText(getContext(), "Error al guardar el producto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }

        binding.btnGaleria.setOnClickListener(){
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnFoto.setOnClickListener(){
            solicitarPermisoCamara.launch(android.Manifest.permission.CAMERA)
        }

        binding.btnDeleteImg.setOnClickListener(){
            showDialog()
        }
    }

    private fun guardarProducto(nuevo: Producto){
        if(uriFoto!=null) {
            productoViewModel.insertWithImage(nuevo, contentResolver, uriFoto!!)
            return
        }

        productoViewModel.insert(nuevo)
    }

    private fun validarCampos(): Boolean{
        if(binding.txtNombre.text.toString().isEmpty() ||
            binding.txtMarca.text.isEmpty() || binding.txtPresentacion.text.toString().isEmpty() ||
            binding.txtPrecio.text.toString().isEmpty() || binding.txtCantidad.text.toString().isEmpty()){
            Toast.makeText(this, "DEBE LLENAR TODOS LOS CAMPOS", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(binding.txtNombre.text.toString().length<3){
            Toast.makeText(this, "EL NOMBRE DEL PRODUCTO DEBE DE TENER MÁS DE TRES LETRAS", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun showDialog(){
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

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){ uri ->
        if(uri != null){
            binding.imgNotFound.visibility = View.GONE
            binding.img.visibility = View.VISIBLE
            binding.btnDeleteImg.visibility = View.VISIBLE
            binding.img.setImageURI(uri)
            uriFoto = uri
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

    private val solicitarPermisoCamara = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            abrirCamara()
        } else {
            Toast.makeText(this, "Se requiere permiso para usar la cámara", Toast.LENGTH_SHORT).show()
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

    private fun getContext(): Context {
        return this
    }
}