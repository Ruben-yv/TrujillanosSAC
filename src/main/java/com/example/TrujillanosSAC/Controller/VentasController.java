package com.example.TrujillanosSAC.Controller;

import com.example.TrujillanosSAC.Dto.CheckoutRequest;
import com.example.TrujillanosSAC.Service.ProductoService;
import com.example.TrujillanosSAC.Service.PedidoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/ventas")
public class VentasController {

    private final ProductoService productoService;
    private final PedidoService pedidoService;

    public VentasController(ProductoService productoService,
                            PedidoService pedidoService) {
        this.productoService = productoService;
        this.pedidoService = pedidoService;
    }

    @GetMapping("/catalogo")
    public String catalogo(Model model, Principal principal) {
        model.addAttribute("productos", productoService.listarProductos());
        model.addAttribute("clienteAutenticado", principal != null);
        model.addAttribute("usuarioNombre", principal != null ? principal.getName() : "Mi cuenta");
        model.addAttribute("carritoKey", obtenerCarritoKey(principal));
        return "ventas/catalogo";
    }

    @GetMapping("/producto/{id}")
    public String detalleProducto(@PathVariable Long id,
                                  Model model,
                                  Principal principal) {
        model.addAttribute("producto", productoService.obtenerProductoPorId(id));
        model.addAttribute("presentacionesProducto", productoService.listarPresentacionesDelProducto(id));
        model.addAttribute("clienteAutenticado", principal != null);
        model.addAttribute("usuarioNombre", principal != null ? principal.getName() : "Mi cuenta");
        model.addAttribute("carritoKey", obtenerCarritoKey(principal));
        return "ventas/detalle-producto";
    }

    @GetMapping("/carrito")
    public String carrito(Model model, Principal principal) {
        model.addAttribute("usuarioNombre", principal != null ? principal.getName() : "Mi cuenta");
        model.addAttribute("carritoKey", obtenerCarritoKey(principal));
        return "ventas/carrito";
    }

    @GetMapping("/checkout")
    public String checkout(Model model, Principal principal) {
        model.addAttribute("usuarioNombre", principal.getName());
        model.addAttribute("carritoKey", obtenerCarritoKey(principal));
        return "ventas/checkout";
    }

    @PostMapping("/checkout/confirmar")
    @ResponseBody
    public Map<String, Object> confirmarPedido(@RequestBody CheckoutRequest request,
                                               Principal principal) {
        Long idPedido = pedidoService.registrarPedido(principal.getName(), request);
        return Map.of(
                "idPedido", idPedido,
                "redirectUrl", "/ventas/pago-confirmado?metodo=" + request.metodoPago()
        );
    }

    @GetMapping("/pago-confirmado")
    public String pagoConfirmado(@RequestParam(defaultValue = "YAPE") String metodo,
                                 Model model,
                                 Principal principal) {
        boolean efectivo = "EFECTIVO".equalsIgnoreCase(metodo);
        model.addAttribute("usuarioNombre", principal.getName());
        model.addAttribute("tituloConfirmacion", efectivo ? "PEDIDO REGISTRADO" : "PAGO REGISTRADO");
        model.addAttribute("mensajeConfirmacion", efectivo
                ? "Gracias por confiar en nosotros. Acerquese a realizar el pago y recoger su pedido."
                : "Su pedido fue registrado correctamente. Validaremos el pago por Yape antes de la entrega.");
        return "ventas/pago-confirmado";
    }

    @GetMapping("/mis-pedidos")
    public String misPedidos(Model model, Principal principal) {
        model.addAttribute("pedidos", pedidoService.listarPedidosDelCliente(principal.getName()));
        model.addAttribute("usuarioNombre", principal.getName());
        return "ventas/mis-pedidos";
    }

    private String obtenerCarritoKey(Principal principal) {
        return "carrito:" + (principal != null ? principal.getName() : "invitado");
    }
}
