package tmz.jcmh.proyecto_robalo.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import tmz.jcmh.proyecto_robalo.data.dao.IUsuarioDAO
import tmz.jcmh.proyecto_robalo.data.models.Producto
import tmz.jcmh.proyecto_robalo.data.models.Usuario

class UsuarioRepository() {
    private val db= FirebaseFirestore.getInstance()
    private val usuariosRef = db.collection("usuarios")
    private val _usuarios = MutableLiveData<List<Usuario>>()
    val usuarios : LiveData<List<Usuario>> get() = _usuarios

    init{
        usuariosRef.addSnapshotListener{
                snapshot, error ->
            if(error==null && snapshot!=null){
                val lista = snapshot.documents.mapNotNull {
                    it.toObject(Usuario::class.java)
                }
                _usuarios.value = lista
            }
        }
    }

    suspend fun getByUser(user: String): Usuario {
        return try {
            val snapshot = usuariosRef.document(user).get().await()
            val user = snapshot.toObject(Usuario::class.java)?:Usuario()
            user
        } catch (e: Exception) {
            Usuario()
        }
    }

    suspend fun insert(usuario: Usuario): Boolean {
        return try {
            usuario.Usuario?.let { usuariosRef.document(it).set(usuario).await() }
            true
        } catch (e: Exception) {
            Log.i("Error", e.message.toString())
            false
        }
    }

    suspend fun update(usuario: Usuario): Boolean {
        return try {
            usuario.Usuario?.let { usuariosRef.document(it).set(usuario).await() }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun delete(usuario: Usuario):Boolean {
        return try {
            usuario.Usuario?.let { usuariosRef.document(it).delete().await() }
            true
        }
        catch (e: Exception) {
            false
        }
    }
}