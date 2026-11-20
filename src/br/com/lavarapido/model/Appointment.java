package br.com.lavarapido.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Appointment implements Serializable {
    private final int id;
    private int customerId;
    private int serviceId;
    private LocalDateTime dateTime;
    private AppointmentStatus status;
    private String notes;
    public Appointment(int id, int customerId, int serviceId, LocalDateTime dateTime, AppointmentStatus status, String notes) { this.id=id; this.customerId=customerId; this.serviceId=serviceId; this.dateTime=dateTime; this.status=status; this.notes=notes; }
    public int getId() { return id; }
    public int getCustomerId() { return customerId; }
    public int getServiceId() { return serviceId; }
    public LocalDateTime getDateTime() { return dateTime; }
    public AppointmentStatus getStatus() { return status; }
    public String getNotes() { return notes; }
    public void update(int customerId, int serviceId, LocalDateTime dateTime, String notes) { this.customerId=customerId; this.serviceId=serviceId; this.dateTime=dateTime; this.notes=notes; }
    public void setStatus(AppointmentStatus status) { this.status=status; }
}
