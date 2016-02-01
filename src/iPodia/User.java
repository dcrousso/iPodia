package iPodia;

import java.util.Arrays;

public class User {
	public static final String Admin = "admin";
	public static final String Student = "student";

	private int m_id;
	private String m_type;
	private String m_username;
	private String m_name;
	private String m_university;
	private int[] m_classes;

	public User() {
		m_id = 0;
		m_type = null;
		m_username = null;
		m_name = null;
		m_university = null;
		m_classes = null;
	}

	public int getId() {
		return m_id;
	}

	public void setId(int id) {
		m_id = id;
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

	public String getUsername() {
		return m_username;
	}

	public void setUsername(String username) {
		m_username = username;
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

	public int[] getClasses() {
		return m_classes;
	}

	public void setClasses(int[] classes) {
		m_classes = classes;
	}

	public void setClasses(String classes) {
		m_classes = Arrays.asList(classes.split("\\s*,\\s*")).stream().mapToInt(item -> Integer.parseInt(item)).toArray();
	}
}
