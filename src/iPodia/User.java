package iPodia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class User {
	public static final String Admin = "admin";
	public static final String Student = "student";

	private String m_type;
	private String m_email;
	private String m_name;
	private String m_university;
	private String[] m_classes;

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

	public String[] getClasses() {
		return m_classes;
	}

	public void setClasses(String classes) {
//		m_classes = Arrays.asList(classes.split("\\s*,\\s*")).stream().mapToInt(item -> Integer.parseInt(item)).toArray();
		m_classes = classes.split("\\s*,\\s*");
	}
	
	public void addClass (String aClass) {
		List<String> myClassesList = new ArrayList<String>(Arrays.asList(m_classes));
		myClassesList.add(aClass);
		m_classes = new String[myClassesList.size()];
		m_classes = myClassesList.toArray(m_classes);
	}
}
