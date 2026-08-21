/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.esteban.tienda.service;
import com.esteban.tienda.domain.EstadoFactura;
import com.esteban.tienda.domain.Factura;
import com.esteban.tienda.domain.Item;
import com.esteban.tienda.domain.Producto;
import com.esteban.tienda.domain.Usuario;
import com.esteban.tienda.domain.Venta;
import com.esteban.tienda.repository.FacturaRepository;
import com.esteban.tienda.repository.ProductoRepository;
import com.esteban.tienda.repository.VentaRepository;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ejfon
 */
@Service
public class CarritoService {

    private static final String ATTRIBUTE_CARRITO = "carrito";

    private final ProductoRepository productoRepository;
    private final FacturaRepository facturaRepository;
    private final VentaRepository ventaRepository;

    public CarritoService(
            ProductoRepository productoRepository,
            FacturaRepository facturaRepository,
            VentaRepository ventaRepository) {

        this.productoRepository = productoRepository;
        this.facturaRepository = facturaRepository;
        this.ventaRepository = ventaRepository;
    }

    public List<Item> obtenerCarrito(HttpSession session) {

        @SuppressWarnings("unchecked")
        List<Item> carrito =
                (List<Item>) session.getAttribute(ATTRIBUTE_CARRITO);

        if (carrito == null) {
            carrito = new ArrayList<>();
        }

        return carrito;
    }

    public void guardarCarrito(
            HttpSession session,
            List<Item> carrito) {

        session.setAttribute(
                ATTRIBUTE_CARRITO,
                carrito
        );
    }

    public void agregarProducto(
            List<Item> carrito,
            Integer idProducto) {

        Producto producto =
                productoRepository.findById(idProducto)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Producto no encontrado."
                                )
                        );

        Optional<Item> itemExistente =
                carrito.stream()
                        .filter(i ->
                                i.getProducto()
                                        .getIdProducto()
                                        .equals(idProducto)
                        )
                        .findFirst();

        int cantidad = 1;

        if (itemExistente.isPresent()) {

            Item item = itemExistente.get();

            int nuevaCantidad =
                    item.getCantidad() + cantidad;

            if (nuevaCantidad > producto.getExistencias()) {

                throw new RuntimeException(
                        "Stock insuficiente para agregar "
                        + cantidad
                        + " unidades."
                );
            }

            item.setCantidad(nuevaCantidad);

        } else {

            if (cantidad > producto.getExistencias()) {

                throw new RuntimeException(
                        "Stock insuficiente para agregar "
                        + cantidad
                        + " unidades."
                );
            }

            Item nuevoItem = new Item();

            nuevoItem.setProducto(producto);
            nuevoItem.setCantidad(cantidad);

            nuevoItem.setPrecioHistorico(
                    producto.getPrecio()
            );

            carrito.add(nuevoItem);
        }
    }

    public Item buscarItem(
            List<Item> carrito,
            Integer idProducto) {

        if (carrito == null) {
            return null;
        }

        return carrito.stream()
                .filter(item ->
                        item.getProducto()
                                .getIdProducto()
                                .equals(idProducto)
                )
                .findFirst()
                .orElse(null);
    }

    public void eliminarItem(
            List<Item> carrito,
            Integer idProducto) {

        carrito.removeIf(item ->
                item.getProducto()
                        .getIdProducto()
                        .equals(idProducto)
        );
    }

    public void actualizarCantidad(
            List<Item> carrito,
            Integer idProducto,
            int nuevaCantidad) {

        if (nuevaCantidad <= 0) {

            eliminarItem(
                    carrito,
                    idProducto
            );

            return;
        }

        Optional<Item> itemExistente =
                carrito.stream()
                        .filter(i ->
                                i.getProducto()
                                        .getIdProducto()
                                        .equals(idProducto)
                        )
                        .findFirst();

        if (itemExistente.isPresent()) {

            Item item = itemExistente.get();

            Producto producto =
                    item.getProducto();

            if (nuevaCantidad
                    > producto.getExistencias()) {

                throw new RuntimeException(
                        "No hay suficiente stock disponible."
                );
            }

            item.setCantidad(nuevaCantidad);
        }
    }

    public int contarUnidades(
            List<Item> carrito) {

        if (carrito == null
                || carrito.isEmpty()) {

            return 0;
        }

        return carrito.stream()
                .mapToInt(Item::getCantidad)
                .sum();
    }

    public BigDecimal calcularTotal(
            List<Item> carrito) {

        if (carrito == null
                || carrito.isEmpty()) {

            return BigDecimal.ZERO;
        }

        return carrito.stream()
                .map(Item::getSubTotal)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    public void limpiarCarrito(
            HttpSession session) {

        List<Item> carrito =
                obtenerCarrito(session);

        carrito.clear();

        guardarCarrito(
                session,
                carrito
        );
    }

    @Transactional
    public Factura procesarCompra(
            List<Item> carrito,
            Usuario usuario) {

        System.out.println(
                "Se va a Procesar la Compra..."
        );

        if (carrito == null
                || carrito.isEmpty()) {

            throw new RuntimeException(
                    "El carrito está vacío para procesar la compra."
            );
        }

        Factura factura = new Factura();

        factura.setUsuario(usuario);
        factura.setFecha(
                LocalDateTime.now()
        );

        factura.setTotal(
                calcularTotal(carrito)
        );

        factura.setEstado(
                EstadoFactura.PAGADA
        );

        factura.setFechaCreacion(
                LocalDateTime.now()
        );

        factura.setFechaModificacion(
                LocalDateTime.now()
        );

        factura =
                facturaRepository.save(factura);

        for (Item item : carrito) {

            Producto producto =
                    productoRepository
                            .findById(
                                    item.getProducto()
                                            .getIdProducto()
                            )
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Producto no encontrado."
                                    )
                            );

            if (item.getCantidad()
                    > producto.getExistencias()) {

                throw new RuntimeException(
                        "Fallo en la compra: El producto "
                        + producto.getDescripcion()
                        + " ya no tiene suficiente stock."
                );
            }

            Venta venta = new Venta();

            venta.setFactura(factura);
            venta.setProducto(producto);

            venta.setPrecioHistorico(
                    item.getPrecioHistorico()
            );

            venta.setCantidad(
                    item.getCantidad()
            );

            venta.setFechaCreacion(
                    LocalDateTime.now()
            );

            venta.setFechaModificacion(
                    LocalDateTime.now()
            );

            ventaRepository.save(venta);

            producto.setExistencias(
                    producto.getExistencias()
                    - item.getCantidad()
            );

            productoRepository.save(producto);
        }

        return factura;
    }
}
