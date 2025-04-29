package tmz.jcmh.proyecto_robalo.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import tmz.jcmh.proyecto_robalo.MyApp
import tmz.jcmh.proyecto_robalo.R
import tmz.jcmh.proyecto_robalo.databinding.ActivityMainBinding
import tmz.jcmh.proyecto_robalo.ui.productos.view.ImportActivity
import tmz.jcmh.proyecto_robalo.ui.productos.viewmodel.ProductosViewModel
import tmz.jcmh.proyecto_robalo.ui.usuarios.viewmodel.UsuariosViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private var doubleBackToExitPressedOnce = false

    val productoViewModel: ProductosViewModel
        get() = (application as MyApp).productoViewModel

    val usuariosViewModel: UsuariosViewModel
        get() = (application as MyApp).usuarioViewModel

    private lateinit var solicitarPermisos: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isAdmin = intent.getBooleanExtra("isAdmin",false)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                exitOnBackPressed()
            }
        })

        solicitarPermisos = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val aceptados = it.all { it.value }
            if (!aceptados) {
                Toast.makeText(this, "SE TIENE QUE ACEPTAR LOS PERMISOS", Toast.LENGTH_LONG).show()
            }
        }
        permisos()

        //INSTANCIACIÓN DE LOS FRAGMENTOS A USAR
        val AdminFragment = AdminFragment()
        val EmpleadosFragment = EmpleadosFragment()

        //INICIO DEL FRAGMENTO POR DEFECTO
        val puesto = usuariosViewModel.logUser.value?.Puesto
        if(puesto == 1)
        {
            supportFragmentManager.beginTransaction()
                .replace(R.id.FragmentContainer, AdminFragment)
                .commit()
        }
        else if(puesto == 2){
            supportFragmentManager.beginTransaction()
                .replace(R.id.FragmentContainer, EmpleadosFragment)
                .commit()
        }
        else if(puesto==null && isAdmin){
            supportFragmentManager.beginTransaction()
                .replace(R.id.FragmentContainer, AdminFragment)
                .commit()
        }

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

        //OBSERVADORES DEL VIEWMODEL
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
    }

    fun exitOnBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            return
        }

        if (doubleBackToExitPressedOnce) {
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            return
        }

        doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Presiona atrás otra vez para cerrar sesión", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)
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
            solicitarPermisos.launch(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }
}
