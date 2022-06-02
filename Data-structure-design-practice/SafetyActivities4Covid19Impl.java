package uoc.ded.practica;

import java.time.LocalDate;
import java.util.Date;

import uoc.ded.practica.exceptions.ActivityNotFoundException;
import uoc.ded.practica.exceptions.GroupNotFoundException;
import uoc.ded.practica.exceptions.LimitExceededException;
import uoc.ded.practica.exceptions.NoActivitiesException;
import uoc.ded.practica.exceptions.NoOrganizationException;
import uoc.ded.practica.exceptions.NoRatingsException;
import uoc.ded.practica.exceptions.NoRecordsException;
import uoc.ded.practica.exceptions.NoUserException;
import uoc.ded.practica.exceptions.NoWorkersException;
import uoc.ded.practica.exceptions.OrderNotFoundException;
import uoc.ded.practica.exceptions.OrganizationNotFoundException;
import uoc.ded.practica.exceptions.UserNotFoundException;
import uoc.ded.practica.exceptions.UserNotInActivityException;
import uoc.ded.practica.model.Activity;
import uoc.ded.practica.model.Group;
import uoc.ded.practica.model.Order;
import uoc.ded.practica.model.Organization;
import uoc.ded.practica.model.Record;
import uoc.ded.practica.model.Role;
import uoc.ded.practica.model.Ticket;
import uoc.ded.practica.model.User;
import uoc.ded.practica.model.Worker;
import uoc.ded.practica.util.OrderedVector;
import uoc.ei.tads.ColaConPrioridad;
import uoc.ei.tads.DiccionarioAVLImpl;
import uoc.ei.tads.Iterador;
import uoc.ei.tads.Lista;
import uoc.ei.tads.ListaEncadenada;
import uoc.ei.tads.TablaDispersion;

public class SafetyActivities4Covid19Impl implements SafetyActivities4Covid19 {

    private TablaDispersion<String, User> users;

    private TablaDispersion<String, Organization> organizations;

    private ColaConPrioridad<Record> records;
    private DiccionarioAVLImpl<String, Activity> activities;

    private int totalRecords;
    private int rejectedRecords;
    private int pendingRecords;

    private OrderedVector<Activity> bestActivities;
    
    private Role[] roles;
    private int numRoles = 0;
    
    private DiccionarioAVLImpl<String, Group> groups;
    
    private DiccionarioAVLImpl<String, Order> orders;
    
    private OrderedVector<Organization> bestOrganizations;

    public SafetyActivities4Covid19Impl() {
    	users = new TablaDispersion<String, User>();
        organizations = new TablaDispersion<String, Organization>();
        records = new ColaConPrioridad<Record>();
        totalRecords = 0;
        rejectedRecords = 0;
        activities = new DiccionarioAVLImpl<String, Activity>();
        roles = new Role[R];
        groups = new DiccionarioAVLImpl<String, Group>();
        orders = new DiccionarioAVLImpl<String, Order>();
        bestActivities= new OrderedVector<Activity>(BEST_10_ACTIVITIES, Activity.CMP_V);
        bestOrganizations = new OrderedVector<Organization>(BEST_ORGANIZATIONS, Organization.CMP_V);
    }
    
