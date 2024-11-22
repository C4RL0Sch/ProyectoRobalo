package tmz.jcmh.proyecto_robalo.ui.productos.view

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import tmz.jcmh.proyecto_robalo.databinding.ActivityMainBinding
import tmz.jcmh.proyecto_robalo.ui.productos.adapter.ProductoAdapter
import tmz.jcmh.proyecto_robalo.ui.productos.viewmodel.ProductosViewModel
import tmz.jcmh.proyecto_robalo.utils.Excel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ProductoAdapter
    private val productoViewModel: ProductosViewModel by viewModels()

    //private lateinit var listaRegistros: LiveData<List<Producto>>()
    private lateinit var Excel:Excel
    private lateinit var solicitarPermisos: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Excel = Excel()

        solicitarPermisos = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            val aceptados = it.all{ it.value }
            if(!aceptados){
                Toast.makeText(this, "SE TIENE QUE ACEPTAR LOS PERMISOS", Toast.LENGTH_LONG).show()
            }
        }

        permisos()

        //setupRecyclerView()
        adapter = ProductoAdapter(emptyList())

        productoViewModel.allProductos.observe(this, Observer {
            adapter.UpdateList(it)
            binding.rvListaRegistros.adapter = adapter
        })

        binding.btnAdd.setOnClickListener(){
            val intent = Intent(this, AddProducto::class.java)
            startActivity(intent)
            Toast.makeText(this,"HEMOS VUELTOOOO",Toast.LENGTH_LONG)
            //setupRecyclerView()
        }

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
    }

    fun permisos(){
        solicitarPermisos.launch(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
    }

    /*fun insertarProducto(producto: Producto){
        lifecycleScope.launch {
            repository.insert(producto)
        }
    }*/

    /*fun consultarProductos(){
        lifecycleScope.launch {
            listaRegistros = repository.getAll()
            setupRecyclerView()
        }
    }*/
}