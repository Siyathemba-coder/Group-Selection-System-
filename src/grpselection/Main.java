package grpselection;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {

		Scanner input = new Scanner(System.in);

		int totalParticipants = 0;
		int numOfGroups = 0;

		// Validate number of participants
		while (true) {
			System.out.print("Enter the total number of participants: ");

			if (input.hasNextInt()) {
				totalParticipants = input.nextInt();
				input.nextLine(); // clear buffer
				break;
			} else {
				System.out.println("Invalid input! Please enter a number.");
				input.nextLine(); // discard invalid input
			}
		}

		// Validate number of groups
		while (true) {
			System.out.print("Enter the number of groups: ");

			if (input.hasNextInt()) {
				numOfGroups = input.nextInt();
				input.nextLine(); // clear buffer
				break;
			} else {
				System.out.println("Invalid input! Please enter a number.");
				input.nextLine(); // discard invalid input
			}
		}

		GroupManager manager = new GroupManager(totalParticipants, numOfGroups);
		manager.createGroups(); // Organizer sets up groups

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
