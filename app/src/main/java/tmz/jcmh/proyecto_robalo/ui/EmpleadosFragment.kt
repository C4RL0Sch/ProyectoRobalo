package tmz.jcmh.proyecto_robalo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import tmz.jcmh.proyecto_robalo.R
import tmz.jcmh.proyecto_robalo.databinding.FragmentEmpleadosBinding
import tmz.jcmh.proyecto_robalo.ui.productos.view.ProductFragment
import tmz.jcmh.proyecto_robalo.ui.usuarios.view.UsersFragment

class EmpleadosFragment : Fragment() {
    private var _binding: FragmentEmpleadosBinding ?= null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEmpleadosBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //INSTANCIACIÓN DE LOS FRAGMENTOS A USAR
        val ProductFragment = ProductFragment()

        //INICIO DEL FRAGMENTO POR DEFECTO
        childFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainer, ProductFragment)
            .commit()
    }
}