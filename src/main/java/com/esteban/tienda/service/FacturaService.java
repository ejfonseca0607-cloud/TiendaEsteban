/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.esteban.tienda.service;
import com.esteban.tienda.domain.Factura;
import com.esteban.tienda.repository.FacturaRepository;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ejfon
 */
@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;

    public FacturaService(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    @Transactional(readOnly = true)
    public Factura getFacturaConVentas(Integer idFactura) {

        return facturaRepository
                .findByIdFacturaConDetalle(idFactura)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Factura con ID "
                                + idFactura
                                + " no encontrada."
                        )
                );
    }
}
