package com.iesvjp.controller;


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

import com.iesvjp.model.Categoria;
import com.iesvjp.service.ICategoriaService;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Controller
@RequestMapping("/admin/categoria")
public class CategoriaController {
	//Inyeccion de dependencias para el servicio de categorías
	@Autowired
	@Qualifier("categoriaService")
	private ICategoriaService categoriaService;
	
	private static final Log LOG = LogFactory.getLog(CategoriaController.class);
	
	//Metodo GET para listar todas las categorías
	@GetMapping(value = { "", "/" })
	private ModelAndView listadoCategorias() {
		ModelAndView mav = new ModelAndView("admin/list-categoria");
		
		//Para comprobar que esta recibiendo categorias
		List<Categoria> categorias = categoriaService.listAllCategorias();
	    
	    
		mav.addObject("listaCategorias", categorias);
		return mav;
	}
	
	//Metodo POST para agregar o actualizar una categoría
	@PostMapping("/addCategoria")
	public String addOrUpdateCategoria(@ModelAttribute("categoriaModel") Categoria categoriaModel, RedirectAttributes ra) {
		//System.out.println("Categoria recibida: " + categoriaModel); //Para saber que estaba recibiendo una categoria

		if (categoriaModel.getId() != null) {
			categoriaModel.setNombre(categoriaModel.getNombre().toUpperCase());
		}
		
		if (categoriaService.addCategoria(categoriaModel) != null) {
			ra.addFlashAttribute("result", 1);
		} else {
			ra.addFlashAttribute("result", 0);
		}
		//Redirecciono
		return "redirect:/admin/categoria/";
	}
	
	//Metodo GET para mostrar el formulario de nueva categoría
	@GetMapping("/nueva")
	private String showCategoriaForm(Model model) {
		LOG.info("METHOD: showCategoriaForm -- PARAMS: " + model);
		Categoria categoria = new Categoria();
		model.addAttribute("categoriaModel", categoria);
		return "admin/form-categoria";
	}
	
	//Metodo para eliminar una categoria por su ID
	@GetMapping("/borrar/{id}")
	private ModelAndView removeCategoria(@PathVariable("id") int id) {
		categoriaService.removeCategoria(id);
		return listadoCategorias();
	}
	
	//Metodo para editar una categoria por su ID
	@GetMapping("/editar/{id}")
	private String editarCategoria(@PathVariable("id") Integer id, Model model) {
		Categoria categoria = categoriaService.findCategoriaById(id);
		model.addAttribute("categoriaModel", categoria);
		return "admin/form-categoria";
	}
}
