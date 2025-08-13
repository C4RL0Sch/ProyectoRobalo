package tmz.jcmh.proyecto_robalo.ui.pedidos.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import tmz.jcmh.proyecto_robalo.R
import tmz.jcmh.proyecto_robalo.databinding.FragmentPedidosBinding
import tmz.jcmh.proyecto_robalo.ui.pedidos.adapter.PedidosAdapter
import tmz.jcmh.proyecto_robalo.ui.pedidos.viewmodel.PedidosViewModel
import kotlin.getValue

class PedidosFragment : Fragment() {
    private lateinit var binding: FragmentPedidosBinding

    private lateinit var adapter: PedidosAdapter

    private val pedidosViewModel: PedidosViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPedidosBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = PedidosAdapter(emptyList(), this.requireContext()) { pedido ->

        }

        pedidosViewModel.pedidos.observe(viewLifecycleOwner) {
            val lista = it.map { it.value }.toList()
            adapter.UpdateList(lista)
            binding.rvListaRegistros.adapter = adapter
        }
    }
}