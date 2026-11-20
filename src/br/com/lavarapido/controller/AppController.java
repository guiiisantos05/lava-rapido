package br.com.lavarapido.controller;

import br.com.lavarapido.model.*;
import br.com.lavarapido.persistence.StorePersistence;
import br.com.lavarapido.view.MainFrame;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AppController {
    private final Store store;
    private final StorePersistence persistence;
    private MainFrame frame;
    public AppController(Store store, StorePersistence persistence) { this.store=store; this.persistence=persistence; }
    public void setFrame(MainFrame frame) { this.frame=frame; refresh(); }
    public List<Customer> customers() { return store.getCustomers().stream().sorted(Comparator.comparing(Customer::getName, String.CASE_INSENSITIVE_ORDER)).toList(); }
    public List<ServiceType> services() { return store.getServices().stream().sorted(Comparator.comparing(ServiceType::getName, String.CASE_INSENSITIVE_ORDER)).toList(); }
    public List<Appointment> appointments() { return store.getAppointments().stream().sorted(Comparator.comparing(Appointment::getDateTime)).toList(); }
    public Customer customer(int id) { return store.getCustomers().stream().filter(x -> x.getId()==id).findFirst().orElse(null); }
    public ServiceType service(int id) { return store.getServices().stream().filter(x -> x.getId()==id).findFirst().orElse(null); }
    public void saveCustomer(Integer id, String name, String cpf, String phone, String email, String plate, String vehicle) {
        required(name,"Nome"); required(cpf,"CPF"); required(phone,"Telefone"); required(plate,"Placa");
        boolean repeated = store.getCustomers().stream().anyMatch(x -> x.getCpf().equalsIgnoreCase(cpf.trim()) && (id==null || x.getId()!=id));
        if (repeated) throw new IllegalArgumentException("Já existe um cliente com este CPF.");
        if (id == null) store.getCustomers().add(new Customer(store.nextCustomerId(),name.trim(),cpf.trim(),phone.trim(),email.trim(),plate.trim().toUpperCase(),vehicle.trim()));
        else customer(id).update(name.trim(),cpf.trim(),phone.trim(),email.trim(),plate.trim().toUpperCase(),vehicle.trim());
        changed();
    }
    public void deleteCustomer(int id) {
        if (store.getAppointments().stream().anyMatch(x -> x.getCustomerId()==id)) throw new IllegalArgumentException("Este cliente possui agendamentos. Cancele ou exclua-os antes.");
        store.getCustomers().removeIf(x -> x.getId()==id); changed();
    }
    public void saveService(Integer id, String name, String description, BigDecimal price, int duration) {
        required(name,"Nome do serviço"); if (price.signum()<0) throw new IllegalArgumentException("O valor não pode ser negativo."); if (duration<=0) throw new IllegalArgumentException("A duração deve ser maior que zero.");
        if (id == null) store.getServices().add(new ServiceType(store.nextServiceId(),name.trim(),description.trim(),price,duration));
        else service(id).update(name.trim(),description.trim(),price,duration);
        changed();
    }
    public void deleteService(int id) {
        if (store.getAppointments().stream().anyMatch(x -> x.getServiceId()==id)) throw new IllegalArgumentException("Este serviço possui agendamentos vinculados.");
        store.getServices().removeIf(x -> x.getId()==id); changed();
    }
    public void saveAppointment(Integer id, int customerId, int serviceId, LocalDateTime dateTime, String notes) {
        if (customer(customerId)==null || service(serviceId)==null) throw new IllegalArgumentException("Selecione cliente e serviço.");
        boolean conflict = store.getAppointments().stream().anyMatch(x -> x.getDateTime().equals(dateTime) && x.getStatus()==AppointmentStatus.AGENDADO && (id==null || x.getId()!=id));
        if (conflict) throw new IllegalArgumentException("Já existe um serviço agendado neste horário.");
        if (id == null) store.getAppointments().add(new Appointment(store.nextAppointmentId(),customerId,serviceId,dateTime,AppointmentStatus.AGENDADO,notes.trim()));
        else { Appointment appointment = appointment(id); appointment.update(customerId,serviceId,dateTime,notes.trim()); }
        changed();
    }
    public void setAppointmentStatus(int id, AppointmentStatus status) { appointment(id).setStatus(status); changed(); }
    public void deleteAppointment(int id) { store.getAppointments().removeIf(x -> x.getId()==id); changed(); }
    public long scheduledCount() { return store.getAppointments().stream().filter(x -> x.getStatus()==AppointmentStatus.AGENDADO).count(); }
    public BigDecimal expectedRevenue() { return store.getAppointments().stream().filter(x -> x.getStatus()==AppointmentStatus.AGENDADO).map(x -> service(x.getServiceId()).getPrice()).reduce(BigDecimal.ZERO,BigDecimal::add); }
    private Appointment appointment(int id) { return store.getAppointments().stream().filter(x -> x.getId()==id).findFirst().orElseThrow(); }
    private void required(String value, String label) { if (value==null || value.trim().isBlank()) throw new IllegalArgumentException(label + " é obrigatório."); }
    private void changed() { persistence.save(store); refresh(); }
    private void refresh() { if (frame != null) frame.refreshAll(); }
}
