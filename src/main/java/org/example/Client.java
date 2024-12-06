package org.example;

import java.util.ArrayList;

public class Client extends Accounts {
    private final ArrayList<Projects> needToDo;
    private final ArrayList<Projects> completed;

    public Client(int id, String username, String email, String password, String bankInfo) {
        super(id, username, email, password, bankInfo);
        this.needToDo = new ArrayList<>();
        this.completed = new ArrayList<>();
    }

    public ArrayList<Projects> getNeedToDo() {
        return needToDo;
    }

    public ArrayList<Projects> getCompleted() {
        return completed;
    }

    public void finishProject(Projects project) {
        if (needToDo.remove(project)) {
            completed.add(project);
            System.out.println("Project moved to Completed list for Client: " + getUsername());
        } else {
            System.out.println("Project not found in NeedToDo list for Client: " + getUsername());
        }
    }
}
