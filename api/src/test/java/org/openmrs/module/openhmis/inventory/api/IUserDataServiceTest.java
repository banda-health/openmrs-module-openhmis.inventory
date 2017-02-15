package org.openmrs.module.openhmis.inventory.api;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.api.LocationService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by ICCHANGE on 9/Nov/2016.
 */
public class IUserDataServiceTest extends BaseModuleContextSensitiveTest {
	IUserDataService service;
	LocationService locationService;
	PersonService personService;
	UserService userService;

	/**
	 * @verifies return a list of users restricted by location
	 * @see IUserDataService#getUsersByLocation(Location)
	 */
	@Test
	public void userInfoTest_shouldGetTwoDifferentListsOfUsersPerTwoDifferentLocations() {
		service = Context.getService(IUserDataService.class);
		locationService = Context.getLocationService();
		personService = Context.getPersonService();
		userService = Context.getUserService();

		Location location1 = new Location();
		location1.setName("location1");
		locationService.saveLocation(location1);

		Map<String, String> map1 = new HashMap<String, String>();
		map1.put("defaultLocation", location1.getId() + "");
		Person person1 = new Person();
		person1.setGender("male");
		Set<PersonName> nameList1 = new TreeSet<PersonName>();
		PersonName name1 = new PersonName();
		name1.setGivenName("personTest1");
		name1.setFamilyName("personTest1");
		nameList1.add(name1);
		person1.setNames(nameList1);
		personService.savePerson(person1);
		User user1 = new User();
		user1.setPerson(person1);
		user1.setName("userTest1");
		user1.setUserProperties(map1);
		userService.saveUser(user1, "testP4ssword");
		Context.flushSession();

		List<User> userList1 = service.getUsersByLocation(location1);
		Assert.assertEquals(1, userList1.size());
		Assert.assertEquals("userTest1", userList1.get(0).getName());

		Location location2 = new Location();
		location2.setName("location2");
		locationService.saveLocation(location2);

		Map<String, String> map2 = new HashMap<String, String>();
		map2.put("defaultLocation", location2.getId() + "");
		Person person2 = new Person();
		person2.setGender("male");
		Set<PersonName> nameList2 = new TreeSet<PersonName>();
		PersonName name2 = new PersonName();
		name2.setGivenName("personTest2");
		name2.setFamilyName("personTest2");
		nameList2.add(name2);
		person2.setNames(nameList2);
		personService.savePerson(person2);
		User user2 = new User();
		user2.setPerson(person2);
		user2.setName("userTest2");
		user2.setUserProperties(map2);
		userService.saveUser(user2, "testP4ssword");
		Context.flushSession();

		Map<String, String> map3 = new HashMap<String, String>();
		map3.put("defaultLocation", location2.getId() + "");
		Person person3 = new Person();
		person3.setGender("male");
		Set<PersonName> nameList3 = new TreeSet<PersonName>();
		PersonName name3 = new PersonName();
		name3.setGivenName("personTest3");
		name3.setFamilyName("personTest3");
		nameList3.add(name3);
		person3.setNames(nameList3);
		personService.savePerson(person3);
		User user3 = new User();
		user3.setPerson(person2);
		user3.setName("userTest3");
		user3.setUserProperties(map3);
		userService.saveUser(user3, "testP4ssword");
		Context.flushSession();

		List<User> userList2 = service.getUsersByLocation(location2);
		Assert.assertEquals(2, userList2.size());
		Assert.assertTrue(userList2.get(0).getName().equals("userTest2") ||
		        userList2.get(0).getName().equals("userTest3"));
		Assert.assertTrue(userList2.get(1).getName().equals("userTest2") ||
		        userList2.get(1).getName().equals("userTest3"));

	}

}
