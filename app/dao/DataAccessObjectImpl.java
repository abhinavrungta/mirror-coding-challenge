package dao;

import java.util.*;
import java.util.stream.Collectors;

public class DataAccessObjectImpl implements DataAccessObject {

    private Map<String, String> userPasswdMap;
    private Map<String, Set<String>> groupsData;

    public DataAccessObjectImpl() {
        userPasswdMap = new HashMap<>();
        groupsData = new HashMap<>();
    }

    @Override
    public String getUserPasswd(String username) {
        return userPasswdMap.get(username);
    }

    @Override
    public void setUserPasswd(String username, String password) {
        userPasswdMap.put(username, password);
    }

    @Override
    public List<String> getUsersList() {
        return new ArrayList<>(userPasswdMap.keySet());
    }

    @Override
    public void addGroup(String groupName, List<String> groupUsers) {
        groupsData.put(groupName, new HashSet<>(groupUsers));
    }

    @Override
    public List<String> getUsersGroups(String username) {
        return groupsData.entrySet().stream()
                .filter(entry -> entry.getValue().contains(username))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
