package com.example.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "student")
@XmlAccessorType(XmlAccessType.FIELD)
public class StudentJdbc {
	private Long id;
	private String firstName;
	private String lastName;
	private String email;
}
