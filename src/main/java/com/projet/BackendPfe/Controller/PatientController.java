package com.projet.BackendPfe.Controller;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.projet.BackendPfe.EmailSenderService;
import com.projet.BackendPfe.Entity.AdminDigitalManager;
import com.projet.BackendPfe.Entity.Medecin;
import com.projet.BackendPfe.Entity.Patient;
import com.projet.BackendPfe.config.JwtTokenUtil;
import com.projet.BackendPfe.domaine.JwtResponse;
import com.projet.BackendPfe.domaine.Message;
import com.projet.BackendPfe.repository.AdminMedicalManagerRepository;
import com.projet.BackendPfe.repository.DossierMedicalRepository;
import com.projet.BackendPfe.repository.PatientRepository;
import com.projet.BackendPfe.repository.UserRepository;
import com.projet.BackendPfe.request.LoginRequest;
import com.projet.BackendPfe.request.RegisterRequestMedecin;
import com.projet.BackendPfe.services.PatientService;
import com.projet.BackendPfe.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/patient")
public class PatientController {

	@Autowired 	AuthenticationManager authenticationManager;
	@Autowired	PatientRepository repository;
	@Autowired	private  PatientService service  ;
	@Autowired  EmailSenderService senderService;
	@Autowired	AdminMedicalManagerRepository repositoryAdmin;
	@Autowired	PasswordEncoder encoder;
	@Autowired	JwtTokenUtil jwtUtils;
	@Autowired	UserRepository repuser;
	@Autowired	DossierMedicalRepository repoDossier;
	@PostMapping("/signupPatient")
	public ResponseEntity<?> registerAdmin(@RequestBody  Patient patient)  {
		
	if (repuser.existsByEmail(patient.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new Message("Error: Email is already in use!"));
		}
		if (repuser.findByCin(patient.getCin())!= null) {
			return ResponseEntity
					.badRequest()
					.body(new Message("Error: Cin of Digital Manager  is already in use!"));
		}
		String motDePasse =service.generatePassword(8, patient.getPrenom(), patient.getNom());
		String image =null ; 
		Patient p = new Patient(
				patient.getCin(),
				patient.getNom(), 
				patient.getPrenom(),
				patient.getGender(), 
				service.generateUniqueUsername(patient.getPrenom(), patient.getNom()),
				patient.getEmail(),
				encoder.encode(motDePasse),
				motDePasse ,
				patient.getTelephone(), 
				image
				,new  Date() ,
				"Patient",
				//medecin ,
				patient.getDate_naissance(),
				patient.getAvis(),
				patient.getNotes()
				);
		repository.save(p);

		return ResponseEntity.ok(new Message("patient  registered successfully!"));}	
	
