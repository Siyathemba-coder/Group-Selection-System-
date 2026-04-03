package grpselection;

import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class GroupController {

    private GroupManager manager;

    // Step 1: Initialize system
    @PostMapping("/setup")
    public String setup(@RequestParam int totalParticipants,
                        @RequestParam int numOfGroups) {
        if (totalParticipants <= 0 || numOfGroups <= 0)
            return "Error: Participants and groups must be greater than 0.";
        if (numOfGroups > totalParticipants)
            return "Error: Number of groups cannot exceed number of participants.";
        manager = new GroupManager(totalParticipants, numOfGroups);
        return "System initialized successfully";
    }

    // Step 2: Create groups
    @PostMapping("/groups")
    public String createGroups(@RequestBody ArrayList<Group> groups) {
        if (manager == null) return "System not initialized. Call /setup first.";
        return manager.createGroups(groups);
    }

    // Step 3: Get all groups
    @GetMapping("/groups")
    public ArrayList<Group> getGroups() {
        if (manager == null) return new ArrayList<>();
        return manager.getGroups();
    }

    // Step 4: Join a group
    @PostMapping("/select")
    public String selectGroup(@RequestParam String name,
                              @RequestParam int groupIndex) {
        if (manager == null) return "System not initialized.";
        if (name == null || name.trim().isEmpty()) return "Participant name cannot be empty.";
        if (manager.isLocked()) return "Group selection is locked. The deadline has passed.";
        if (manager.participantExists(name)) return "Participant name already exists.";
        if (manager.allGroupsFull()) return "All groups are full.";

        Participant p = new Participant(name.trim());
        Group selectedGroup = manager.getGroups().get(groupIndex);
        if (!selectedGroup.isFull()) return manager.selectGroup(p, groupIndex);

        Group assigned = manager.assignExtraParticipant(p);
        if (assigned != null)
            return name + "'s chosen group was full. Auto-assigned to " + assigned.getGroupName();
        return "All groups are full.";
    }

    // Leave group
    @PostMapping("/leave")
    public String leaveGroup(@RequestParam String name) {
        if (manager == null) return "System not initialized.";
        if (manager.isLocked()) return "Group selection is locked. The deadline has passed.";
        return manager.leaveGroup(name);
    }

    // Admin: remove participant
    @PostMapping("/admin/removeParticipant")
    public String removeParticipant(@RequestParam String name) {
        if (manager == null) return "System not initialized.";
        return manager.removeParticipant(name);
    }

    // Admin: rename group
    @PostMapping("/admin/renameGroup")
    public String renameGroup(@RequestParam int groupIndex,
                              @RequestParam String newName) {
        if (manager == null) return "System not initialized.";
        return manager.renameGroup(groupIndex, newName);
    }

    // Admin: delete group and redistribute
    @PostMapping("/admin/deleteGroup")
    public String deleteGroup(@RequestParam int groupIndex) {
        if (manager == null) return "System not initialized.";
        return manager.deleteGroup(groupIndex);
    }

    // Admin: set countdown timer (minutes)
    @PostMapping("/admin/setDeadline")
    public String setDeadline(@RequestParam int minutes) {
        if (manager == null) return "System not initialized.";
        manager.setDeadline((long) minutes * 60 * 1000);
        return "Deadline set for " + minutes + " minutes from now.";
    }

    // Check if selection is locked + time remaining
    @GetMapping("/isLocked")
    public Map<String, Object> isLocked() {
        Map<String, Object> result = new HashMap<>();
        if (manager == null) {
            result.put("locked", false);
            result.put("remainingMs", -1);
            return result;
        }
        result.put("locked", manager.isLocked());
        long remaining = manager.getDeadlineTime() == -1 ? -1
                : Math.max(0, manager.getDeadlineTime() - System.currentTimeMillis());
        result.put("remainingMs", remaining);
        return result;
    }

    // Summary
    @GetMapping("/summary")
    public Map<String, Object> summary() {
        Map<String, Object> result = new HashMap<>();
        if (manager == null) {
            result.put("error", "System not initialized.");
            return result;
        }
        result.put("totalParticipants", manager.getTotalParticipants());
        result.put("numOfGroups", manager.getGroups().size());
        result.put("totalJoined", manager.getTotalJoined());
        result.put("totalSlots", manager.getTotalSlots());
        result.put("remainingSlots", manager.getTotalSlots() - manager.getTotalJoined());
        result.put("locked", manager.isLocked());
        return result;
    }

    // Reset system
    @PostMapping("/admin/reset")
    public String reset() {
        manager = null;
        return "System has been reset.";
    }

    @GetMapping("/test")
    public String test() {
        return "Backend is working!";
    }
}
