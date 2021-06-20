package ar.edu.unahur.obj2.servidorWeb

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class ServidorWebTest : DescribeSpec({
  describe("Un servidor web sin modulos") {
    val servidor1 = ServidorWeb()
    it("de un protocolo soportado"){
      val pedido = Pedido("231.124.70.25", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
      val respuesta = servidor1.atenderPedido(pedido)
      respuesta.codigo.shouldBe(CodigoHttp.NOT_FOUND)
    }
    it("de un protocolo no soportado"){
      val pedido = Pedido("231.124.70.25", "https://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
      val respuesta = servidor1.atenderPedido(pedido)
      respuesta.codigo.shouldBe(CodigoHttp.NOT_IMPLEMENTED)
    }
  }

  describe("Un pedido"){
    val pedido = Pedido("231.124.70.25", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
    it("Tiene protocolo http"){
      pedido.protocolo().shouldBe("http")
    }
    it("Su ruta es /documentos/doc1.html"){
      pedido.ruta().shouldBe("/documentos/doc1.html")
    }
    it("Su extension es html"){
      pedido.extension().shouldBe("html")
    }
  }
})
