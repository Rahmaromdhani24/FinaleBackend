package com.projet.BackendPfe.Controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projet.BackendPfe.Entity.DossierMedical;
import com.projet.BackendPfe.Entity.Medecin;
import com.projet.BackendPfe.Entity.Operation;
import com.projet.BackendPfe.Entity.Patient;
import com.projet.BackendPfe.Entity.RendezVous;
import com.projet.BackendPfe.Entity.Specialite;
import com.projet.BackendPfe.repository.MedecinRepository;
import com.projet.BackendPfe.repository.PatientRepository;
import com.projet.BackendPfe.repository.RendezVousRepository;
import com.projet.BackendPfe.services.RendezVousService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/rendezVous")
public class RendezVousController {

	
	  @Autowired RendezVousRepository repository ; 
	  @Autowired PatientRepository repositoryP; 
	  @Autowired MedecinRepository repositoryM; 
	
	  @GetMapping( "/getAll" )
		public List<RendezVous> getAll()  {
		  List<RendezVous> rv = new ArrayList<RendezVous>();
		  rv = repository.findAll();
				return rv;
		}
	/***********************************************************************************************/
	  @DeleteMapping("/deletRendezVous/{id}")
		public void deleteSpecialite(@PathVariable("id") int id){

			repository.deleteById(id);
		} 
  /*****************************************************************************************************/
	  @GetMapping("/allToDay")
		public int  getAllRendezVousByDateInscription(){
		  LocalDate dateAuj = LocalDate.now();
		  int jourAuj = dateAuj.getDayOfMonth();
		 
          
		 List<RendezVous> liste = repository.findAll(); 
		 List<RendezVous> rend = new ArrayList<RendezVous>() ; 
		 for(RendezVous rendezVous : liste) {
          Date date = rendezVous.getDateRdv();
		  SimpleDateFormat sdf = new SimpleDateFormat("dd");
		  String jourStr = sdf.format(date);
          int jour = Integer.parseInt(jourStr);
			 if (rendezVous.getPresence() == null) {
			 if (jourAuj == jour) {
				    rend.add(rendezVous);
				}
		 }      
		 }
		 return rend.size() ;
}
	  /*******************************************************************/
	  @GetMapping("/allRVReserve")
		public List<RendezVous>  getAllRendezVous(){
		  LocalDate dateAuj = LocalDate.now();
		  int jourAuj = dateAuj.getDayOfMonth();
		 
          
		 List<RendezVous> liste = repository.findAll(); 
		 List<RendezVous> rend = new ArrayList<RendezVous>() ; 
		 for(RendezVous rendezVous : liste) {
          Date date = rendezVous.getDateRdv();
		  SimpleDateFormat sdf = new SimpleDateFormat("dd");
		  String jourStr = sdf.format(date);
          int jour = Integer.parseInt(jourStr);
			 if (rendezVous.getPresence() == null) {
			 if (jourAuj == jour) {
				    rend.add(rendezVous);
				}
		 }      
		 }
		 return rend ;
}
	  /*****************************************************************************************************/
	  @GetMapping("/rendez")
		public int  getAllRendezVousAttent(){
		 List<RendezVous> liste = repository.findAll(); 
		 List<RendezVous> rend = new ArrayList<RendezVous>() ; 
		 for(RendezVous rendezVous : liste) {
			 if (rendezVous.getMedecinRV() == null) {
		 }      
		 }
		 return rend.size() ;
}
	 /***********************************************************************************************/
		 @PostMapping( "/addRV/{idPatient}/{idMedecin}" )
			public ResponseEntity<?> addRV(@PathVariable("idPatient") long idP ,
					@PathVariable("idMedecin") long idM , @RequestBody RendezVous rv)  {
			 Patient p = repositoryP.findById(idP).get();
			 Medecin m = repositoryM.findById(idM).get();
			 RendezVous rendezvous = new RendezVous(m,p,rv.getDateRdv(),LocalDate.now(),rv.getDescription(), null);
			    return new ResponseEntity<>(repository.save(rendezvous), HttpStatus.OK);
			}
/*****************************************************************************************************/
      @GetMapping("/myRv/{idPatient}")
	 public List<RendezVous>  getAllMyRV(@PathVariable("idPatient") long idPatient){
	     List<RendezVous> liste = repository.findAll(); 
	    List<RendezVous> finale = new ArrayList<RendezVous>() ; 
	    for(RendezVous rendezVous : liste) {
		 if (rendezVous.getPresence() == null && rendezVous.getPatientRV().getId()==(idPatient)) {
			 finale.add(rendezVous);
	 }      
	 }
	 return  finale;
}
      /*****************************************************************************************************/
      @GetMapping("/myRvMedecin/{idMedecin}")
	 public List<RendezVous>  myRvMedecin(@PathVariable("idMedecin") long idMedecin){
	     List<RendezVous> liste = repository.findAll(); 
	    List<RendezVous> finale = new ArrayList<RendezVous>() ; 
	    for(RendezVous rendezVous : liste) {
		 if (rendezVous.getPresence() == null && rendezVous.getMedecinRV().getId()==(idMedecin)) {
			 finale.add(rendezVous);
	 }      
	 }
	 return  finale;
}
      
      /********************************************************************************/
      @PostMapping("/presence/{idRV}")
 	 public void  prencense(@PathVariable("idRV") int idDossier){
 	    RendezVous rv= repository.findById(idDossier); 
 	    rv.setPresence("oui");
 }
      }
