package uoc.ded.practica.model;

import java.time.LocalDate;
import java.util.Comparator;

import uoc.ded.practica.SafetyActivities4Covid19.Badge;
import uoc.ei.tads.Iterador;
import uoc.ei.tads.Lista;
import uoc.ei.tads.ListaEncadenada;

public class User implements Comparable<User>{
    public static final Comparator<String> CMP = new Comparator<String>() {
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    };

    private String id;
    private String name;
    private String surname;
    private Lista<Activity> activities;
    private LocalDate birthday; 
    private boolean covidCertificate;
    private Badge badge;
    private int numRatings = 0;

	public User(String idUser, String name, String surname, LocalDate birthday, boolean covidCertificate) {
        this.setId(idUser);
        this.setName(name);
        this.setSurname(surname);
        this.setBirthday(birthday);
        this.setCovidCertificate(covidCertificate);
        this.activities = new ListaEncadenada<Activity>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public boolean isCovidCertificate() {
		return covidCertificate;
	}

    
    public int compareTo(User o) {
        return getId().compareTo(o.getId());
    }

    public Iterador<Activity> answers() {
        return activities.elementos();
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setCovidCertificate(boolean covidCertificate) {
        this.covidCertificate = covidCertificate;
    }

    public boolean is(String userId) {
        return id.equals(userId);
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

    public boolean isInActivity(String actId) {
        Iterador<Activity> it = activities.elementos();

        boolean found = false;
        Activity act = null;

        while (!found && it.haySiguiente()) {
        	act = it.siguiente();
            found = act.is(actId);
        }

        return found;
    }
    
    private void updateBadge(LocalDate localDate) {
    	int ages = localDate.getYear() - birthday.getYear();
    	if(ages < 12) {
    		badge = Badge.JUNIOR;
    	} else if(!covidCertificate) {
    		badge = Badge.DARK;
    	} else {
    		if(ages >= 65) badge = Badge.SENIOR_PLUS;
    		if(ages >= 50 && ages <= 64) badge = Badge.SENIOR;
    		if(ages >= 30 && ages <= 49 && numRatings >= 5) badge = Badge.MASTER_PLUS;
    		if(ages >= 30 && ages <= 49 && numRatings < 5) badge = Badge.MASTER;
    		if(ages >= 18 && ages <= 29 && numRatings >= 5) badge = Badge.YOUTH_PLUS;
    		if(ages >= 18 && ages <= 29 && numRatings < 5) badge = Badge.YOUTH;
    		if(ages >= 12 && ages <= 17) badge = Badge.JUNIOR_PLUS;
    		if(ages >= 12 && ages <= 17) badge = Badge.JUNIOR_PLUS;
    	}
    }

    public boolean hasActivities() {
        return activities.numElems() > 0;
    }
       
    
    public Badge getBadge(LocalDate localDate ) {
    	updateBadge(localDate);
       	return badge;
    }

    public int getNumActivities() {
    	return activities.numElems();
    }
}
