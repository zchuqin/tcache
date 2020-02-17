package stoner.tcache.service;

import stoner.tcache.bean.User;

public interface UserService {

    User getUser();

    User getUser(String name);

    User getUser(int id);

    User saveUser(User user);

    boolean cleanUser(String name);
}
