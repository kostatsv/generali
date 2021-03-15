package com.generali.dao;

import com.generali.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends AbstractDao<User> {

    public User getByUsername(String username) {
        return (User) getSession().createQuery("from User where username = :username")
                .setParameter("username", username)
                .uniqueResult();
    }

}
