package tmz.jcmh.proyecto_robalo.ui.productos.view

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import tmz.jcmh.proyecto_robalo.MyApp
import tmz.jcmh.proyecto_robalo.R
import tmz.jcmh.proyecto_robalo.databinding.ActivityMainBinding
import tmz.jcmh.proyecto_robalo.ui.productos.adapter.ProductoAdapter
import tmz.jcmh.proyecto_robalo.ui.productos.viewmodel.ProductosViewModel
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout

    private lateinit var adapter: ProductoAdapter
    val productoViewModel: ProductosViewModel
        get() = (application as MyApp).productoViewModel

    private lateinit var solicitarPermisos: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Referencia al DrawerLayout
        drawerLayout = binding.drawerLayout

        val navigationView: NavigationView = binding.navigationView

        // Configurar el icono de menú y sincronizar el estado del drawer
        val toolbar: MaterialToolbar = binding.topAppBar
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_export -> {
                    // Acción para el ítem "Exportar"
                    if(productoViewModel.allProductos.value?.toMutableList() == null){
                        Toast.makeText(this, "NO HAY REGISTROS", Toast.LENGTH_LONG).show()
                        return@setNavigationItemSelectedListener true
                    }

                    createFileLauncher.launch("registros.xlsx")
                }
                R.id.nav_import -> {
                    // Acción para el ítem "Importar"
                    selectFileLauncher.launch(arrayOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                }
                // Maneja otros ítems
            }
            drawerLayout.closeDrawers()
            true
        }

        solicitarPermisos = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            val aceptados = it.all{ it.value }
            if(!aceptados){
                Toast.makeText(this, "SE TIENE QUE ACEPTAR LOS PERMISOS", Toast.LENGTH_LONG).show()
            }
        }

        permisos()

        adapter = ProductoAdapter(emptyList(), emptyMap<String, File>())

        productoViewModel.allProductos.observe(this, Observer {
            val images: Map<String, File> = it.mapNotNull { producto->
                val imageFile = productoViewModel.getImageFile(producto.Codigo)
                if (imageFile != null) {
                    producto.Codigo to imageFile
                } else {
                    null
                }
            }.toMap()
            adapter.UpdateList(it, images)
            binding.rvListaRegistros.adapter = adapter
        })

        productoViewModel.mensaje.observe(this) { msj ->
            Toast.makeText(this, msj, Toast.LENGTH_LONG).show()
        }

        productoViewModel.lecturaFinalizada.observe(this) { finalizada ->
            if (finalizada) {
                // Abrir la pantalla del preview
                val intent = Intent(this, ImportActivity::class.java)
                startActivity(intent)
            }
        }

        binding.btnAdd.setOnClickListener(){
            val intent = Intent(this, AddProducto::class.java)
            startActivity(intent)
        }
    }

    private val createFileLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) { uri ->
        uri?.let {
            productoViewModel.exportarExcel(it, contentResolver)
        }
    }

    private val selectFileLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            productoViewModel.importarExcel(it, contentResolver)
        }
    }

    private fun permisos() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            // Solicita permisos solo para Android 10 o inferior
            solicitarPermisos.launch(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }
}