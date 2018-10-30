package com.yjy.core.jpa.domain;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;

/**步骤一：编写默认实体父类*/
@MappedSuperclass
public class IdEntity implements Serializable {
	
	private static final long serialVersionUID = 7988377299341530426L;
	
    protected String id;
	
	public void setId(String id) {
		this.id = id;
	}
	
	@Id
    @GenericGenerator(name="uuid", strategy="uuid")
    @GeneratedValue(generator="uuid")
	public String getId() {
		return id;
	}
}
