package grpselection;

import java.util.ArrayList;

public class Group{
	private String groupName;
	private String leaderName;
	private int maxMembers;
	private ArrayList<Participant> members;
	
	public Group(String groupName, String leaderName, int maxMembers) {
		this.groupName = groupName;
		this.leaderName = leaderName;
		this.maxMembers = maxMembers;
		this.members = new ArrayList<>();
	}
	public Group() {}
	//Getters
	public String getGrpName() {
		return groupName;
	}
	public String getLeaderName() {
		return leaderName;
	}
	public int getMemberCount() {
		return maxMembers;
	}
	//Core methods
	public boolean isFull() {
		return members.size() >= maxMembers;
	}
	public void addMember(Participant participant) {
		if (!isFull()) {
			members.add(participant);
			participant.setGroup(this); // Assign participant to this group
		}
	}
	//Utility method
	public void printMembers() {
		System.out.println("Members of " + groupName + ":");
		for (Participant p : members) {
			System.out.println("- " + p.getName());
		}
	}
	public ArrayList<Participant> getMembers() {
		return members;
	}
	public void setMaxMembers(int maxMembers) {
	    this.maxMembers = maxMembers;
	}
	public void setGrpName(String groupName) {
	    this.groupName = groupName;
	}

	public void setLeaderName(String leaderName) {
	    this.leaderName = leaderName;
	}
}
