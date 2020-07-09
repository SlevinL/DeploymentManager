package de.bas.deploymentmanager.logic.domain.dicd.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * User aus dem Jenkins
 */
@Getter
@Setter
@Entity
@Table(name = "JENKINS_USER")
public class User {

    @Id
    @Column(name = "ID")
    private String id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "LOGINNAME")
    private String loginName;
    @Column(name = "API_TOKEN")
    private String apiToken;
}
