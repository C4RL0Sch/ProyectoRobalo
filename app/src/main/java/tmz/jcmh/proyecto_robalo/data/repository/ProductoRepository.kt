package tmz.jcmh.proyecto_robalo.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import tmz.jcmh.proyecto_robalo.data.models.Producto

class ProductoRepository() {
    private val db= FirebaseFirestore.getInstance()
    private val productosRef = db.collection("productos")
    private val _productos = MutableLiveData<List<Producto>>()
    val productos : LiveData<List<Producto>> get() = _productos

    init{
        productosRef.addSnapshotListener{
            snapshot, error ->
            if(error==null && snapshot!=null){
                val lista = snapshot.documents.mapNotNull {
                    it.toObject(Producto::class.java)
                }
                _productos.value = lista
            }
        }
    }

    suspend fun getById(id: String): Producto? {
        return try {
            val snapshot = productosRef.document(id).get().await()
            snapshot.toObject(Producto::class.java)
        } catch (e: Exception) {
            Log.i("Error", e.message.toString())
            null
        }
    }

    suspend fun insert(producto: Producto): Boolean {
        return try {
            producto.Codigo.let { productosRef.document(it).set(producto).await() }
            true
        } catch (e: Exception) {
            Log.i("Error", e.message.toString())
            false
        }
    }

    suspend fun update(producto: Producto): Boolean {
        return try {
            producto.Codigo.let { productosRef.document(it).set(producto).await() }
            true
        } catch (e: Exception) {
            Log.i("Error", e.message.toString())
            false
        }
    }

    suspend fun delete(producto: Producto):Boolean {
        return try {
            producto.Codigo.let { productosRef.document(it).delete().await() }
            true
        }
        catch (e: Exception) {
            Log.i("Error", e.message.toString())
            false
        }
    }

}