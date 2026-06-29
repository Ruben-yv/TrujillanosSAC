package com.example.TrujillanosSAC.ServiceImpl;

import com.example.TrujillanosSAC.Dto.CheckoutItemRequest;
import com.example.TrujillanosSAC.Dto.CheckoutRequest;
import com.example.TrujillanosSAC.Dto.PedidoResumenDto;
import com.example.TrujillanosSAC.Model.Cliente;
import com.example.TrujillanosSAC.Model.DetallePedido;
import com.example.TrujillanosSAC.Model.EstadoPedido;
import com.example.TrujillanosSAC.Model.Pedido;
import com.example.TrujillanosSAC.Model.Producto;
import com.example.TrujillanosSAC.Model.Usuario;
import com.example.TrujillanosSAC.Repository.ClienteRepository;
import com.example.TrujillanosSAC.Repository.DetallePedidoRepository;
import com.example.TrujillanosSAC.Repository.EstadoPedidoRepository;
import com.example.TrujillanosSAC.Repository.PedidoRepository;
import com.example.TrujillanosSAC.Repository.ProductoRepository;
import com.example.TrujillanosSAC.Repository.UsuarioRepository;
import com.example.TrujillanosSAC.Service.PedidoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;

    public PedidoServiceImpl(PedidoRepository pedidoRepository,
                             DetallePedidoRepository detallePedidoRepository,
                             ProductoRepository productoRepository,
                             UsuarioRepository usuarioRepository,
                             ClienteRepository clienteRepository,
                             EstadoPedidoRepository estadoPedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.estadoPedidoRepository = estadoPedidoRepository;
    }

    @Override
    public List<PedidoResumenDto> listarPedidosDelCliente(String username) {
        List<Pedido> pedidos = pedidoRepository.findByUsername(username);

        if (pedidos.isEmpty()) {
            return List.of();
        }

        List<Long> pedidoIds = pedidos.stream()
                .map(Pedido::getIdPedido)
                .toList();

        Map<Long, String> productosPorPedido = detallePedidoRepository.findByPedidoIds(pedidoIds).stream()
                .collect(Collectors.groupingBy(
                        detalle -> detalle.getPedido().getIdPedido(),
                        Collectors.mapping(this::formatearDetalle, Collectors.joining(", "))
                ));

        return pedidos.stream()
                .map(pedido -> new PedidoResumenDto(
                        pedido.getIdPedido(),
                        pedido.getFechaEmision(),
                        pedido.getEstado().getNombre(),
                        pedido.getTotal(),
                        productosPorPedido.getOrDefault(pedido.getIdPedido(), "Sin productos")
                ))
                .toList();
    }

    @Override
    @Transactional
    public Long registrarPedido(String username, CheckoutRequest request) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("El carrito esta vacio");
        }

        Usuario usuario = usuarioRepository.findByUsernameConRoles(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Cliente cliente = clienteRepository.findById(usuario.getPersona().getIdPersona())
                .orElseThrow(() -> new IllegalArgumentException("El usuario no esta registrado como cliente"));
        EstadoPedido estado = obtenerEstadoInicial();

        List<DetallePendiente> detalles = request.items().stream()
                .map(this::crearDetallePendiente)
                .toList();

        BigDecimal total = detalles.stream()
                .map(DetallePendiente::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Pedido pedido = Pedido.builder()
                .fechaEmision(LocalDate.now())
                .hora(LocalDateTime.now())
                .total(total)
                .cliente(cliente)
                .estado(estado)
                .build();

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        detalles.forEach(detalle -> {
            Producto producto = detalle.producto();
            producto.setStock(producto.getStock() - detalle.cantidad());

            detallePedidoRepository.save(DetallePedido.builder()
                    .pedido(pedidoGuardado)
                    .producto(producto)
                    .cantidad(detalle.cantidad())
                    .precioUnitario(producto.getPrecio())
                    .subtotal(detalle.subtotal())
                    .build());
        });

        return pedidoGuardado.getIdPedido();
    }

    private EstadoPedido obtenerEstadoInicial() {
        return estadoPedidoRepository.findFirstByNombreContainingIgnoreCase("PENDIENTE")
                .or(() -> estadoPedidoRepository.findFirstByOrderByIdEstadoAsc())
                .orElseThrow(() -> new IllegalStateException("No hay estados de pedido registrados"));
    }

    private DetallePendiente crearDetallePendiente(CheckoutItemRequest item) {
        if (item.cantidad() == null || item.cantidad() <= 0) {
            throw new IllegalArgumentException("Cantidad invalida");
        }

        Producto producto = productoRepository.findByIdConPresentacion(item.idProducto())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        if (producto.getStock() < item.cantidad()) {
            throw new IllegalArgumentException("Stock insuficiente para " + producto.getNombre());
        }

        BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(item.cantidad()));
        return new DetallePendiente(producto, item.cantidad(), subtotal);
    }

    private String formatearDetalle(DetallePedido detalle) {
        String presentacion = detalle.getProducto().getPresentacion() != null
                ? detalle.getProducto().getPresentacion().getNombre()
                : "Unidad";

        return detalle.getProducto().getNombre()
                + " (" + presentacion + ") x" + detalle.getCantidad();
    }

    private record DetallePendiente(Producto producto, Integer cantidad, BigDecimal subtotal) {
    }
}
