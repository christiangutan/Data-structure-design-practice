package uoc.ded.practica.model;

import java.time.LocalDate;

public class Ticket {
    private User user;
    private Activity activity;
    private int seat;
    private LocalDate date;

    public Ticket(User user, Activity activity, LocalDate date) {
        this.user = user;
        this.activity = activity;
        this.date = date;
    }

    public int getSeat() {
        return seat;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }

    @Override
    public String toString() {
        return "**" + activity.getActId() + " " + seat + " " + user.getId();
    }

    public User getUser() {
        return user;
    }

    public Activity getActivity() {
        return activity;
    }
    
    public LocalDate getDate() {
    	return date;
    }
}
