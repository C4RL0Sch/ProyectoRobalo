package tmz.jcmh.proyecto_robalo.data.models

data class Producto(
    var Codigo: String = "",
    var Nombre: String = "",
    var Marca: String = "",
    var Categoria: String = "",
    var Presentacion: String = "",
    var Medida: String = "",
    var Precio: Double = 0.0,
    var Cantidad: Double = 0.0,
    var imgUrl: String? = null
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        other as Producto

        return Codigo == other.Codigo &&
                Nombre == other.Nombre &&
                Marca == other.Marca &&
                Categoria == other.Categoria &&
                Presentacion == other.Presentacion &&
                Medida == other.Medida &&
                Precio == other.Precio &&
                Cantidad == other.Cantidad
    }

    override fun hashCode(): Int {
        return Codigo.hashCode() * 31 +
                Nombre.hashCode() * 31 +
                Presentacion.hashCode() * 31 +
                Precio.hashCode() * 31 +
                Cantidad.hashCode() * 31
    }
}
