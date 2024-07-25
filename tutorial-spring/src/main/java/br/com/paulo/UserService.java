package br.com.paulo;

public class UserService {

    private MessageService emailService;
    private MessageService twitterService;

    public UserService(EmailService emailService, TwitterService twitterService) {
        this.emailService = emailService;
        this.twitterService = twitterService;
    }

    public void notifyUsers() {
        boolean result1 = this.emailService.sendMessage();
        System.out.println("Result is: " + result1);

        boolean result2 = this.twitterService.sendMessage();
        System.out.println("Result is: " + result2);
    }

    public MessageService getEmailService() {
        return emailService;
    }

    public void setEmailService(MessageService emailService) {
        this.emailService = emailService;
    }

    public MessageService getTwitterService() {
        return twitterService;
    }

    public void setTwitterService(MessageService twitterService) {
        this.twitterService = twitterService;
    }
}
