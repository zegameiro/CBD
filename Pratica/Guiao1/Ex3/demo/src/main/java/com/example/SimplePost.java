package com.example;

import java.util.Set;
import java.util.List;
import java.util.Map;
import redis.clients.jedis.Jedis;
 
public class SimplePost {
 
	private Jedis jedis;

	public static String USERS_KEY = "users"; // Key set for users' name
	public static String USERS_LIST = "users_list"; // List for user's names
	public static String USERS_HASHMAP = "users_hashmap"; // HashMap for user's names
	
	public SimplePost() {
		this.jedis = new Jedis();
	}

	// List methods -------------------------------

	public void saveUserList(String username) {
        jedis.rpush(USERS_LIST, username);
    }

    public List<String> getUserList() {
        return jedis.lrange(USERS_LIST, 0, -1);
    }


	// Set methods --------------------------------
 
	public void saveUser(String username) {
		jedis.sadd(USERS_KEY, username);
	}

	public Set<String> getUser() {
		return jedis.smembers(USERS_KEY);
	}
	
	public Set<String> getAllKeys() {
		return jedis.keys("*");
	}


	// Hashmap methods ---------------------------

	public void saveUserMap(String user_first_name, String user_last_name) {
		jedis.hset(USERS_HASHMAP, user_first_name, user_last_name);
	}

	public Map<String, String> getUserMap() {
		return jedis.hgetAll(USERS_HASHMAP);
	}

	public Set<String> getAllKeysMap() {
		return jedis.hkeys(USERS_HASHMAP);
	}
 
	public static void main(String[] args) {
		SimplePost board = new SimplePost();
		// set some users
		String[] users = { "Ana", "Pedro", "Maria", "Luis" };
		String[] users_list = { "Rafael", "Daniela", "Marco", "Paulo"};

		// Save user's name in a key set
		for(String user : users) 
			board.saveUser(user);

		System.out.println();
		System.out.println("Set test --------------------------------------");
		System.out.println();

		// Key set method's test
		board.getAllKeys().stream().forEach(System.out::println);
		board.getUser().stream().forEach(System.out::println);

		System.out.println();
		System.out.println("List test --------------------------------------");
		System.out.println();

		// Save user's name in a list
		for (String user: users_list)
            board.saveUserList(user);

        board.getUserList().stream().forEach(System.out::println);

		System.out.println();
		System.out.println("HashMap test --------------------------------------");
		System.out.println();

		// Hashmap method's test
		String[] users_map = { "Carla", "Costa", "Manuel", "Gonçalves", "AnaBela", "Ferreira", "Rodrigo", "Martins" };

		for(int i = 0 ; i < users_map.length ; i+=2) 
			board.saveUserMap(users_map[i], users_map[i + 1]);

		for(String key : board.getAllKeysMap()) 
			System.out.println(key);
		
		for(Map.Entry<String, String> entry : board.getUserMap().entrySet())
			System.out.println(entry.getKey() + " " + entry.getValue());

	}
}



