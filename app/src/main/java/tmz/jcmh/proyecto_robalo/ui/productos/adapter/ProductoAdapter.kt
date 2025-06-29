package tmz.jcmh.proyecto_robalo.ui.productos.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import tmz.jcmh.proyecto_robalo.R
import tmz.jcmh.proyecto_robalo.data.models.Producto
import tmz.jcmh.proyecto_robalo.ui.productos.view.EditProducto
import java.io.File

class ProductoAdapter(
    private var listaProductos: List<Producto>
): RecyclerView.Adapter<ProductoAdapter.ViewHolder>() {

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val tvCodigo: TextView = itemView.findViewById(R.id.tvCodigo)
        val tvNombre: TextView = itemView.findViewById(R.id.tvProducto)
        val tvPresentacion: TextView = itemView.findViewById(R.id.tvPresentacion)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        val tvCantidad: TextView = itemView.findViewById(R.id.tvCantidad)
        val tvMarca: TextView = itemView.findViewById(R.id.tvMarca)
        val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoria)
        val btnEdit: ImageButton = itemView.findViewById(R.id.img)
        val Img: ImageView = itemView.findViewById(R.id.img_producto)
        val ImgNotFound: TextView = itemView.findViewById(R.id.img_notFound)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_productos, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = listaProductos[position]
        holder.tvCodigo.text = producto.Codigo
        holder.tvNombre.text = producto.Nombre
        holder.tvPrecio.text = producto.Precio.toString()
        holder.tvPresentacion.text = "${producto.Presentacion} | ${producto.Medida}"
        holder.tvMarca.text = producto.Marca
        holder.tvCategoria.text = producto.Categoria
        if(producto.Medida=="Pieza"){
            holder.tvCantidad.text = producto.Cantidad?.toInt().toString()
        }
        else{
            holder.tvCantidad.text = producto.Cantidad.toString()
        }

        if ( producto.imgUrl != null) {
            holder.ImgNotFound.visibility = View.GONE
            holder.Img.visibility = View.VISIBLE

            val uri = Uri.parse(producto.imgUrl)

            Glide
                .with(holder.itemView.context)
                .load(uri)
                .into(holder.Img)
        }else {
            holder.ImgNotFound.visibility = View.VISIBLE
            holder.Img.visibility = View.GONE
        }

        holder.btnEdit.setOnClickListener {
            val intent = Intent(holder.itemView.context, EditProducto::class.java)
            intent.putExtra("codigo", producto.Codigo)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listaProductos.size
    }

    fun UpdateList(new_list: List<Producto>){
        listaProductos = new_list
    }
}