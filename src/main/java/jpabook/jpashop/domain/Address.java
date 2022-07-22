package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable // jpa의 내장타입
@Getter // 값타입은 변경이 되면 안됨. 즉, 생성할때만 값이 세팅됨
public class Address {
    private String city;
    private String street;
    private String zipcode;

    protected Address() {} // 기본 생성자, 얘는 jpa 스펙상 만들어 둔 것이고 사용하지는 말자.(public 안쓰고 protected 쓴것을 보면 알 수 있음..?)

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
