package ar.edu.unahur.obj2.servidorWeb


import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class AnalizadorTest : DescribeSpec({

    describe("Un servidor con analizadores"){
        val servidor1 = ServidorWeb()
        val analizadorDeteccionDemora = DeteccionDemora()
        val analizadorIpSospechosa = IpSospechosa()
        val analizadorEstadisticas = Estadisticas()
        servidor1.agregarAnalizador(analizadorDeteccionDemora)
        servidor1.agregarAnalizador(analizadorIpSospechosa)
        servidor1.agregarAnalizador(analizadorEstadisticas)
        val extensionesSoportadasDocs = mutableListOf("doc", "docx", "txt")
        val extensionesSoportadasImagenes = mutableListOf("jpg", "png", "gif")
        val moduloDocumentos = Modulo(extensionesSoportadasDocs, "Modulos de documentos con imagenes", 30)
        val moduloImagen = Modulo(extensionesSoportadasImagenes, "Modulo de imagenes", 15)
        servidor1.agregarModulo(moduloDocumentos)
        val pedido1 = Pedido("200.135.138.24", "http://pepito.com.ar/documentos/doc1.docx", LocalDateTime.of(2021, 6, 22, 18, 0,0))
        val pedido2 = Pedido("201.50.170.10", "http://pepito.com.ar/imagenes/imagen1.jpg", LocalDateTime.of(2021, 6, 21, 18, 30,0))
        it("Analiza un pedido con deteccion de demora"){
            servidor1.atenderPedido(pedido1)
            analizadorDeteccionDemora.respuestasDemoradas(moduloDocumentos).shouldBe(1)
        }

        it("Analiza un pedido con ip's sospechosas"){
            analizadorIpSospechosa.agregarIpSospechosa("200.135.138.24")
            servidor1.atenderPedido(pedido1)
            analizadorIpSospechosa.pedidosPorIp("200.135.138.24").shouldContain(pedido1)
        }

        it("Conjunto de ip sospechosas que requieren cierta ruta"){
            analizadorIpSospechosa.agregarIpSospechosa("200.135.138.24")
            servidor1.atenderPedido(pedido1)
            analizadorIpSospechosa.ipsSospechosasQueRequierenRuta("/documentos/doc1.docx").shouldContain("200.135.138.24")
        }

        it("Estadisticas: tiempo de respuesta promedio"){
            servidor1.agregarModulo(moduloImagen)
            servidor1.atenderPedido(pedido1)
            servidor1.atenderPedido(pedido2)
            analizadorEstadisticas.tiempoRespuestaPromedio().shouldBe(22.5)
        }

        it("Estadisticas: cantidad de pedidos entre dos momentos"){
            servidor1.agregarModulo(moduloImagen)
            servidor1.atenderPedido(pedido1)
            servidor1.atenderPedido(pedido2)
            analizadorEstadisticas.cantidadDePedidos(LocalDateTime.of(2021, 6, 20, 15,0,0)
                , LocalDateTime.of(2021, 6, 22, 23, 0 , 0)).shouldBe(2)
        }

        it("Estadisticas: Cantidad de respuestas con determinado body"){
            servidor1.agregarModulo(moduloImagen)
            servidor1.atenderPedido(pedido1)
            servidor1.atenderPedido(pedido2)
            analizadorEstadisticas.cantidadDeRespuestasConBody("imagenes").shouldBe(2)
        }

        it("Estadisticas: Porcentaje de pedidos con respuesta exitosa"){
            val pedido3 = Pedido("201.50.170.10", "https://pepito.com.ar/imagenes/imagen1.jpg", LocalDateTime.of(2021, 6, 21, 18, 30,0))
            servidor1.agregarModulo(moduloImagen)
            servidor1.atenderPedido(pedido1)
            servidor1.atenderPedido(pedido2)
            servidor1.atenderPedido(pedido3)
            analizadorEstadisticas.porcentajeRespuestasExitosas().shouldBe(66)
        }
    }

})