package br.com.lavarapido;

import br.com.lavarapido.controller.AppController;
import br.com.lavarapido.model.Store;
import br.com.lavarapido.persistence.StorePersistence;
import br.com.lavarapido.view.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) { }
            StorePersistence persistence = new StorePersistence();
            Store store = persistence.load();
            AppController controller = new AppController(store, persistence);
            MainFrame frame = new MainFrame(controller);
            controller.setFrame(frame);
            frame.setVisible(true);
        });
    }
}
