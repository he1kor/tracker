package com.helkor.project.connection.websocket.util.serialized;

import com.google.gson.Gson;
import com.helkor.project.connection.websocket.util.Command;

public class Message {
    private Command command;
    private String data;
    private Message(Command command, String data){
        this.command = command;
        this.data = data;
    }
    public static Message fromJson(String json){
        return new Gson().fromJson(json,Message.class);
    }
    public static String toJson(Command command, String data){
        return new Gson().toJson(new Message(command,data));
    }

    public Command getCommand() {
        return command;
    }

    public String getData() {
        return data;
    }
}
