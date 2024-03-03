import javax.swing.*;

public class AddRecordDialog extends JDialog {
    private AddRecordDialogUI dialogUI;
    private RecordManager recordManager;

    public AddRecordDialog(EmployeeDetails parent) {
        this.recordManager = new RecordManager();
        this.dialogUI = new AddRecordDialogUI();
        this.dialogUI.setParent(parent);
        
        initializeComponents();
        addListeners();

        setTitle("Add Record");
        setModal(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 370);
        setLocation(350, 250);
        setVisible(true);
    }

    private void initializeComponents() {
        setContentPane(new JScrollPane(dialogUI.createUIComponents()));
        getRootPane().setDefaultButton(dialogUI.getSaveButton());
    }

    private void addListeners() {
        dialogUI.addSaveButtonListener(e -> {
            if (recordManager.validateInput(dialogUI)) {
                recordManager.addRecord(dialogUI.getInputData());
                dispose();
                dialogUI.getParent().setChangesMade(true);
            } else {
                JOptionPane.showMessageDialog(null, "Wrong values or format! Please check!");
                dialogUI.setToWhite();
            }
        });

        dialogUI.addCancelButtonListener(e -> dispose());
    }
}
