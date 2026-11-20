package br.com.lavarapido.view;

import br.com.lavarapido.controller.AppController;
import br.com.lavarapido.model.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class MainFrame extends JFrame {
    private final AppController controller;
    private final CustomerPanel customerPanel;
    private final ServicePanel servicePanel;
    private final AppointmentPanel appointmentPanel;
    private final DashboardPanel dashboardPanel;
    public MainFrame(AppController controller) {
        this.controller=controller;
        setTitle("Lava Rápido | Controle de Clientes e Agenda");
        setDefaultCloseOperation(EXIT_ON_CLOSE); setMinimumSize(new Dimension(1000,650)); setSize(1150,720); setLocationRelativeTo(null);
        dashboardPanel=new DashboardPanel(); customerPanel=new CustomerPanel(); servicePanel=new ServicePanel(); appointmentPanel=new AppointmentPanel();
        JTabbedPane tabs=new JTabbedPane(); tabs.setFont(new Font("SansSerif",Font.BOLD,14));
        tabs.addTab("Resumo",dashboardPanel); tabs.addTab("Clientes",customerPanel); tabs.addTab("Serviços",servicePanel); tabs.addTab("Agenda",appointmentPanel);
        add(tabs);
    }
    public void refreshAll() { dashboardPanel.refresh(); customerPanel.refresh(); servicePanel.refresh(); appointmentPanel.refresh(); }
    private abstract class BasePanel extends JPanel {
        final DefaultTableModel tableModel;
        final JTable table;
        BasePanel(String[] columns) {
            setLayout(new BorderLayout(12,12)); setBorder(new EmptyBorder(16,16,16,16));
            tableModel=new DefaultTableModel(columns,0) { public boolean isCellEditable(int r,int c){ return false; } };
            table=new JTable(tableModel); table.setRowHeight(28); table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); table.setAutoCreateRowSorter(true);
            add(new JScrollPane(table),BorderLayout.CENTER);
        }
        JPanel actions(String title) { JPanel bar=new JPanel(new FlowLayout(FlowLayout.LEFT,8,0)); JLabel label=new JLabel(title); label.setFont(new Font("SansSerif",Font.BOLD,20)); bar.add(label); bar.add(Box.createHorizontalStrut(20)); return bar; }
        JButton button(String text) { JButton b=new JButton(text); b.setFocusPainted(false); return b; }
        Integer selectedId() { int row=table.getSelectedRow(); return row<0?null:(Integer)tableModel.getValueAt(table.convertRowIndexToModel(row),0); }
        void error(Exception exception) { JOptionPane.showMessageDialog(MainFrame.this,exception.getMessage(),"Atenção",JOptionPane.WARNING_MESSAGE); }
        boolean confirm(String message) { return JOptionPane.showConfirmDialog(MainFrame.this,message,"Confirmar",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION; }
    }
    private class CustomerPanel extends BasePanel {
        CustomerPanel() { super(new String[]{"ID","Nome","CPF","Telefone","E-mail","Placa","Veículo"}); JPanel bar=actions("Clientes"); JButton add=button("Novo cliente"), edit=button("Editar"), remove=button("Excluir"); bar.add(add);bar.add(edit);bar.add(remove); add(bar,BorderLayout.NORTH); add.addActionListener(e->dialog(null)); edit.addActionListener(e->{Integer id=selectedId();if(id==null) error(new IllegalArgumentException("Selecione um cliente.")); else dialog(controller.customer(id));}); remove.addActionListener(e->{Integer id=selectedId();if(id!=null&&confirm("Excluir o cliente selecionado?"))try{controller.deleteCustomer(id);}catch(Exception x){error(x);}}); }
        void refresh() { tableModel.setRowCount(0); for(Customer c:controller.customers()) tableModel.addRow(new Object[]{c.getId(),c.getName(),c.getCpf(),c.getPhone(),c.getEmail(),c.getPlate(),c.getVehicle()}); }
        void dialog(Customer current) {
            JTextField name=new JTextField(current==null?"":current.getName()),cpf=new JTextField(current==null?"":current.getCpf()),phone=new JTextField(current==null?"":current.getPhone()),email=new JTextField(current==null?"":current.getEmail()),plate=new JTextField(current==null?"":current.getPlate()),vehicle=new JTextField(current==null?"":current.getVehicle());
            if(showForm(current==null?"Novo cliente":"Editar cliente",new String[]{"Nome", "CPF", "Telefone", "E-mail", "Placa", "Veículo"},new JComponent[]{name,cpf,phone,email,plate,vehicle})) try { controller.saveCustomer(current==null?null:current.getId(),name.getText(),cpf.getText(),phone.getText(),email.getText(),plate.getText(),vehicle.getText()); } catch(Exception x){error(x);} }
    }
    private class ServicePanel extends BasePanel {
        ServicePanel() { super(new String[]{"ID","Serviço","Descrição","Valor","Duração (min)"}); JPanel bar=actions("Tipos de serviços"); JButton add=button("Novo serviço"), edit=button("Editar"), remove=button("Excluir");bar.add(add);bar.add(edit);bar.add(remove);add(bar,BorderLayout.NORTH);add.addActionListener(e->dialog(null));edit.addActionListener(e->{Integer id=selectedId();if(id==null)error(new IllegalArgumentException("Selecione um serviço."));else dialog(controller.service(id));});remove.addActionListener(e->{Integer id=selectedId();if(id!=null&&confirm("Excluir o serviço selecionado?"))try{controller.deleteService(id);}catch(Exception x){error(x);}}); }
        void refresh() { tableModel.setRowCount(0); for(ServiceType s:controller.services()) tableModel.addRow(new Object[]{s.getId(),s.getName(),s.getDescription(),"R$ "+s.getPrice().setScale(2),s.getDurationMinutes()}); }
        void dialog(ServiceType current) { JTextField name=new JTextField(current==null?"":current.getName()),description=new JTextField(current==null?"":current.getDescription()),price=new JTextField(current==null?"":current.getPrice().toString()),duration=new JTextField(current==null?"":String.valueOf(current.getDurationMinutes())); if(showForm(current==null?"Novo serviço":"Editar serviço",new String[]{"Nome","Descrição","Valor (R$)","Duração em minutos"},new JComponent[]{name,description,price,duration}))try{controller.saveService(current==null?null:current.getId(),name.getText(),description.getText(),new BigDecimal(price.getText().trim().replace(',','.')),Integer.parseInt(duration.getText().trim()));}catch(Exception x){error(new IllegalArgumentException("Preencha valor e duração corretamente."));} }
    }
    private class AppointmentPanel extends BasePanel {
        private final DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        AppointmentPanel() { super(new String[]{"ID","Data e hora","Cliente","Veículo","Serviço","Valor","Status","Observações"}); JPanel bar=actions("Agenda de serviços"); JButton add=button("Novo agendamento"),edit=button("Editar"),done=button("Concluir"),cancel=button("Cancelar"),remove=button("Excluir"); for(JButton b:new JButton[]{add,edit,done,cancel,remove})bar.add(b);add(bar,BorderLayout.NORTH);add.addActionListener(e->dialog(null));edit.addActionListener(e->{Integer id=selectedId();if(id==null)error(new IllegalArgumentException("Selecione um agendamento."));else dialog(find(id));});done.addActionListener(e->status(AppointmentStatus.CONCLUIDO));cancel.addActionListener(e->status(AppointmentStatus.CANCELADO));remove.addActionListener(e->{Integer id=selectedId();if(id!=null&&confirm("Excluir o agendamento selecionado?"))controller.deleteAppointment(id);}); }
        Appointment find(int id) { return controller.appointments().stream().filter(x->x.getId()==id).findFirst().orElseThrow(); }
        void refresh() { tableModel.setRowCount(0);for(Appointment a:controller.appointments()){Customer c=controller.customer(a.getCustomerId());ServiceType s=controller.service(a.getServiceId());tableModel.addRow(new Object[]{a.getId(),formatter.format(a.getDateTime()),c.getName(),c.getPlate(),s.getName(),"R$ "+s.getPrice().setScale(2),a.getStatus(),a.getNotes()});} }
        void status(AppointmentStatus status) { Integer id=selectedId();if(id==null){error(new IllegalArgumentException("Selecione um agendamento."));return;}controller.setAppointmentStatus(id,status); }
        void dialog(Appointment current) {
            if(controller.customers().isEmpty()||controller.services().isEmpty()){error(new IllegalArgumentException("Cadastre ao menos um cliente e um serviço antes de agendar."));return;}
            JComboBox<Customer> customer=new JComboBox<>(controller.customers().toArray(Customer[]::new)); JComboBox<ServiceType> service=new JComboBox<>(controller.services().toArray(ServiceType[]::new)); JTextField date=new JTextField(current==null?LocalDateTime.now().plusHours(1).withMinute(0).withSecond(0).format(formatter):current.getDateTime().format(formatter)); JTextField notes=new JTextField(current==null?"":current.getNotes()); if(current!=null){customer.setSelectedItem(controller.customer(current.getCustomerId()));service.setSelectedItem(controller.service(current.getServiceId()));} if(showForm(current==null?"Novo agendamento":"Editar agendamento",new String[]{"Cliente","Serviço","Data e hora","Observações"},new JComponent[]{customer,service,date,notes}))try{controller.saveAppointment(current==null?null:current.getId(),((Customer)customer.getSelectedItem()).getId(),((ServiceType)service.getSelectedItem()).getId(),LocalDateTime.parse(date.getText().trim(),formatter),notes.getText());}catch(Exception x){error(new IllegalArgumentException("Use a data no formato dd/MM/aaaa HH:mm e escolha um horário disponível."));} }
    }
    private class DashboardPanel extends JPanel {
        private final JLabel customers=new JLabel(),services=new JLabel(),scheduled=new JLabel(),revenue=new JLabel();
        DashboardPanel(){setLayout(new BorderLayout());setBorder(new EmptyBorder(28,28,28,28));JLabel title=new JLabel("Visão geral do lava-rápido");title.setFont(new Font("SansSerif",Font.BOLD,24));add(title,BorderLayout.NORTH);JPanel cards=new JPanel(new GridLayout(1,4,18,0));for(JLabel label:new JLabel[]{customers,services,scheduled,revenue}){label.setHorizontalAlignment(SwingConstants.CENTER);label.setVerticalAlignment(SwingConstants.CENTER);label.setFont(new Font("SansSerif",Font.BOLD,18));label.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(210,210,210)),new EmptyBorder(25,12,25,12)));cards.add(label);}add(cards,BorderLayout.CENTER);}
        void refresh(){customers.setText("Clientes\n"+controller.customers().size());services.setText("Serviços\n"+controller.services().size());scheduled.setText("Agendados\n"+controller.scheduledCount());revenue.setText("Previsto\nR$ "+controller.expectedRevenue().setScale(2));}
    }
    private boolean showForm(String title,String[] labels,JComponent[] fields){JPanel panel=new JPanel(new GridLayout(0,2,10,10));for(int i=0;i<labels.length;i++){panel.add(new JLabel(labels[i]+":"));panel.add(fields[i]);}return JOptionPane.showConfirmDialog(this,panel,title,JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION;}
}
