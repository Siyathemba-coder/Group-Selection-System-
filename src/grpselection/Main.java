package grpselection;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {

		Scanner input = new Scanner(System.in);

		System.out.print("Enter the total number of participants: ");
		int totalParticipants = input.nextInt();

		System.out.print("Enter the number of groups: ");
		int numOfGroups = input.nextInt();
		input.nextLine(); // clear buffer

		GroupManager manager = new GroupManager(totalParticipants, numOfGroups);

		// Organizer sets up groups
		manager.createGroups();

		// Participant interaction
		for (int i = 0; i < totalParticipants; i++) {

		    String name;

		    while (true) {

		        System.out.print("\nEnter participant name: ");
		        name = input.nextLine().trim();

		        if (name.isEmpty()) {
		            System.out.println("Name cannot be empty.");
		        }
		        else if (manager.participantExists(name)) {
		            System.out.println("This name is already taken. Please choose another.");
		        }
		        else {
		            break;
		        }
		    }

		    Participant p = new Participant(name);

		    if (!manager.allGroupsFull()) {
		        manager.selectGroup(p);
		    }
		    else {
		        manager.assignExtraParticipant(p);
		    }
		}

		// Display results
		System.out.println("\n===== FINAL GROUPS =====");
		manager.printFinalGroups();

		input.close();
	}
}