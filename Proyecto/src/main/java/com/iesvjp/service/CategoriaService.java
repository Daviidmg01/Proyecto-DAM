package com.iesvjp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.iesvjp.model.Categoria;
import com.iesvjp.repository.ICategoriaRepository;



@Service("categoriaService")
public class CategoriaService implements ICategoriaService{
	
	//Inyeccion del repositorio de categorías
	@Autowired
	@Qualifier("categoriaRepository")
	private ICategoriaRepository categoriaRepository;
	
	//Guarda o actualiza una categoría en la Base de Datos
	@Override
	public Categoria addCategoria(Categoria categoriaModel) {
		Categoria categoria = categoriaRepository.save(categoriaModel);
		return categoria;
	}
	
	//Devuelve una lista de todas las categorías
	@Override
	public List<Categoria> listAllCategorias(){
		List<Categoria> categorias =  categoriaRepository.findAll();
		return categorias;
	}

	//Busca una categoría por el ID
	@Override
	public Categoria findCategoriaById(int id) {
		return categoriaRepository.findById(id);
	}

	//Elimina una categoría por el ID
	@Override
	public void removeCategoria(int id) {
		Categoria categoria = categoriaRepository.findById(id);
		if (categoria != null) { //Si existe lo borra
			categoriaRepository.delete(categoria);
		}
	}
	
}
