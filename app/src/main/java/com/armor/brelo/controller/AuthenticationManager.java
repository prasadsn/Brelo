package com.armor.brelo.controller;

import com.armor.brelo.db.model.User;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by prsn0001 on 6/30/2016.
 */
public class AuthenticationManager {

    public static boolean isUserExists(String username){
        RealmResults<User> users = Realm.getDefaultInstance().where(User.class).contains("email", username).or().contains("phoneNumber", username).findAll();
        if(users!=null && users.size()>0)
            return true;
        else return false;
    }

    public static boolean validateUser(String username, String password){
        RealmResults<User> users = Realm.getDefaultInstance().where(User.class).contains("email", username).or().contains("phoneNumber", username).findAll();
        if(users.get(0).getPassword().equals(password))
            return true;
        else return false;
    }

    public static void addUser(String fullName, String email, String phoneNumber, String password){
        Realm.getDefaultInstance().beginTransaction();
        User user = Realm.getDefaultInstance().createObject(User.class);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(password);
        Realm.getDefaultInstance().commitTransaction();
    }
}
