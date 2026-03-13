package grpselection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class GroupSelectionGUI {

    private GroupManager manager;
    private int totalParticipants;
    private int numOfGroups;
    private int currentParticipant = 0;

    private JTable groupTable;
    private DefaultTableModel groupTableModel;
    private JProgressBar progressBar;
    private JTextArea resultsArea;

    public void start() {
        SwingUtilities.invokeLater(this::showDashboard);
    }

    private void showDashboard() {

        JFrame frame = new JFrame("Group Selection System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(650, 500);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Setup", createSetupPanel(frame, tabs));
        tabs.addTab("Groups", createGroupsPanel(tabs));
        tabs.addTab("Participants", createParticipantsPanel());
        tabs.addTab("Results", createResultsPanel());

        frame.add(tabs);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // ================= SETUP PANEL =================

    private JPanel createSetupPanel(JFrame frame, JTabbedPane tabs) {

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JLabel totalLabel = new JLabel("Total Participants:");
        JTextField totalField = new JTextField();

        JLabel groupLabel = new JLabel("Number of Groups:");
        JTextField groupField = new JTextField();

        JButton nextButton = new JButton("Initialize");

        panel.add(totalLabel);
        panel.add(totalField);
        panel.add(groupLabel);
        panel.add(groupField);
        panel.add(new JLabel());
        panel.add(nextButton);

        nextButton.addActionListener(e -> {

            try {

                totalParticipants = Integer.parseInt(totalField.getText().trim());
                numOfGroups = Integer.parseInt(groupField.getText().trim());

                if (totalParticipants <= 0 || numOfGroups <= 0) {
                    JOptionPane.showMessageDialog(panel, "Numbers must be positive.");
                    return;
                }

                manager = new GroupManager(totalParticipants, numOfGroups);

                progressBar.setMaximum(totalParticipants);
                progressBar.setValue(0);

                JOptionPane.showMessageDialog(panel, "Setup complete! Now create groups.");

                tabs.setSelectedIndex(1);

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(panel, "Please enter valid numbers.");
            }

        });

        return panel;
    }

    // ================= GROUP PANEL =================

    private JPanel createGroupsPanel(JTabbedPane tabs) {

        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        groupTableModel = new DefaultTableModel(
                new Object[]{"Group Name", "Leader", "Capacity", "Members"}, 0);

        groupTable = new JTable(groupTableModel);

        groupTable.setRowHeight(25);
        groupTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        groupTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton addGroupButton = new JButton("Add Group");

        addGroupButton.addActionListener(e -> {

            if (manager == null) {
                JOptionPane.showMessageDialog(panel, "Please complete setup first.");
                return;
            }

            if (manager.getGroups().size() >= numOfGroups) {
                JOptionPane.showMessageDialog(panel,
                        "All required groups already created.\nMove to Participants tab.");
                tabs.setSelectedIndex(2);
                return;
            }

            String groupName = JOptionPane.showInputDialog("Enter Group Name:");
            String leaderName = JOptionPane.showInputDialog("Enter Leader Name:");

            if (groupName != null && leaderName != null &&
                    !groupName.trim().isEmpty() && !leaderName.trim().isEmpty()) {

                manager.getGroups().add(
                        new Group(groupName, leaderName, manager.getMaxPerGroup()));

                groupTableModel.addRow(new Object[]{
                        groupName,
                        leaderName,
                        manager.getMaxPerGroup(),
                        0
                });
            }

            if (manager.getGroups().size() == numOfGroups) {

                JOptionPane.showMessageDialog(panel,
                        "All groups created!\nProceed to Participants tab.");

                tabs.setSelectedIndex(2);
            }

        });

        panel.add(new JScrollPane(groupTable), BorderLayout.CENTER);
        panel.add(addGroupButton, BorderLayout.SOUTH);

        return panel;
    }

    // ================= PARTICIPANTS PANEL =================

    private JPanel createParticipantsPanel() {

        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        JPanel formPanel = new JPanel(new GridLayout(2,2,10,10));

        JLabel nameLabel = new JLabel("Participant Name:");
        JTextField nameField = new JTextField();

        JLabel groupLabel = new JLabel("Select Group:");
        JComboBox<String> groupBox = new JComboBox<>();

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(groupLabel);
        formPanel.add(groupBox);

        JButton joinButton = new JButton("Join Group");

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);

        panel.add(progressBar, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(joinButton, BorderLayout.SOUTH);

        joinButton.addActionListener(e -> {

            if (manager == null || manager.getGroups().isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please create groups first.");
                return;
            }

            if (currentParticipant >= totalParticipants) {
                JOptionPane.showMessageDialog(panel,
                        "All participants already registered.");
                return;
            }

            String name = nameField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Name cannot be empty.");
                return;
            }

            if (manager.participantExists(name)) {
                JOptionPane.showMessageDialog(panel, "Name already taken.");
                return;
            }

            Participant p = new Participant(name);

            String selectedGroupName = (String) groupBox.getSelectedItem();

            Group selectedGroup = manager.getGroups().stream()
                    .filter(g -> g.getGrpName().equals(selectedGroupName))
                    .findFirst().orElse(null);

            if (selectedGroup != null) {

                if (selectedGroup.isFull()) {

                    Group assigned = manager.assignExtraParticipant(p);

                    JOptionPane.showMessageDialog(panel,
                            name + " was automatically assigned to "
                                    + assigned.getGrpName());

                } else {

                    selectedGroup.addMember(p);

                    JOptionPane.showMessageDialog(panel,
                            name + " joined " + selectedGroup.getGrpName());
                }

                updateGroupTable();
            }

            currentParticipant++;

            progressBar.setValue(currentParticipant);
            progressBar.setString(currentParticipant + " / "
                    + totalParticipants + " Registered");

            nameField.setText("");

        });

        panel.addHierarchyListener(e -> {

            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && panel.isShowing()) {

                groupBox.removeAllItems();

                if (manager != null) {

                    for (Group g : manager.getGroups()) {
                        groupBox.addItem(g.getGrpName());
                    }
                }
            }
        });

        return panel;
    }

    // ================= RESULTS PANEL =================

    private JPanel createResultsPanel() {

        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Consolas", Font.PLAIN, 14));

        JButton refreshButton = new JButton("Refresh Results");

        refreshButton.addActionListener(e -> {

            StringBuilder sb = new StringBuilder();

            for (Group g : manager.getGroups()) {

                sb.append("========== ")
                        .append(g.getGrpName())
                        .append(" ==========\n");

                sb.append("Leader: ")
                        .append(g.getLeaderName())
                        .append("\n");

                for (Participant p : g.getMembers()) {

                    sb.append(" - ")
                            .append(p.getName())
                            .append("\n");
                }

                sb.append("\n");
            }

            resultsArea.setText(sb.toString());
        });

        panel.add(new JScrollPane(resultsArea), BorderLayout.CENTER);
        panel.add(refreshButton, BorderLayout.SOUTH);

        return panel;
    }

    // ================= TABLE UPDATE =================

    private void updateGroupTable() {

        for (int i = 0; i < manager.getGroups().size(); i++) {

            Group g = manager.getGroups().get(i);

            groupTableModel.setValueAt(g.getMembers().size(), i, 3);
        }
    }

    // ================= MAIN =================

    public static void main(String[] args) {

        try {

            UIManager.setLookAndFeel(
                    "javax.swing.plaf.nimbus.NimbusLookAndFeel");

        } catch (Exception e) {
            e.printStackTrace();
        }

        new GroupSelectionGUI().start();
    }
}