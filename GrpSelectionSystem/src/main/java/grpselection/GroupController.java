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

        manager = new GroupManager(totalParticipants, numOfGroups);
        return "System initialized successfully";
    }

    // Step 2: Create groups
    @PostMapping("/groups")
    public String createGroups(@RequestBody ArrayList<Group> groups) {

        if (manager == null) {
            return "System not initialized. Call /setup first.";
        }

        return manager.createGroups(groups);
    }

    // Step 3: Get all groups (for display)
    @GetMapping("/groups")
    public ArrayList<Group> getGroups() {

        if (manager == null) {
            return new ArrayList<>();
        }

        return manager.getGroups();
    }

    // Step 4: Add participant to selected group
    @PostMapping("/select")
    public String selectGroup(@RequestParam String name,
                              @RequestParam int groupIndex) {

        if (manager == null) {
            return "System not initialized.";
        }

        if (manager.participantExists(name)) {
            return "Participant name already exists.";
        }

        Participant p = new Participant(name);

        if (!manager.allGroupsFull()) {
            return manager.selectGroup(p, groupIndex);
        } else {
            Group assigned = manager.assignExtraParticipant(p);

            if (assigned != null) {
                return name + " auto-assigned to " + assigned.getGrpName();
            } else {
                return "All groups are full.";
            }
        }
    }
    @GetMapping("/test")
    public String test() {
        return "Backend is working!";
    }
}