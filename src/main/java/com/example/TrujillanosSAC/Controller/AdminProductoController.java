package com.example.TrujillanosSAC.Controller;

import com.example.TrujillanosSAC.Dto.ProductoAdminForm;
import com.example.TrujillanosSAC.Model.Producto;
import com.example.TrujillanosSAC.Repository.AlmacenRepository;
import com.example.TrujillanosSAC.Repository.CategoriaRepository;
import com.example.TrujillanosSAC.Repository.PresentacionProductoRepository;
import com.example.TrujillanosSAC.Repository.ProductoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

@Controller
@RequestMapping("/admin/productos")
public class AdminProductoController {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final PresentacionProductoRepository presentacionRepository;
    private final AlmacenRepository almacenRepository;

    public AdminProductoController(ProductoRepository productoRepository,
                                   CategoriaRepository categoriaRepository,
                                   PresentacionProductoRepository presentacionRepository,
                                   AlmacenRepository almacenRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.presentacionRepository = presentacionRepository;
        this.almacenRepository = almacenRepository;
    }

    @GetMapping
    public String listar(Model model, Principal principal) {
        model.addAttribute("productos", productoRepository.findAllParaGestion());
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("presentaciones", presentacionRepository.findAll());
        model.addAttribute("usuarioNombre", principal != null ? principal.getName() : "Admin");
        model.addAttribute("productoForm", new ProductoAdminForm("", BigDecimal.ZERO, "", 0, 0, 0, null, null));
        return "admin/productos";
    }

    @PostMapping
    public String crear(@ModelAttribute ProductoAdminForm productoForm,
                        @RequestParam("imagen") MultipartFile imagen) throws IOException {
        Producto producto = Producto.builder()
                .nombre(productoForm.nombre())
                .descripcion(productoForm.descripcion() == null || productoForm.descripcion().isBlank()
                        ? "Sin descripcion"
                        : productoForm.descripcion())
                .precio(productoForm.precio())
                .stock(valorEntero(productoForm.stock(), 0))
                .stockMinimo(valorEntero(productoForm.stockMinimo(), 0))
                .stockLimite(valorEntero(productoForm.stockLimite(), 0))
                .categoria(categoriaRepository.findById(productoForm.idCategoria())
                        .orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada")))
                .presentacion(presentacionRepository.findById(productoForm.idPresentacion())
                        .orElseThrow(() -> new IllegalArgumentException("Presentacion no encontrada")))
                .almacen(almacenRepository.findFirstByOrderByIdAlmacenAsc()
                        .orElseThrow(() -> new IllegalArgumentException("No hay almacenes registrados")))
                .imagenRuta(guardarImagen(imagen))
                .build();

        productoRepository.save(producto);
        return "redirect:/admin/productos";
    }

    private Integer valorEntero(Integer valor, Integer defecto) {
        return valor == null ? defecto : valor;
    }

    private String guardarImagen(MultipartFile imagen) throws IOException {
        if (imagen == null || imagen.isEmpty()) {
            return null;
        }

        String original = imagen.getOriginalFilename() == null ? "producto.jpg" : imagen.getOriginalFilename();
        String extension = obtenerExtension(original);
        String nombreBase = Normalizer.normalize(original, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^A-Za-z0-9.]+", "-")
                .toLowerCase(Locale.ROOT);
        String nombreArchivo = UUID.randomUUID() + "-" + nombreBase;

        if (!nombreArchivo.endsWith(extension)) {
            nombreArchivo += extension;
        }

        Path destino = Path.of("uploads", "productos").toAbsolutePath().normalize();
        Files.createDirectories(destino);
        Files.copy(imagen.getInputStream(), destino.resolve(nombreArchivo), StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/productos/" + nombreArchivo;
    }

    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null) {
            return ".jpg";
        }

        int punto = nombreArchivo.lastIndexOf('.');
        if (punto < 0 || punto == nombreArchivo.length() - 1) {
            return ".jpg";
        }

        return nombreArchivo.substring(punto).toLowerCase(Locale.ROOT);
    }
}
