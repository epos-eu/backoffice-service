package org.epos.backoffice.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.epos.eposdatamodel.*;
import org.epos.handler.dbapi.DBAPIClient;
import org.epos.handler.dbapi.DBAPIClient.SaveQuery;
import org.epos.handler.dbapi.dbapiimplementation.PersonDBAPI;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.epos.backoffice.bean.EntityTypeEnum.*;

public class User {

	//@JsonIgnore
	//private static final PersonDBAPI personDBAPI = (PersonDBAPI) new PersonDBAPI().metadataMode(false);

	protected static DBAPIClient dbapi = new DBAPIClient();

	private String eduPersonUniqueId;
	private String instanceId;
	private String lastName;
	private List<Group> groups;
	@JsonIgnore
	private String sub;
	private String firstName;
	private String email;
	private String metaId;
	private RoleEnum role;
	private List<EntityTypeEnum> accessibleSection;


	public User() {
	}

	public User(String instanceId) {
		this.instanceId = instanceId;
		Person person = dbapi.retrieve(Person.class, new DBAPIClient.GetQuery().instanceId(instanceId)).get(0);


		Objects.requireNonNull(person, "User with instanceId: [" + instanceId + "] not found");
		Objects.requireNonNull(person.getAuthIdentifier(), "Person with instanceId: [" + instanceId + "] is not a user");
		Objects.requireNonNull(person.getRole(), "Person with instanceId: [" + instanceId + "] is not a user");

		this.eduPersonUniqueId = person.getAuthIdentifier();
		this.instanceId = person.getInstanceId();
		this.lastName = person.getFamilyName();
		this.firstName = person.getGivenName();
		this.email = person.getEmail().get(0);
		this.metaId = person.getMetaId();
		this.role = RoleEnum.valueOf(person.getRole().toString());
		this.groups = person.getAuthorizedGroup();

	}

	/**
	 * The method check if the eduPersonUniqueId is present in some record of the person table
	 *
	 * @return true if the user is already registered
	 */
	public boolean isRegistered() {
		List<Person> person = dbapi.retrieve(Person.class, new DBAPIClient.GetQuery());
		boolean registered = false;
		for(Person p : person) {
			if(p.getAuthIdentifier()!=null && p.getAuthIdentifier().equals(this.eduPersonUniqueId)) registered = true;
		}
		return registered;
		//return personDBAPI.getByAuthId(this.eduPersonUniqueId) != null;
	}

	public void signUp() {
		Person person = mapUserToPerson();
		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();
		LinkedEntity le = dbapi.create(person);
		dbapi.closeTransaction(true);
		dbapi.setTransactionModeAuto(true);
		//personDBAPI.save(person);
		Person personPersisted = dbapi.retrieve(Person.class, new DBAPIClient.GetQuery().instanceId(le.getInstanceId())).get(0);
		this.metaId = personPersisted.getMetaId();
		this.role = RoleEnum.valueOf(personPersisted.getRole().toString());
		this.instanceId = personPersisted.getInstanceId();
	}

	public void signIn() {
		Person person = null;
		List<Person> people = dbapi.retrieve(Person.class, new DBAPIClient.GetQuery());
		for(Person p : people) {
			if(p.getAuthIdentifier()!=null && p.getAuthIdentifier().equals(this.eduPersonUniqueId)) person = p;
		}
		if(person!=null) {
			this.metaId = person.getMetaId();
			this.email = person.getEmail().get(0);
			this.firstName = person.getGivenName();
			this.lastName = person.getFamilyName();
			this.role = RoleEnum.valueOf(person.getRole().toString());
			this.instanceId = person.getInstanceId();
		}
	}

	public String getEduPersonUniqueId() {
		return eduPersonUniqueId;
	}

	public User setEduPersonUniqueId(String eduPersonUniqueId) {
		this.eduPersonUniqueId = eduPersonUniqueId;
		return this;
	}

	public String getLastName() {
		return lastName;
	}

	public User setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public String getSub() {
		return sub;
	}

	public User setSub(String sub) {
		this.sub = sub;
		return this;
	}

	public String getFirstName() {
		return firstName;
	}

	public User setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public User setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getMetaId() {
		return metaId;
	}

	public void setMetaId(String metaId) {
		this.metaId = metaId;
	}

	public RoleEnum getRole() {
		return role;
	}

	public void setRole(RoleEnum role) {
		this.role = role;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public void update() {
		Person person = mapUserToPerson();
		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();
		dbapi.createUpdate(person, new SaveQuery().setInstanceId(person.getInstanceId()));
		dbapi.closeTransaction(true);
		dbapi.setTransactionModeAuto(true);
		
		//person.setRole(this.role.toString());
		//personDBAPI.hardUpdate(person.getInstanceId(), person);
	}

	private Person mapUserToPerson() {
		Person person = new Person();
		person.setAuthIdentifier(this.eduPersonUniqueId);
		person.setFamilyName(this.lastName);
		person.setGivenName(this.firstName);
		person.setUid("Person/"+UUID.randomUUID());
		person.setEmail(List.of(this.email));
		person.setState(State.PLACEHOLDER);
		person.setEditorId("backoffice");
		person.setRole(Role.valueOf(Objects.nonNull(this.role) ? this.role.toString() : String.valueOf(RoleEnum.VIEWER)));
		person.setAuthorizedGroup(this.groups);
		Identifier identifier = new Identifier();
		identifier.setType("email");
		identifier.setIdentifier(this.email);
		person.setIdentifier(List.of(identifier));
		return person;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	@Override
	public String toString() {
		return "User{" +
				"eduPersonUniqueId='" + eduPersonUniqueId + '\'' +
				", instanceId='" + instanceId + '\'' +
				", lastName='" + lastName + '\'' +
				", groups=" + groups +
				", sub='" + sub + '\'' +
				", firstName='" + firstName + '\'' +
				", email='" + email + '\'' +
				", metaId='" + metaId + '\'' +
				", role=" + role +
				", accessibleSection=" + accessibleSection +
				'}';
	}

	public List<EntityTypeEnum> getAccessibleSection() {
		return accessibleSection;
	}

	public void setAccessibleSection(List<EntityTypeEnum> accessibleSection) {
		this.accessibleSection = accessibleSection;
	}

	public void generateAccessibleSection() {
		switch (this.role) {
		case ADMIN:
			this.accessibleSection = List.of(USER, CONTACTPOINT, DATAPRODUCT, DISTRIBUTION, WEBSERVICE, OPERATION, ORGANIZATION, PERSON);
			break;
		case REVIEWER:
			this.accessibleSection = List.of(DATAPRODUCT, DISTRIBUTION, WEBSERVICE, OPERATION, ORGANIZATION, PERSON);
			break;
		case EDITOR:
			this.accessibleSection = List.of(DATAPRODUCT, DISTRIBUTION, WEBSERVICE, OPERATION);
			break;
		case VIEWER:
			this.accessibleSection = List.of(DATAPRODUCT, DISTRIBUTION);
			break;
		}
	}
}
