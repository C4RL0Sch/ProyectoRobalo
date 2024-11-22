package tmz.jcmh.proyecto_robalo.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import tmz.jcmh.proyecto_robalo.data.dao.IProductoDAO
import tmz.jcmh.proyecto_robalo.data.models.Producto

@Database(entities =[Producto::class], version = 1)
abstract class DatabaseRobalo: RoomDatabase() {
    abstract val daoProductos: IProductoDAO

    companion object {
        @Volatile
        private var INSTANCE: DatabaseRobalo? = null

        fun getDatabase(context: Context): DatabaseRobalo {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseRobalo::class.java,
                    "db_robalo"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