	/***********************************************************************************************/
	@PostMapping("/login")
	public ResponseEntity<?> authenticateAdmin(@Valid @RequestBody LoginRequest data) {
		System.out.println(data.getPassword());
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						data.getUsername(),
						data.getPassword()));	
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
		return ResponseEntity.ok(new JwtResponse(jwt, 
												 userDetails.getId(), 
												 userDetails.getUsername(), 
												 userDetails.getEmail()));}
											
	
	/***********************************************************************************************/	
	@PostMapping("/addPatientParAdminSansImageAvecDossier/{id}/{idDossier}")
	public long addPatientParAdmin( @PathVariable("id") long  idAdmin ,  @PathVariable("idDossier") long  idDossier ,@RequestBody Patient patient ) throws IOException {
		if (repuser.existsByUsername(patient.getUsername()) ) {
            throw new DataIntegrityViolationException("username existe déjà");}
		
		
		if (repuser.existsByEmail(patient.getEmail())) {
            throw new DataIntegrityViolationException("email existe déjà");}
		
		if (repuser.findByCin(patient.getCin())!= null) {
            throw new DataIntegrityViolationException("Cin existe déjà");}
		
		String motDePasse =service.generatePassword(8, patient.getPrenom(), patient.getNom());
		Patient user = new Patient( 
									patient.getCin() ,
									patient.getNom() ,
									patient.getPrenom() ,
									patient.getGender() ,
									service.generateUniqueUsername(patient.getPrenom(), patient.getNom()),
									patient.getEmail(),
									encoder.encode(motDePasse), 
									motDePasse ,
									patient.getTelephone()  ,
									patient.getImage(),
									new Date()
									, "Patient",
									repositoryAdmin.findById(idAdmin) ,
									patient.getDate_naissance(),
									patient.getAvis(),
									patient.getNotes(),
									repoDossier.findById(idDossier).get()) ;
				
                repository.save(user);
		return   repository.findByUsername(user.getUsername()).get().getId() ;
	}
	
	/***********************************************************************************************/	
	@PostMapping("/{idAdminMedical}")
	public long addPatientParAdminMedicalSansDossier( @PathVariable("idAdminMedical") long  idAdmin , @RequestBody Patient patient ) throws IOException {
		if (repuser.existsByUsername(patient.getUsername()) ) {
            throw new DataIntegrityViolationException("username existe déjà");}
		
		
		if (repuser.existsByEmail(patient.getEmail())) {
            throw new DataIntegrityViolationException("email existe déjà");}
		
		if (repuser.findByCin(patient.getCin())!= null) {
            throw new DataIntegrityViolationException("Cin existe déjà");}
		
		String motDePasse =service.generatePassword(8, patient.getPrenom(), patient.getNom());
		Patient user = new Patient( 
									patient.getCin() ,
									patient.getNom() ,
									patient.getPrenom() ,
									patient.getGender() ,
									service.generateUniqueUsername(patient.getPrenom(), patient.getNom()),
									patient.getEmail(),
									encoder.encode(motDePasse), 
									motDePasse ,
									patient.getTelephone()  ,
									patient.getImage(),
									new Date()
									, "Patient",
									repositoryAdmin.findById(idAdmin) ,
									patient.getDate_naissance(),
									patient.getAvis(),
									patient.getNotes());
				
                repository.save(user);
		return   repository.findByUsername(user.getUsername()).get().getId() ;
	}
	
	/************************************************************************************/
	  @PutMapping("/updatePatient/{id}")
	  public ResponseEntity<?> updateMedecin(@PathVariable("id") long id, @RequestBody Patient patient) {
	    System.out.println("Update Utilisateur with ID = " + id + "...");
	    Optional<Patient> UtilisateurInfo = repository.findById(id);
	    Patient utilisateur = UtilisateurInfo.get();
	    if (repuser.existsByUsername(patient.getUsername())) {
	    	if(repuser.findById(id).get().getId() != (repuser.findByUsername(patient.getUsername()).get().getId())) {
			return ResponseEntity
					.badRequest()
					.body(new Message("Error: Username is already taken!"));
		}
	    }
		if (repuser.existsByEmail(patient.getEmail())) {
			if(repuser.findById(id).get().getId() != (repuser.findByEmail(patient.getEmail()).getId())) {
			return ResponseEntity
					.badRequest()
					.body(new Message("Error: Email is already in use!"));}}
		
		if (repuser.existsByCin(patient.getCin())) {
			if(repuser.findById(id).get().getId() != (repuser.findByCin(patient.getCin()).getId())) {
			return ResponseEntity
					.badRequest()
					.body(new Message("Error: Cin is already in use!"));}}
		utilisateur.setCin(patient.getCin());
		utilisateur.setNom(patient.getNom());
		utilisateur.setPrenom(patient.getPrenom());
		utilisateur.setGender(patient.getGender());
		utilisateur.setEmail(patient.getEmail());
		utilisateur.setTelephone(patient.getTelephone());  
		utilisateur.setDate_naissance(patient.getDate_naissance());
	      return new ResponseEntity<>(repository.save(utilisateur), HttpStatus.OK);
	    } 
	  
	/***********************************************************************************************/	  
	
	   @PutMapping("/updateImageProfilePatient/{id}")
			public String updateImageProfile(@PathVariable("id") long id  , @RequestParam("file") MultipartFile file ) throws IOException {
					service.updateImagePatient(id,  file);
					
				return "Image profile Admin Medical Manager Updated  !!!!" ; }
	   

   /*************************************************************************************************/		
	
	    @GetMapping(path="getImageProfilePatient/{id}" , produces= MediaType.IMAGE_JPEG_VALUE)
	    public  byte[] getImagePatient(@PathVariable("id") long id) throws Exception{
	    	return 	service.getImagePatient(id);
	    }
   /**********************************************************************************************/
	    @GetMapping( "/getPatient/{id}" )
			public Patient getPatient(@PathVariable("id") long id) throws IOException {

			  Patient p = repository.findById(id).get();
				    return p;}
	  /***********************************************************************************************/
		@DeleteMapping("/deletPatient/{id}")
		public void deleteProduct(@PathVariable("id") long id){

			repository.deleteById(id);
		} 
		/*******************************************************************************************/
		@GetMapping("/all")
		public List<Patient> getAll(){
			return repository.findByRole("Patient");}
		
		/***********************************************************************************************/
		@GetMapping("/age/{idPatient}")
	     public int getAgePatient(@PathVariable("idPatient") long idPatient) {
		 
			 Patient patient = repository.findById(idPatient).get() ; 
			 // get date de NAissance de patient 
			Date date_naissance=patient.getDate_naissance();
			 // date d'aujourd'hui
		     LocalDate date = LocalDate.now() ; 
		     LocalDate localDate = date_naissance.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		     int yearDateNaissance  =localDate.getYear()  ;
		     int yearDateToDay = date.getYear()  ;
		     int agePatient = yearDateToDay-yearDateNaissance ; 
		  
	   	return agePatient ; } 
		
	/***********************************************************************************************/
		
		 @GetMapping("/homme")
		  public int getPatientsHomme() {
			 
				 List <Patient> patients = repository.findAll() ; 
			     List<Patient> resultat = new ArrayList<Patient>() ; 

				for(Patient patient :patients) {
			       if(patient.getGender().equals("homme")) {
		          	resultat.add(patient) ; 	}

				}
				int nbrPatient=resultat.size();
				return nbrPatient;
			 }
	/***********************************************************************************************/

		 @GetMapping("/femme")
		  public int getPatientsFemme() {
			 
				 List <Patient> patients = repository.findAll() ; 
			     List<Patient> resultat = new ArrayList<Patient>() ; 

				for(Patient patient :patients) {
			       if(patient.getGender().equals("femme")) {
		          	resultat.add(patient) ; 	}
}
				int nbrPatient=resultat.size();
				return nbrPatient;
			 }
		 
  /***********************************************************************************************/
		 
		 @GetMapping("/nbrAll")
		  public int getAllNbr() {
			 
				 List <Patient> patients = repository.findAll() ; 

				int nbrPatient=patients.size();
				return nbrPatient;
			 }
		 
 /***********************************************************************************************/
		 @GetMapping("/patientParMonth")
	     public int getPatients(@RequestParam("month") Integer month) {
		     List<Patient> resultat = new ArrayList<Patient>() ; 
			 List<Patient> liste = repository.findAll() ; 
			 Calendar calendar = Calendar.getInstance();
			
			 for(Patient patient :liste) {
			calendar.setTime(patient.getDate_inscription());
			int monthInscription = calendar.get(Calendar.MONTH)+1; // Le mois commence à 0 pour janvier
			int yearInscription = calendar.get(Calendar.YEAR);
			if (month.equals(monthInscription) && yearInscription == LocalDate.now().getYear()) {
			    resultat.add(patient);
			}	 
			 }
			 int nbrPatients = resultat.size() ; 
			 
		  return nbrPatients ;
		  } 
 
 /***********************************************************************************************/

		 // pour faire stat par date aujourdhui
		 @GetMapping("/allToDay")
			public int  getAllPatientsByDateInscription(){
			  LocalDate dateAuj = LocalDate.now();
			  int jourAuj = dateAuj.getDayOfMonth();
			  
			 List<Patient> liste = repository.findAll(); 
			 List<Patient> res = new ArrayList<Patient>() ; 
			 for(Patient patient : liste) {
				  Date date = patient.getDate_inscription();
				  SimpleDateFormat sdf = new SimpleDateFormat("dd");
				  String jourStr = sdf.format(date);
		          int jour = Integer.parseInt(jourStr);
				 if (jourAuj == jour) {
					    res.add(patient);
					}
			 }      
			 return res.size() ; }
 /***********************************************************************************************/
		// pour faire stat par cette semaine
			 @GetMapping("/allParSemaine")
				public int  getAllPatientsBySemaine(){
				 List<Patient> liste = repository.findAll(); 
				 List<Patient> res = new ArrayList<Patient>() ; 
				 Integer dateYearActuel = LocalDate.now().getYear() ; 
				 Integer dateMonthActuel = LocalDate.now().getMonthValue() ; 
				 Integer dateDayActuel = LocalDate.now().getDayOfMonth() ;
				 Integer testSemaine = dateDayActuel / 4 ;
				 Integer nbrSemaineS =0  ;
				 
				 if(testSemaine <= 1.75) {
					 nbrSemaineS =1 ; 
				 }
				 else if(testSemaine <= 3.5) {
					 nbrSemaineS =2 ; 
				 }else if(testSemaine <= 5.25) {
					 nbrSemaineS =3 ; 
				 }
				 else if(testSemaine <= 7.75) {
					  nbrSemaineS =4 ; 
				 }
				 
				 for(Patient patient : liste) {
					 
					 Integer dateInscripYearActuel = LocalDate.now().getYear() ; 
					 Integer dateInscripMonthActuel = LocalDate.now().getMonthValue() ; 
					 Integer dateInscripDayActuel = LocalDate.now().getDayOfMonth() ;
					 Integer nbrSemaineP=0 ;
					 Integer testSemaineP = dateInscripDayActuel / 4 ;
					 if(testSemaineP <= 1.75) {
						 nbrSemaineP =1 ; 
					 }
					 else if(testSemaineP <= 3.5) {
						 nbrSemaineP =2 ; 
					 }else if(testSemaineP <= 5.25) {
						 nbrSemaineP =3 ; 
					 }
					 else if(testSemaineP <= 7.75) {
						 nbrSemaineP =4 ; 
					 }
			if(dateYearActuel.equals(dateInscripYearActuel)) {
			    if(dateMonthActuel.equals(dateInscripMonthActuel)) {
				    if(nbrSemaineP.equals(nbrSemaineS) ) {		  
				 		 res.add(patient) ;
					 }
				 }  
			   }    
				 
				}
				 return res.size() ; 
			 } 
