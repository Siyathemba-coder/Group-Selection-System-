package grpselection;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"group"})
public class Group {

    @JsonProperty("groupName")
    private String groupName;

    @JsonProperty("leaderName")
    private String leaderName;

    @JsonProperty("maxMembers")
    private int maxMembers;

    @JsonProperty("members")
    private ArrayList<Participant> members;

    public Group() { this.members = new ArrayList<>(); }

    public Group(String groupName, String leaderName, int maxMembers) {
        this.groupName = groupName;
        this.leaderName = leaderName;
        this.maxMembers = maxMembers;
        this.members = new ArrayList<>();
    }

    public String getGroupName() { return groupName; }
    public String getLeaderName() { return leaderName; }
    public int getMaxMembers() { return maxMembers; }
    public ArrayList<Participant> getMembers() { return members; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public void setLeaderName(String leaderName) { this.leaderName = leaderName; }
    public void setMaxMembers(int maxMembers) { this.maxMembers = maxMembers; }

    public boolean isFull() { return members.size() >= maxMembers; }

    public void addMember(Participant participant) {
        if (!isFull()) {
            members.add(participant);
            participant.setGroup(this);
        }
    }

    public void printMembers() {
        System.out.println("Members of " + groupName + ":");
        for (Participant p : members) {
            System.out.println("- " + p.getName());
        }
    }
}
