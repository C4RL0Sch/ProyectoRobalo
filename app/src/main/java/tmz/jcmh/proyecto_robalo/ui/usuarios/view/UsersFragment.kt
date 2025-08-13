package tmz.jcmh.proyecto_robalo.ui.usuarios.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import tmz.jcmh.proyecto_robalo.MyApp
import tmz.jcmh.proyecto_robalo.R
import tmz.jcmh.proyecto_robalo.databinding.FragmentUsersBinding
import tmz.jcmh.proyecto_robalo.ui.productos.view.AddProducto
import tmz.jcmh.proyecto_robalo.ui.usuarios.adapter.UsuarioAdapter
import tmz.jcmh.proyecto_robalo.ui.usuarios.viewmodel.UsuariosViewModel
import java.io.File

class UsersFragment : Fragment() {
    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    private val usuarioViewModel: UsuariosViewModel by activityViewModels()

    private lateinit var adapter: UsuarioAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUsersBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UsuarioAdapter(emptyList(), emptyMap())

        usuarioViewModel.allUsuarios.observe(viewLifecycleOwner, Observer {
            val images: Map<String, File> = it.mapNotNull { user ->
                val imageFile = usuarioViewModel.getImageFile(user.Usuario?:"")
                if (imageFile != null) {
                    (user.Usuario?:"") to imageFile
                } else {
                    null
                }
            }.toMap()
            adapter.UpdateList(it, images)
            binding.rvListaRegistros.adapter = adapter
        })

        usuarioViewModel.mensaje.observe(viewLifecycleOwner) { msj ->
            Toast.makeText(requireContext(), msj, Toast.LENGTH_LONG).show()
        }

        binding.btnAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddUsuario::class.java)
            startActivity(intent)
        }
    }
}