package com.projet.BackendPfe.services;


import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import com.projet.BackendPfe.Entity.AdminMedicalManager;
import com.projet.BackendPfe.Entity.DossierMedical;
import com.projet.BackendPfe.repository.AdminMedicalManagerRepository;
import com.projet.BackendPfe.repository.DossierMedicalRepository;

@Service
public class DossierMedicalService  implements IDossierMedical{

	@Autowired DossierMedicalRepository repository ;
	@Autowired AdminMedicalManagerRepository adminrepo ;

	@Override
	public DossierMedical ajouterDossier(DossierMedical dossierMedicale) {
		// TODO Auto-generated method stub
		 return repository.save(dossierMedicale);
	}

	@Override
	public List<DossierMedical> getAll() {
		// TODO Auto-generated method stub
		return repository.findAll();
	}

	@Override
	public DossierMedical modifierDossier(long id, DossierMedical dossierMedicale) {
		// TODO Auto-generated method stub
		return repository.findById(id)
                .map(d -> {
                   // car date deja automatiquement d.setDate_creation_dossier(dossierMedicale.getDate_creation_dossier());
                    d.setDiagnostic(dossierMedicale.getDiagnostic());
                   // d.setPatient(dossierMedicale.getPatient());
                    return repository.save(d);
                }).orElseThrow(() -> new RuntimeException("Dossier non trové"));
	}

	@Override
	public String  SupprimerDossier(long id) {
		// TODO Auto-generated method stub
		  repository.deleteById(id);
		  return "dossier Supprimée";}
			

	@Override
	public DossierMedical getDossier(long id) {
		// TODO Auto-generated method stub
		 return repository.findById(id).get();
	}

	@Override
	public byte[] getFile(long id) throws Exception {
		String urlfilePdf =repository.findById(id).get().getUrlFile() ; 
		Path p =Paths.get(System.getProperty("user.home")+"/Desktop/fils/Rapports/",urlfilePdf);
		return Files.readAllBytes(p);
	}

	@Override
	 public long addFile(MultipartFile file, long id) throws IOException {
        AdminMedicalManager admin = adminrepo.findById(id);
        File myFile = new File(System.getProperty("user.home") + "/Desktop/fils/Rapports/", file.getOriginalFilename() + "");
        System.out.printf("fileeeeee" + System.getProperty("user.home") + "/Desktop/fils/Rapports/", file.getOriginalFilename());
        myFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(myFile);
        fos.write(file.getBytes());
        fos.close();
        DossierMedical dossier = new DossierMedical();
        dossier.setDate_creation_dossier(LocalDate.now());
        dossier.setUrlFile(myFile.getName());
        dossier.setAdminMedicalManagerDossier(admin);
        DossierMedical savedDossier = repository.save(dossier);
        return savedDossier.getIdDossierMedical(); // Retourne l'ID du dossier médical ajouté
    }
	
	 public long modifierFile(MultipartFile file, long id) throws IOException {
	        DossierMedical dossier = repository.findById(id).get();
	        StringBuilder fileNames=new StringBuilder();
			 Path fileNameAndPath=Paths.get(System.getProperty("user.home")+"/Desktop/fils/Rapports/",file.getOriginalFilename()+"");
			 fileNames.append(file);
			 Files.write(fileNameAndPath, file.getBytes());
			 dossier.setUrlFile(file.getOriginalFilename());
	        DossierMedical savedDossier = repository.save(dossier);
	        return savedDossier.getIdDossierMedical(); // Retourne l'ID du dossier médical ajouté
	    }
}