/***********************************************************************************************/
		 //pour faire stat sur moins actuel
			@GetMapping("/parMonth")
			public int nbrPAtientsMois() {
				List<Patient> liste = repository.findAll() ; 
				List<Patient> res = new ArrayList<Patient>() ; 
				Integer monthActuel = LocalDate.now().getMonthValue();
				Integer yearActuel = LocalDate.now().getYear();
				for(Patient  patient  : liste) {
					Date dateInscription = patient.getDate_inscription();
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(dateInscription);
					// Extraire le mois et l'année
					Integer dateInscrptionMonth = calendar.get(Calendar.MONTH) + 1; // Les mois commencent à 0, donc on ajoute 1
					Integer dateInscrptionYear = calendar.get(Calendar.YEAR);

					if((dateInscrptionMonth.equals(monthActuel)) && (dateInscrptionYear.equals(yearActuel))) {
						res.add(patient) ; 
					}
				}
				return res.size() ; 
			}
	/*******************************************************************************************/
			 //pour faire stat sur annee actuel
			@GetMapping("/parYear")
			public int nbrPatientsPArAnnee() {
				List<Patient> liste = repository.findAll() ; 
				List<Patient> res = new ArrayList<Patient>() ; 
		         
				Integer yearActuel = LocalDate.now().getYear();
				for(Patient  patient  : liste) {
					Integer dateInscrptionYear = patient.getDate_inscription().getYear();

					if((dateInscrptionYear.equals(yearActuel))) {
						res.add(patient) ; 
					}
				}
				return res.size() ; 
			}
	/*********************************************************************************************/		
		 @GetMapping("/ageS")
		  public int getAgePatientSupp() {
			 
				List <Patient> patients = repository.findAll() ; 
			     List<Patient> resultat = new ArrayList<Patient>() ; 

				 // get date de NAissance de patient --> de type string 
				for(Patient patient :patients) {
				 Date  dateNaiss= patient.getDate_naissance() ; 

				 // regle pour transformer string to ---> localDate 
				 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				 // date de nasiisance de type local date 
				// LocalDate dateNaissance = LocalDate.parse(dateNaiss, formatter);
				 // date d'aujourd'hui
			     LocalDate date = LocalDate.now() ; 
			  int yearDateNaissance  =dateNaiss.getYear()  ;
			  int yearDateToDay = date.getYear()  ;
			  int agePatient = yearDateToDay-yearDateNaissance ; 
			  if(agePatient>50) {
			resultat.add(patient) ; 
				}
			  
				}
				int nbrPatient=resultat.size();
				return nbrPatient;
			 }
		/********************************************************************************************/	 
			 @GetMapping("/ageI")
			  public int getAgePatientSup() {
				 
					List <Patient> patients = repository.findAll() ; 
				     List<Patient> resultat = new ArrayList<Patient>() ; 

					 // get date de NAissance de patient --> de type string 
					for(Patient patient :patients) {
					 Date  dateNaiss= patient.getDate_naissance() ; 
					 // date d'aujourd'hui
				     LocalDate date = LocalDate.now() ; 
				  int yearDateNaissance  =dateNaiss.getYear()  ;
				  int yearDateToDay = date.getYear()  ;
				  int agePatient = yearDateToDay-yearDateNaissance ; 
				  if(agePatient<50) {
				resultat.add(patient) ; 
					}
				  
					}
					int nbrPatient=resultat.size();
					return nbrPatient;
				 }
	/*********************************************************************************************/
			 // Gender des patients
			 @GetMapping("/hommme")
			  public int getPatientsHommme() {
				 
					 List <Patient> patients = repository.findAll() ; 
				     List<Patient> resultat = new ArrayList<Patient>() ; 

					for(Patient patient :patients) {
				       if(patient.getGender().equals("homme")) {
			          	resultat.add(patient) ; 	}

					}
					int nbrPatient=resultat.size();
					return nbrPatient;
				 }
	/*********************************************************************************************/
			 @GetMapping("/femmme")
			  public int getPatientsFemmme() {
				 
					 List <Patient> patients = repository.findAll() ; 
				     List<Patient> resultat = new ArrayList<Patient>() ; 

					for(Patient patient :patients) {
				       if(patient.getGender().equals("femme")) {
			          	resultat.add(patient) ; 	}

					}
					int nbrPatient=resultat.size();
					return nbrPatient;
				 }
