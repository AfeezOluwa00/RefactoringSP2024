import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SearchBySurnameDialog extends JDialog implements ActionListener {
    private EmployeeDetails parent;
    private JButton searchButton, cancelButton;
    private JTextField searchField;

    public SearchBySurnameDialog(EmployeeDetails parent) {
        setTitle("Search by Surname");
        setModal(true);
        this.parent = parent;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JScrollPane scrollPane = new JScrollPane(createSearchPane());
        setContentPane(scrollPane);

        getRootPane().setDefaultButton(searchButton);

        setSize(500, 190);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private Container createSearchPane() {
        JPanel searchPanel = new JPanel(new GridLayout(3, 1));
        JPanel textPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        JLabel searchLabel;

        searchPanel.add(new JLabel("Search by Surname"));

        textPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        textPanel.add(searchLabel = new JLabel("Enter Surname:"));
        searchLabel.setFont(this.parent.getFont1());
        textPanel.add(searchField = new JTextField(20));
        searchField.setFont(this.parent.getFont1());
        searchField.setDocument(new JTextFieldLimit(20));

        buttonPanel.add(searchButton = new JButton("Search"));
        searchButton.addActionListener(this);
        searchButton.requestFocus();

        buttonPanel.add(cancelButton = new JButton("Cancel"));
        cancelButton.addActionListener(this);

        searchPanel.add(textPanel);
        searchPanel.add(buttonPanel);

        return searchPanel;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton) {
            this.parent.getSearchBySurnameField().setText(searchField.getText());
            this.parent.searchEmployeeBySurname();
            dispose();
        } else if (e.getSource() == cancelButton) {
            dispose();
        }
    }
}
