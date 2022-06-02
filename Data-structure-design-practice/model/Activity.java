package uoc.ded.practica.model;

import java.util.Comparator;
import java.util.Date;

import uoc.ded.practica.SafetyActivities4Covid19;
import uoc.ei.tads.Cola;
import uoc.ei.tads.ColaConPrioridad;
import uoc.ei.tads.ColaVectorImpl;
import uoc.ei.tads.Iterador;
import uoc.ei.tads.Lista;
import uoc.ei.tads.ListaEncadenada;

public class Activity implements Comparable<Activity> {
    public static final Comparator<String> CMP_K = (String o1, String o2)->o1.compareTo(o2);
    public static final Comparator<Activity> CMP_V = (Activity a1, Activity a2)->Double.compare(a1.rating(), a2.rating());

    private String actId;
    private String description;
    private Date date;
    private SafetyActivities4Covid19.Mode mode;
    private int total;
    private int nextSeat;
    private int availabilityOfTickets;
    private Record record;
    private Lista<Rating> ratings;
    private int totalRatings;
    private ColaConPrioridad<Order> orders;
    private Lista<User> users;
    private Organization organization;

    public Activity(String actId, String description, Date dateAct, SafetyActivities4Covid19.Mode mode, int num, Record record) {
    	
        this.actId = actId;
        this.description = description;
        this.date = dateAct;
        this.mode = mode;
        this.total = num;
        this.nextSeat = 1;
        this.availabilityOfTickets = num;
        this.record = record;
        ratings = new ListaEncadenada<Rating>();
        orders = new ColaConPrioridad<Order>();
        users = new ListaEncadenada<User>();
    }

    public String getActId() {
        return actId;
    }
    
    public void setOrganization(Organization org) {
    	organization = org;
    }

    public boolean hasAvailabilityOfTickets() {
        return (availabilityOfTickets > 0  );
    }
    
    public boolean hasAvailabilityOfTickets(int num) {
    	return (availabilityOfTickets - num) > 0;
    }
    
    public Order addTicket(Ticket ticket) {
    	Order ord = new Order(actId, ticket.getUser(), ticket.getDate());
    	ord.addTicket(ticket);
    	orders.encolar(ord);
        availabilityOfTickets--;
        return ord;
    }
    
    public Order pop() {
    	Order or = orders.desencolar();
    	Iterador<Ticket> it = or.tickets();
    	while(it.haySiguiente()) {
    		it.siguiente().setSeat(nextSeat++);
    	}
    	return or;
    }

    public void addOrder(Order order) {
    	orders.encolar(order);
    	availabilityOfTickets -= order.numTickets();
    }

    public boolean is(String actId) {
        return this.actId.equals(actId);
    }

    public void addRating(SafetyActivities4Covid19.Rating rating, String message, User user) {
        Rating newRating = new Rating(rating, message, user);
        ratings.insertarAlFinal(newRating);
        totalRatings += rating.getValue();
    }

    public double rating() {
        return (ratings.numElems() != 0 ? (double)totalRatings / ratings.numElems() : 0);
    }

    public Iterador<Rating> ratings() {
        return ratings.elementos();
    }

    public boolean hasRatings() {
        return ratings.numElems() > 0;
    }

    public int availabilityOfTickets() {
        return availabilityOfTickets;
    }

    @Override
    public int compareTo(Activity o) {
        return actId.compareTo(o.actId);
    }
    
    public Iterador<User> getUsers() {
    	return users.elementos();
    }
    
    public void addUser(User user) {
    	users.insertarAlFinal(user);
    }
    
    public boolean emptyListUsersActivity() {
    	return users.estaVacio();
    }
    
    public boolean thereIsAvaiableSeats() {
    	return availabilityOfTickets > 0;
    }
    
    public Iterador<Rating> getRatings() {
    	return ratings.elementos();
    }
    
    public int numRatings() {
    	return ratings.numElems();
    }
    
    public Organization getOrganization() {
    	return organization;
    }
}

