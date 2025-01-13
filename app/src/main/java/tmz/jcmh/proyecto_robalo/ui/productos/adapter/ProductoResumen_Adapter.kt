package tmz.jcmh.proyecto_robalo.ui.productos.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tmz.jcmh.proyecto_robalo.R
import tmz.jcmh.proyecto_robalo.data.models.Producto
import tmz.jcmh.proyecto_robalo.ui.productos.view.EditProducto

class ProductoResumen_Adapter (
    private var listaProductos: List<Producto>
): RecyclerView.Adapter<ProductoResumen_Adapter.ViewHolder>() {
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val tvCodigo: TextView = itemView.findViewById(R.id.tvCodigo)
        val tvNombre: TextView = itemView.findViewById(R.id.tvProducto)
        val tvPresentacion: TextView = itemView.findViewById(R.id.tvPresentacion)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        val tvCantidad: TextView = itemView.findViewById(R.id.tvCantidad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_productos_resumen, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = listaProductos[position]
        holder.tvCodigo.text = producto.Codigo
        holder.tvNombre.text = producto.Nombre
        holder.tvPresentacion.text = producto.Presentacion
        holder.tvPrecio.text = producto.Precio.toString()
        holder.tvCantidad.text = producto.Cantidad.toString()
    }

    override fun getItemCount(): Int {
        return listaProductos.size
    }

    fun updateList(nuevaLista: List<Producto>) {
        listaProductos = nuevaLista
        notifyDataSetChanged()  // Notifica al RecyclerView que la lista ha cambiado
    }
}