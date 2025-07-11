package tmz.jcmh.proyecto_robalo.util

class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? =
        if (hasBeenHandled) null
        else {
            hasBeenHandled = true
            content
        }

    fun peekContent(): T? =
        if (hasBeenHandled) null
        else {
            content
        }
}