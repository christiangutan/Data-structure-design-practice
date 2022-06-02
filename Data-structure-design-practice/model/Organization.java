package uoc.ded.practica.model;

import uoc.ei.tads.Iterador;
import uoc.ei.tads.Lista;
import uoc.ei.tads.ListaEncadenada;
import uoc.ei.tads.Posicion;
import uoc.ei.tads.Recorrido;
import java.util.Comparator;

public class Organization implements Comparable<Organization> {
	
	public static final Comparator<Organization> CMP_V = (Organization a1, Organization a2)->Double.compare(a1.rating(), a2.rating());
	
    private String organizationId;
    private String description;
    private String name;
    private Lista<Activity> activities;
    //private int numActivities = 0;
    private Lista<Record> records;
    //private int numRecords = 0;
    private Lista<Worker> workers;
    //private int numWorkers = 0;		//numWorkers?

    public Organization(String organizationId, String name, String description) {
        this.organizationId = organizationId;
        this.name = name;
        this.description = description;
        activities = new ListaEncadenada<Activity>();
        records = new ListaEncadenada<Record>();
        workers = new ListaEncadenada<Worker>();
    }

    public String getName() {
        return name;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getDescription() {
        return description;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Iterador<Activity> activities() {
        return activities.elementos();
    }

    public void addActivity(Activity activity) {
        activities.insertarAlFinal(activity);
    }

    public int numActivities() {
        return activities.numElems();
    }
    
    

    public boolean hasActivities() {
        return activities.numElems() > 0;
    }
    
    public void addWorker(Worker worker) {
    	workers.insertarAlFinal(worker);
    }
    
    
    public Worker removeOneWorker(Worker worker) {
    	Worker eleminatedWorker = null;
    	boolean found = false;
    	Recorrido<Worker> rec = workers.posiciones();
    	Posicion<Worker> previous = null, actual = null;
    	
    	while(!found && rec.haySiguiente()) {
    		previous = actual;
    		actual = rec.siguiente();
    		found = (actual != null && worker.getWorkerId().equals(actual.getElem().getWorkerId()));
    	}
    	
    	if(found) {
    		eleminatedWorker = workers.borrarSiguiente(previous);
    	}
    	return eleminatedWorker;
    	
    }
    
    public Worker getWorker(String workerId) {
    	Iterador<Worker> elements = workers.elementos();
    	while(elements.haySiguiente()) {
	    	Worker w = elements.siguiente();
	    	if(w.getWorkerId().equals(workerId)) {
	    		return w;
	    	}
    	}	
    	return null;
    }
    
    public int getnumWorkers() {
    	return workers.numElems();
    }
    
    public Iterador<Worker> getWorkers(){
    	return workers.elementos();
    }
    
    public int getNumActivities() {
    	return activities.numElems();
    }
    
    public Iterador<Record> records(){
    	return records.elementos();
    }
    
    public void addRecord(Record re) {
    	records.insertarAlFinal(re);
    }
    
    public int getNumRecords() {
    	return records.numElems();
    }
    
    public double rating() {
    	Iterador<Activity> acts = activities.elementos();
    	double averageRating = 0;
    	int count = 0;
    	while(acts.haySiguiente()) {
    		averageRating += acts.siguiente().rating();
    	}
    	return averageRating/count;
    }

	@Override
	public int compareTo(Organization o) {
		return organizationId.compareTo(o.organizationId);
	}
}

