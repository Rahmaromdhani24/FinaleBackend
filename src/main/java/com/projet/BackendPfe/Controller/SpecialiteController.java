package com.projet.BackendPfe.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.projet.BackendPfe.Entity.Medecin;
import com.projet.BackendPfe.Entity.Patient;
import com.projet.BackendPfe.Entity.Specialite;
import com.projet.BackendPfe.domaine.Message;
import com.projet.BackendPfe.repository.MedecinRepository;
import com.projet.BackendPfe.repository.SpecialiteRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/specialite")
public class SpecialiteController {

	
	  @Autowired SpecialiteRepository repository ; 
	  @Autowired MedecinRepository medecinR ; 
	
 /****************************************************************************************************/	
	  @RequestMapping(value = "/addSpecialite", method = RequestMethod.POST, produces = "application/json")
	  //@PostMapping("/addSpecialite")
	  public ResponseEntity<?> ajouterSpecialie(@Valid @RequestBody Specialite specialite) {
	
			if (repository.existsByNom(specialite.getNom())) {
				return ResponseEntity
						.badRequest()
						.body(new Message("Error: cette Specialté existe deja !"));
			}
		  Specialite s = new Specialite(specialite.getNom(),new Date());
	  return new ResponseEntity<>(repository.save(s), HttpStatus.OK);} 
		 
  /***************************************************************************************************/	
	  @GetMapping( "/getSpecialite/{id}" )
		public Specialite getSpecialite(@PathVariable("id") int id)  {

		  Specialite specialite = repository.findById(id);
		  Specialite s = new Specialite(specialite.getId(), specialite.getNom(),specialite.getDate_creation());
				return s;
		}
	  /***************************************************************************************************/	
		@GetMapping("/getSpecialitee/{nom}")
		public Specialite getAll(@PathVariable ("nom") String nom){
			return repository.findByNom(nom);
		}
	  /***********************************************************************************************/
		 @PutMapping("updateSpecialite/{id}")
		  public ResponseEntity<?> updateDepartement(@PathVariable("id") int id, @RequestBody Specialite departement) throws IOException {
				   
			    Specialite specialite = repository.findById(id);
			    if (repository.existsByNom(departement.getNom())) {
			    	if(repository.findById(id).getId() != (repository.findByNom(departement.getNom()).getId())) {
					return ResponseEntity
							.badRequest()
							.body(new Message("Error: Departement déja existe !"));
				}
			    }

			        specialite.setNom(departement.getNom());
			    	//utilisateur.setPassword(encoder.encode(Admin.getPassword()));
			    	//utilisateur.setReservePassword(Admin.getReservePassword());
			    	
			      return new ResponseEntity<>(repository.save(specialite), HttpStatus.OK);} 
		 

		
     /***************************************************************************************************/	
	  @GetMapping( "/getAll" )
		public List<Specialite> getAll()  {
		  List<Specialite> sp = new ArrayList<Specialite>();
		  sp = repository.findAll();
				return sp;
		}
	/***********************************************************************************************/
	  @DeleteMapping("/deletSpecialite/{id}")
		public void deleteSpecialite(@PathVariable("id") int id){

			repository.deleteById(id);
		} 
  /***************************************************************************************************/	
    @GetMapping( "/getNbrMedecinParS/{nom}" )
	  public int getAllnbr(@PathVariable("nom") String nom)  {
			int nb = 0;
			List<Medecin> liste = medecinR.findAll() ;
			for(Medecin medecin :liste) {
		       if(medecin.getSpecialite().getNom().equals(nom)) {
	          	nb++ ; 	}
			}
			return nb;
		 }
	  
}
