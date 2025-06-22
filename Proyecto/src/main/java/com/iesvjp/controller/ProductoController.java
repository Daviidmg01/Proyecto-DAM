package com.iesvjp.controller;

import java.security.ProtectionDomain;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iesvjp.model.Producto;
import com.iesvjp.service.ICategoriaService;
import com.iesvjp.service.IProductoService;

@Controller
@RequestMapping("/admin/producto")
public class ProductoController {
	
	//Inyeccion de dependecias para el servicio de categorias
	@Autowired
	@Qualifier("categoriaService")
	private ICategoriaService categoriaService;
	
	
	//Inyeccion de dependencias para el servicio de productos
	@Autowired
	@Qualifier("productoService")
	private IProductoService productoService;
	
	// Metodo GET para listar todos los productos
	@GetMapping("/")
	private ModelAndView listadoProductos() {
		ModelAndView mav = new ModelAndView("/admin/list-producto");
		
		List<Producto> productos = productoService.listAllProductos();
		
		
		mav.addObject("listaProductos", productos);
		return mav;
	}
	
	//Metodo GET para mostrar el formulario del nuevo producto
	@GetMapping("/nuevo")
	private String showProductoForm(Model model) {
		Producto producto = new Producto();
		model.addAttribute("productoModel", producto);
		model.addAttribute("listaCategorias", categoriaService.listAllCategorias());
		return "admin/form-producto";
	}
	
	//Metodo para borrar un producto por su ID
	@GetMapping("/borrar/{id}")
	private ModelAndView removeProducto(@PathVariable("id") int id) {
		productoService.removeProducto(id);
		return listadoProductos();
	}
	
	//Metodo para editar un producto por su ID
	@GetMapping("/editar/{id}")
	private String editarProducto(@PathVariable("id") Integer id, Model model) {
		//Busco el producto por su ID y se la paso al modelo
		Producto producto = productoService.findProductoById(id);
		model.addAttribute("productoModel", producto);
		
		//Paso el listado de categorias
		model.addAttribute("listaCategorias", categoriaService.listAllCategorias());
		return "admin/form-producto";
	}
	
	//Metodo POST para agregar o actualizar un producto
	@PostMapping("/addProducto")
	public String addOrUpdateProducto(@ModelAttribute("productoModel") Producto productoModel, RedirectAttributes ra) {
		

		System.out.println("\n\nid: " + productoModel.getId());
		if (productoModel.getId() == null) {
			if (productoModel.getPvp() < 0) {
				
				System.out.println("El Pvp no puede ser menor que 0");
				
			} else if (productoService.addProducto(productoModel) != null) {
				ra.addFlashAttribute("result" , 1);
			} else {
				ra.addFlashAttribute("result" , 0);
			}
			
		}else if (productoService.addProducto(productoModel) != null) {
			ra.addFlashAttribute("result" , 1);
		} else {
			ra.addFlashAttribute("result" , 0);
		}
		
		 
		
		return "redirect:/admin/producto/";
	}
	
}
