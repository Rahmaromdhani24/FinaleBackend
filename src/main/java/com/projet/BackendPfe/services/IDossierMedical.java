package com.projet.BackendPfe.services;


import java.io.IOException;
import java.util.List;
import com.projet.BackendPfe.Entity.DossierMedical;
import org.springframework.web.multipart.MultipartFile;
public interface IDossierMedical {

	
	     DossierMedical ajouterDossier(DossierMedical dossierMedicale);
	     List<DossierMedical> getAll();
	     DossierMedical modifierDossier(long id ,DossierMedical dosssierMedicale);
	     String SupprimerDossier(long id);
	     DossierMedical getDossier(long id);
	     public byte[] getFile(long  id) throws Exception;
	     long addFile(MultipartFile file, long id) throws IOException;

}
