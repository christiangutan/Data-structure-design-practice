package uoc.ded.practica.model;

import java.time.LocalDate;

import uoc.ded.practica.SafetyActivities4Covid19.Badge;
import uoc.ei.tads.Iterador;
import uoc.ei.tads.Lista;
import uoc.ei.tads.ListaEncadenada;

public class Order implements Comparable<Order> {
	
	private Lista<Ticket> tickets;
	private String orderId;
	private LocalDate date;
	private Badge badge;
	private double value = 0;
	
	public Order(String actId, User user, LocalDate date) {
		this.orderId = "O-" + date.getYear();
		if(date.getMonth().getValue() < 10) orderId += "0";
		orderId += date.getMonth().getValue();
		if(date.getDayOfMonth() < 10) orderId += "0";
		orderId += date.getDayOfMonth();
		orderId += "-" + user.getId();
		this.date = date;
		tickets = new ListaEncadenada<Ticket>();
	}
	
	public Order(String actId, Group group, LocalDate date) {
		this.orderId = "O-" + date.getYear();
		if(date.getMonth().getValue() < 10) orderId += "0";
		orderId += date.getMonth().getValue();
		if(date.getDayOfMonth() < 10) orderId += "0";
		orderId += date.getDayOfMonth();
		orderId += "-" + group.getId();
		this.date = date;
		tickets = new ListaEncadenada<Ticket>();
	}
	
	
	public String getId() {
		return orderId;
	}
	
	public void addTicket(Ticket ti) {
		tickets.insertarAlFinal(ti);
		badge = tickets.elementos().siguiente().getUser().getBadge(date);
		value += (double)badge.getValue();
	}
	
	public Iterador<Ticket> tickets() {
		return tickets.elementos();
	}

	@Override
	public int compareTo(Order o) {
		if(this.badge.getValue() < o.badge.getValue()) return 1;
		if(this.badge.getValue() > o.badge.getValue()) return -1;
		return 0;
	}
	
	public int numTickets() {
		return tickets.numElems();
	}
	
	public void setValue(double val) {
		value = val;
	}
	
	
	public double getValue() {
		return value;
	}
}
