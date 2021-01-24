package com.akryvtsun.populus;

public class DeleteAction {
    public final String action;
    public final String item_id;

    public DeleteAction(String item_id) {
        this.item_id = item_id;
        action = "delete"; // or "delete"
    }
}
