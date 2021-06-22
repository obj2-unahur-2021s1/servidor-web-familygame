package ar.edu.unahur.obj2.servidorWeb

import java.time.LocalDateTime
import kotlin.math.roundToInt

abstract class Analizador(){

    abstract fun analizarRespuesta(respuesta: Respuesta, modulo: Modulo?)
}

class DeteccionDemora() : Analizador() {
    var demoraMinima = 15
    var respuestasDemoradas = mutableMapOf<Modulo, Int>()

    override fun analizarRespuesta(respuesta: Respuesta, modulo: Modulo?) {
        if(respuesta.tiempo > demoraMinima && modulo != null) {
            if(respuestasDemoradas.contains(modulo)){
                respuestasDemoradas[modulo] = respuestasDemoradas.getValue(modulo) + 1
            }else{
                respuestasDemoradas[modulo] = 1
            }
        }
    }

    fun respuestasDemoradas(modulo: Modulo): Int {
        var cantRespuestasDemoradas = 0
        if (respuestasDemoradas.containsKey(modulo)){
            cantRespuestasDemoradas = respuestasDemoradas.getValue(modulo)
        }
        return cantRespuestasDemoradas
    }

}

class IpSospechosa() : Analizador() {

    var ipsSospechosas = mutableListOf<String>()

    var respuestas = mutableMapOf<Respuesta, Modulo?>()

    fun agregarIpSospechosa(ip : String){
        ipsSospechosas.add(ip)
    }

    override fun analizarRespuesta(respuesta: Respuesta, modulo: Modulo?) {
        var ip = respuesta.pedido.ip
        if(ipsSospechosas.contains(ip)){
            respuestas[respuesta] = modulo
        }
    }

    fun pedidosPorIp(ip : String): List<Pedido> {
        return respuestas.filter { it.key.pedido.ip == ip }.map { it.key.pedido }
    }

    //Falta implementar modulo
    fun moduloMasConsultado() {}

    fun ipsSospechosasQueRequierenRuta(ruta : String) = respuestas.filter { it.key.pedido.ruta() == ruta }.map { it.key.pedido.ip }
}


class Estadisticas() : Analizador() {

    private var respuestas = mutableListOf<Respuesta>()

    override fun analizarRespuesta(respuesta: Respuesta, modulo: Modulo?) {
        respuestas.add(respuesta)
    }

    fun tiempoRespuestaPromedio(): Double {
        return (respuestas.sumBy { it.tiempo } / respuestas.size.toDouble())
    }

    fun cantidadDePedidos(fechaInicio : LocalDateTime, fechaFin : LocalDateTime): Int {
        return respuestas.filter { it.pedido.fechaHora in fechaInicio..fechaFin}.count()
    }

    fun cantidadDeRespuestasConBody(body : String) : Int{
        return respuestas.filter { it.body.contains(body) }.count()
    }

    fun porcentajeRespuestasExitosas(): Int {
        return ((respuestas.filter { it.codigo == CodigoHttp.OK }.count() * 100) / respuestas.count())
    }
}
