package org.springframework.samples.idus_martii.turno;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.expression.AccessException;
import org.springframework.samples.idus_martii.faccion.FaccionesConverter;
import org.springframework.samples.idus_martii.faccion.FaccionesEnumerado;
import org.springframework.samples.idus_martii.jugador.Jugador;
import org.springframework.samples.idus_martii.partida.Partida;
import org.springframework.samples.idus_martii.partida.PartidaService;
import org.springframework.samples.idus_martii.turno.Estados.EstadoTurno;
import org.springframework.samples.idus_martii.turno.VotosTurno;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TurnoService {

    TurnoRepository repo;
    VotosTurnoRepository repoVotosTurno;
    PartidaService partidaService;
    FaccionesConverter faccionesConverter;

    @Autowired
    public TurnoService(TurnoRepository repo, @Lazy PartidaService partidaService, FaccionesConverter faccionesConverter, VotosTurnoRepository repoVotosTurno){
        this.repo = repo;
        this.partidaService = partidaService;
        this.faccionesConverter = faccionesConverter;
        this.repoVotosTurno = repoVotosTurno;
    }

    List<Turno> getTurnos(){
        return repo.findAll();
    }
    
    public Turno getById(int id){
        return repo.findById(id).get();
    }

    public void deleteTurnoById(int id){
        repo.deleteById(id);
    }

    public void save(Turno turno){
        repo.save(turno);
    }

    //TODO restriccion un jugador solo puede votar una vez
    public void anadirVoto(Integer turnoId, Jugador jugador, String strVoto) throws AccessException {
        Turno turno = repo.findById(turnoId).get();
        
        if (!(jugador.equals(turno.getEdil1()) || jugador.equals(turno.getEdil2()))) {
            throw new AccessException("Solo pueden votar los ediles");
        }

        anadirVotoTurno(turno, jugador, faccionesConverter.convert(strVoto));

        save(turno);
    }

    public void cambiarVoto(Integer turnoId, Jugador jugador, Integer edilId, String voto) throws AccessException {
        Turno turno = repo.findById(turnoId).get();
        VotosTurno v = findVoto(turnoId, edilId);

        if (!(jugador.equals(turno.getPredor()) || 
            (findVoto(turnoId, jugador.getId()) != null && findVoto(turnoId, jugador.getId()).getTipoVoto() == FaccionesEnumerado.Mercader))) {

            throw new AccessException("Solo pueden cambiar los votos los Predores o los Ediles con voto amarillo");
        }

        if (!v.getEspiado()) {
            throw new AccessException("Solo se pueden cambiar votos espiados");
        }

        if (v.getVotoOriginal() != null) {
            throw new AccessException("No se puede cambiar un voto ya cambiado");
        }

        v.setVotoOriginal(v.getTipoVoto());

        v.setTipoVoto(faccionesConverter.convert(voto));

        repoVotosTurno.save(v);
    }
    
    public VotosTurno findVoto(Integer turnoId, Integer jugadorId){
    	return repo.findVotoByturnoAndPlayer(turnoId, jugadorId);
    }
    
    public void anadirVotoTurno(Turno turno, Jugador jugador, FaccionesEnumerado voto) {
        VotosTurno votosTurno = new VotosTurno();
        votosTurno.turno = turno;
        votosTurno.jugador = jugador;
        votosTurno.tipoVoto = voto;
    	repoVotosTurno.save(votosTurno);
    }

    public void espiarVoto(Integer partidaId, Jugador jugador, String voto) throws AccessException {
        Turno turno = partidaService.getTurnoActual(partidaId);

        if (jugador != turno.getPredor()) {
            throw new AccessException("Solo los Predores pueden espiar");
        }

        VotosTurno votoEdil1 = findVoto(turno.getId(), turno.getEdil1().getId());
        VotosTurno votoEdil2 = findVoto(turno.getId(), turno.getEdil2().getId());
        
        if (votoEdil1.getEspiado() || votoEdil2.getEspiado()) {
            throw new AccessException("Solo puedes espiar una vez por turno");
        }

        if (voto.equals("1")) {
            votoEdil1.setEspiado(true);
            repoVotosTurno.save(votoEdil1);
        } else
        if (voto.equals("2")) {
            votoEdil2.setEspiado(true);
            repoVotosTurno.save(votoEdil2);
        }
    }
    
    /*public void asignarRol(String rol, Jugador jugador, Integer turnoId) { //TODO revisar
    	
    	Turno turno = getById(turnoId);
    	int anterior = repo.findById(turnoId).get().getNumTurno()-1;
    	
    	
    	List<Jugador> edilesTurnoAnterior= new ArrayList<>();
    	edilesTurnoAnterior.add(repo.turnoPorNumero(anterior).getEdil1());
    	edilesTurnoAnterior.add(repo.turnoPorNumero(anterior).getEdil2());
    	
    	List<Jugador> edilesTurnoActual= new ArrayList<>();
    	if(repo.turnoPorNumero(anterior+1).getEdil1()!=null) {edilesTurnoActual.add(jugador);}
    	if(repo.turnoPorNumero(anterior+1).getEdil2()!=null) {edilesTurnoActual.add(jugador);}
    	
    	
    	
    	
    	if(rol.equals("predor") && repo.turnoPorNumero(anterior).getPredor() != jugador) {
    		turno.setPredor(jugador);
    	}
    	else if(!edilesTurnoAnterior.contains(jugador) 
    			&& (edilesTurnoActual.get(0)==null || (edilesTurnoActual.get(1)==null)) ) {
        			edilesTurnoActual.add(jugador);
    	}
    	else if(edilesTurnoAnterior.contains(jugador) && turno.getRonda().getPartida().getNumeroJugadores()==5
    			&& (edilesTurnoActual.isEmpty() || !edilesTurnoActual.contains(jugador))) {
    			edilesTurnoActual.add(jugador);
    	}
    	else {
    		System.out.println("Ha petao el edil, mal hecho");
    	}
    	
    	turno.setEdil1(edilesTurnoActual.get(0));
    	turno.setEdil1(edilesTurnoActual.get(1));
    	save(turno);
    }*/

}