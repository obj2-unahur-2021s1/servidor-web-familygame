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

  private val analizadores = mutableListOf<Analizador>()

  fun atenderPedido(pedido: Pedido) : Respuesta {
    return if(pedido.esHttp() && this.algunModuloSoporta(pedido.url)){
      val moduloElegido = this.modulos.find { it.puedeTrabajarCon(pedido.url) }!!
      val respuesta = Respuesta(CodigoHttp.OK, moduloElegido.body, moduloElegido.tiempoRespuesta, pedido)
      this.analizadores.forEach{ it.analizarRespuesta(respuesta, moduloElegido) }
      respuesta
    }else if (pedido.esHttp() && !this.algunModuloSoporta(pedido.url)){
      var respuesta = Respuesta(CodigoHttp.NOT_FOUND, "", 10, pedido)
      this.analizadores.forEach{ it.analizarRespuesta(respuesta, null) }
      return respuesta
    }else{
      var respuesta = Respuesta(CodigoHttp.NOT_IMPLEMENTED, "", 10, pedido)
      this.analizadores.forEach{ it.analizarRespuesta(respuesta, null) }
      return respuesta
    }
  }

  private fun algunModuloSoporta(url: String) = this.modulos.any { it.puedeTrabajarCon(url) }

  fun agregarModulo(modulo: Modulo) {
    this.modulos.add(modulo)
  }

  fun quitarModulo(modulo: Modulo) {
    this.modulos.remove(modulo)
  }

  fun agregarAnalizador(analizador : Analizador){
    this.analizadores.add(analizador)
  }

  fun quitarAnalizador(analizador : Analizador){
    this.analizadores.remove(analizador)
  }
}