    @Override
	public void addUser(String userId, String name, String surname, LocalDate birthday, boolean covidCertificate) {
    	User u = getUser(userId);
        if (u != null) {
            u.setName(name);
            u.setSurname(surname);
            u.setBirthday(birthday);
            u.setCovidCertificate(covidCertificate);
        } else {
            u = new User(userId, name, surname, birthday, covidCertificate);
            users.insertar(userId, u);
        }		
	}
	@Override
	public void addOrganization(String organizationId, String name, String description) {
		Organization organization = getOrganization(organizationId);
        if (organization != null) {
            organization.setName(name);
            organization.setDescription(description);
        } else {
            organization = new Organization(organizationId, name, description);
            organizations.insertar(organizationId, organization);
        }
	}
	@Override
	public void addRecord(String recordId, String actId, String description, Date date, LocalDate dateRecord, Mode mode, int num, String organizationId) throws OrganizationNotFoundException {
		
			Organization organization = getOrganization(organizationId);
	        if (organization == null) {
	        	throw new OrganizationNotFoundException();
	        }
	
	        Record re = new Record(recordId, actId, description, date, dateRecord, mode, num, organization);
	        
	        records.encolar(re);
	       
	        organization.addRecord(re);
	        
	        totalRecords++;
	        pendingRecords++;
	}
	@Override
	public Record updateRecord(Status status, Date date, String description) throws NoRecordsException {
		Record record = null;
		if(records.numElems() == 0) throw new NoRecordsException();
		record = records.desencolar();
		
        record.update(status, date, description);
        
        if (record.isEnabled()) {
            Activity activity = record.newActivity();
            activities.insertar(activity.getActId(), activity);
        } else {
        	rejectedRecords++;
        }
        
        pendingRecords--;
        
        return record;
	}
	@Override
	public Order createTicket(String userId, String actId, LocalDate date) throws UserNotFoundException, ActivityNotFoundException, LimitExceededException {
		User user = getUser(userId);
		Activity activity = getActivity(actId);
        
		if (user == null)throw new UserNotFoundException();       
        if (activity  == null) throw new ActivityNotFoundException();
        if (!activity.hasAvailabilityOfTickets()) throw new LimitExceededException();
        
        Ticket ti = new Ticket(user, activity, date);
        Order ord = activity.addTicket(ti);
        user.addActivity(activity);
        activity.addUser(user);
        
        orders.insertar(ord.getId(), ord);
    
        return ord;
	}
	@Override
	public Order assignSeat(String actId) throws ActivityNotFoundException {
		Activity activity = getActivity(actId);
        if (activity == null) {
        	throw new ActivityNotFoundException();
        }
        
        return activity.pop();
	}
	@Override
	public void addRating(String actId, Rating rating, String message, String userId) throws ActivityNotFoundException, UserNotFoundException, UserNotInActivityException {
		Activity activity = getActivity(actId);
        if (activity == null) {
        	throw new ActivityNotFoundException();
        }

        User user = getUser(userId);
        if (user == null) {
        	throw new UserNotFoundException();
        }

        if (!user.isInActivity(actId)) {
        	throw new UserNotInActivityException();
        }

        activity.addRating(rating, message, user);
        
        updateBestActivity(activity);
        updateBestOrganization(activity.getOrganization());
	}
	@Override
	public Iterador<uoc.ded.practica.model.Rating> getRatings(String actId) throws ActivityNotFoundException, NoRatingsException {
		Activity act = getActivity(actId);
		if(act == null) throw new ActivityNotFoundException();
		if(act.numRatings() == 0) throw new NoRatingsException();
		return act.getRatings();
	}
	@Override
	public Organization getOrganization(String organizationId) {
		return organizations.consultar(organizationId);
	}
	@Override
	public int numActivitiesByOrganization(String organizationId) {
		return getOrganization(organizationId).getNumActivities();
	}
	@Override
	public int numRecordsByOrganization(String organizationId) {
		return getOrganization(organizationId).getNumRecords();
	}
	@Override
	public void addRole(String roleId, String name) {
		Role role = getRole(roleId);
		if(role != null) role.setName(name);
		else roles[numRoles++] = new Role(roleId, name);	
	}
	@Override
	public void addWorker(String userId, String name, String surname, LocalDate birthday, boolean covidCertificate, String roleId, String organizationId) {

		Organization org = getOrganization(organizationId);
		
		User user = getUser(userId);
		if(user == null)  {
			addUser(userId, name, surname, birthday, covidCertificate);
		}
		Worker worker = getWorker(userId);
		if(worker == null) {
			org.addWorker(new Worker(userId, name, surname, roleId, birthday, covidCertificate, org));
			getRole(roleId).addOneWorker();
		} else {
			worker.setName(name);
			worker.setSurname(surname);
			worker.setBirth(birthday);
			worker.setCovidCertificate(covidCertificate);
			
			if(!getWorker(userId).getRoleId().equals(roleId)) {
				getRole(getWorker(userId).getRoleId()).subtractOneWorker();
				getRole(roleId).addOneWorker();
				worker.setRoleId(roleId);
			}
			
			if(!worker.getOrganization().getOrganizationId().equals(organizationId)) {
				Worker wor = worker.getOrganization().removeOneWorker(getWorker(userId));	
				org.addWorker(wor/*new Worker(userId, name, surname, roleId, birthday, covidCertificate, org)*/);
			}
		}
			
	}
	@Override
	public Iterador<Worker> getWorkersByOrganization(String organizationId) throws OrganizationNotFoundException, NoWorkersException {
		Organization org = getOrganization(organizationId);
		if(org == null) throw new OrganizationNotFoundException();
		if(org.getnumWorkers() == 0) throw new NoWorkersException();
		return org.getWorkers();
	}
	@Override
	public Iterador<User> getUsersInActivity(String activityId) throws ActivityNotFoundException, NoUserException {
		Activity act = getActivity(activityId);
		if(act == null) throw new ActivityNotFoundException();
		if(act.emptyListUsersActivity()) throw new NoUserException();
		return act.getUsers();
	}
	@Override
	public Badge getBadge(String userId, LocalDate day) throws UserNotFoundException {
		User user = getUser(userId);
		if(user == null) throw new UserNotFoundException();
		return user.getBadge(day);
	}
	@Override
	public void addGroup(String groupId, String description, LocalDate date, String... members) {
		Group group = getGroup(groupId);
		if(group == null) {
			groups.insertar(groupId, new Group(groupId, description, date, members));
		} else {
			group.setDescription(description);
			group.setUsers(members);
		}
	}
	@Override
	public Iterador<User> membersOf(String groupId) throws GroupNotFoundException, NoUserException {
		Group group = getGroup(groupId);
		if(group == null) throw new GroupNotFoundException();
		if(group.numMembers() == 0) throw new NoUserException();
		Lista<User> usersMembersOfGroup = new ListaEncadenada<User>();
		Iterador<String> usersGroup = group.getUsers();
		while(usersGroup.haySiguiente()) {
			usersMembersOfGroup.insertarAlFinal(getUser(usersGroup.siguiente()));
		}
		return usersMembersOfGroup.elementos();
	}
	@Override
	public double valueOf(String groupId) throws GroupNotFoundException {
		Group group = getGroup(groupId);
		if(group == null) throw new GroupNotFoundException();
		int totalSumBadges = 0;
		Iterador<String> it = group.getUsers();
		while(it.haySiguiente()) {
			totalSumBadges+=getUser(it.siguiente()).getBadge(group.getDate()).getValue();
		}
		double dat = (double)totalSumBadges/(double)getGroup(groupId).numMembers(); 
		return dat; 
	}
	@Override
	public Order createTicketByGroup(String groupId, String actId, LocalDate date) throws GroupNotFoundException, ActivityNotFoundException, LimitExceededException {
		Group group = getGroup(groupId);
		Activity act = getActivity(actId);
		if(group == null) throw new GroupNotFoundException();
		if(act == null) throw new ActivityNotFoundException();
		if(!act.hasAvailabilityOfTickets(group.numMembers())) throw new LimitExceededException();
		
		Order ord = new Order(actId, group, date);
		Iterador <String> members = group.getUsers();
		while(members.haySiguiente()) {
			User user = getUser(members.siguiente());
			ord.addTicket(new Ticket(user, act, date));
			user.addActivity(act);
			act.addUser(user);
		}
		
		act.addOrder(ord);
		
		ord.setValue(this.valueOf(groupId));
		
		orders.insertar(ord.getId(), ord);
		
		return ord;
	}
	@Override
	public Order getOrder(String orderId) throws OrderNotFoundException {
		Order order = orders.consultar(orderId);
		if(order == null) throw new OrderNotFoundException();
		return order;
	}
	@Override
	public Iterador<Worker> getWorkersByRole(String roleId) throws NoWorkersException {
		Role role = getRole(roleId);
		if(role== null) throw new NoWorkersException();
		return role.getWorkers();
	}
	@Override
	public Iterador<Activity> getActivitiesByOrganization(String organizationId) throws NoActivitiesException {
		Organization org = getOrganization(organizationId);
		if(org.getNumActivities() == 0) throw new NoActivitiesException();
		return org.activities();
	}
	@Override
	public Iterador<Record> getRecordsByOrganization(String organizationId) throws NoRecordsException {
		return getOrganization(organizationId).records();
	}
	@Override
	public Iterador<Organization> best5Organizations() throws NoOrganizationException {
		Iterador<Organization> orgs = bestOrganizations.elementos();
		ListaEncadenada<Organization> orgs2 = new ListaEncadenada<Organization>();
		int cont = 0;
		while(orgs.haySiguiente() && cont < 10) {
			orgs2.insertarAlFinal(orgs.siguiente());
			cont++;
		}
		return orgs2.elementos();
	}
	@Override
	public Worker getWorker(String workerId) {
		Iterador<Organization> ite = organizations.elementos();
		while (ite.haySiguiente()) {
			 Worker worker = ite.siguiente().getWorker(workerId);
			 if(worker != null) {
				 return worker;
			 }
		}
		return null;
	}
	@Override
	public Role getRole(String roleId) {
		for(int i = 0; i < numRoles; i++) {
			if (roles[i].getId().equals(roleId)) return roles[i];
		}
		return null;
	}
	@Override
	public Group getGroup(String groupId) {
		return groups.consultar(groupId);
	}
	@Override
	public int numWorkers() {
		int totalWorkers = 0;
		Iterador<Organization> orgs = organizations.elementos();
		while(orgs.haySiguiente()) {
			totalWorkers += numWorkers(orgs.siguiente().getOrganizationId());
		}
		return totalWorkers;
	}
	@Override
	public int numWorkers(String organizationId) {
		return getOrganization(organizationId).getnumWorkers();
	}
	@Override
	public int numRoles() {
		return numRoles;
	}
	@Override
	public int numWorkersByRole(String roleId) {
		return getRole(roleId).getNumWorkers();
	}
	@Override
	public int numGroups() {
		return groups.numElems();
	}
	@Override
	public int numOrders() {
		return orders.numElems();
	}
	@Override
	public Iterador<Activity> best10Activities() throws ActivityNotFoundException {
		if(activities.numElems()==0) throw new ActivityNotFoundException();
		Iterador<Activity> acts = bestActivities.elementos();
		ListaEncadenada<Activity> acts2 = new ListaEncadenada<Activity>();
		int cont = 0;
		while(acts.haySiguiente() && cont < 10) {
			acts2.insertarAlFinal(acts.siguiente());
			cont++;
		}
		return acts2.elementos();
	}
	@Override
	public User getUser(String userId) {
		return users.consultar(userId);
	}
	@Override
	public Activity bestActivity() throws ActivityNotFoundException {
		if(bestActivities.numElems() == 0) throw new ActivityNotFoundException();
		return bestActivities.elementAt(0);
	}
	@Override
	public User mostActiveUser() throws UserNotFoundException {
		if(users.numElems() == 0) throw new UserNotFoundException();
		
		Iterador <User> us = users.elementos();
		User user1 = null;
		User user2 = null;
		if(us.haySiguiente()) user1 = us.siguiente();
		
		while(us.haySiguiente()) {
			user2 = us.siguiente();
			if( !(user1.getNumActivities() > user2.getNumActivities()) ) user1 = user2; 
		}
		return user1;
	}
	@Override
	public double getInfoRejectedRecords() {
		return (double)rejectedRecords / totalRecords;
	}
	@Override
	public Iterador<Activity> getAllActivities() throws NoActivitiesException {
		if(activities.numElems() == 0) throw new NoActivitiesException();
		return activities.elementos();
	}
	@Override
	public Iterador<Activity> getActivitiesByUser(String userId) throws NoActivitiesException {
		User user = getUser(userId);
		if(!user.hasActivities()) throw new NoActivitiesException();
		return getUser(userId).activities();
	}
	@Override
	public Record currentRecord() {
		return (records.numElems() > 0 ? records.desencolar(): null);
	}
	@Override
	public int numUsers() {
		return users.numElems();
	}
	@Override
	public int numOrganizations() {
		return organizations.numElems();
	}
	@Override
	public int numPendingRecords() {
		return pendingRecords;
	}
	@Override
	public int numRecords() {
		return totalRecords;
	}
	@Override
	public int numRejectedRecords() {
		return rejectedRecords;
	}
	@Override
	public int numActivities() {
		return activities.numElems();
	}
	@Override
	public Activity getActivity(String actId) {
		return activities.consultar(actId);
	}
	@Override
	public int availabilityOfTickets(String actId) {
		Activity activity = getActivity(actId);
        return (activity != null ? activity.availabilityOfTickets() : 0);
	}
	private void updateBestActivity(Activity activity) {
        bestActivities.delete(activity);
        bestActivities.update(activity);
    }
	private void updateBestOrganization(Organization org) {
		bestOrganizations.delete(org);
		bestOrganizations.update(org);
	}
}
