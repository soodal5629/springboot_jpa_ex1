package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotEmpty
    private String username;

    @Embedded // 내장타입을 포함했다는 의미
    //@JsonIgnore
    private Address address;

    @OneToMany(mappedBy = "member")
    @JsonIgnore
    private List<Orders> orders = new ArrayList<>();

}
