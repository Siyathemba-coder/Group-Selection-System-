package grpselection;

import java.util.ArrayList;

public class GroupManager {

    private int totalParticipants;
    private int numOfGroups;
    private int maxPerGroup;
    private ArrayList<Group> groups;

    public GroupManager(int totalParticipants, int numOfGroups) {
        this.totalParticipants = totalParticipants;
        this.numOfGroups = numOfGroups;
        this.groups = new ArrayList<>();
        this.maxPerGroup = totalParticipants / numOfGroups;
    }

    // Create groups from fron-tend input
    public String createGroups(ArrayList<Group> newGroups) {

        // Validate unique names
        for (int i = 0; i < newGroups.size(); i++) {
            for (int j = i + 1; j < newGroups.size(); j++) {
                if (newGroups.get(i).getGrpName()
                        .equalsIgnoreCase(newGroups.get(j).getGrpName())) {
                    return "Duplicate group names are not allowed.";
                }
            }
        }

        for (Group g : newGroups) {
            g.setMaxMembers(this.maxPerGroup);
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
            return "Group is already full.";
        }

        selectedGroup.addMember(participant);
        return participant.getName() + " joined " + selectedGroup.getGrpName();
    }

    //  Auto-assign extra participants
    public Group assignExtraParticipant(Participant participant) {

        if (allGroupsFull()) {
            return null;
        }

        char participantChar = Character.toLowerCase(
                participant.getName().charAt(0));

        int smallestDifference = Integer.MAX_VALUE;
        Group closestGroup = null;

        for (Group g : groups) {

            if (g.isFull()) {
                continue;
            }

            char leaderChar = Character.toLowerCase(
                    g.getLeaderName().charAt(0));

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

    // Check if participant exists
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
            if (!g.isFull()) {
                return false;
            }
        }

        return true;
    }

    // Return groups (for front-end display)
    public ArrayList<Group> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }

    public int getMaxPerGroup() {
        return maxPerGroup;
    }
}
