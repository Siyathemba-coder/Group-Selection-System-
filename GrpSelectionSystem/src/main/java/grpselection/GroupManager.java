package grpselection;

import java.util.ArrayList;

public class GroupManager {

    private int totalParticipants;
    private int numOfGroups;
    private int maxPerGroup;
    private int remainder;
    private ArrayList<Group> groups;

    public GroupManager(int totalParticipants, int numOfGroups) {
        this.totalParticipants = totalParticipants;
        this.numOfGroups = numOfGroups;
        this.groups = new ArrayList<>();
        this.maxPerGroup = totalParticipants / numOfGroups;
        this.remainder = totalParticipants % numOfGroups;
    }

    // Create groups from front-end input
    public String createGroups(ArrayList<Group> newGroups) {

        for (int i = 0; i < newGroups.size(); i++) {
            for (int j = i + 1; j < newGroups.size(); j++) {
                if (newGroups.get(i).getGroupName()
                        .equalsIgnoreCase(newGroups.get(j).getGroupName())) {
                    return "Duplicate group names are not allowed.";
                }
            }
        }

        // Distribute remainder slots — first `remainder` groups get one extra seat
        for (int i = 0; i < newGroups.size(); i++) {
            int extra = (i < remainder) ? 1 : 0;
            newGroups.get(i).setMaxMembers(this.maxPerGroup + extra);
        }

        this.groups = newGroups;
        return "Groups created successfully.";
    }

    // Participant selects group using index
    public String selectGroup(Participant participant, int groupIndex) {

        if (groupIndex < 0 || groupIndex >= groups.size()) {
            return "Invalid group selection.";
        }

        Group selectedGroup = groups.get(groupIndex);

        if (selectedGroup.isFull()) {
            return "That group is full. Please choose another group.";
        }

        selectedGroup.addMember(participant);
        return participant.getName() + " joined " + selectedGroup.getGroupName();
    }

    // Auto-assign by closest leader name initial
    public Group assignExtraParticipant(Participant participant) {

        if (allGroupsFull()) {
            return null;
        }

        char participantChar = Character.toLowerCase(participant.getName().charAt(0));
        int smallestDifference = Integer.MAX_VALUE;
        Group closestGroup = null;

        for (Group g : groups) {
            if (g.isFull()) continue;

            char leaderChar = Character.toLowerCase(g.getLeaderName().charAt(0));
            int difference = Math.abs(participantChar - leaderChar);

            if (difference < smallestDifference) {
                smallestDifference = difference;
                closestGroup = g;
            }
        }

        if (closestGroup != null) {
            closestGroup.addMember(participant);
        }

        return closestGroup;
    }

    // Check if participant already exists across all groups
    public boolean participantExists(String name) {
        for (Group g : groups) {
            for (Participant p : g.getMembers()) {
                if (p.getName().equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Check if all groups are full
    public boolean allGroupsFull() {
        for (Group g : groups) {
            if (!g.isFull()) return false;
        }
        return true;
    }

    public ArrayList<Group> getGroups() { return groups; }
    public void setGroups(ArrayList<Group> groups) { this.groups = groups; }
    public int getMaxPerGroup() { return maxPerGroup; }
    public int getTotalParticipants() { return totalParticipants; }
    public int getNumOfGroups() { return numOfGroups; }
