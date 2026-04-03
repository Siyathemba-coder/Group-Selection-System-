package grpselection;

import java.util.ArrayList;

public class GroupManager {

    private int totalParticipants;
    private int numOfGroups;
    private int maxPerGroup;
    private int remainder;
    private ArrayList<Group> groups;
    private long deadlineTime = -1; // it's -1, means no deadline set

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
        for (int i = 0; i < newGroups.size(); i++) {
            int extra = (i < remainder) ? 1 : 0;
            newGroups.get(i).setMaxMembers(this.maxPerGroup + extra);
        }
        this.groups = newGroups;
        return "Groups created successfully.";
    }

    // Participant selects group using index
    public String selectGroup(Participant participant, int groupIndex) {
        if (isLocked()) return "Group selection is locked.";
        if (groupIndex < 0 || groupIndex >= groups.size()) return "Invalid group selection.";
        Group selectedGroup = groups.get(groupIndex);
        if (selectedGroup.isFull()) return "That group is full. Please choose another group.";
        selectedGroup.addMember(participant);
        return participant.getName() + " joined " + selectedGroup.getGroupName();
    }

    // Participant leaves their current group
    public String leaveGroup(String name) {
        if (isLocked()) return "Group selection is locked.";
        for (Group g : groups) {
            for (Participant p : g.getMembers()) {
                if (p.getName().equalsIgnoreCase(name)) {
                    g.getMembers().remove(p);
                    p.setGroup(null);
                    return name + " has left " + g.getGroupName();
                }
            }
        }
        return "Participant not found.";
    }

    // Admin removes a participant from a group
    public String removeParticipant(String name) {
        for (Group g : groups) {
            for (Participant p : g.getMembers()) {
                if (p.getName().equalsIgnoreCase(name)) {
                    g.getMembers().remove(p);
                    p.setGroup(null);
                    return name + " has been removed from " + g.getGroupName();
                }
            }
        }
        return "Participant not found.";
    }

    // Admin renames a group
    public String renameGroup(int groupIndex, String newName) {
        if (groupIndex < 0 || groupIndex >= groups.size()) return "Invalid group index.";
        for (int i = 0; i < groups.size(); i++) {
            if (i != groupIndex && groups.get(i).getGroupName().equalsIgnoreCase(newName)) {
                return "A group with that name already exists.";
            }
        }
        groups.get(groupIndex).setGroupName(newName);
        return "Group renamed to " + newName;
    }

    // Admin deletes a group and redistributes its members
    public String deleteGroup(int groupIndex) {
        if (groupIndex < 0 || groupIndex >= groups.size()) return "Invalid group index.";
        Group toDelete = groups.get(groupIndex);
        ArrayList<Participant> displaced = new ArrayList<>(toDelete.getMembers());
        groups.remove(groupIndex);

        StringBuilder result = new StringBuilder("Group deleted. ");
        for (Participant p : displaced) {
            p.setGroup(null);
            Group assigned = assignExtraParticipant(p);
            if (assigned != null) {
                result.append(p.getName()).append(" → ").append(assigned.getGroupName()).append(". ");
            } else {
                result.append(p.getName()).append(" could not be reassigned (all groups full). ");
            }
        }
        return result.toString().trim();
    }

    // Auto-assign by closest leader name initial
    public Group assignExtraParticipant(Participant participant) {
        if (allGroupsFull()) return null;
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
        if (closestGroup != null) closestGroup.addMember(participant);
        return closestGroup;
    }

    // Check if participant already exists
    public boolean participantExists(String name) {
        for (Group g : groups) {
            for (Participant p : g.getMembers()) {
                if (p.getName().equalsIgnoreCase(name)) return true;
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

    // Deadline / lock
    public void setDeadline(long durationMs) {
        this.deadlineTime = System.currentTimeMillis() + durationMs;
    }

    public boolean isLocked() {
        if (deadlineTime == -1) return false;
        return System.currentTimeMillis() > deadlineTime;
    }

    public long getDeadlineTime() {
        return deadlineTime;
    }

    // Summary
    public int getTotalJoined() {
        int count = 0;
        for (Group g : groups) count += g.getMembers().size();
        return count;
    }

    public int getTotalSlots() {
        int count = 0;
        for (Group g : groups) count += g.getMaxMembers();
        return count;
    }

    // Getters & setters
    public ArrayList<Group> getGroups() { return groups; }
    public void setGroups(ArrayList<Group> groups) { this.groups = groups; }
    public int getMaxPerGroup() { return maxPerGroup; }
    public int getTotalParticipants() { return totalParticipants; }
    public int getNumOfGroups() { return numOfGroups; }
}
