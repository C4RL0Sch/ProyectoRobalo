package tmz.jcmh.proyecto_robalo.ui.productos.view

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.launch
import tmz.jcmh.proyecto_robalo.data.database.Database
import tmz.jcmh.proyecto_robalo.data.models.Producto
import tmz.jcmh.proyecto_robalo.data.repository.ProductoRepository
import tmz.jcmh.proyecto_robalo.databinding.ActivityMainBinding
import tmz.jcmh.proyecto_robalo.ui.productos.adapter.ProductoAdapter
import tmz.jcmh.proyecto_robalo.utils.Excel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ProductoAdapter

    //instancias de la base de datos
    private val db = Room.databaseBuilder(this, Database::class.java, "db_robalo").build()
    private val daoProductos = db.daoProductos
    private val repository = ProductoRepository(daoProductos)

    private var listaRegistros = listOf<Producto>()
    private lateinit var Excel:Excel
    private lateinit var solicitarPermisos: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        Excel = Excel()
        setContentView(binding.root)

        solicitarPermisos = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            val aceptados = it.all{ it.value }
            if(!aceptados){
                Toast.makeText(this, "SE TIENE QUE ACEPTAR LOS PERMISOS", Toast.LENGTH_LONG).show()
            }
        }

        permisos()

        /*binding.btnAdd.setOnClickListener(){
            val lista=listaRegistros.toMutableList()

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
                listaRegistros=lista
                binding.txtNombre.setText("")
                binding.txtPresentacion.setText("")
                binding.txtPrecio.setText("")
                binding.txtCantidad.setText("")

                lista.add(nuevo)
                setupRecyclerView()
                insertarProducto(nuevo)
            }
        }*/

        /*binding.btnSave.setOnClickListener(){
            if(listaRegistros.isNotEmpty()){
                Excel.crearExcel(listaRegistros.toMutableList())
                Toast.makeText(this, "SE GUARDO EL ARCHIVO CORRECTAMENTE", Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(this, "NO HAY NINGUN REGISTRO PARA GUARDAR", Toast.LENGTH_LONG).show()
            }
        }*/

        /*binding.btnRead.setOnClickListener(){
            listaRegistros=Excel.leerExcel()
            setupRecyclerView()
        }*/

        /*binding.btnReadDb.setOnClickListener(){
            consultarProductos()
        }*/

        consultarProductos()
    }

    fun setupRecyclerView(){
        adapter = ProductoAdapter(listaRegistros)
        binding.rvListaRegistros.adapter = adapter
    }

    fun permisos(){
        solicitarPermisos.launch(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
    }

    fun insertarProducto(producto: Producto){
        lifecycleScope.launch {
            repository.insert(producto)
        }
    }

    fun consultarProductos(){
        lifecycleScope.launch {
            listaRegistros = repository.getAll()
            setupRecyclerView()
        }
    }
}