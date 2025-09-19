package jpabook.javaspring.features.admin.domains;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum AdminRole {
    ADMIN, SUPER_ADMIN,MANAGER;
}