/*********************************************************************************************/

			 @GetMapping("/nbrAlll")
			  public int getAllNbrr() {
				 
					 List <Patient> patients = repository.findAll() ; 

					int nbrPatient=patients.size();
					return nbrPatient;
				 }
/*********************************************************************************************/			 
			 @GetMapping("/getYear")
			  public int getYaer() {
				 
					return LocalDate.now().getYear(); }
				
/*********************************************************************************************/			 
			 
		@GetMapping("/getMoyenneAgeParMonth")
			  public int methode2 (@RequestParam("month") int month) {
			 int  ages_patients = 0  ;
			 int  vari = 0  ;
			 /********/
			 LocalDate dateActuelle = LocalDate.now();
			 int yearDateActuel = dateActuelle.getYear() ;
			 int monthActuel = dateActuelle.getMonthValue() ;
			 /*******/
			 List<Patient> resultat = new ArrayList<Patient>() ; 
			 List<Patient> liste = repository.findAll() ; 
			 for(Patient patient :liste) {
				 
				  Date date_naissance = patient.getDate_naissance();
				  Calendar calendar = Calendar.getInstance();
				  calendar.setTime(date_naissance);
				  int mois = calendar.get(Calendar.MONTH) + 1; // Les mois sont indexés à partir de 0, donc on ajoute 1
				  int annee = calendar.get(Calendar.YEAR);
				  
				 if(month == mois ) {
					 resultat.add(patient) ; 
					 vari ++ ;
					 ages_patients = ages_patients+ (yearDateActuel -annee) ; 
				 }
				 }
			
				if(vari == 0) {
					return 0 ; 
				}
				else {
					return ages_patients /vari ;
				}
					
				 
		}	
 }

