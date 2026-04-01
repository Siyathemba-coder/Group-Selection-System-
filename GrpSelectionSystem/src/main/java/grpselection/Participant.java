package grpselection;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Participant {
	
	@JsonProperty("name")
	private String name;
	private Group group; // reference to the Group this participant belongs to

	public Participant(String name) {
		this.name = name;
		this.group = null; // initailly not assigned
	}
	//Getters
	public String getName() {
		return name;
	}
	public Group getGroup() {
		return group;
	}
	//setter 
	public void setGroup(Group grp) {
		this.group = grp;
		
	}
	public void setName(String name) {
		this.name = name;
	}
}
