package com.interview.accounts.domain;

import lombok.*;

import javax.persistence.*;

@Table(name = "accounts")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class Account {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "number")
    private int number;
    @Column(name = "name")
    private String name;
    @Column(name = "balance")
    private double balance;


}
