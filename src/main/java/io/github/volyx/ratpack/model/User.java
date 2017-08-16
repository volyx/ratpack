package io.github.volyx.ratpack.model;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {

    public User(){
        this.id = null;
        this.email = null;
        this.first_name = null;
        this.last_name = null;
        this.gender = null;
        this.birth_date = null;
    }
    /**
     * id - уникальный внешний идентификатор пользователя.
     * Устанавливается тестирующей системой и используется затем, для проверки ответов сервера.
     * 32-разрядное целое число.
     */
    public final Integer id;
    /**
     * email - адрес электронной почты пользователя.
     * Тип - unicode-строка длиной до 100 символов.
     * Гарантируется уникальность.
     */
    @Max(value = 100, message = "email should not be greater than 100")
    @Email(message = "Is not email")
    public final String email;
    @Max(value = 50, message = "first_name should not be greater than 50")
    public final String first_name;
    @Max(value = 50, message = "last_name should not be greater than 50")
    public final String last_name;
    public final Gender gender;
    public final Long birth_date;


    public User(Integer id, String email, String first_name, String last_name, Gender gender, Long birth_date) {
        this.id = id;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.gender = gender;
        this.birth_date = birth_date;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", gender=" + gender +
                ", birth_date=" + birth_date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
