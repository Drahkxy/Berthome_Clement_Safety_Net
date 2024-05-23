package com.openclassrooms.safety_net.model.response;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Data
public class ChildInfo {
	private String firstName;
	private String lastName;
	private int age;
	private List<String> familyMembersCompleteName = new ArrayList<>();

	public ChildInfo (String firstName, String lastName, int age) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
	}

	public ChildInfo () {
	}

	public void addFamilyMember (String familyMemberCompleteName) {
		this.familyMembersCompleteName.add(familyMemberCompleteName);
	}
}
