package com.projet.BackendPfe.Entity;

import java.time.LocalDate;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DossierMedical")
public class DossierMedical {
	/** Associations de Classe Association **/
	    @ManyToOne(fetch = FetchType.EAGER)
	    @JoinColumn(name = "adminMedical_id")
	    private AdminMedicalManager adminMedicalManagerDossier;

	    @ManyToOne(fetch = FetchType.EAGER)
	    @JoinColumn(name = "medecin_id")
	    private Medecin medecinDossier;
	    
     /**  Association entre dossier medical et patient **/
	  @JsonIgnore 
	  @OneToOne(mappedBy = "dossierMedical", cascade = CascadeType.ALL)
	   private Patient patient;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long  idDossierMedical ;
    @Column(name="Diagnostic")
	private String diagnostic;
    @Column(name="Date_creation_dossier")
    private LocalDate date_creation_dossier;
    @Column(name="urlFile")
    private String urlFile ; 
    
	public DossierMedical(long idDossierMedical,Patient patient ,LocalDate date_creation_dossier, 
			String diagnostic, AdminMedicalManager adminMedical, Medecin medecinDossier) {
		super();
		this.adminMedicalManagerDossier = adminMedical;
		this.idDossierMedical = idDossierMedical;
		this.diagnostic = diagnostic;
		this.date_creation_dossier = date_creation_dossier;
		this.patient=patient ; 
		this.medecinDossier=medecinDossier;
		
	}
	public DossierMedical(Patient patient ,LocalDate date_creation_dossier, 
			String diagnostic, AdminMedicalManager adminMedical, Medecin medecinDossier) {
		super();
		this.adminMedicalManagerDossier = adminMedical;
		this.diagnostic = diagnostic;
		this.date_creation_dossier = date_creation_dossier;
		this.patient=patient ; 
		this.medecinDossier=medecinDossier;
		
	}
	

	public AdminMedicalManager getAdminMedicalManagerDossier() {
		return adminMedicalManagerDossier;
	}



	public void setAdminMedicalManagerDossier(AdminMedicalManager adminMedicalManagerDossier) {
		this.adminMedicalManagerDossier = adminMedicalManagerDossier;
	}



	public Medecin getMedecinDossier() {
		return medecinDossier;
	}



	public void setMedecinDossier(Medecin medecinDossier) {
		this.medecinDossier = medecinDossier;
	}



	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public long getIdDossierMedical() {
		return idDossierMedical;
	}

	public void setIdDossierMedical(long idDossierMedical) {
		this.idDossierMedical = idDossierMedical;
	}

	public String getDiagnostic() {
		return diagnostic;
	}

	public void setDiagnostic(String diagnostic) {
		this.diagnostic = diagnostic;
	}

	public LocalDate getDate_creation_dossier() {
		return date_creation_dossier;
	}

	public void setDate_creation_dossier(LocalDate date_creation_dossier) {
		this.date_creation_dossier = date_creation_dossier;
	}	
	
	public String getUrlFile() {
		return urlFile;
	}
	public void setUrlFile(String urlFile) {
		this.urlFile = urlFile;
	}
	public DossierMedical() {
		super();
	}
	
}
