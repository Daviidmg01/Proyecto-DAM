package com.iesvjp.service;

import java.util.List;

import com.iesvjp.model.Puntuacion;

public interface IPuntuacionService {
	public Puntuacion addPuntuacion(Puntuacion puntuacionModel);
	public List<Puntuacion> listAllPuntuacion();
	public Puntuacion findPuntuaciontById(int id);
	public void removePuntuacion(int id);
}
