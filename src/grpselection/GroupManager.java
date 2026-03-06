package grpselection;

import java.util.ArrayList;
import java.util.Scanner;

public class GroupManager {

	private int totalParticipants;
	private int numOfGroups;
	private int maxPerGroup;
	private ArrayList<Group> groups;
	private Scanner sc;

	public GroupManager(int totalParticipants, int numOfGroups) {
		this.totalParticipants = totalParticipants;
		this.numOfGroups = numOfGroups;
		this.groups = new ArrayList<>();
		this.maxPerGroup = totalParticipants / numOfGroups;
		this.sc = new Scanner(System.in);
	}

	// Setup methods
	public void createGroups() {

		System.out.println("=== CREATE GROUPS ===\n");

		for (int i = 0; i < numOfGroups; i++) {

			String groupName;

			while (true) {

				System.out.print("Enter name for Group " + (i + 1) + ": ");
				groupName = sc.nextLine().trim();

				boolean exists = false;

				for (Group g : groups) {
					if (g.getGrpName().equalsIgnoreCase(groupName)) {
						exists = true;
						break;
					}
				}

				if (groupName.isEmpty()) {
					System.out.println("Group name cannot be empty. Try again.");
				}
				else if (exists) {
					System.out.println("Group name already exists. Try again.");
				}
				else {
					break;
				}
			}

			String leaderName;

			while (true) {

				System.out.print("Enter leader name for Group " + (i + 1) + ": ");
				leaderName = sc.nextLine().trim();

				if (leaderName.isEmpty()) {
					System.out.println("Leader name cannot be empty. Try again.");
				}
				else {
					break;
				}
			}

			Group group = new Group(groupName, leaderName, maxPerGroup);
			groups.add(group);

			System.out.println("Group '" + groupName + "' created.\n");
		}

		System.out.println("All groups created successfully!\n");
	}

	// Participant interaction
	public void selectGroup(Participant participant) {

		System.out.println("Available groups:");

		for (int i = 0; i < groups.size(); i++) {
			System.out.println((i + 1) + ". " + groups.get(i).getGrpName());
		}

		System.out.print("Select group: ");
		int choice = sc.nextInt();
		sc.nextLine(); // clear buffer

		if (choice < 1 || choice > groups.size()) {
			System.out.println("Invalid group selection.");
			return;
		}

		Group selectedGroup = groups.get(choice - 1);

		if (selectedGroup.isFull()) {
			System.out.println("Sorry, this group is already full. Choose another group.");
		}
		else {
			selectedGroup.addMember(participant);
			System.out.println(participant.getName() + " joined " + selectedGroup.getGrpName());
		}
	}

	// Handle extra participants
	public void assignExtraParticipant(Participant participant) {

		System.out.print("Enter participant name: ");
		String name = sc.nextLine().trim();

		if (name.isEmpty()) {
			System.out.println("Name cannot be empty.");
			return;
		}

		participant.setName(name);

		char participantChar = Character.toLowerCase(name.charAt(0));

		int smallestDifference = Integer.MAX_VALUE;
		Group closestGroup = null;

		for (Group g : groups) {

			String leaderName = g.getLeaderName();
			char leaderChar = Character.toLowerCase(leaderName.charAt(0));

			int difference = Math.abs(participantChar - leaderChar);

			if (difference < smallestDifference) {
				smallestDifference = difference;
				closestGroup = g;
			}
		}

		if (closestGroup != null) {
			closestGroup.addMember(participant);

			System.out.println(
				participant.getName() + " assigned to group " +
				closestGroup.getGrpName() +
				" (closest leader match)"
			);
		}
	}
	// Existence of a participant
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

	// Utility method
	public boolean allGroupsFull() {

		for (Group g : groups) {
			if (!g.isFull())
				return false;
		}

		return true;
	}

	// Display results
	public void printFinalGroups() {

		for (Group g : groups) {

			System.out.println(
				"Group name: " + g.getGrpName() +
				" | Leader: " + g.getLeaderName()
			);

			g.printMembers();
			System.out.println();
		}
	}
}
