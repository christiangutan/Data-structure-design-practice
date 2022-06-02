package uoc.ded.practica.model;

import java.time.LocalDate;

public class Worker {
	//private String userId;
	private String workerId;
	private String name;
	private String surname;
	private String roleId;
	private LocalDate birth;
	private boolean covidCertificate;
	private Organization organization;
	
	
	public Worker(String userId, String name, String surname, String roleId, LocalDate birth, boolean covidCertificate, Organization organization) {
		this.workerId = userId;
		this.name = name;
		this.surname = surname;
		this.roleId = roleId;
		this.birth = birth;
		this.covidCertificate = covidCertificate;
		this.organization = organization;		
	}
		
	public String getWorkerId() {
		return workerId;
	}

	public String getName() {
		return name;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public String getRoleId() {
		return roleId;
	}
	
    public void setName(String name) {
    	this.name = name;
    }
    
    public void setSurname(String surname) {
    	this.surname = surname;
    }
    
    public void setBirth(LocalDate date) {
    	birth = date;
    }
    
    public void setCovidCertificate(boolean bool) {
    	this.covidCertificate = bool;
    }
    
    public void setRoleId(String id) {
    	this.roleId = id;
    }
    
    public Organization getOrganization() {
    	return organization;
    }
}