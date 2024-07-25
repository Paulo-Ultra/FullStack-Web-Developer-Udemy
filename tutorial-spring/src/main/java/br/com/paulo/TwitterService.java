package br.com.paulo;

import java.util.List;

public class TwitterService implements MessageService{

    private String object;
    private String message;
    private List<String> destination;

    @Override
    public boolean sendMessage() {
        if(this.object == null || this.message == null || this.destination == null){
            return false;
        }
        System.out.println("Twitter with object: " + this.object + ", message: " + this.message + " sent to: ");
        this.destination.forEach(System.out::println);
        return true;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getDestination() {
        return destination;
    }

    public void setDestination(List<String> destination) {
        this.destination = destination;
    }
}
