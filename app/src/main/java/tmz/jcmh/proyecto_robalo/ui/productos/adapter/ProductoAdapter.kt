package tmz.jcmh.proyecto_robalo.ui.productos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tmz.jcmh.proyecto_robalo.R
import tmz.jcmh.proyecto_robalo.data.models.Producto

class ProductoAdapter(
    private val listaProductos: List<Producto>
): RecyclerView.Adapter<ProductoAdapter.ViewHolder>() {
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvProducto)
        val tvPresentacion: TextView = itemView.findViewById(R.id.tvPresentacion)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        val tvCantidad: TextView = itemView.findViewById(R.id.tvCantidad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_productos, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = listaProductos[position]
        holder.tvNombre.text = producto.Nombre
        holder.tvPresentacion.text = producto.Presentacion
        holder.tvPrecio.text = producto.Precio.toString()
        holder.tvCantidad.text = producto.Cantidad.toString()
    }

    override fun getItemCount(): Int {
        return listaProductos.size
    }
}