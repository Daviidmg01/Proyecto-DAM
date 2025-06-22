package com.iesvjp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.iesvjp.model.Puntuacion;
import com.iesvjp.repository.IPuntuacionRepository;

@Service("puntuacionService")
public class PuntuacionService implements IPuntuacionService{

	//Inyeccion del repositorio de Puntuacion
	@Autowired
	@Qualifier("puntuacionRepository")
	private IPuntuacionRepository puntuacionRepository;
	
	@Override
	public Puntuacion addPuntuacion(Puntuacion puntuacionModel) {
		// TODO Auto-generated method stub
		Puntuacion puntuacion = puntuacionRepository.save(puntuacionModel);
		return puntuacion;
	}

	@Override
	public List<Puntuacion> listAllPuntuacion() {
		// TODO Auto-generated method stub
		List<Puntuacion> listaPuntuacion = puntuacionRepository.findAll();
		return listaPuntuacion;
	}

	@Override
	public Puntuacion findPuntuaciontById(int id) {
		// TODO Auto-generated method stub
		return puntuacionRepository.findById(id);
	}

	@Override
	public void removePuntuacion(int id) {
		// TODO Auto-generated method stub
		Puntuacion puntuacion = puntuacionRepository.findById(id);
		
		if (puntuacion != null) {
			puntuacionRepository.delete(puntuacion);
		}
	}

}
