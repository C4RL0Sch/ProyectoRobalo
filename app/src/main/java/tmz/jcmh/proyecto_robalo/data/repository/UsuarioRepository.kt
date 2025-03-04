package tmz.jcmh.proyecto_robalo.data.repository

import androidx.lifecycle.LiveData
import tmz.jcmh.proyecto_robalo.data.dao.IUsuarioDAO
import tmz.jcmh.proyecto_robalo.data.models.Producto
import tmz.jcmh.proyecto_robalo.data.models.Usuario

class UsuarioRepository(private val dao: IUsuarioDAO) {

    fun getAll(): LiveData<List<Usuario>> {
      return dao.getAll()
    }

    fun getAllNow(): List<Usuario> {
        return dao.getAllNow()
    }

    suspend fun getById(id: Int): Usuario {
        return dao.getById(id)
    }

    suspend fun getByUser(usuario: String): Usuario {
        return dao.getByUser(usuario)
    }

    suspend fun CountByUser(usuario: String): Int {
        return dao.CountByUser(usuario)
    }

    suspend fun insert(usuario: Usuario) {
        dao.insert(usuario)
    }
    suspend fun update(usuario: Usuario) {
        dao.Update(usuario)
    }

    suspend fun delete(usuario: Usuario) {
        dao.Delete(usuario)
    }
}