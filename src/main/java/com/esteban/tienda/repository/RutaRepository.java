/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.esteban.tienda.repository;
import com.esteban.tienda.domain.Ruta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ejfon
 */
@Repository
public interface RutaRepository extends JpaRepository<Ruta, Integer> {

    public List<Ruta> findAllByOrderByRequiereRolAsc();

}
