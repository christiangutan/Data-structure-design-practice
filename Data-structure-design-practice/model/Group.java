package uoc.ded.practica.model;

import uoc.ei.tads.Iterador;
import uoc.ei.tads.Lista;
import uoc.ei.tads.ListaEncadenada;

import java.time.LocalDate;

import uoc.ded.practica.SafetyActivities4Covid19.Badge;

public class Group {
	private Lista<String> users;
	private String groupId;
	private String description;
	private int numUsers;
	private LocalDate date;
	
	public Group(String groupId, String description, LocalDate date, String[] members) {
		this.date = date;
		this.setUsers(members);
		this.groupId = groupId;
		this.description = description;
	}
	
	public void setDescription(String description) {
		this.description = description; 
	}
	
	public String getId() {
		return groupId;
	}
	
	public void setUsers(String [] members) {
		users = new ListaEncadenada<String>();
		numUsers = members.length;
		for(int i = 0; i < members.length; i++) users.insertarAlFinal(members[i]);
	}
	
	public Iterador<String> getUsers() {
		return users.elementos();
	}
	
	public int numMembers() {
		return users.numElems();
	}
	
	public boolean hasMembers() {
		return !users.estaVacio();
	}
		
	public LocalDate getDate() {
		return date;
	}
	
	
}
