package tmz.jcmh.proyecto_robalo.ui.usuarios.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import tmz.jcmh.proyecto_robalo.R
import tmz.jcmh.proyecto_robalo.data.models.Usuario
import tmz.jcmh.proyecto_robalo.ui.usuarios.view.EditUsuario
import java.io.File

class UsuarioAdapter (
    private var listaUsuarios: List<Usuario>,
    private var Images: Map<String, File>
): RecyclerView.Adapter<UsuarioAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvUsuario: TextView = itemView.findViewById(R.id.tvUsuario)
        val tvPuesto: TextView = itemView.findViewById(R.id.tvPuesto)
        val btnAdd: ImageButton = itemView.findViewById(R.id.img)
        val Img: ImageView = itemView.findViewById(R.id.img_producto)
        val ImgNotFound: TextView = itemView.findViewById(R.id.img_notFound)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_usuarios, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val usuario = listaUsuarios[position]
        val imageFile = Images[usuario.Usuario]
        val fullName = "${usuario.Nombre} ${usuario.ApellidoP} ${usuario.ApellidoM}"
        holder.tvNombre.text = fullName
        holder.tvUsuario.text = usuario.Usuario
        if(usuario.Puesto == 1){
            holder.tvPuesto.setText("Administrador")
        }else{
            holder.tvPuesto.setText("Empleado")
        }

        if (imageFile != null && imageFile.exists()) {
            holder.ImgNotFound.visibility = View.GONE
            holder.Img.visibility = View.VISIBLE

            Picasso.get()
                .load(imageFile)
                .into(holder.Img)
        }else {
            holder.ImgNotFound.visibility = View.VISIBLE
            holder.Img.visibility = View.GONE
        }

        holder.btnAdd.setOnClickListener {
            //TODO Cambiar a clase EditUsuario
            val intent = Intent(holder.itemView.context, EditUsuario::class.java)
            intent.putExtra("user", usuario.Usuario)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listaUsuarios.size
    }

    fun UpdateList(new_list: List<Usuario>, new_images: Map<String, File>){
        listaUsuarios = new_list
        Images = new_images
    }
}