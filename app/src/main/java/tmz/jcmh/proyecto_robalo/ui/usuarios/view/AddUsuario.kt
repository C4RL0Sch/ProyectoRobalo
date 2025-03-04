package tmz.jcmh.proyecto_robalo.ui.usuarios.view

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import tmz.jcmh.proyecto_robalo.MyApp
import tmz.jcmh.proyecto_robalo.R
import tmz.jcmh.proyecto_robalo.data.models.Usuario
import tmz.jcmh.proyecto_robalo.databinding.ActivityAddUsuarioBinding
import tmz.jcmh.proyecto_robalo.ui.productos.viewmodel.ProductosViewModel
import tmz.jcmh.proyecto_robalo.ui.usuarios.viewmodel.UsuariosViewModel
import java.io.File

class AddUsuario : AppCompatActivity() {
    private lateinit var binding: ActivityAddUsuarioBinding
    val usuariosViewModel: UsuariosViewModel
        get() = (application as MyApp).usuarioViewModel

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
        binding = ActivityAddUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ArrayAdapter.createFromResource(this, R.array.UsersTypes, android.R.layout.simple_spinner_dropdown_item)
        binding.SpinnerTypeUser.adapter = adapter

        binding.btnCancelar.setOnClickListener(){
            finish()
        }

        binding.btnGuardar.setOnClickListener(){
            if(binding.txtNombre.text.toString().isEmpty() || binding.txtApellidoP.text.toString().isEmpty() || binding.txtApellidoM.text.toString().isEmpty() ||
                        binding.txtUser.text.toString().isEmpty() || binding.txtPassword.text.toString().isEmpty() || binding.txtPassword2.text.toString().isEmpty())
            {
                Toast.makeText(this, "DEBE LLENAR TODOS LOS CAMPOS", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val password = binding.txtPassword.text.toString()
            val passLength = password.length
            if(password.length<8){
                binding.PasswordError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if(password.replace("[0-9]".toRegex(), "").length == passLength){
                binding.PasswordError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if(password.replace("[a-z]".toRegex(), "").length == passLength){
                binding.PasswordError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if(password.replace("[A-Z]".toRegex(), "").length == passLength){
                binding.PasswordError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            binding.PasswordError.visibility = View.GONE

            if(binding.txtPassword.text.toString() != binding.txtPassword2.text.toString()){
                binding.PasswordError2.visibility = View.VISIBLE
                return@setOnClickListener
            }

            binding.PasswordError2.visibility = View.GONE

            val nuevo = Usuario(
                null,
                binding.txtNombre.text.toString(),
                binding.txtApellidoP.text.toString(),
                binding.txtApellidoM.text.toString(),
                binding.txtUser.text.toString(),
                binding.txtPassword.text.toString(),
                (binding.SpinnerTypeUser.selectedItemPosition+1)
            )

            lifecycleScope.launch {
                if(!usuariosViewModel.insert(nuevo)){
                    Toast.makeText(getContext(), "HAY OTRO USUARIO CON EL MISMO NOMBRE DE USUARIO", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                if (binding.img.drawable != null) {
                    // El ImageView tiene imagen
                    val drawable = binding.img.drawable
                    val bitmap = (drawable as BitmapDrawable).bitmap

                    usuariosViewModel.saveImage(bitmap, nuevo.Usuario)
                }

                binding.txtNombre.setText("")
                binding.txtApellidoP.setText("")
                binding.txtApellidoM.setText("")
                binding.txtUser.setText("")
                binding.txtPassword.setText("")
                finish()
            }
        }

        binding.btnGaleria.setOnClickListener(){
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnFoto.setOnClickListener(){
            solicitarPermisoCamara.launch(android.Manifest.permission.CAMERA)
        }

        binding.btnDeleteImg.setOnClickListener(){
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

    private fun abrirCamara() {
        uriFoto = crearUriTemporal()
        tomarFoto.launch(uriFoto!!)
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