package com.example.sergi.fitelite;

/**
 * Created by sergi on 07/04/2017.
 */

public class UserInformation {

    public String userName;
    public String userSurname;
    public String userAge;
    public String userWeight;
    public String userHeight;

    public UserInformation(){//empty required constructor

    }
    //constructors to get the user information and to arrange it in order to be sent to the database
    public UserInformation(String name, String surname, String age, String weight, String height) {
        this.userName = name;
        this.userSurname = surname;
        this.userAge = age;
        this.userWeight = weight;
        this.userHeight = height;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public String getUserAge() {
        return userAge;
    }

    public String getUserWeight() {
        return userWeight;
    }

    public String getUserHeight() {
        return userHeight;
    }
}

