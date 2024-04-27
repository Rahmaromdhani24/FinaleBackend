package com.projet.BackendPfe.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.projet.BackendPfe.Entity.AdminDigitalManager;
import com.projet.BackendPfe.Entity.DossierMedical;
import com.projet.BackendPfe.Entity.Patient;
import com.projet.BackendPfe.repository.AdminMedicalManagerRepository;
import com.projet.BackendPfe.repository.DossierMedicalRepository;
import com.projet.BackendPfe.repository.MedecinRepository;
import com.projet.BackendPfe.repository.PatientRepository;
import com.projet.BackendPfe.services.DossierMedicalService;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/DossierMedical")
public class DossierMedicalController {

	@Autowired DossierMedicalRepository  repository ; 
	@Autowired MedecinRepository  repositoryM ; 
	@Autowired PatientRepository  repositoryP ; 
	@Autowired AdminMedicalManagerRepository  repositoryA ;
	@Autowired DossierMedicalService  service ; 
	
	 @PostMapping("/add/{idMedecin}/{idPatient}/{adminM}")
	    public DossierMedical create(@PathVariable("idMedecin") long idMedecin ,
	    	                       	@PathVariable("idPatient") long idPatient ,
	    	                    	@PathVariable("adminM") long adminM ,
	    		                    @RequestBody DossierMedical dosssierMedicale){
	    
		 DossierMedical dossier = new DossierMedical( repositoryP.findById(idPatient).get(),LocalDate.now(),
				                                      dosssierMedicale.getDiagnostic(),
				                                      repositoryA.findById(adminM),
				                                      repositoryM.findById(idMedecin).get());
	        return repository.save(dossier);
	    }
	    @PutMapping("update/{id}")
	    public DossierMedical update(@PathVariable int id ,@RequestBody DossierMedical dosssierMedicale){
	        return service.modifierDossier(id,dosssierMedicale);
	    }
	    @DeleteMapping("{id}")
	    public String delet(@PathVariable int id){
	        return service.SupprimerDossier(id);
	    }

	    @GetMapping("/get/{id}")
	    public DossierMedical getById(@PathVariable int id){
	        return service.getDossier(id);
	    }
	    
	    /*************************************************************************/
		@PostMapping("/addDossierMedical/{idAdmin}")
		  public ResponseEntity<Long> fileUpload(@RequestParam("file") MultipartFile file,
                  @PathVariable("idAdmin") long idAdmin) throws IOException {
             long  dossierMedicalId = service.addFile(file, idAdmin);
             return ResponseEntity.ok().body(dossierMedicalId);
} 
		
	    @GetMapping( path="/getRapport/{id}" , produces= MediaType.APPLICATION_PDF_VALUE)
			public byte[] getImage(@PathVariable("id") long id)throws Exception{
	    	DossierMedical dossierMedical = repository.findById(id).get();
		  return  service.getFile(dossierMedical.getIdDossierMedical());
				
		}
		
		@GetMapping("/all")
		public List<DossierMedical> getAll(){
			return repository.findAll() ; 
		}
		@GetMapping("/nbrall")
		public int getnbrAll(){
			return (repository.findAll()).size() ; 
		}
		@DeleteMapping("/deleteDossierMedical/{id}")
		public void  deleteDossierMedical(@PathVariable("id") long id){
			repository.deleteById(id); ; 
		}
   @PutMapping("/updateDossier/{idDossier}")
   public String updateDossierMedical(@PathVariable("idDossier") long id  , @RequestParam("file") MultipartFile file ) throws IOException {
     service.modifierFile(file , id);
				
			return "Dossier uploaded !!!!" ; }
   
   @GetMapping("/getAllDossierDePatient")
	public List<Patient> getAllPatients(){
	   List<Patient> initiale = repositoryP.findAll() ; 
	   List<Patient> finale  = new ArrayList<Patient>() ; 
	   
	   for(Patient patient:initiale) {
		   if(patient.getDossierMedical() != null && patient.getDossierMedical().getIdDossierMedical() != 0) {
			   finale.add(patient);
			}
	}
	   return finale ; 
}
   @GetMapping("/getAllDossierDePatientDeMedecin/{idMedecin}")
	public List<Patient> getAllPatientsMedecinX(@PathVariable("idMedecin") long idMedecin){
	   List<Patient> initiale = repositoryP.findAll() ; 
	   List<Patient> finale  = new ArrayList<Patient>() ; 
	   
	   for(Patient patient:initiale) {
		   if(patient.getDossierMedical() != null && patient.getDossierMedical().getIdDossierMedical() != 0) {
			   if(patient.getDossierMedical().getMedecinDossier().getId() == idMedecin )
			   finale.add(patient);
			}
	}
	   return finale ; 
}
   @GetMapping("/getNumAllDossierDashbord")
	public int getAllNumDossierPatients(){
	   List<Patient> initiale = repositoryP.findAll() ; 
	   List<Patient> finale  = new ArrayList<Patient>() ; 
	   
	   for(Patient patient:initiale) {
		   if(patient.getDossierMedical() != null && patient.getDossierMedical().getIdDossierMedical() != 0) {
			   finale.add(patient);
			}
	}
	   return finale.size(); 
}
}
