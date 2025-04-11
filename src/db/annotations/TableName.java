/*
    COMP305 - Java Application Development
    Assignment 2 - Library Management System
    Julian Seitz
*/

package db.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TableName {
    String name();
}
