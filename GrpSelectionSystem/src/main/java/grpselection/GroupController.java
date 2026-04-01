package grpselection;

import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;

@RestController
@RequestMapping("/api")
@CrossOrigin // allows your HTML/JS to connect
public class GroupController {

    private GroupManager manager;

    // Step 1: Initialize system
    @PostMapping("/setup")
    public String setup(@RequestParam int totalParticipants,
                        @RequestParam int numOfGroups) {

        if (totalParticipants <= 0 || numOfGroups <= 0) {
            return "Error: Participants and groups must be greater than 0.";
        }
        if (numOfGroups > totalParticipants) {
            return "Error: Number of groups cannot exceed number of participants.";
        }

        manager = new GroupManager(totalParticipants, numOfGroups);
        return "System initialized successfully";
    }

    // Step 2: Create groups
    @PostMapping("/groups")
    public String createGroups(@RequestBody ArrayList<Group> groups) {

        if (manager == null) {
            return "System not initialized. Call /setup first.";
        }

        for (Group g : groups) {
            System.out.println("Group: " + g.getGroupName() + ", Leader: " + g.getLeaderName());
        }

        return manager.createGroups(groups);
    }

    // Step 3: Get all groups
    @GetMapping("/groups")
    public ArrayList<Group> getGroups() {
        if (manager == null) return new ArrayList<>();
        return manager.getGroups();
    }

    // Step 4: Add participant — try chosen group first, auto-assign if full
    @PostMapping("/select")
    public String selectGroup(@RequestParam String name,
                              @RequestParam int groupIndex) {

        if (manager == null) return "System not initialized.";

        if (name == null || name.trim().isEmpty()) {
            return "Participant name cannot be empty.";
        }

        if (manager.participantExists(name)) {
            return "Participant name already exists.";
        }

        if (manager.allGroupsFull()) {
            return "All groups are full.";
        }

        Participant p = new Participant(name.trim());
        Group selectedGroup = manager.getGroups().get(groupIndex);

        if (!selectedGroup.isFull()) {
            return manager.selectGroup(p, groupIndex);
        }

        // Selected group is full — auto-assign to closest available group
        Group assigned = manager.assignExtraParticipant(p);
        if (assigned != null) {
            return name + "'s chosen group was full. Auto-assigned to " + assigned.getGroupName();
        }

        return "All groups are full.";
    }

    @GetMapping("/test")
    public String test() {
        return "Backend is working!";
    }
}
