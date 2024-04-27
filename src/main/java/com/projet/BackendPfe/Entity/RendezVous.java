package com.projet.BackendPfe.Entity;

import java.time.LocalDate;
import java.util.Date;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
public class RendezVous {
	  
    /*** Association entre Plage horaire et Rendezvous 
    @OneToOne(mappedBy = "rendezVous")
    private RendezVous rendezVous;
    ***/
	 @OneToOne(mappedBy = "rendezVous", cascade = CascadeType.ALL)
	 private PlageHorraire plageHorraire;
    /** classe Association entre patient et medecin **/
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medecin_id")
    private Medecin medecinRV;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id")
    private Patient patientRV;
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idRdv;
    
    @Column(name ="dateRdv")
    private Date dateRdv;
    
    @Column(name ="datePrendreRdv")
    private LocalDate datePrendreRdv;
    
    @Column(name ="description")
    private String description;
    
    @Column(name ="presence")
    private String presence;


	public RendezVous( Medecin medecinRV, Patient patientRV, Date dateRdv,
			LocalDate datePrendreRdv, String description , String presence) {
		super();
		this.medecinRV = medecinRV;
		this.patientRV = patientRV;
		this.dateRdv = dateRdv;
		this.datePrendreRdv = datePrendreRdv;
		this.description = description;
		this.presence =presence ;
	}

	public RendezVous() {
		super();
	}

	public int getIdRdv() {
		return idRdv;
	}

	public void setIdRdv(int idRdv) {
		this.idRdv = idRdv;
	}

	public Date getDateRdv() {
		return dateRdv;
	}

	public void setDateRdv(Date dateRdv) {
		this.dateRdv = dateRdv;
	}

	public LocalDate getDatePrendreRdv() {
		return datePrendreRdv;
	}

	public void setDatePrendreRdv(LocalDate datePrendreRdv) {
		this.datePrendreRdv = datePrendreRdv;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Medecin getMedecinRV() {
		return medecinRV;
	}

	public void setMedecinRV(Medecin medecinRV) {
		this.medecinRV = medecinRV;
	}

	public Patient getPatientRV() {
		return patientRV;
	}

	public void setPatientRV(Patient patientRV) {
		this.patientRV = patientRV;
	}

	public PlageHorraire getPlageHorraire() {
		return plageHorraire;
	}

	public void setPlageHorraire(PlageHorraire plageHorraire) {
		this.plageHorraire = plageHorraire;
	}
	public String getPresence() {
		return presence;
	}
	public void setPresence(String presence) {
		this.presence = presence;
	}
    
    
}
