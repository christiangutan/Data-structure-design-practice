package uoc.ded.practica.model;

import uoc.ei.tads.Iterador;
import uoc.ei.tads.Lista;
import uoc.ei.tads.ListaEncadenada;

public class Role {
	private String roleId;
	private String name;
	private int numWorkers = 0;
	
	private Lista<Worker> workers;
	
	public Role(String roleId, String name) {
		this.roleId = roleId;
		this.name = name;
		workers = new ListaEncadenada<Worker>();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String getId() {
		return roleId;
	}
	
	public void addOneWorker() {
		numWorkers++;
	}
	
	public void subtractOneWorker() {
		numWorkers--;
	}
	
	public int getNumWorkers() {
		return numWorkers;
	}
	
	public Iterador<Worker> getWorkers(){
		return workers.elementos();
	}
}
