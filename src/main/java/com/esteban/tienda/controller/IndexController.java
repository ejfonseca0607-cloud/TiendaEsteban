/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.esteban.tienda.controller;
import com.esteban.tienda.service.CategoriaService;
import com.esteban.tienda.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
/**
 *
 * @author ejfon
 */
@Controller
public class IndexController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public IndexController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/")
    public String listado(Model model) {
        var productos = productoService.getProductos(false);
        model.addAttribute("productos", productos);

        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("categorias", categorias);

        return "/index";
    }

    @GetMapping("/consultas/{idCategoria}")
    public String listado(@PathVariable("idCategoria") Integer idCategoria, Model model) {

        model.addAttribute("idCategoriaActual", idCategoria);

        var categoriaOptional = categoriaService.getCategoria(idCategoria);

        if (categoriaOptional.isEmpty()) {
            // Puede ser que no exista la categoría buscada...
            model.addAttribute("productos", java.util.Collections.emptyList());
        } else {
            var categoria = categoriaOptional.get();
            var productos = categoria.getProductos();
            model.addAttribute("productos", productos);
        }

        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("categorias", categorias);

        return "/index";
    }

}
