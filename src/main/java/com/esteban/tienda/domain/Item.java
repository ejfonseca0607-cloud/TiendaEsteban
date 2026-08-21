/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.esteban.tienda.domain;
import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author ejfon
 */
@Data
public class Item {

    private Producto producto;
    private int cantidad;
    private BigDecimal precioHistorico;

    public Item() {
    }

    public Item(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;

        if (producto != null) {
            this.precioHistorico = producto.getPrecio();
        }
    }

    public BigDecimal getSubTotal() {

        if (precioHistorico == null) {
            return BigDecimal.ZERO;
        }

        return precioHistorico.multiply(
                BigDecimal.valueOf(cantidad)
        );
    }
}
