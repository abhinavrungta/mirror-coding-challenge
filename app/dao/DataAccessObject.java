package dao;

import java.util.List;

public interface DataAccessObject {
    String getUserPasswd(String username);

    void setUserPasswd(String username, String password);

    List<String> getUsersList();

    void addGroup(String groupName, List<String> groupUsers);

    List<String> getUsersGroups(String username);
}
