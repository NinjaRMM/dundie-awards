package com.ninjaone.dundie_awards.employee;

import com.ninjaone.dundie_awards.activity.MessageBroker;
import jakarta.persistence.PostPersist;
import org.springframework.context.annotation.Lazy;

import static com.ninjaone.dundie_awards.activity.Message.createMessageNow;
import static java.lang.String.format;

public class EmployeeListener {

    private final MessageBroker messageBroker;

    public EmployeeListener(@Lazy MessageBroker messageBroker) {
        this.messageBroker = messageBroker;
    }

    /**
     * Currently also alerts for all the employees setup in DataLoader.
     * If we're loading from the db on startup, we wouldn't want this.
     * However, it makes for a nice demo.
     */
    @PostPersist
    void onNewEmployee(Employee employee) {
        messageBroker.sendMessage(
                createMessageNow(
                        format(
                                "%s %s has joined the company!",
                                employee.getFirstName(),
                                employee.getLastName()
                        )
                )
        );
    }
}
