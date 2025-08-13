package tmz.jcmh.proyecto_robalo.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import tmz.jcmh.proyecto_robalo.data.models.Pedido

class PedidoRepository {
    private val db= FirebaseFirestore.getInstance()
    private val pedidosRef = db.collection("pedidos")

    private val _pedidos = MutableLiveData<Map<String, Pedido>>()
    val pedidos : LiveData<Map<String, Pedido>> get() = _pedidos

    init{
        pedidosRef.addSnapshotListener { snapshot, error ->
            if (error == null && snapshot != null) {
                val lista = snapshot.documents.mapNotNull {
                    it.toObject(Pedido::class.java)
                }
                _pedidos.value = lista.associateBy { it.idPedido }
            }
            else{
                _pedidos.value = emptyMap()
            }
        }
    }
}