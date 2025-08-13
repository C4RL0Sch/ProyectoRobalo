package tmz.jcmh.proyecto_robalo.ui.pedidos.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import tmz.jcmh.proyecto_robalo.R
import tmz.jcmh.proyecto_robalo.data.models.Pedido
import java.text.SimpleDateFormat
import java.util.Locale

class PedidosAdapter (
    private var lista: List<Pedido>,
    private val context: Context,
    private val onCancel: (Pedido) -> Unit
): RecyclerView.Adapter<PedidosAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFecha: TextView = itemView.findViewById(R.id.Fecha)
        val tvEstado: TextView = itemView.findViewById(R.id.Estado)
        val tvTotal: TextView = itemView.findViewById(R.id.Total)
        val BtnInfo: FloatingActionButton = itemView.findViewById(R.id.BtnInfo)

        fun bind(pedido: Pedido){
            tvFecha.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(pedido.Fecha)
            tvEstado.text = pedido.Estado
            tvTotal.text = String.format("%.2f",pedido.Total)

            BtnInfo.setOnClickListener {
                showDialog(pedido)
            }
        }

        fun showDialog(pedido: Pedido){
            val dialogView = LayoutInflater.from(context).inflate(R.layout.item_detalles_pedido, null)
            val dialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .create()

            val tvFecha = dialogView.findViewById<TextView>(R.id.tvFecha)
            val tvEstado = dialogView.findViewById<TextView>(R.id.tvEstado)
            val tvTotal = dialogView.findViewById<TextView>(R.id.tvTotal)
            val btnCerrar = dialogView.findViewById<Button>(R.id.btnCerrar)
            val btnCancelar = dialogView.findViewById<Button>(R.id.btnCancelar)
            val rvListaPedido = dialogView.findViewById<RecyclerView>(R.id.rvListaDetalles)

            tvFecha.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(pedido.Fecha)
            tvEstado.text = pedido.Estado
            tvTotal.text = String.format("%.2f",pedido.Total)

            if(pedido.Estado == "Pendiente"){
                btnCancelar.visibility = View.VISIBLE
                btnCancelar.setOnClickListener {
                    onCancel(pedido)
                    dialog.dismiss()
                }
            }else{
                btnCancelar.visibility = View.GONE
            }

            btnCerrar.setOnClickListener {
                dialog.dismiss()
            }

            val listaProductos = pedido.Productos.map{
                it.value
            }.toList()

            val adapter = PedidoDetallesAdapter(listaProductos)
            //rvListaPedido.adapter = adapter

            dialog.show()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_pedidos, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val pedido = lista[position]
        holder.bind(pedido)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    fun UpdateList(new_list: List<Pedido>) {
        lista = new_list
    }
}