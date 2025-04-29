package tmz.jcmh.proyecto_robalo.ui.productos.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import tmz.jcmh.proyecto_robalo.MyApp
import tmz.jcmh.proyecto_robalo.databinding.FragmentProductBinding
import tmz.jcmh.proyecto_robalo.ui.productos.adapter.ProductoAdapter
import tmz.jcmh.proyecto_robalo.ui.productos.viewmodel.ProductosViewModel
import java.io.File

class ProductFragment : Fragment() {
    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ProductoAdapter
    private val productoViewModel: ProductosViewModel
        get() = (requireActivity().application as MyApp).productoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProductBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ProductoAdapter(emptyList(), emptyMap())

        productoViewModel.allProductos.observe(viewLifecycleOwner, Observer {
            val images: Map<String, File> = it.mapNotNull { producto ->
                val imageFile = productoViewModel.getImageFile(producto.Codigo?:"")
                if (imageFile != null) {
                    (producto.Codigo?:"") to imageFile
                } else {
                    null
                }
            }.toMap()
            adapter.UpdateList(it, images)
            binding.rvListaRegistros.adapter = adapter
        })

        productoViewModel.mensaje.observe(viewLifecycleOwner) { msj ->
            Toast.makeText(requireContext(), msj, Toast.LENGTH_LONG).show()
        }

        binding.btnAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddProducto::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}