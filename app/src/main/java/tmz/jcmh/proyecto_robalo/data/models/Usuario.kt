package tmz.jcmh.proyecto_robalo.data.models

data class Usuario (
    var Usuario: String = "",
    var Nombre: String = "",
    var ApellidoP: String = "",
    var ApellidoM: String = "",
    var Password: String = "",
    var Puesto: Int = 0,
    var imgUrl: String? = null
)