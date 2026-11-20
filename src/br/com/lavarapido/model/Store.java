package br.com.lavarapido.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Store implements Serializable {
    private final List<Customer> customers = new ArrayList<>();
    private final List<ServiceType> services = new ArrayList<>();
    private final List<Appointment> appointments = new ArrayList<>();
    private int nextCustomerId = 1;
    private int nextServiceId = 1;
    private int nextAppointmentId = 1;
    public List<Customer> getCustomers() { return customers; }
    public List<ServiceType> getServices() { return services; }
    public List<Appointment> getAppointments() { return appointments; }
    public int nextCustomerId() { return nextCustomerId++; }
    public int nextServiceId() { return nextServiceId++; }
    public int nextAppointmentId() { return nextAppointmentId++; }
}
