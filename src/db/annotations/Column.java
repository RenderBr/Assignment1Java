/*
    COMP305 - Java Application Development
    Assignment 2 - Library Management System
    Julian Seitz
*/

package db.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String name();
    ColumnDataType type();
    boolean primary_key() default false;
    boolean auto_increment() default false;

    boolean not_null() default false;

    String default_value() default "";

    /**
     * Foreign key example: Users(id). This column will reference -> Users(id)
     * @return Foreign key column reference
     */
    String foreign_key() default "";
}
