package tcache.service;

import tcache.bean.User;

public interface UserService {

    User getUser();

    User getUser(String name);

    User getUser(int id);
}
