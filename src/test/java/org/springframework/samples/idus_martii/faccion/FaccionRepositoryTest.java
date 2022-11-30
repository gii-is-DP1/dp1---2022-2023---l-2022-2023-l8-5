package org.springframework.samples.idus_martii.faccion;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.samples.idus_martii.jugador.Jugador;

@DataJpaTest
public class FaccionRepositoryTest {
	@Autowired
	FaccionRepository faccionRepository;
	
	@Test
	public void testGetJugadoresPartida() {
		List<Jugador> listaJugadores = faccionRepository.getJugadoresPartida(1);
		assertNotNull(listaJugadores);
		assertFalse(listaJugadores.isEmpty());
		List<Jugador> listaJugadores2 = faccionRepository.getJugadoresPartida(2);
		assertNotNull(listaJugadores2);
		assertTrue(listaJugadores2.isEmpty());
	}

	@Test
	public void testGetFaccionesPartida() {
		List<Faccion> listafacciones = faccionRepository.getFaccionesPartida(1);
		assertNotNull(listafacciones);
		assertFalse(listafacciones.isEmpty());
	}
	@Test
	public void testGetFaccionJugadorPartida() {
		Faccion faccion = faccionRepository.getFaccionJugadorPartida(1,1);
		assertNotNull(faccion);
		assertFalse(faccion.isNew());
	}
	
	
	
}


	
