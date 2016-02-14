package iPodia;

import java.util.Arrays;
import java.util.HashSet;

public class User {
	public static final String Admin = "admin";
	public static final String Student = "student";

	private String m_type;
	private String m_email;
	private String m_name;
	private String m_university;
	private HashSet<String> m_classes;

	public User() {
		m_type = null;
		m_email = null;
		m_name = null;
		m_university = null;
		m_classes = null;
	}

	public String getType() {
		return m_type;
	}

	public void setType(String type) {
		m_type = type;
	}

	public boolean isAdmin() {
		return m_type.equals(Admin);
	}

	public boolean isStudent() {
		return m_type.equals(Student);
	}

	public String getEmail() {
		return m_email;
	}

	public void setEmail(String email) {
		m_email = email;
	}

	public String getName() {
		return m_name;
	}

	public void setName(String firstName, String lastName) {
		m_name = firstName + " " + lastName;
	}

	public String getUniversity() {
		return m_university;
	}

	public void setUniversity(String university) {
		m_university = university;
	}

	public HashSet<String> getClasses() {
		return m_classes;
	}

	public void setClasses(String classes) {
		m_classes = new HashSet<String>(Arrays.asList(classes.split("\\s*,\\s*")));
	}

	public boolean hasClass(String className) {
		return m_classes.contains(className);
	}

	public void addClass(String className) {
		m_classes.add(className);
	}
}
