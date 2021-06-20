package ar.edu.unahur.obj2.servidorWeb

import java.time.LocalDateTime

// Para no tener los códigos "tirados por ahí", usamos un enum que le da el nombre que corresponde a cada código
// La idea de las clases enumeradas es usar directamente sus objetos: CodigoHTTP.OK, CodigoHTTP.NOT_IMPLEMENTED, etc
enum class CodigoHttp(val codigo: Int) {
  OK(200),
  NOT_IMPLEMENTED(501),
  NOT_FOUND(404)
}

class Pedido(val ip: String, val url: String, val fechaHora: LocalDateTime){
  fun protocolo() = url.split(":").first()
  fun ruta() = "/" + url.split("://").last().substringAfter("/")
  fun extension() = url.split(".").last()

  fun esHttp() = this.protocolo() == "http"
}
class Respuesta(val codigo: CodigoHttp, val body: String, val tiempo: Int, val pedido: Pedido)

class ServidorWeb{
  private val modulos = mutableListOf<Modulo>()

  fun atenderPedido(pedido: Pedido) : Respuesta {
    return if(pedido.esHttp() && this.algunMetodoSoporta(pedido.url)){
      val moduloElegido = this.modulos.find { it.puedeTrabajarCon(pedido.url) }!!
      Respuesta(CodigoHttp.OK, moduloElegido.body, moduloElegido.tiempoRespuesta, pedido)
    }else if (pedido.esHttp() && !this.algunMetodoSoporta(pedido.url)){
      Respuesta(CodigoHttp.NOT_FOUND, "", 10, pedido)
    }else{
      Respuesta(CodigoHttp.NOT_IMPLEMENTED, "", 10, pedido)
    }
  }

  private fun algunMetodoSoporta(url: String) = this.modulos.any { it.puedeTrabajarCon(url) }

  fun agregarModulo(modulo: Modulo) {
    this.modulos.add(modulo)
  }
}
