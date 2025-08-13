package tmz.jcmh.proyecto_robalo.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tmz.jcmh.proyecto_robalo.R
import tmz.jcmh.proyecto_robalo.databinding.FragmentAdminBinding
import tmz.jcmh.proyecto_robalo.ui.pedidos.view.PedidosFragment
import tmz.jcmh.proyecto_robalo.ui.productos.view.ProductFragment
import tmz.jcmh.proyecto_robalo.ui.usuarios.view.UsersFragment

class AdminFragment : Fragment() {
    private var _binding: FragmentAdminBinding ? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAdminBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //INSTANCIACIÓN DE LOS FRAGMENTOS A USAR
        val ProductFragment = ProductFragment()
        val UsersFragment = UsersFragment()
        val PedidosFragment = PedidosFragment()

        //INICIO DEL FRAGMENTO POR DEFECTO
        childFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainer, ProductFragment)
            .commit()

        //MANEJO DE FRAGMENTOS CON EL NAV INFERIOR
        binding.menuBottom.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nav_product -> {replaceFragment(ProductFragment)
                    true}
                R.id.nav_pedidos ->{replaceFragment(PedidosFragment)
                    true}
                R.id.nav_users -> {replaceFragment(UsersFragment)
                    true}
                else -> false
            }
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainer, fragment)
            .commit()
    }
}